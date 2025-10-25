package com.example;

import java.io.IOException;

import com.model.Puzzle;
import com.model.PuzzlePalaceFacade;
import com.model.Settings;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;


public class GameController {

    @FXML
    private Label roomTitleLabel;

    @FXML
    private Label puzzlePromptLabel;

    @FXML
    private Label feedbackLabel;

    @FXML
    private Label hintLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Label progressSummaryLabel;

    @FXML
    private TextField answerField;

    @FXML
    private Button submitButton;

    @FXML
    private Button hintButton;

    @FXML
    private Button nextButton;

    private Puzzle activePuzzle;

    private Timeline timerTimeline;

    @FXML
    private void initialize() {
        loadPuzzle();
        updateProgressSummary();
    }

    private void loadPuzzle() {
        PuzzlePalaceFacade facade = App.getFacade();
        activePuzzle = facade.getActivePuzzle();
        if (roomTitleLabel != null) {
            Settings.Difficulty difficulty = facade.getSelectedDifficulty();
            String label = String.format("%s Challenge – %s",
                    difficulty.getDisplayName(),
                    facade.getCurrentRoomName());
            roomTitleLabel.setText(label);    
        }
        if (activePuzzle == null) {
            puzzlePromptLabel.setText("No puzzle available.");
            submitButton.setDisable(true);
            hintButton.setDisable(true);
            nextButton.setVisible(false); 
            nextButton.setManaged(false);
            nextButton.setDisable(true);
            nextButton.setText("Next Room");

        

            stopTimer();
            updateTimerLabelWithSeconds(0);
            updateProgressSummary();
            return;
        }

        puzzlePromptLabel.setText(activePuzzle.getDescription());
        feedbackLabel.setText("");
        hintLabel.setText("");
        answerField.setDisable(false);
        answerField.clear();
        submitButton.setDisable(false);
        hintButton.setDisable(activePuzzle.getMaxHints() == 0);
        nextButton.setVisible(false); // hide by default
        nextButton.setManaged(false);
        nextButton.setDisable(true);

        if ("SOLVED".equalsIgnoreCase(activePuzzle.getStatus())) {
            displaySolvedState();
        } else {
            facade.ensureActivePuzzleTimerStarted();
            startTimer();
        }
        updateProgressSummary();
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
            stopTimer();
            App.setRoot("dashboard");
        } catch (IOException e) {
            feedbackLabel.setText("Unable to return to the dashboard.");
        }
    }

    private void displaySolvedState() {
        answerField.setDisable(true);
        submitButton.setDisable(true);
        hintButton.setDisable(true);
        boolean hasNextRoom = App.getFacade().hasNextRoom();
        if (hasNextRoom && App.getFacade().isNextRoomFinal()) {
            nextButton.setText("Final Room");
        }
        nextButton.setVisible(hasNextRoom);
        nextButton.setManaged(hasNextRoom);
        nextButton.setDisable(!hasNextRoom);

        stopTimer();
        long lastSeconds = App.getFacade().getLastCompletionSeconds();
        updateTimerLabelWithSeconds(lastSeconds);
        updateProgressSummary();
    }

    @FXML
    private void handleNextRoom() {
        PuzzlePalaceFacade facade = App.getFacade();
        stopTimer();
        boolean advanced = facade.moveToNextRoom();
        if (!advanced) {
            feedbackLabel.setText("No more rooms to explore. Return to the dashboard to celebrate!");
            return;
        }
        loadPuzzle();

    }
    private void startTimer() {
        stopTimer();
        updateTimerLabelWithSeconds(App.getFacade().getActivePuzzleElapsedSeconds());
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event ->
                updateTimerLabelWithSeconds(App.getFacade().getActivePuzzleElapsedSeconds())));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void startTimer(boolean resetDisplay) {
        stopTimer();
        App.getFacade().ensureActivePuzzleTimerStarted();
        if (resetDisplay) {
            updateTimerLabelWithSeconds(0);
        } else {
            updateTimerLabelWithSeconds(App.getFacade().getActivePuzzleElapsedSeconds());
        }
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event ->
                updateTimerLabelWithSeconds(App.getFacade().getActivePuzzleElapsedSeconds())));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void stopTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
            timerTimeline = null;
        }
    }

    private void updateTimerLabelWithSeconds(long totalSeconds) {
        if (timerLabel == null) {
            return;
        }
        long clamped = Math.max(0, totalSeconds);
        long minutes = clamped / 60;
        long seconds = clamped % 60;
        timerLabel.setText(String.format("Timer: %02d:%02d", minutes, seconds));
    }

    private void updateProgressSummary() {
        if (progressSummaryLabel == null) {
            return;
        }
        long lastSeconds = App.getFacade().getLastCompletionSeconds();
        if (lastSeconds > 0) {
            long minutes = lastSeconds / 60;
            long seconds = lastSeconds % 60;
            progressSummaryLabel.setText(String.format("Previous escape time: %02d:%02d", minutes, seconds));
        } else {
            progressSummaryLabel.setText("No escape time recorded yet.");
        }
    }
}