package com.faceScan.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static DatabaseManager instance;
    private static String URL = "jdbc:sqlite:face_scan.db";
    private static boolean initialized = false;

    public static void useTestDatabase() {
        URL = "jdbc:sqlite:test.db";
        instance = null;
        initialized = false;
    }

    public static void useProductionDatabase() {
        URL = "jdbc:sqlite:face_scan.db";
        instance = null;
        initialized = false;
    }

    private DatabaseManager() {
        System.out.println("[INFO] Inicjalizacja bazy danych...");
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();

            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "role TEXT NOT NULL," +
                    "first_name TEXT NOT NULL," +
                    "last_name TEXT NOT NULL," +
                    "photo_path TEXT" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS groups (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "professor_id INTEGER NOT NULL," +
                    "FOREIGN KEY(professor_id) REFERENCES users(id)" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS group_members (" +
                    "group_id INTEGER NOT NULL," +
                    "student_id INTEGER NOT NULL," +
                    "PRIMARY KEY (group_id, student_id)," +
                    "FOREIGN KEY(group_id) REFERENCES groups(id)," +
                    "FOREIGN KEY(student_id) REFERENCES users(id)" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "student_id INTEGER NOT NULL," +
                    "group_id INTEGER NOT NULL," +
                    "date TEXT NOT NULL," +
                    "present BOOLEAN NOT NULL," +
                    "FOREIGN KEY(student_id) REFERENCES users(id)," +
                    "FOREIGN KEY(group_id) REFERENCES groups(id)" +
                    ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (!initialized) {
            instance = new DatabaseManager();
            initialized = true;
        }
        return instance;
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void init() {
        getInstance();
    }

}

