package com.example.day3.gui;

import com.example.day3.DB;
import com.example.day3.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AuthController {
    @FXML
    Button enterBtn;
    @FXML
    Button regenerateBtn;
    @FXML
    ImageView imageView;
    @FXML
    TextField numField;
    @FXML
    TextField codeField;
    @FXML
    PasswordField passwordField;
    @FXML
    Label label;
    public String lab;
    int code = 0;

    public void initialize() {
        regenerateBtn.setGraphic(new ImageView(new Image("regenerate.jpg", 20, 20, false, true)));
        imageView.setImage(new Image("logo.png", 200, 200, false, true));
    }

    public void numKey(KeyEvent event) throws SQLException {
        DB db = new DB();
        if(event.getCode() == KeyCode.ENTER){
            if(db.checkNumber(numField.getText())) {
                numField.setDisable(true);
                passwordField.setDisable(false);
//                codeField.setDisable(false);
//                regenerateBtn.setDisable(false);
//                enterBtn.setDisable(false);
            }
        }
    }

    public void passKey(KeyEvent event) throws SQLException {
        DB db = new DB();
        if(event.getCode() == KeyCode.ENTER){
            String number = numField.getText();
            String password = passwordField.getText();
            if(code == 0){
                if(passwordField.getText().isEmpty()){
                    label.setText("Введите пароль");
                }
                if(numField.getText().isEmpty()){
                    label.setText("Введите номер");
                }
                boolean flag = db.validateCredentials(number, password);

                if(!flag){
                    label.setText("Неправильный номер или пароль");
                }else {
                    code = (int) ((Math.random() * (9999 - 1000)) + 1000);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Код:"+code);
                    alert.show();
                    codeField.setDisable(false);
                    enterBtn.setDisable(false);
                    regenerateBtn.setDisable(false);
                }
            } else {
                String codeToText = String.valueOf(code);
                if(codeToText.equals(codeField.getText())){
                    lab = db.getRole(number, password);
                    label.setText(lab);
                } else {
                    label.setText("Неверный код");
                }
            }
        }
    }

    public void login(ActionEvent event) throws SQLException, InterruptedException, IOException {
        String number = numField.getText();
        String password = passwordField.getText();
        DB db = new DB();
        if(code == 0){
            if(passwordField.getText().isEmpty()){
                label.setText("Введите пароль");
            }
            if(numField.getText().isEmpty()){
                label.setText("Введите номер");
            }
            boolean flag = db.validateCredentials(number, password);
            if(!flag){
                label.setText("Неправильный номер или пароль");
            }else {
                code = (int) ((Math.random() * (9999 - 1000)) + 1000);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Код:"+code);
                alert.show();
            }
        } else {
            String codeToText = String.valueOf(code);
            if(codeToText.equals(codeField.getText())){
                MainFormController.role = db.getRole(number, password);
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainForm.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 450, 300);
                stage.getIcons().add(new Image("icon.png"));
                stage.setTitle("MVP");
                stage.setScene(scene);
                stage.show();
            } else {
                label.setText("Неверный код");
            }
        }
    }
    public void onCancel(){
        numField.clear();
        passwordField.clear();
        codeField.clear();
    }

    public void onRegenerate(){
        code = (int) ((Math.random() * (9999 - 1000)) + 1000);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Код:"+code);
        alert.show();
    }
}
