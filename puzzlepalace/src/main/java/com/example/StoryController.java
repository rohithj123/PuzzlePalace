package com.example;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StoryController {

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }

    @FXML
    private void handleNext() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
        try {
            App.setRoot("dashboard");
        } catch (IOException e) {
            if (errorLabel != null) {
                errorLabel.setText("Unable to open the dashboard view.");
            }
        }
    }
}