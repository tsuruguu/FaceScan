package com.faceScan.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static DatabaseManager instance;
    private static final String URL = "jdbc:sqlite:face_scan.db";

    static {
        try {
            try (Connection conn = getConnection()) {
                Statement stmt = conn.createStatement();

                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT UNIQUE NOT NULL," +
                        "password TEXT NOT NULL" +
                        ");");

                stmt.execute("CREATE TABLE IF NOT EXISTS groups (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "user_id INTEGER NOT NULL," +
                        "FOREIGN KEY(user_id) REFERENCES users(id)" +
                        ");");

                stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "first_name TEXT NOT NULL," +
                        "last_name TEXT NOT NULL," +
                        "photo_path TEXT," +
                        "group_id INTEGER NOT NULL," +
                        "FOREIGN KEY(group_id) REFERENCES groups(id)" +
                        ");");

                stmt.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "student_id INTEGER NOT NULL," +
                        "timestamp TEXT NOT NULL," +
                        "FOREIGN KEY(student_id) REFERENCES students(id)" +
                        ");");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }
}

