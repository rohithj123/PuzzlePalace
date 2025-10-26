package com.example;

import java.io.IOException;

import com.model.Player;
import com.model.PuzzlePalaceFacade;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label feedbackLabel;

    @FXML
    private void initialize() {
        feedbackLabel.setText("");
        passwordField.setOnAction(event -> handleLogin());
        usernameField.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        PuzzlePalaceFacade facade = App.getFacade();
        Player player = facade.login(username, password);
        if (player == null) {
            feedbackLabel.setText("Login failed. Please check your credentials.");
            return;
        }

        feedbackLabel.setText("");
        try {
            App.setRoot("story");
        } catch (IOException e) {
            feedbackLabel.setText("Unable to open the story view.");
        }
    }

    @FXML
    private void handleShowSignup() {
        try {
            App.setRoot("signup");
        } catch (IOException e) {
            feedbackLabel.setText("Unable to open the sign up view.");
        }
    }
}
