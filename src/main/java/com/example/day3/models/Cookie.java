package com.example.day3.models;

import com.example.day3.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Cookie extends Food {
    public Boolean withSugar;// с сахаром ли?
    public Boolean withPoppy;// или маком?
    public Boolean withSesame;// а может с кунжутом?

    public Cookie(int kkal, String title, Boolean withSugar, Boolean withPoppy, Boolean withSesame) throws SQLException {
        super(kkal, title);
        this.withSugar = withSugar;
        this.withPoppy = withPoppy;
        this.withSesame = withSesame;

        PreparedStatement preparedStatement = DB.connection.prepareStatement("INSERT INTO cookies (food_id, with_sugar, with_poppy, with_sesame) values (?, ?, ?, ?)");
        preparedStatement.setInt(1, foodId);
        preparedStatement.setBoolean(2, this.withSugar);
        preparedStatement.setBoolean(3, this.withSugar);
        preparedStatement.setBoolean(4, this.withSugar);
        preparedStatement.execute();

        ResultSet resultSetId = DB.executeQuery("SELECT LAST_INSERT_ID()");
        resultSetId.next();
        id = resultSetId.getInt(1);
    }

    public Cookie(int id) throws SQLException {
        super();
        ResultSet resultSetId = DB.executeQuery("SELECT id, food_id, with_sugar, with_poppy, with_sesame FROM cookies WHERE id = " + id);
        resultSetId.next();
        this.id = resultSetId.getInt(1);
        this.setFood(resultSetId.getInt(2));
        this.withSugar = resultSetId.getBoolean(3);
        this.withPoppy = resultSetId.getBoolean(4);
        this.withSesame = resultSetId.getBoolean(5);
    }

    @Override
    public void update() throws SQLException {
        super.update();
        PreparedStatement preparedStatement = DB.connection.prepareStatement("UPDATE cookies SET with_sugar = ?, with_poppy = ?, with_sesame = ? WHERE id = ?");
        preparedStatement.setBoolean(1, withSugar);
        preparedStatement.setBoolean(2, withPoppy);
        preparedStatement.setBoolean(3, withSesame);
        preparedStatement.setInt(4, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public String getDescription() {
        ArrayList<String> items = new ArrayList<>();
        if (this.withSugar)
            items.add("с сахаром");
        if (this.withPoppy)
            items.add("с маком");
        if (this.withSesame)
            items.add("с кунжутом");

        return String.format("Булочка %s", String.join(", ", items));
    }
}
