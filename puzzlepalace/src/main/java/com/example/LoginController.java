package com.example;

import java.io.IOException;

import com.model.Player;
import com.model.PuzzlePalaceFacade;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


/**
 * Simple controller for the login screen.
 * Handles logging in and going to sign-up/story screens.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label feedbackLabel;

        /** Runs when the view loads. Clears feedback and wires Enter key. */
    @FXML
    private void initialize() {
        feedbackLabel.setText("");
        passwordField.setOnAction(event -> handleLogin());
        usernameField.setOnAction(event -> handleLogin());
    }

        /** Try to log in with the entered username/password. */
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

        /** Open the signup screen. */
    @FXML
    private void handleShowSignup() {
        try {
            App.setRoot("signup");
        } catch (IOException e) {
            feedbackLabel.setText("Unable to open the sign up view.");
        }
    }
}
