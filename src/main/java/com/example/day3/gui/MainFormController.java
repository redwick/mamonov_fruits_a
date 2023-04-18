package com.example.day3.gui;

import com.example.day3.DB;
import com.example.day3.Main;
import com.example.day3.gui.FoodFormController;
import com.example.day3.models.Chocolate;
import com.example.day3.models.Cookie;
import com.example.day3.models.Food;
import com.example.day3.models.Fruit;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
//import com.fasterxml.jackson.databind.ObjectMapper;

public class MainFormController implements Initializable{
    public TableView mainTable;
    @FXML
    Menu fileMenu;
    @FXML
    Menu dataMenu;

    public static String role;

    ObservableList<Food> foodList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(role.equals("Консультант")){
            fileMenu.setDisable(true);
            dataMenu.setDisable(true);
        }
        // заполнили список данными
        try {
            ResultSet resultSetFruits = DB.executeQuery("SELECT * FROM fruits");
            while (resultSetFruits.next()) {
                int id = resultSetFruits.getInt("id");
                foodList.add(new Fruit(id));
            }

            ResultSet resultSetChocolates = DB.executeQuery("SELECT * FROM chocolates");
            while (resultSetChocolates.next()) {
                int id = resultSetChocolates.getInt("id");
                foodList.add(new Chocolate(id));
            }

            ResultSet resultSetCookies = DB.executeQuery("SELECT * FROM cookies");
            while (resultSetCookies.next()) {
                int id = resultSetCookies.getInt("id");
                foodList.add(new Cookie(id));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // подключили к таблице
        mainTable.setItems(foodList);
        TableColumn<Food, String> titleColumn = new TableColumn<>("Название");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Food, String> kkalColumn = new TableColumn<>("Калорийность");
        kkalColumn.setCellValueFactory(new PropertyValueFactory<>("kkal"));
        // добавляем столбец с описанием
        TableColumn<Food, String> descriptionColumn = new TableColumn<>("Описание");
        // если хотим что-то более хитрое выводить, то используем лямбда выражение
        descriptionColumn.setCellValueFactory(cellData -> {
            // плюс надо обернуть возвращаемое значение в обертку свойство
            return new SimpleStringProperty(cellData.getValue().getDescription());
        });

        // добавляем сюда descriptionColumn
        mainTable.getColumns().addAll(titleColumn, kkalColumn, descriptionColumn);


    }

    public void onAddClick(ActionEvent actionEvent) throws IOException {
        // эти три строчки создюат форму из fxml файлика
        // в принципе можно было бы обойтись
        // Parent root = FXMLLoader.load(getClass().getResource("FoodForm.fxml"));
        // но дальше вот это разбиение на три строки упростит нам жизнь
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("FoodForm.fxml"));
        Parent root = loader.load();
        // ну а тут создаем новое окно
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        // указываем что оно модальное
        stage.initModality(Modality.WINDOW_MODAL);
        // указываем что оно должно блокировать главное окно
        // ну если точнее, то окно, на котором мы нажали на кнопку
        stage.initOwner(this.mainTable.getScene().getWindow());
        // открываем окно и ждем пока его закроют
        stage.showAndWait();
        // вытаскиваем контроллер который привязан к форме
        FoodFormController controller = loader.getController();
        // проверяем что наали кнопку save
        if (controller.getModalResult()) {
            // собираем еду с формы
            Food newFood = null;
            try {
                newFood = controller.getFood();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // добавляем в список
            this.foodList.add(newFood);
        }
    }

    public void onEditClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("FoodForm.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.mainTable.getScene().getWindow());

        FoodFormController controller = loader.getController();
        controller.setFood((Food) this.mainTable.getSelectionModel().getSelectedItem());

        stage.showAndWait();

        // если нажали кнопку сохранить
        if (controller.getModalResult()) {
            // узнаем индекс выбранной в таблице строки
            int index = this.mainTable.getSelectionModel().getSelectedIndex();
            // подменяем строку в таблице данными на форме
            try {
                this.mainTable.getItems().set(index, controller.getFood());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onDeleteClick(ActionEvent actionEvent) throws SQLException {
        // берем выбранную на форме еду
        Food food = (Food) this.mainTable.getSelectionModel().getSelectedItem();

        // выдаем подтверждающее сообщение
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(String.format("Точно удалить %s?", food.getTitle()));

        // если пользователь нажал OK
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            // удаляем строку из таблицы
            this.mainTable.getItems().remove(food);
            food.destroy();
        }
    }

    public void onSaveToFileClick(ActionEvent actionEvent) {
        // берем выбранную на форме еду
        Food food = (Food) this.mainTable.getSelectionModel().getSelectedItem();

        // выдаем подтверждающее сообщение
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(String.format("Точно удалить %s?", food.getTitle()));

        // если пользователь нажал OK
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            // удаляем строку из таблицы
            this.mainTable.getItems().remove(food);
        }
    }
}