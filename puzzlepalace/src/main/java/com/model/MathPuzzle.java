package com.model;

public class MathPuzzle {
    private int puzzleId;
    private String question;
    private String correctAnswer;
    private boolean isSolved;

    public MathPuzzle() {
        // default constructor
    }

    public MathPuzzle(int puzzleId, String question, String correctAnswer) {
        this.puzzleId = puzzleId;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.isSolved = false;
    }

    public int getPuzzleId() {
        return puzzleId;
    }

    public String getQuestion() {
        return question;
    }

    public boolean isSolved() {
        return isSolved;
    }

    /**
     * Basic answer checker (case-insensitive). Prints stub logs.
     */
    public boolean checkAnswer(String attempt) {
        System.out.println("MathPuzzle.checkAnswer() called with attempt: " + attempt + " (stub)");
        if (attempt == null) return false;

        boolean correct = attempt.trim().equalsIgnoreCase(correctAnswer);
        if (correct) {
            System.out.println("Correct! MathPuzzle solved (stub).");
            isSolved = true;
        } else {
            System.out.println("Incorrect answer (stub).");
        }
        return correct;
    }

    public void revealHint() {
        System.out.println("MathPuzzle.revealHint() called (stub)");
    }

    public void resetPuzzle() {
        System.out.println("MathPuzzle.resetPuzzle() called (stub)");
        isSolved = false;
    }

    @Override
    public String toString() {
        return "MathPuzzle{id=" + puzzleId + ", question='" + question + "', solved=" + isSolved + "}";
    }
}
