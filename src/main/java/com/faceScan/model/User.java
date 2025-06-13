package com.faceScan.model;

public class User {
    private int id;
    private String username;
    private String password;  // do rejestracji, nie przesyłaj hasła po zalogowaniu!
    private String role;
    private String firstName;
    private String lastName;
    private String photoPath;

    public User(int id, String username, String role, String firstName, String lastName, String photoPath) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoPath = photoPath;
    }

    public User(String username, String password, String role, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhotoPath() { return photoPath; }
    public boolean isProfessor() {return "professor".equalsIgnoreCase(role);}

    public void setId(int id) { this.id = id; }
    public void setPassword(String password) { this.password = password; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}
