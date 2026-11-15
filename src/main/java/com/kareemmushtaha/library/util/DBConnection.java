package com.kareemmushtaha.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// this class only gives me a connection to the database
public class DBConnection {

    // XAMPP default settings
    private static final String URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}