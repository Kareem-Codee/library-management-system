package com.kareemmushtaha.library.service;

import com.kareemmushtaha.library.dao.BookDAO;
import com.kareemmushtaha.library.dao.LoanDAO;
import com.kareemmushtaha.library.dao.MemberDAO;
import com.kareemmushtaha.library.exception.LibraryException;
import com.kareemmushtaha.library.model.Book;
import com.kareemmushtaha.library.model.Loan;
import com.kareemmushtaha.library.model.Member;
import com.kareemmushtaha.library.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

// the rules of the library are here (checking the input, borrow, return)
public class LibraryService {

    // a book can be borrowed for 14 days
    private static final int LOAN_DAYS = 14;

    private BookDAO bookDAO = new BookDAO();
    private MemberDAO memberDAO = new MemberDAO();
    private LoanDAO loanDAO = new LoanDAO();

    public void addBook(String title, String author, String isbn, int copies)
            throws LibraryException, SQLException {
        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            throw new LibraryException("Title, author and ISBN are required.");
        }
        if (copies < 1) {
            throw new LibraryException("Copies must be at least 1.");
        }
        Book book = new Book(title, author, isbn, copies);
        bookDAO.add(book);
    }

    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.getAll();
    }

    public List<Book> searchBooks(String word) throws SQLException {
        return bookDAO.searchByTitle(word);
    }

    public void deleteBook(int id) throws LibraryException, SQLException {
        boolean deleted = bookDAO.delete(id);
        if (!deleted) {
            throw new LibraryException("Book not found.");
        }
    }

    public void addMember(String fullName, String email) throws LibraryException, SQLException {
        if (fullName.isEmpty() || !email.contains("@")) {
            throw new LibraryException("Please enter a valid name and email.");
        }
        Member member = new Member(fullName, email);
        memberDAO.add(member);
    }

    public List<Member> getAllMembers() throws SQLException {
        return memberDAO.getAll();
    }

    // borrow a book
    // two things must happen together: save the loan AND decrease the copies
    // so I use a transaction (commit / rollback)
    public void borrowBook(int bookId, int memberId) throws LibraryException, SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false); // start the transaction

            if (loanDAO.hasActiveLoan(conn, bookId, memberId)) {
                throw new LibraryException("This member already borrowed this book.");
            }

            boolean done = bookDAO.decreaseCopies(conn, bookId);
            if (!done) {
                throw new LibraryException("No copies available for this book.");
            }

            LocalDate today = LocalDate.now();
            Loan loan = new Loan(bookId, memberId, today, today.plusDays(LOAN_DAYS));
            loanDAO.add(conn, loan);

            conn.commit(); // everything is ok, save it
        } catch (LibraryException | SQLException e) {
            conn.rollback(); // something failed, cancel everything
            throw e;
        } finally {
            conn.close();
        }
    }

    // return a book (same idea with the transaction)
    public void returnBook(int bookId, int memberId) throws LibraryException, SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            boolean found = loanDAO.markReturned(conn, bookId, memberId);
            if (!found) {
                throw new LibraryException("No active loan for this book and member.");
            }

            bookDAO.increaseCopies(conn, bookId);

            conn.commit();
        } catch (LibraryException | SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    public List<Loan> getOverdueLoans() throws SQLException {
        return loanDAO.getOverdue();
    }
}