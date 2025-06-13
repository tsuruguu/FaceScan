package com.faceScan.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty photoPath;
    private final List<Group> groups = new ArrayList<>();

    public Student(int id, String firstName, String lastName, String photoPath) {
        this.id = new SimpleIntegerProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.photoPath = new SimpleStringProperty(photoPath == null ? "" : photoPath);
    }

    // getters + properties
    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }

    public String getFirstName() { return firstName.get(); }
    public SimpleStringProperty firstNameProperty() { return firstName; }

    public String getLastName() { return lastName.get(); }
    public SimpleStringProperty lastNameProperty() { return lastName; }

    public String getPhotoPath() { return photoPath.get(); }
    public SimpleStringProperty photoPathProperty() { return photoPath; }

    @Override
    public String toString() {
        return firstName.get() + " " + lastName.get();
    }

    public static Student fromUser(User user) {
        return new Student(user.getId(), user.getFirstName(), user.getLastName(), user.getPhotoPath());
    }

    public List<Group> getGroups() {return groups;}

    public void addGroup(Group group) {groups.add(group);}
}
