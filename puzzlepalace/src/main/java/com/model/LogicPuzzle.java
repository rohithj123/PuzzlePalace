package com.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This represents a logic puzzle in the game.
 * This stores the puzzle question, answer, hints, and how many hints have been used.
 */

public class LogicPuzzle {

    private int puzzleId;
    private String question;
    private String correctAnswer;
    private List<String> hints;
    private int hintsUsed;

    /**
     * This creates a new logic puzzle with default settings.
     * This automatically generates a sample puzzle.
     */
    public LogicPuzzle() {
        this.hints = new ArrayList<>();
        this.hintsUsed = 0;
        generatePuzzle();
    }

    /**
     * This creates a logic puzzle with specific details.
     *
     * @param puzzleId the ID of the puzzle
     * @param question the question of the puzzle
     * @param correctAnswer the correct answer
     * @param hints the list of hints for the puzzle
     */

    public LogicPuzzle(int puzzleId, String question, String correctAnswer, List<String> hints) {
        this.puzzleId = puzzleId;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.hints = hints;
        this.hintsUsed = 0;
    }

    // Generate a sample logic puzzle automatically
    private void generatePuzzle() {
        this.puzzleId = 1003;
        this.question = "Re-route the circuits to turn off the alarm. What must you connect?";
        this.correctAnswer = "connect red to red";
        this.hints = Arrays.asList(
            "Start by connecting red to red.",
            "Follow the wire path from power to alarm.",
            "Make sure all circuits are closed."
        );
    }

    /**
     * This evaluates the player's answer for the puzzle.
     *
     * @param userAnswer the answer given by the player
     * @return the evaluation result as a string
     */
    public String evaluateSolution(String userAnswer) {
        LogicPuzzleEvaluateSolution evaluator = new LogicPuzzleEvaluateSolution();
        return evaluator.evaluateSolution(this.question, this.correctAnswer, userAnswer);
    }

    /**
     * This gives the next available hint for the puzzle.
     *
     * @return the next hint or a message if none are available
     */
    public String getHint() {
        if (hints == null || hints.isEmpty()) return "No hints available.";
        if (hintsUsed >= hints.size()) return "All hints have been used.";
        return hints.get(hintsUsed++);
    }

    /**
     * This resets the puzzle’s hint usage.
     */
    public void resetPuzzle() {
        this.hintsUsed = 0;
    }

    /**
     * This returns the puzzle ID.
     *
     * @return the puzzle ID
     */
    public int getPuzzleId() { return puzzleId; }
     /**
     * This returns the puzzle question.
     *
     * @return the puzzle question
     */
    public String getQuestion() { return question; }
    /**
     * This returns the correct answer.
     *
     * @return the correct answer
     */
    public String getCorrectAnswer() { return correctAnswer; }
     /**
     * This returns the list of hints for the puzzle.
     *
     * @return the list of hints
     */
    public List<String> getHints() { return hints; }
     /**
     * This returns how many hints have been used.
     *
     * @return the number of used hints
     */
    public int getHintsUsed() { return hintsUsed; }
}
