package com.faceScan.model;

public class User {
    private int id;
    private String username;
    private String password;  // do rejestracji, nie przesyłaj hasła po zalogowaniu!

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
