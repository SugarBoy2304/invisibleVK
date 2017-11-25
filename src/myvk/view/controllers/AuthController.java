package myvk.view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import myvk.Main;
import myvk.screens.LoginScreen;

import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {

    private Main pullUp;

    @FXML
    private Button loginButton;

    @FXML
    private Button regButton;

    public void setPullUp(Main pullUp) {
        this.pullUp = pullUp;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void handleButtonLogin() throws InterruptedException {
        new LoginScreen().init(Main.getApp().manager());
    }

    @FXML
    private void handleButtonReg() {
        //pullUp.showRegWindow();
    }

    @FXML
    private void handleOnLoginButtonAction() {
        loginButton.setStyle(" -fx-background-color: rgb(61,184,117); -fx-effect: dropshadow(gaussian, rgb(53,152,95),0,7,0,5); ");
    }

    @FXML
    private void handleOffLoginButtonAction() {
        loginButton.setStyle(" -fx-background-color: rgb(64,199,129); -fx-effect: dropshadow(gaussian, rgb(53,167,110),0,7,0,5); ");
    }

    @FXML
    private void handleOnRegButtonAction() {
        regButton.setStyle(" -fx-background-color: rgb(61,184,117); -fx-effect: dropshadow(gaussian, rgb(53,152,95),0,7,0,5); ");
    }

    @FXML
    private void handleOffRegButtonAction() {
        regButton.setStyle(" -fx-background-color: rgb(64,199,129); -fx-effect: dropshadow(gaussian, rgb(53,167,110),0,7,0,5); ");
    }

}
