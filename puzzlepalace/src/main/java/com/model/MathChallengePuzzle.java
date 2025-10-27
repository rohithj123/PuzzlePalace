package com.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a math challenge puzzle where the player must guess a numeric solution.
 */
public class MathChallengePuzzle extends Puzzle {

    private final double solution;

    /**
     * Creates a new MathChallengePuzzle with a list of hints.
     */ 
    public MathChallengePuzzle(int puzzleId, String prompt, double solution, List<String> hints) {
        super(puzzleId, prompt, "UNSOLVED", new Hint(hints, hints == null ? 0 : hints.size()));
        this.solution = solution;
    }

    /**
     * Creates a new MathChallengePuzzle with variable-length hint arguments.
     */ 
    public MathChallengePuzzle(int puzzleId, String prompt, double solution, String... hints) {
        this(puzzleId, prompt, solution,
                hints == null ? Collections.emptyList() : Arrays.asList(hints));
    }

     /**
     * Attempts to solve the puzzle by comparing the user's guess to the solution.
     */
    @Override
    public boolean trySolve(String attempt) {
        String trimmedAttempt = attempt == null ? "" : attempt.trim();
        boolean solved = false;
        if (!trimmedAttempt.isEmpty()) {
            try {
                double guess = Double.parseDouble(trimmedAttempt);
                solved = Math.abs(guess - solution) < 0.0001;
            } catch (NumberFormatException ignored) {
                solved = false;
            }
        }
        this.status = solved ? "SOLVED" : "ATTEMPTED";
        return solved;
    }
     /**
     * Returns the correct numeric solution.
     */ 
    public double getSolution() {
        return solution;
    }
}
