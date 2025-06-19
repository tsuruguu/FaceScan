package com.faceScan.model;

import javafx.beans.property.*;

public class StudentPresence {
    private final IntegerProperty id;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final BooleanProperty present;
    private Integer attendanceId;

    public StudentPresence(int id, String firstName, String lastName) {
        this.id = new SimpleIntegerProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.present = new SimpleBooleanProperty(false);
    }

    public int getId() { return id.get(); }
    public String getFirstName() { return firstName.get(); }
    public String getLastName() { return lastName.get(); }
    public boolean isPresent() { return present.get(); }

    public void setPresent(boolean value) { present.set(value); }
    public BooleanProperty presentProperty() { return present; }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }
}
