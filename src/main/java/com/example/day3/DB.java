package com.example.day3;

import java.sql.*;
import java.util.Map;

public class DB {
    public static Connection connection;

    public DB() throws SQLException {
        Map<String, String> env = System.getenv();
        String host = env.getOrDefault("DB_HOST", "localhost");;
        String port = env.getOrDefault("DB_PORT", "3306");
        String database = env.getOrDefault("DB_NAME", "mysql");
        String user = env.getOrDefault("DB_USER", "root");
        String password = env.getOrDefault("DB_PASS", "root");

        String connectionUrl = String.format(
                "jdbc:mysql://%s:%s/%s?serverTimezone=UTC",
                host,
                port,
                database
        );

        System.out.println(connectionUrl);

        if (connection == null) {
            connection = DriverManager.getConnection(
                    connectionUrl,
                    user,
                    password
            );
            System.out.println("Connected to database");
        }
    }


    public boolean checkNumber(String number) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM employees WHERE number = ?");
        preparedStatement.setString(1, number);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    public boolean validateCredentials(String login, String password) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM employees where number = ? and password = ?");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    public String getRole(String login, String password) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT roles.name FROM employees, roles where " +
                "employees.id = ? and employees.password = ? and roles.id = employees.role_id;");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        if((resultSet.next()) && (!resultSet.wasNull())){
            return resultSet.getString(1);
        } else{
            return "fail";
        }
    }

    public static ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    int executeUpdate(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(query);
    }

    boolean execute(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.execute(query);
    }
}