package com.kareemmushtaha.library.dao;

import com.kareemmushtaha.library.model.Loan;
import com.kareemmushtaha.library.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// this class talks to the loans table
public class LoanDAO {

    // save a new loan (uses the connection from the service because of the transaction)
    public void add(Connection conn, Loan loan) throws SQLException {
        String sql = "INSERT INTO loans (book_id, member_id, loan_date, due_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, loan.getBookId());
            ps.setInt(2, loan.getMemberId());
            ps.setDate(3, Date.valueOf(loan.getLoanDate()));
            ps.setDate(4, Date.valueOf(loan.getDueDate()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                loan.setId(keys.getInt(1));
            }
        }
    }

    // check if the member already has this book and did not return it
    public boolean hasActiveLoan(Connection conn, int bookId, int memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM loans WHERE book_id = ? AND member_id = ? AND return_date IS NULL";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, memberId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    // put today's date as return date. returns false if there is no active loan
    public boolean markReturned(Connection conn, int bookId, int memberId) throws SQLException {
        String sql = "UPDATE loans SET return_date = ? WHERE book_id = ? AND member_id = ? AND return_date IS NULL";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, bookId);
            ps.setInt(3, memberId);
            return ps.executeUpdate() > 0;
        }
    }

    // loans that are not returned and the due date is before today
    // I use JOIN to get the book title and the member name
    public List<Loan> getOverdue() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id, l.book_id, l.member_id, l.loan_date, l.due_date, b.title, m.full_name "
                + "FROM loans l "
                + "JOIN books b ON l.book_id = b.id "
                + "JOIN members m ON l.member_id = m.id "
                + "WHERE l.return_date IS NULL AND l.due_date < CURRENT_DATE "
                + "ORDER BY l.due_date";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Loan loan = new Loan(
                        rs.getInt("book_id"),
                        rs.getInt("member_id"),
                        rs.getDate("loan_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate());
                loan.setId(rs.getInt("id"));
                loan.setBookTitle(rs.getString("title"));
                loan.setMemberName(rs.getString("full_name"));
                loans.add(loan);
            }
        }
        return loans;
    }
}