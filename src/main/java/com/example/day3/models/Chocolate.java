package com.example.day3.models;

import com.example.day3.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Chocolate extends Food {
    public enum Type {white, black, milk;} // какие типы шоколада бывают
    public Type type;// а это собственно тип шоколада


    public Chocolate(int id) throws SQLException {
        super();
        ResultSet resultSetId = DB.executeQuery("SELECT id, food_id, type FROM chocolates WHERE id = " + id);
        resultSetId.next();
        this.id = resultSetId.getInt(1);
        this.setFood(resultSetId.getInt(2));
        this.type = Type.valueOf(resultSetId.getString(3));
    }

    public Chocolate(int kkal, String title, Type type) throws SQLException {
        super(kkal, title);
        this.type = type;

        PreparedStatement preparedStatement = DB.connection.prepareStatement("INSERT INTO chocolates (food_id, type) values (?, ?)");
        preparedStatement.setInt(1, foodId);
        preparedStatement.setString(2, String.valueOf(this.type));
        preparedStatement.execute();

        ResultSet resultSetId = DB.executeQuery("SELECT LAST_INSERT_ID()");
        resultSetId.next();
        id = resultSetId.getInt(1);
    }

    @Override
    public void update() throws SQLException {
        super.update();
        PreparedStatement preparedStatement = DB.connection.prepareStatement("UPDATE chocolates SET type = ? WHERE id = ?");
        preparedStatement.setString(1, String.valueOf(type));
        preparedStatement.setInt(2, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public String getDescription() {
        String typeString = "";
        switch (this.type)
        {
            case white:
                typeString = "белый";
                break;
            case black:
                typeString = "черный";
                break;
            case milk:
                typeString = "молочный";
                break;
        }

        return String.format("Шоколад %s", typeString);
    }
}
