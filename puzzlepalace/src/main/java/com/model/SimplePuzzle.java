package com.model;

import java.util.Arrays;
import java.util.List;

/**
 * A simple text-based puzzle.
 * Checks if the player's answer matches the solution.
 */
public class SimplePuzzle extends Puzzle {

    private final String solution;

        /**
     * Creates a puzzle with a question, solution, and hints.
     *
     * @param puzzleId the puzzle ID
     * @param prompt the puzzle question
     * @param solution the correct answer
     * @param hints list of hints for the puzzle
     * @param maxHints max number of hints allowed
     */
    public SimplePuzzle(int puzzleId, String prompt, String solution, List<String> hints, int maxHints) {
        super(puzzleId, prompt, "UNSOLVED", new Hint(hints, Math.max(maxHints, hints == null ? 0 : hints.size())));
        this.solution = solution == null ? "" : solution.trim();
    }

    /**
     * Creates a puzzle with a variable number of hints.
     *
     * @param puzzleId the puzzle ID
     * @param prompt the puzzle question
     * @param solution the correct answer
     * @param hints optional hints
     */
    public SimplePuzzle(int puzzleId, String prompt, String solution, String... hints) {
        this(puzzleId, prompt, solution, hints == null ? null : Arrays.asList(hints), hints == null ? 0 : hints.length);
    }

    /**
     * Checks if the user's answer is correct.
     *
     * @param attempt the player's answer
     * @return true if correct, false otherwise
     */
    @Override
    public boolean trySolve(String attempt) {
        String cleanedAttempt = attempt == null ? "" : attempt.trim();
        boolean solved = !solution.isEmpty() && solution.equalsIgnoreCase(cleanedAttempt);
        this.status = solved ? "SOLVED" : "ATTEMPTED";
        return solved;
    }

        /** Returns the correct solution. */
    public String getSolution() {
        return solution;
    }

        /** Returns the next hint for this puzzle. */
    public String provideHint() {
        return requestHint();
    }
}
