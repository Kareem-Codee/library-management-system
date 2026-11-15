package com.kareemmushtaha.library.ui;

import com.kareemmushtaha.library.exception.LibraryException;
import com.kareemmushtaha.library.model.Book;
import com.kareemmushtaha.library.model.Loan;
import com.kareemmushtaha.library.model.Member;
import com.kareemmushtaha.library.service.LibraryService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// the menu that the user sees in the console
public class ConsoleMenu {

    private LibraryService service = new LibraryService();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1:
                        addBook();
                        break;
                    case 2:
                        listBooks();
                        break;
                    case 3:
                        searchBooks();
                        break;
                    case 4:
                        deleteBook();
                        break;
                    case 5:
                        addMember();
                        break;
                    case 6:
                        listMembers();
                        break;
                    case 7:
                        borrowBook();
                        break;
                    case 8:
                        returnBook();
                        break;
                    case 9:
                        showOverdue();
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option, try again.");
                }
            } catch (LibraryException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }

        System.out.println("Goodbye!");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== Library Management System =====");
        System.out.println("1. Add book");
        System.out.println("2. List books");
        System.out.println("3. Search books by title");
        System.out.println("4. Delete book");
        System.out.println("5. Add member");
        System.out.println("6. List members");
        System.out.println("7. Borrow book");
        System.out.println("8. Return book");
        System.out.println("9. Show overdue loans");
        System.out.println("0. Exit");
    }

    private void addBook() throws LibraryException, SQLException {
        String title = readText("Title: ");
        String author = readText("Author: ");
        String isbn = readText("ISBN: ");
        int copies = readInt("Number of copies: ");

        service.addBook(title, author, isbn, copies);
        System.out.println("Book added.");
    }

    private void listBooks() throws SQLException {
        List<Book> books = service.getAllBooks();

        if (books.isEmpty()) {
            System.out.println("There are no books.");
        }
        for (Book book : books) {
            System.out.println(book);
        }
    }

    private void searchBooks() throws SQLException {
        String word = readText("Search for: ");
        List<Book> books = service.searchBooks(word);

        if (books.isEmpty()) {
            System.out.println("No books found.");
        }
        for (Book book : books) {
            System.out.println(book);
        }
    }

    private void deleteBook() throws LibraryException, SQLException {
        int id = readInt("Book id to delete: ");
        service.deleteBook(id);
        System.out.println("Book deleted.");
    }

    private void addMember() throws LibraryException, SQLException {
        String name = readText("Full name: ");
        String email = readText("Email: ");

        service.addMember(name, email);
        System.out.println("Member added.");
    }

    private void listMembers() throws SQLException {
        List<Member> members = service.getAllMembers();

        if (members.isEmpty()) {
            System.out.println("There are no members.");
        }
        for (Member member : members) {
            System.out.println(member);
        }
    }

    private void borrowBook() throws LibraryException, SQLException {
        int bookId = readInt("Book id: ");
        int memberId = readInt("Member id: ");

        service.borrowBook(bookId, memberId);
        System.out.println("Done! The book is due in 14 days.");
    }

    private void returnBook() throws LibraryException, SQLException {
        int bookId = readInt("Book id: ");
        int memberId = readInt("Member id: ");

        service.returnBook(bookId, memberId);
        System.out.println("Book returned. Thank you!");
    }

    private void showOverdue() throws SQLException {
        List<Loan> loans = service.getOverdueLoans();

        if (loans.isEmpty()) {
            System.out.println("No overdue loans.");
        }
        for (Loan loan : loans) {
            System.out.println(loan);
        }
    }

    // read a number, ask again if the user writes something that is not a number
    private int readInt(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    private String readText(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
}