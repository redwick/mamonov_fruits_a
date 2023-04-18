package com.example.day3.models;

import com.example.day3.DB;
import com.example.day3.models.Food;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Fruit extends Food {
    public Boolean isFresh;// свежий ли фрукт

    public Fruit(int id) throws SQLException {
        super();
        ResultSet resultSetId = DB.executeQuery("SELECT id, food_id, is_fresh FROM fruits WHERE id = " + id);
        resultSetId.next();
        this.id = resultSetId.getInt(1);
        this.isFresh = resultSetId.getBoolean(3);
        this.setFood(resultSetId.getInt(2));
    }

    public Fruit(int kkal, String title, Boolean isFresh) throws SQLException {
        super(kkal, title);
        this.isFresh = isFresh;

        PreparedStatement preparedStatement = DB.connection.prepareStatement("INSERT INTO fruits (food_id, is_fresh) values (?, ?)");
        preparedStatement.setInt(1, foodId);
        preparedStatement.setBoolean(2, this.isFresh);
        preparedStatement.execute();

        ResultSet resultSetId = DB.executeQuery("SELECT LAST_INSERT_ID()");
        resultSetId.next();
        id = resultSetId.getInt(1);
    }

    @Override
    public void update() throws SQLException {
        super.update();
        PreparedStatement preparedStatement = DB.connection.prepareStatement("UPDATE fruits SET is_fresh = ? WHERE id = ?");
        preparedStatement.setBoolean(1, isFresh);
        preparedStatement.setInt(2, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public String getDescription() {
        String isFreshString = this.isFresh ? "свежий" : "не свежий";
        return String.format("Фрукт %s", isFreshString);
    }


}
