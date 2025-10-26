package com.example;

import java.io.IOException;

import com.speech.Speak;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class StoryController {

    @FXML
    private Label errorLabel;

    @FXML
    private TextArea storyTextArea;

    @FXML
    private void initialize() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }

        Platform.runLater(() -> {
            if (storyTextArea != null) {
                String storyText = storyTextArea.getText();
                if (storyText != null && !storyText.isBlank()) {
                    Thread speechThread = new Thread(() -> Speak.speak(storyText));
                    speechThread.setDaemon(true);
                    speechThread.start();
                }
            }
        });
    } // Closing brace for initialize method

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