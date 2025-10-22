package com.model;

import java.util.Arrays;
import java.util.List;

public class SimplePuzzle extends Puzzle {

    private final String solution;

    public SimplePuzzle(int puzzleId, String prompt, String solution, List<String> hints, int maxHints) {
        super(puzzleId, prompt, "UNSOLVED", new Hint(hints, Math.max(maxHints, hints == null ? 0 : hints.size())));
        this.solution = solution == null ? "" : solution.trim();
    }

    public SimplePuzzle(int puzzleId, String prompt, String solution, String... hints) {
        this(puzzleId, prompt, solution, hints == null ? null : Arrays.asList(hints), hints == null ? 0 : hints.length);
    }

    @Override
    public boolean trySolve(String attempt) {
        String cleanedAttempt = attempt == null ? "" : attempt.trim();
        boolean solved = !solution.isEmpty() && solution.equalsIgnoreCase(cleanedAttempt);
        this.status = solved ? "SOLVED" : "ATTEMPTED";
        return solved;
    }

    public String getSolution() {
        return solution;
    }

    public String provideHint() {
        return requestHint();
    }
}
