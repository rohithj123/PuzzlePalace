package com.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MathChallengePuzzle extends Puzzle {

    private final double solution;

    public MathChallengePuzzle(int puzzleId, String prompt, double solution, List<String> hints) {
        super(puzzleId, prompt, "UNSOLVED", new Hint(hints, hints == null ? 0 : hints.size()));
        this.solution = solution;
    }

    public MathChallengePuzzle(int puzzleId, String prompt, double solution, String... hints) {
        this(puzzleId, prompt, solution,
                hints == null ? Collections.emptyList() : Arrays.asList(hints));
    }

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

    public double getSolution() {
        return solution;
    }
}
