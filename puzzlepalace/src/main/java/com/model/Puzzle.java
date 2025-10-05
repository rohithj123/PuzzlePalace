package com.model;

public abstract class Puzzle {
    protected int puzzleId;
    protected String description;
    protected String status;
    protected Hint hints;

    public Puzzle() {
    }

    public Puzzle(int puzzleId, String description, String status, Hint hints) {
        this.puzzleId = puzzleId;
        this.description = description;
        this.status = "UNSOLVED";
        this.hints = new Hint();
    }

    public boolean solve() {
        this.status = "SOLVED";
        return true;
    }

    public void resetPuzzle() {
        this.status = "UNSOLVED";
    }

    public void undoLastMove() {

    }

    @Override
    public String toString() {
        return "Puzzle{" + "id=" + puzzleId + ", description=" + description + ", status=" + status + ", hints=" + hints + '}';
    }

}
