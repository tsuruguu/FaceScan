package com.faceScan.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBInit {
    public static void init() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:face_scan.db");
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE,
                    password TEXT
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS groups (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT,
                    user_id INTEGER,
                    FOREIGN KEY(user_id) REFERENCES users(id)
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT,
                    surname TEXT,
                    group_id INTEGER,
                    FOREIGN KEY(group_id) REFERENCES groups(id)
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS attendance (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER,
                    date TEXT,
                    present INTEGER,
                    FOREIGN KEY(student_id) REFERENCES students(id)
                );
            """);

            System.out.println("✅ Baza danych zainicjalizowana.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}