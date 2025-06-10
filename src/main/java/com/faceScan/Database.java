package com.faceScan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:sqlite:face_scan.db";
    private static Database instance;

    private Connection connection;

    private Database() {
        try {
            connection = DriverManager.getConnection(URL);
            System.out.println("Połączono z bazą danych SQLite.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Błąd połączenia z bazą danych");
        }
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
