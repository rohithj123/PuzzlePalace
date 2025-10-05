package com.model;

public class WordPuzzle {
    private int puzzleId;
    private String question;
    private String correctAnswer;
    private boolean isSolved;

    public WordPuzzle() {
        // default constructor
    }

    public WordPuzzle(int puzzleId, String question, String correctAnswer) {
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

    public boolean checkAnswer(String attempt) {
        System.out.println("checkAnswer() called with attempt: " + attempt);
        if (attempt == null) return false;

        boolean correct = attempt.trim().equalsIgnoreCase(correctAnswer);
        if (correct) {
            System.out.println("Correct answer! Puzzle solved (stub).");
            isSolved = true;
        } else {
            System.out.println("Incorrect answer (stub).");
        }
        return correct;
    }

    public void revealHint() {
        System.out.println("revealHint() called (stub)");
    }

    public void resetPuzzle() {
        System.out.println("resetPuzzle() called (stub)");
        isSolved = false;
    }

    @Override
    public String toString() {
        return "WordPuzzle{id=" + puzzleId + ", question='" + question + "', solved=" + isSolved + "}";
    }
}
