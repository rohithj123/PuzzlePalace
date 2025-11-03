package com.model;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PuzzleBaseTest {

    private static class BasicPuzzle extends Puzzle {
        BasicPuzzle(Hint hint) {
            super(12, "Riddle", "", hint);
        }
    }

    @Test
    public void trySolve_marksAttemptAndSolveChangesStatus() {
        Puzzle puzzle = new BasicPuzzle(new Hint(List.of("First"), 1));

        assertFalse(puzzle.trySolve("wrong"));
        assertEquals("ATTEMPTED", puzzle.getStatus());

        assertTrue(puzzle.solve());
        assertEquals("SOLVED", puzzle.getStatus());
    }

    @Test
    public void resetPuzzle_clearsStatusAndHintUsage() {
        Puzzle puzzle = new BasicPuzzle(new Hint(List.of("First"), 1));
        puzzle.requestHint();
        puzzle.solve();

        puzzle.resetPuzzle();
        assertEquals("UNSOLVED", puzzle.getStatus());
        assertEquals(0, puzzle.getHintsUsed());
    }

    @Test
    public void requestHint_handlesMissingHintObject() {
        Puzzle puzzle = new BasicPuzzle(null);
        assertEquals("No hints available.", puzzle.requestHint());
    }

    @Test
    public void requestHint_returnsHintsSequentially() {
        Puzzle puzzle = new BasicPuzzle(new Hint(List.of("Hint1", "Hint2"), 2));
        assertEquals("Hint1", puzzle.requestHint());
        assertEquals("Hint2", puzzle.requestHint());
        assertEquals("All hints have been used.", puzzle.requestHint());
    }

    @Test
    public void trySolve_multipleAttemptsBehaveCorrectly() {
        Puzzle puzzle = new BasicPuzzle(new Hint(List.of("Try again"), 1));
        assertFalse(puzzle.trySolve("Nope"));
        assertEquals("ATTEMPTED", puzzle.getStatus());
        assertTrue(puzzle.solve());
        assertEquals("SOLVED", puzzle.getStatus());
    }
}
