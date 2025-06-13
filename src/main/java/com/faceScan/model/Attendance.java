package com.faceScan.model;

public class Attendance {
    private int id;
    private int studentId;
    private int groupId;
    private String date;
    private boolean present;
    private String studentName;

    public Attendance(int id, int studentId, int groupId, String date, boolean present) {
        this.id = id;
        this.studentId = studentId;
        this.groupId = groupId;
        this.date = date;
        this.present = present;
    }

    public Attendance(int studentId, int groupId, String date, boolean present) {
        this(0, studentId, groupId, date, present);
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public int getGroupId() { return groupId; }
    public String getDate() { return date; }
    public boolean isPresent() { return present; }
    public String getStudentName() {return studentName;}

    public void setId(int id) { this.id = id; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public void setDate(String date) { this.date = date; }
    public void setPresent(boolean present) { this.present = present; }
    public void setStudentName(String studentName) {this.studentName = studentName;}

    @Override
    public String toString() {
        return "Attendance{" +
                "studentId=" + studentId +
                ", groupId=" + groupId +
                ", date='" + date + '\'' +
                ", present=" + present +
                '}';
    }
}
