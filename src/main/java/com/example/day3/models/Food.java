package com.example.day3.models;

import com.example.day3.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Food {
    public int kkal;// количество калорий
    public String title;// название
    public DB db;

    public int id;

    public int getFoodId() {
        return foodId;
    }

    public void setFood(int foodId) throws SQLException {
        ResultSet resultSetId = DB.executeQuery("SELECT id, title, kkal FROM food WHERE id = " + foodId);
        resultSetId.next();

        this.foodId = resultSetId.getInt(1);
        this.title = resultSetId.getString(2);
        this.kkal = resultSetId.getInt(3);
    }

    public int foodId;

    public Food() {
    }

    public Food(int foodId) throws SQLException {
        this.setFood(foodId);
    }

    public Food(int kkal, String title) throws SQLException {
        this.kkal = kkal;
        this.title = title;

        PreparedStatement preparedStatement = DB.connection.prepareStatement("INSERT INTO food (title, kkal) values (?, ?)");
        preparedStatement.setString(1, title);
        preparedStatement.setInt(2, kkal);
        preparedStatement.execute();

        ResultSet resultSetId = DB.executeQuery("SELECT LAST_INSERT_ID()");
        resultSetId.next();
        foodId = resultSetId.getInt(1);
    }

    public void update() throws SQLException {
        PreparedStatement preparedStatement = DB.connection.prepareStatement("UPDATE food SET title = ?, kkal = ? WHERE id = ?");
        preparedStatement.setString(1, title);
        preparedStatement.setInt(2, kkal);
        preparedStatement.setInt(3, foodId);
        preparedStatement.executeUpdate();
    }

    public void destroy() throws SQLException {
        PreparedStatement preparedStatement = DB.connection.prepareStatement("DELETE FROM food WHERE id = ?");
        preparedStatement.setInt(1, foodId);
        preparedStatement.executeUpdate();
    }

    @Override
    public String toString() {
        return String.format("%s: %s ккал", this.getTitle(), this.getKkal());
    }

    public int getKkal() {
        return kkal;
    }

    public void setKkal(int kkal) {
        this.kkal = kkal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return "";
    }
}
