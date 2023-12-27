package ulearnprojectlobanov.database;

import ulearnprojectlobanov.entities.*;
import ulearnprojectlobanov.utils.ExcelParser;
import ulearnprojectlobanov.utils.VKApi;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class Tables {
    private static final String CREATE_TABLES = """
            CREATE TABLE Student (
            ulearnID UUID PRIMARY KEY,
            firstname VARCHAR(50) NOT NULL,
            lastname VARCHAR(50) NOT NULL,
            email VARCHAR(100) NOT NULL,
            "group" VARCHAR(100) NOT NULL,
            total_grade INT NOT NULL,
            birthdate DATE,
            city VARCHAR(60),
            country VARCHAR(60),
            gender VARCHAR(6),
            university_name VARCHAR(50)
            );
                        
            CREATE TABLE Theme (
            "id" INT PRIMARY KEY,
            "name" VARCHAR(80) NOT NULL,
            max_score INT NOT NULL
            );
                        
            CREATE TABLE StudentGrade (
            ulearnID UUID,
            themeID INT,
            student_score INT NOT NULL,
            FOREIGN KEY (ulearnID) REFERENCES Student (ulearnID),
            FOREIGN KEY (themeID) REFERENCES Theme ("id")
            )""";
    private static final String INSERT_INTO_STUDENTS = """
            INSERT INTO Student VALUES
            (?, ?, ?, ?, ?, ?)""";
    private static final String INSERT_INTO_STUDENTS_GRADES = """
            INSERT INTO StudentGrade VALUES
            (?, ?, ?)
            """;
    private static final String INSERT_INTO_THEMES = """
            INSERT INTO Theme VALUES
            (?, ?, ?)
            """;
    private static final String UPDATE_STUDENTS = """
            UPDATE Student
            SET birthdate = ?, city = ?, country = ?, gender = ?, university_name = ?
            WHERE firstname = ? AND lastname = ?""";

    public static void Create(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(CREATE_TABLES);
        System.out.println("Таблицы созданы");
    }

    public static void AddParsedData(Connection connection, ExcelParser parser) throws SQLException {
        PreparedStatement stmt1 = connection.prepareStatement(INSERT_INTO_STUDENTS);
        for (Student student : parser.students) {
            stmt1.setObject(1, student.ulearnID);
            stmt1.setString(2, student.firstname);
            stmt1.setString(3, student.lastname);
            stmt1.setString(4, student.email);
            stmt1.setString(5, student.group);
            stmt1.setInt(6, student.totalGrade);

            stmt1.addBatch();
        }
        stmt1.executeBatch();

        PreparedStatement stmt2 = connection.prepareStatement(INSERT_INTO_THEMES);
        for (Theme theme : parser.themes) {
            stmt2.setInt(1, theme.id);
            stmt2.setString(2, theme.name);
            stmt2.setInt(3, theme.maxScore);

            stmt2.addBatch();
        }
        stmt2.executeBatch();

        PreparedStatement stmt3 = connection.prepareStatement(INSERT_INTO_STUDENTS_GRADES);
        for (StudentGrade grade : parser.studentGrades) {
            stmt3.setObject(1, grade.ulearnID);
            stmt3.setInt(2, grade.themeID);
            stmt3.setInt(3, grade.studentScore);

            stmt3.addBatch();
        }
        stmt3.executeBatch();


        System.out.println("Данные из файла успешно добавлены в базу данных");
    }

    public static void UpdateStudents(Connection connection, VKApi profiles) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(UPDATE_STUDENTS);
        for (Map.Entry<String, StudentVKProfile> profile : profiles.infoMap.entrySet()) {
            String[] name = profile.getKey().split(" ");
            String firstname = name[0];
            String lastname = name[1];
            StudentVKProfile profileInfo = profile.getValue();

            if (profileInfo.birthDate != null && profileInfo.birthDate.split("\\.").length == 3) {
                DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date birthdate = new Date(df.parse(profileInfo.birthDate).getTime());
                    stmt.setDate(1, birthdate);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                stmt.setDate(1, null);
            }

            stmt.setString(2, profileInfo.city);
            stmt.setString(3, profileInfo.country);
            stmt.setString(4, profileInfo.gender);
            stmt.setString(5, profileInfo.university_name);
            stmt.setString(6, firstname);
            stmt.setString(7, lastname);

            stmt.addBatch();
        }
        stmt.executeBatch();
        System.out.println("Данные из Вконтакте добавлены в базу даных");
    }
}
