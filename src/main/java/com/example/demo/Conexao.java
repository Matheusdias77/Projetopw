package com.example.demo;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
     public static Connection getConnection() throws SQLException, URISyntaxException {
         String dbUrl = "jdbc:postgresql://localhost:5432/ProjetoPW?user=postgres&password=1234567";

        return DriverManager.getConnection(dbUrl);
    }
}
