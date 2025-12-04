package com.example;

import java.io.IOException;

import com.model.Player;
import com.model.PuzzlePalaceFacade;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Handles the sign-up screen.
 * Lets users create an account or go back to login.
 */
public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label feedbackLabel;

        /** Runs when the screen loads. Sets up default text. */
    @FXML
    private void initialize() {
        feedbackLabel.setText("");
        feedbackLabel.setStyle("-fx-text-fill: crimson;");
    }

    /** Creates a new account if inputs are valid. */
    @FXML
    private void handleCreateAccount() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username == null || username.isBlank()) {
            feedbackLabel.setStyle("-fx-text-fill: crimson;");
            feedbackLabel.setText("Please enter a username.");
            return;
        }

        if (password == null || password.isBlank()) {
            feedbackLabel.setStyle("-fx-text-fill: crimson;");
            feedbackLabel.setText("Please choose a password.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            feedbackLabel.setStyle("-fx-text-fill: crimson;");
            feedbackLabel.setText("Passwords do not match.");
            return;
        }

        PuzzlePalaceFacade facade = App.getFacade();
        Player created = facade.createAccount(username, password);
        if (created == null) {
            feedbackLabel.setStyle("-fx-text-fill: crimson;");
            feedbackLabel.setText("Unable to create account. Try a different username.");
            return;
        }

        feedbackLabel.setStyle("-fx-text-fill: seagreen;");
        feedbackLabel.setText("Account created! You can now return to the login screen.");
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    /** Goes back to the login screen. */
    @FXML
    private void handleBackToLogin() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            feedbackLabel.setStyle("-fx-text-fill: crimson;");
            feedbackLabel.setText("Unable to open the login view.");
        }
    }
}