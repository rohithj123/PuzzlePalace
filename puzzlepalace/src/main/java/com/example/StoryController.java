package com.example;

import java.io.IOException;

import com.speech.Speak;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * Controller for the story screen.
 * Handles reading the story out loud and moving to the next screen.
 */
public class StoryController {

    @FXML
    private Label errorLabel;

    @FXML
    private TextArea storyTextArea;

    private Thread speechThread;

    /** Runs when the screen loads. Starts reading the story. */
    @FXML
    private void initialize() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }

        Platform.runLater(() -> {
            if (storyTextArea != null) {
                String storyText = storyTextArea.getText();
                if (storyText != null && !storyText.isBlank()) {
                    speechThread = new Thread(() -> Speak.speak(storyText));
                    speechThread.setDaemon(true);
                    speechThread.start();
                }
            }
        });
    }
    /** Stops the story and goes to the dashboard screen. */
    @FXML
    private void handleNext() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
        Speak.stop();
        if (speechThread != null && speechThread.isAlive()) {
            speechThread.interrupt();
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