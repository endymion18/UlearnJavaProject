package ulearnprojectlobanov.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Connector {

    public static Connection getConnection() {
        Properties properties = GetProperties();
        String user = properties.getProperty("DB_USER");
        String pass = properties.getProperty("DB_PASS");
        String host = properties.getProperty("DB_HOST");
        String port = properties.getProperty("DB_PORT");
        String name = properties.getProperty("DB_NAME");
        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, name);

        try {
            Connection connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Соединение установлено");
            return connection;

        } catch (SQLException e) {
            System.err.println("Ошибка: " + e.getMessage());
            return null;
        }
    }

    public static Properties GetProperties() {
        Properties properties = new Properties();

        try {
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }
}