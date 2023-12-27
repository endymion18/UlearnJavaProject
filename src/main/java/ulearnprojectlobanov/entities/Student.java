package ulearnprojectlobanov.entities;

import java.util.UUID;

public class Student {
    public final String firstname;
    public final String lastname;
    public final UUID ulearnID;
    public final String email;
    public final String group;
    public final int totalGrade;

    public Student(String[] name, UUID ulearnID, String email, String group, int totalGrade) {
        this.firstname = name[1];
        this.lastname = name[0];
        this.ulearnID = ulearnID;
        this.email = email;
        this.group = group;
        this.totalGrade = totalGrade;
    }
}
