package com.model;

import java.util.Collections;
import java.util.List;


public abstract class Puzzle {
    protected int puzzleId;
    protected String description;
    protected String status;
    protected Hint hints;

    public Puzzle() {
        this(0, null, null, null);
    }

    public Puzzle(int puzzleId, String description, String status, Hint hints) {
        this.puzzleId = puzzleId;
        this.description = description;
        this.status = status == null || status.isBlank() ? "UNSOLVED" : status;
        this.hints = hints != null ? hints : new Hint();
    }

    public boolean trySolve(String attempt) {
        this.status = "ATTEMPTED";
        return false;
    }

    public boolean solve() {
        this.status = "SOLVED";
        return true;
    }

    public void resetPuzzle() {
        this.status = "UNSOLVED";
        if (hints != null) {
            hints.resetHintsUsed();
        }
    }

    public void undoLastMove() {
    }
    
    public int getPuzzleId() {
        return puzzleId;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public int getHintsUsed() {
        return hints == null ? 0 : hints.getHintsUsed();
    }

    public int getPenaltyHintsUsed() {
        return hints == null ? 0 : hints.getPenaltyHintsUsed();
    }

    public int getMaxHints() {
        return hints == null ? 0 : hints.getMaxHints();
    }

    public String requestHint() {
        return hints == null ? "No hints available." : hints.getHint();
    }
    
    public void markLastHintFree() {
        if (hints != null) {
            hints.markLastHintFree();
        }
    }

    public List<String> getAvailableHints() {
        if (hints == null) {
            return Collections.emptyList();
        }
        return hints.getAvailableHintsSnapshot();
    }



    @Override
    public String toString() {
        return "Puzzle{" + "id=" + puzzleId + ", description=" + description + ", status=" + status + ", hints=" + hints + '}';
    }

}
