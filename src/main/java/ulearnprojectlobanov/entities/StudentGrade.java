package ulearnprojectlobanov.entities;

import java.util.UUID;

public class StudentGrade {
    public final UUID ulearnID;
    public final int themeID;
    public final int studentScore;

    public StudentGrade(UUID ulearnID, int themeID, int studentScore) {
        this.ulearnID = ulearnID;
        this.themeID = themeID;
        this.studentScore = studentScore;
    }
}
