module com.example.day3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.day3 to javafx.fxml;
    exports com.example.day3;
    exports com.example.day3.models;
    opens com.example.day3.models to javafx.fxml;
    exports com.example.day3.gui;
    opens com.example.day3.gui to javafx.fxml;
}