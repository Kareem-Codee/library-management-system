package com.kareemmushtaha.library.dao;

import com.kareemmushtaha.library.model.Book;
import com.kareemmushtaha.library.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// this class talks to the books table
public class BookDAO {

    // insert a new book
    public void add(Book book) throws SQLException {
        // I use ? and PreparedStatement so nobody can do SQL injection
        String sql = "INSERT INTO books (title, author, isbn, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setInt(4, book.getTotalCopies());
            ps.setInt(5, book.getAvailableCopies());
            ps.executeUpdate();

            // get the id that the database created and put it in the object
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                book.setId(keys.getInt(1));
            }
        }
    }

    // get all the books
    public List<Book> getAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                books.add(readBook(rs));
            }
        }
        return books;
    }

    // search books by a word in the title
    public List<Book> searchByTitle(String word) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? ORDER BY title";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + word + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                books.add(readBook(rs));
            }
        }
        return books;
    }

    // delete a book, returns true if it was found
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    // minus one copy. I get the connection from outside because it is part of a transaction
    public boolean decreaseCopies(Connection conn, int bookId) throws SQLException {
        // the check "available_copies > 0" is inside the update, so it can never go below 0
        String sql = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ? AND available_copies > 0";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    // plus one copy (when the book is returned)
    public void increaseCopies(Connection conn, int bookId) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
        }
    }

    // turn one row of the result into a Book object
    private Book readBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getInt("total_copies"),
                rs.getInt("available_copies"));
    }
}