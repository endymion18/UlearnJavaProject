package ulearnprojectlobanov.utils;

import org.apache.poi.ss.usermodel.*;
import ulearnprojectlobanov.entities.Student;
import ulearnprojectlobanov.entities.StudentGrade;
import ulearnprojectlobanov.entities.Theme;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;


public class ExcelParser {
    public final ArrayList<Student> students;
    public final ArrayList<StudentGrade> studentGrades;
    public final ArrayList<Theme> themes;
    private final HashSet<String> valuesToSkip = new HashSet<>() {{
        add("За весь курс");
        add("Преподавателю о курсе");
    }};


    public ExcelParser(String filename) {
        try (FileInputStream inputStream = new FileInputStream(filename)) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            this.students = getStudents(sheet);
            this.studentGrades = getStudentsGrades(sheet);
            this.themes = getThemes(sheet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Student> getStudents(Sheet sheet) {
        ArrayList<Student> students = new ArrayList<>();
        for (int i = 3; sheet.getRow(i) != null; i++) {
            Row row = sheet.getRow(i);
            String[] studentName = row.getCell(0).toString().split("\\s+");
            if (studentName.length != 2) {
                continue;
            }
            UUID ulearnID = UUID.fromString(row.getCell(1).toString());
            String email = row.getCell(2).toString();
            String group = row.getCell(3).toString();
            int exercises = Double.valueOf(row.getCell(5).toString()).intValue();
            int homework = Double.valueOf(row.getCell(6).toString()).intValue();
            students.add(new Student(studentName, ulearnID, email, group, exercises + homework));
        }
        return students;
    }

    private ArrayList<StudentGrade> getStudentsGrades(Sheet sheet) {
        ArrayList<StudentGrade> studentsGrades = new ArrayList<>();

        for (int i = 3; sheet.getRow(i) != null; i++) {
            Row row = sheet.getRow(i);

            String[] studentName = row.getCell(0).toString().split("\\s+");
            if (studentName.length != 2) {
                continue;
            }
            UUID ulearnID = UUID.fromString(row.getCell(1).toString());
            Row namesRow = sheet.getRow(0);
            int themeID = 1;
            for (int j = 3; namesRow.getCell(j) != null; j++) {
                String name = namesRow.getCell(j).toString();
                if (valuesToSkip.contains(name) | name.isEmpty()) continue;
                int studentScore = 0;

                for (int k = j + 1; namesRow.getCell(k) != null && namesRow.getCell(k).toString().isEmpty(); k++) {
                    String exerciseNaming = sheet.getRow(1).getCell(k).toString();

                    if (exerciseNaming.equals("Упр") | exerciseNaming.equals("ДЗ")) {
                        int exerciseScore = Double.valueOf(sheet.getRow(i).getCell(k).toString()).intValue();
                        studentScore += exerciseScore;
                    }
                }
                studentsGrades.add(new StudentGrade(ulearnID, themeID, studentScore));
                themeID++;
            }
        }
        return studentsGrades;
    }

    private ArrayList<Theme> getThemes(Sheet sheet) {
        ArrayList<Theme> themes = new ArrayList<>();

        Row namesRow = sheet.getRow(0);
        int themeID = 1;

        for (int i = 3; namesRow.getCell(i) != null; i++) {
            String name = namesRow.getCell(i).toString();
            if (valuesToSkip.contains(name) | name.isEmpty()) continue;
            int themeScore = 0;
            for (int j = i + 1; namesRow.getCell(j) != null && namesRow.getCell(j).toString().isEmpty(); j++) {
                String exerciseNaming = sheet.getRow(1).getCell(j).toString();
                if (exerciseNaming.equals("Упр") | exerciseNaming.equals("ДЗ")) {
                    int exerciseScore = Double.valueOf(sheet.getRow(2).getCell(j).toString()).intValue();
                    themeScore += exerciseScore;
                }
            }
            themes.add(new Theme(themeID, name, themeScore));
            themeID++;
        }
        return themes;
    }
}
