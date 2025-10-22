package com.example;

import java.io.IOException;

import com.model.Puzzle;
import com.model.PuzzlePalaceFacade;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class GameController {

    @FXML
    private Label puzzlePromptLabel;

    @FXML
    private Label feedbackLabel;

    @FXML
    private Label hintLabel;

    @FXML
    private TextField answerField;

    @FXML
    private Button submitButton;

    @FXML
    private Button hintButton;

    private Puzzle activePuzzle;

    @FXML
    private void initialize() {
        loadPuzzle();
    }

    private void loadPuzzle() {
        PuzzlePalaceFacade facade = App.getFacade();
        activePuzzle = facade.getActivePuzzle();
        if (activePuzzle == null) {
            puzzlePromptLabel.setText("No puzzle available.");
            submitButton.setDisable(true);
            hintButton.setDisable(true);
            return;
        }

        puzzlePromptLabel.setText(activePuzzle.getDescription());
        feedbackLabel.setText("");
        hintLabel.setText("");
        answerField.setDisable(false);
        answerField.clear();
        submitButton.setDisable(false);
        hintButton.setDisable(activePuzzle.getMaxHints() == 0);

        if ("SOLVED".equalsIgnoreCase(activePuzzle.getStatus())) {
            displaySolvedState();
        }
    }

    @FXML
    private void handleSubmitAnswer() {
        if (activePuzzle == null) {
            return;
        }
        String answer = answerField.getText();
        boolean solved = App.getFacade().submitPuzzleAnswer(activePuzzle.getPuzzleId(), answer);
        if (solved) {
            feedbackLabel.setText("Correct! The door unlocks with a satisfying click.");
            displaySolvedState();
        } else {
            feedbackLabel.setText("That's not quite right. Try another combination.");
        }
    }

    @FXML
    private void handleRequestHint() {
        if (activePuzzle == null) {
            return;
        }
        String hint = App.getFacade().requestHint(activePuzzle.getPuzzleId());
        hintLabel.setText(hint);
        if (activePuzzle.getHintsUsed() >= activePuzzle.getMaxHints()) {
            hintButton.setDisable(true);
        }
    }

    @FXML
    private void handleReturnToDashboard() {
        try {
            App.setRoot("dashboard");
        } catch (IOException e) {
            feedbackLabel.setText("Unable to return to the dashboard.");
        }
    }

    private void displaySolvedState() {
        answerField.setDisable(true);
        submitButton.setDisable(true);
        hintButton.setDisable(true);
    }
}