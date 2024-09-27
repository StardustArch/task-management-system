package com.taskmanagementee.model.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gerenciamento_tarefas?useUnicode=true&characterEncoding=utf8"; // Substitua pelo seu URL
    private static final String USER = "root"; // Substitua pelo seu usuário
    private static final String PASSWORD = ""; // Substitua pela sua senha

    static {
        try {
            // Carregar o driver do MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        DatabaseConnection databaseConnection = new DatabaseConnection();

    }
}
