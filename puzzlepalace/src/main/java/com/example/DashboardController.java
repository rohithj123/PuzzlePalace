package com.example;

import java.io.IOException;

import com.model.Player;
import com.model.PlayerProgressReport;
import com.model.PuzzlePalaceFacade;
import com.model.PuzzleProgressSnapshot;
import com.model.Score;
import com.model.Settings;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;


/**
 * Controls the dashboard screen.
 * Handles showing player info, saving progress, and switching views.
 */
public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label puzzlesSolvedLabel;

    @FXML
    private Label hintsUsedLabel;

    @FXML
    private Label timeTakenLabel;

    @FXML
    private Label completionLabel;

    @FXML
    private ListView<String> answeredQuestionsList;

    @FXML
    private ListView<String> hintUsageList;

    @FXML
    private TextArea saveFilePreview;

    @FXML
    private Label saveFileStatusLabel;



    @FXML
    private Label statusLabel;

    @FXML
    private Label puzzleStatusLabel;
    @FXML
    private ChoiceBox<Settings.Difficulty> difficultyChoiceBox;

    @FXML
    private Label difficultyDescriptionLabel;

    /** Runs when the dashboard loads. */
    @FXML
    private void initialize() {
        configureDifficultySelector();
        resetSaveFilePreview();
        refreshProgressDetails(PlayerProgressReport.empty());
        refreshPlayerDetails();
    }

        /** Logs out and goes back to the login screen. */

    @FXML
    private void handleLogout() {
        PuzzlePalaceFacade facade = App.getFacade();
        Settings.Difficulty selected = difficultyChoiceBox != null
            ? difficultyChoiceBox.getSelectionModel().getSelectedItem()
            : Settings.Difficulty.EASY;
        facade.setSelectedDifficulty(selected);
        facade.logout();
        resetSaveFilePreview();
        refreshProgressDetails(PlayerProgressReport.empty());
        try {
            App.getFacade().startEscapeRoom();
            App.setRoot("login");
        } catch (IOException e) {
            if (statusLabel != null) {
                statusLabel.setText("Unable to return to login view.");
            }
        }
    }

        /** Saves player progress and updates the display. */

    @FXML
    private void handleSaveProgress() {
        PuzzlePalaceFacade facade = App.getFacade();
        facade.saveCurrentPlayerProgress();
        refreshProgressDetails(facade.getCurrentPlayerProgressReport());
        if (statusLabel != null) {
            statusLabel.setText("Progress saved successfully.");
        }
    }

        /** Shows saved player data in the text area. */

    @FXML
    private void handleStartPuzzle() {
        try {
            PuzzlePalaceFacade facade = App.getFacade();
            Settings.Difficulty selected = difficultyChoiceBox != null
                ? difficultyChoiceBox.getSelectionModel().getSelectedItem()
                : Settings.Difficulty.EASY;
            facade.setSelectedDifficulty(selected);
            facade.resetProgressToFirstRoom();
            App.setRoot("game");
        } catch (IOException e) {
            if (statusLabel != null) {
                statusLabel.setText("Unable to open the puzzle view.");
            }
        }
    }

    @FXML
    private void handleShowSavedData() {
        PuzzlePalaceFacade facade = App.getFacade();
        String content = facade.readUserDataFileContents();
        boolean error = isErrorMessage(content);
        if (saveFilePreview != null) {
            saveFilePreview.setText(content == null ? "" : content);
            boolean show = content != null && !error;
            saveFilePreview.setVisible(show);
            saveFilePreview.setManaged(show);
        }
        if (saveFileStatusLabel != null) {
            if (content == null) {
                saveFileStatusLabel.setText("No saved data available.");
            } else if (error) {
                saveFileStatusLabel.setText(content);
            } else {
                saveFileStatusLabel.setText("Displaying " + facade.getUserDataPath());
            }
        }
    }

        /** Updates labels with current player info. */

    private void refreshPlayerDetails() {
        PuzzlePalaceFacade facade = App.getFacade();
        Player current = facade.getCurrentPlayer();
        if (current == null) {
            welcomeLabel.setText("No player logged in.");
            scoreLabel.setText("Score: --");
            puzzlesSolvedLabel.setText("Puzzles solved: --");
            hintsUsedLabel.setText("Hints used: --");
            if (timeTakenLabel != null) {
                timeTakenLabel.setText("Last escape time: --");
            }

            if (statusLabel != null) {
                statusLabel.setText("Please log in again.");
            }
            if (puzzleStatusLabel != null) {
                puzzleStatusLabel.setText("Log in to access puzzles.");
            }
            refreshProgressDetails(PlayerProgressReport.empty());
            resetSaveFilePreview();
            return;
        }

        welcomeLabel.setText("Welcome back, " + current.getUsername() + "!");
        Score score = current.getScoreDetails();
        int totalScore = score != null ? score.calculateScore() : 0;
        scoreLabel.setText("Score: " + totalScore);
        puzzlesSolvedLabel.setText("Puzzles solved: " + (score != null ? score.getPuzzlesSolved() : 0));
        hintsUsedLabel.setText("Hints used: " + (score != null ? score.getHintsUsed() : 0));
        if (timeTakenLabel != null) {
            timeTakenLabel.setText("Last escape time: " + formatTime(score != null ? score.getTimeTaken() : 0));
        }
        if (statusLabel != null) {
            statusLabel.setText("Your adventure awaits!");
        }
        if (puzzleStatusLabel != null) {
            puzzleStatusLabel.setText(facade.describeCurrentPuzzleStatus());
        }
        if (difficultyChoiceBox != null) {
            Settings.Difficulty difficulty = facade.getSelectedDifficulty();
            difficultyChoiceBox.getSelectionModel().select(difficulty);
            updateDifficultyDescription(difficulty);
        }
        refreshProgressDetails(facade.getCurrentPlayerProgressReport());
    }

    private void refreshProgressDetails(PlayerProgressReport report) {
        if (completionLabel != null) {
            if (report == null) {
                completionLabel.setText("Game completion: --");
            } else {
                int total = Math.max(report.getTotalPuzzles(), report.getSolvedCount());
                completionLabel.setText(String.format("game completion: %d%% (%d/%d puzzles solved)",
                    report.getCompletionPercent(),
                    report.getSolvedCount(),
                    total));
            }
        }

        if (answeredQuestionsList != null) {
            List<String> solvedEntries = new ArrayList<>();
            if (report != null) {
                for (PuzzleProgressSnapshot snapshot : report.getSnapshots()) {
                    if (snapshot != null && snapshot.isSolved()) {
                        String answer = snapshot.getAnswer();
                        if (answer == null || answer.isBlank()) {
                            answer = "solved";
                        }
                        solvedEntries.add(summariseQuestion(snapshot) + " - Answer: " + answer);
                    }
                }
            }
            if (solvedEntries.isEmpty()) {
                solvedEntries.add("No puzzles solved yet.");
            }
            answeredQuestionsList.setItems(FXCollections.observableArrayList(solvedEntries));
        }

        if (hintUsageList != null) {
            List<String> hintEntries = new ArrayList<>();
            if (report != null) {
                for (PuzzleProgressSnapshot snapshot : report.getSnapshots()) {
                    if (snapshot == null) {
                        continue;
                    }
                    String summary = summariseQuestion(snapshot);
                    for (String hint : snapshot.getHintsUsed()) {
                        hintEntries.add(summary + " - Hint: " + hint);
                    }
                }
            }
            if (hintEntries.isEmpty()) {
                hintEntries.add("No hints used yet.");
            }
            hintUsageList.setItems(FXCollections.observableArrayList(hintEntries));
        }
    }

    private String summariseQuestion(PuzzleProgressSnapshot snapshot) {
        if (snapshot == null) {
            return "Unknown puzzle";
        }
        String question = snapshot.getQuestion();
        if (question == null || question.isBlank()) {
            return "Puzzle " + snapshot.getPuzzleId();
        }
        String cleaned = question.replaceAll("\\s+", " ").trim();
        return cleaned.length() > 80 ? cleaned.substring(0, 77) + "..." : cleaned;
    }

    private void resetSaveFilePreview() {
        if (saveFilePreview != null) {
            saveFilePreview.clear();
            saveFilePreview.setVisible(false);
            saveFilePreview.setManaged(false);
        }
        if (saveFileStatusLabel != null) {
            saveFileStatusLabel.setText("");
        }
    }

    private boolean isErrorMessage(String content) {
        if (content == null) {
            return false;
        }
        return content.startsWith("Save file not found") || content.startsWith("Unable to read");
    }

    private String formatTime(int seconds) {
        if (seconds <= 0) {
            return "--";
        }
        int mins = seconds / 60;
        int secs = seconds % 60;
        if (mins <= 0) {
            return secs + "s";
        }
        return mins + "m " + String.format("%02ds", secs);
    }
    
        /** Sets up the difficulty dropdown box. */

    private void configureDifficultySelector() {
        if (difficultyChoiceBox == null) {
            return;
        }
        difficultyChoiceBox.getItems().setAll(Settings.Difficulty.values());
        Settings.Difficulty current = App.getFacade().getSelectedDifficulty();
        difficultyChoiceBox.getSelectionModel().select(current);
        updateDifficultyDescription(current);
        difficultyChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Settings.Difficulty resolved = newVal == null ? Settings.Difficulty.EASY : newVal;
            updateDifficultyDescription(resolved);
        });
    }

        /** Updates the difficulty description label. */

    private void updateDifficultyDescription(Settings.Difficulty difficulty) {
        if (difficultyDescriptionLabel == null) {
            return;
        }
        Settings.Difficulty resolved = difficulty == null ? Settings.Difficulty.EASY : difficulty;
        String text;
        switch (resolved) {
            case MEDIUM:
                text = "A balanced challenge with multi-step clues and trickier riddles.";
                break;
            case HARD:
                text = "Designed for veterans: layered puzzles that demand careful reasoning.";
                break;
            case EASY:
            default:
                text = "Great for warming up—straightforward puzzles with generous hints.";
                break;
        }
        difficultyDescriptionLabel.setText(text);
    }
    
}
