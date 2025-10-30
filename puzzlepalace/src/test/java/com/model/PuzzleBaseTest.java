package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class PuzzleBaseTest {

    private static class BasicPuzzle extends Puzzle {
        BasicPuzzle(Hint hint) {
            super(12, "Riddle", "", hint);
        }
    }

    @Test
    public void trySolveMarksAttemptAndSolveChangesStatus() {
        Puzzle puzzle = new BasicPuzzle(new Hint(List.of("First"), 1));

        assertFalse(puzzle.trySolve("answer"));
        assertEquals("ATTEMPTED", puzzle.getStatus());

        assertTrue(puzzle.solve());
        assertEquals("SOLVED", puzzle.getStatus());
    }

    @Test
    public void resetPuzzleClearsStatusAndHintUsage() {
        Puzzle puzzle = new BasicPuzzle(new Hint(List.of("First"), 1));
        puzzle.requestHint();
        puzzle.solve();

        puzzle.resetPuzzle();
        assertEquals("UNSOLVED", puzzle.getStatus());
        assertEquals(0, puzzle.getHintsUsed());
    }

    @Test
    public void requestHintHandlesMissingHintObject() {
        Puzzle puzzle = new BasicPuzzle(null);
        assertEquals("No hints available.", puzzle.requestHint());
    }
}
