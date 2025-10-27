package com.model;

import java.util.Collections;
import java.util.List;

/**
 * Represents a generic puzzle structure with ID, description, status, and hint management.
 * Specific puzzle types should extend this class and implement their own solving logic.
 */
public abstract class Puzzle {
       
    /** Unique identifier for the puzzle. */
    protected int puzzleId;
    /** Description or prompt for the puzzle. */
    protected String description;
    /** Current status of the puzzle (e.g., UNSOLVED, ATTEMPTED, SOLVED). */
    protected String status;
    /** Hint object containing available hints and tracking usage. */
    protected Hint hints;

    /** Creates an empty puzzle with default values. */
    public Puzzle() {
        this(0, null, null, null);
    }

    /**
     * Creates a puzzle with specified details.
     */
    public Puzzle(int puzzleId, String description, String status, Hint hints) {
        this.puzzleId = puzzleId;
        this.description = description;
        this.status = status == null || status.isBlank() ? "UNSOLVED" : status;
        this.hints = hints != null ? hints : new Hint();
    }

    /**
     * Attempts to solve the puzzle using the given input.
     * Default implementation always sets status to "ATTEMPTED".
     */
    public boolean trySolve(String attempt) {
        this.status = "ATTEMPTED";
        return false;
    }

    /**
     * Marks the puzzle as solved.
     */
    public boolean solve() {
        this.status = "SOLVED";
        return true;
    }

    /** 
     * Resets the puzzle to its initial unsolved state and resets hint usage. 
     */
    public void resetPuzzle() {
        this.status = "UNSOLVED";
        if (hints != null) {
            hints.resetHintsUsed();
        }
    }

    /** 
     * Placeholder method for undoing the last move (to be implemented if needed). 
     */
    public void undoLastMove() {
    }
    /** return the puzzle ID */
    public int getPuzzleId() {
        return puzzleId;
    }

    /** return the puzzle description */
    public String getDescription() {
        return description;
    }
    /** return the puzzle's current status */
    public String getStatus() {
        return status;
    }
    /** return number of hints used */
    public int getHintsUsed() {
        return hints == null ? 0 : hints.getHintsUsed();
    }

    /** return number of penalty hints used */
    public int getPenaltyHintsUsed() {
        return hints == null ? 0 : hints.getPenaltyHintsUsed();
    }
    /** return number of bonus hints used */
    public int getBonusHintsUsed() {
        return hints == null ? 0 : hints.getBonusHintsUsed();
    }

    /** return total number of hints available */
    public int getMaxHints() {
        return hints == null ? 0 : hints.getMaxHints();
    }
    /**
     * Requests the next available hint.
     */
    public String requestHint() {
        return hints == null ? "No hints available." : hints.getHint();
    }
    /** 
     * Marks the most recently used hint as free (no penalty).
     */
    public void markLastHintFree() {
        if (hints != null) {
            hints.markLastHintFree();
        }
    }
     /**
     * Returns a snapshot of all available hints.
     */
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
