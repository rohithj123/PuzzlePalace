package com.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogicPuzzle {

    private int puzzleId;
    private String question;
    private String correctAnswer;
    private List<String> hints;
    private int hintsUsed;

    public LogicPuzzle() {
        this.hints = new ArrayList<>();
        this.hintsUsed = 0;
        generatePuzzle();
    }

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

    public String evaluateSolution(String userAnswer) {
        LogicPuzzleEvaluateSolution evaluator = new LogicPuzzleEvaluateSolution();
        return evaluator.evaluateSolution(this.question, this.correctAnswer, userAnswer);
    }

    public String getHint() {
        if (hints == null || hints.isEmpty()) return "No hints available.";
        if (hintsUsed >= hints.size()) return "All hints have been used.";
        return hints.get(hintsUsed++);
    }

    public void resetPuzzle() {
        this.hintsUsed = 0;
    }

    public int getPuzzleId() { return puzzleId; }
    public String getQuestion() { return question; }
    public String getCorrectAnswer() { return correctAnswer; }
    public List<String> getHints() { return hints; }
    public int getHintsUsed() { return hintsUsed; }
}
