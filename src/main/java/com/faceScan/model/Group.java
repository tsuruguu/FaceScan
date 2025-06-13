package com.faceScan.model;

public class Group {
    private int id;
    private String name;
    private int professorId;

    public Group(int id, String name, int professorId) {
        this.id = id;
        this.name = name;
        this.professorId = professorId;
    }

    public Group(String name, int professorId) {
        this.id = 0;
        this.name = name;
        this.professorId = professorId;
    }


    public int getId() { return id; }
    public String getName() { return name; }
    public int getProfessorId() { return professorId; }

    @Override
    public String toString() {
        return name;
    }
}
