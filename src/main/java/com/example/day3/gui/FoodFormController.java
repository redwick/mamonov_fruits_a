package com.example.day3.gui;

import com.example.day3.models.Chocolate;
import com.example.day3.models.Cookie;
import com.example.day3.models.Food;
import com.example.day3.models.Fruit;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FoodFormController implements Initializable {
    public ChoiceBox cmbFoodType;
    public TextField txtFoodTitle;
    public TextField txtFoodKkal;

    public VBox fruitPane;
    public CheckBox chkIsFresh;

    public HBox chocolatePane;
    public ChoiceBox cmbChocolateType;

    public VBox cookiePane;
    public CheckBox chkWithSugar;
    public CheckBox chkWithPoppy;
    public CheckBox chkWithSesame;

    final String FOOD_FRUIT = "Фрукт";
    final String FOOD_CHOCOLATE = "Шоколадка";
    final String FOOD_COOKIE = "Булочка";

    // добавляем новое поле
    private Boolean modalResult = false;

    private Integer foodId;

    // ...

    // обработчик нажатия на кнопку Сохранить
    public void onSaveClick(ActionEvent actionEvent) {
        this.modalResult = true; // ставим результат модального окна на true
        // закрываем окно к которому привязана кнопка
        ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
    }

    public void onCancelClick(ActionEvent actionEvent) {
        this.modalResult = false; // ставим результат модального окна на false
        // закрываем окно к которому привязана кнопка
        ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
    }

    // геттер для результата модального окна
    public Boolean getModalResult() {
        return modalResult;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbFoodType.setItems(FXCollections.observableArrayList(
                FOOD_FRUIT,
                FOOD_CHOCOLATE,
                FOOD_COOKIE
        ));
        cmbFoodType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // вызываю новую функцию
            updatePanes((String) newValue);
        });
        // добавляем все три типа шоколада в комобобокс
        cmbChocolateType.getItems().setAll(
                Chocolate.Type.white,
                Chocolate.Type.black,
                Chocolate.Type.milk
        );
        // и используем метод setConverter,
        // чтобы типы объекты рендерились как строки
        cmbChocolateType.setConverter(new StringConverter<Chocolate.Type>() {
            @Override
            public String toString(Chocolate.Type object) {
                // просто указываем как рендерить
                switch (object) {
                    case white:
                        return "Белый";
                    case black:
                        return "Черный";
                    case milk:
                        return "Молочный";
                }
                return null;
            }

            @Override
            public Chocolate.Type fromString(String string) {
                // этот метод не трогаем так как наш комбобкос имеет фиксированный набор элементов
                return null;
            }
        });

        // вызываю новую функцию при инициализации формы
        updatePanes("");
    }

    // добавил новую функцию
    public void updatePanes(String value) {
        this.fruitPane.setVisible(value.equals(FOOD_FRUIT));
        this.fruitPane.setManaged(value.equals(FOOD_FRUIT));
        this.chocolatePane.setVisible(value.equals(FOOD_CHOCOLATE));
        this.chocolatePane.setManaged(value.equals(FOOD_CHOCOLATE));
        this.cookiePane.setVisible(value.equals(FOOD_COOKIE));
        this.cookiePane.setManaged(value.equals(FOOD_COOKIE));
    }
    public Food getFood() throws SQLException {
        Food result = null;
        int kkal = Integer.parseInt(this.txtFoodKkal.getText());
        String title = this.txtFoodTitle.getText();

        switch ((String)this.cmbFoodType.getValue()) {
            case FOOD_CHOCOLATE:
                if (foodId != null) {
                    result = new Chocolate(foodId);
                    result.kkal = kkal;
                    result.title = title;
                    ((Chocolate) result).type = (Chocolate.Type)this.cmbChocolateType.getValue();
                } else {
                    result = new Chocolate(kkal, title, (Chocolate.Type)this.cmbChocolateType.getValue());
                }
                break;
            case FOOD_COOKIE:
                if (foodId != null) {
                    result = new Cookie(foodId);
                    result.kkal = kkal;
                    result.title = title;
                    ((Cookie) result).withSugar = this.chkWithSugar.isSelected();
                    ((Cookie) result).withPoppy = this.chkWithPoppy.isSelected();
                    ((Cookie) result).withSesame = this.chkWithSesame.isSelected();
                } else {
                    result = new Cookie(
                            kkal,
                            title,
                            this.chkWithSugar.isSelected(),
                            this.chkWithPoppy.isSelected(),
                            this.chkWithSesame.isSelected()
                    );
                }
                break;
            case FOOD_FRUIT:
                if (foodId != null) {
                    result = new Fruit(foodId);
                    result.kkal = kkal;
                    result.title = title;
                    ((Fruit) result).isFresh = this.chkIsFresh.isSelected();
                } else {
                    result = new Fruit(kkal, title, this.chkIsFresh.isSelected());
                }
                break;
        }
        if (foodId != null) {
            result.update();
        }
        return result;
    }
    public void setFood(Food food) {
        // делаем так, что если объект редактируется, то нельзя переключать тип
        this.cmbFoodType.setDisable(food != null);
        if (food != null) {
            foodId = food.id;
            // ну а тут стандартное заполнение полей в соответствии с переданной едой
            this.txtFoodKkal.setText(String.valueOf(food.getKkal()));
            this.txtFoodTitle.setText(food.getTitle());

            if (food instanceof Fruit) { // если фрукт
                this.cmbFoodType.setValue(FOOD_FRUIT);
                this.chkIsFresh.setSelected(((Fruit) food).isFresh);
            } else if (food instanceof Cookie) { // если булочка
                this.cmbFoodType.setValue(FOOD_COOKIE);
                this.chkWithSugar.setSelected(((Cookie) food).withSugar);
                this.chkWithPoppy.setSelected(((Cookie) food).withPoppy);
                this.chkWithSesame.setSelected(((Cookie) food).withSesame);
            } else if (food instanceof Chocolate) { // если шоколад
                this.cmbFoodType.setValue(FOOD_CHOCOLATE);
                this.cmbChocolateType.setValue(((Chocolate) food).type);
            }
            try {
                food.update();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
