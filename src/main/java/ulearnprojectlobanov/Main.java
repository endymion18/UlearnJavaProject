package ulearnprojectlobanov;

import ulearnprojectlobanov.database.Connector;
import ulearnprojectlobanov.database.Tables;
import ulearnprojectlobanov.utils.ExcelParser;
import ulearnprojectlobanov.utils.VKApi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ExcelParser parser = new ExcelParser("src/basicprogramming.xlsx");
        Connection conn = Connector.getConnection();

        if (conn != null) {
            try {
                Tables.Create(conn);
                Tables.AddParsedData(conn, parser);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try {
                VKApi parsedProfiles = new VKApi();
                Tables.UpdateStudents(conn, parsedProfiles);
            } catch (IOException | URISyntaxException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
