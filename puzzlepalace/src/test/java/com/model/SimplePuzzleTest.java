package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class SimplePuzzleTest {

    @Test
    public void trySolveMarksPuzzleSolvedWhenAnswerMatches() {
        SimplePuzzle puzzle = new SimplePuzzle(1, "Question?", "Answer", Arrays.asList("Hint1"), 1);

        assertTrue(puzzle.trySolve(" answer "));
        assertEquals("SOLVED", puzzle.getStatus());
    }

    @Test
    public void trySolveMarksAttemptedWhenAnswerIncorrect() {
        SimplePuzzle puzzle = new SimplePuzzle(1, "Question?", "Answer", Arrays.asList("Hint1"), 1);

        assertFalse(puzzle.trySolve("Wrong"));
        assertEquals("ATTEMPTED", puzzle.getStatus());
    }

    @Test
    public void provideHintDelegatesToHintPool() {
        SimplePuzzle puzzle = new SimplePuzzle(1, "Question?", "Answer", Arrays.asList("First", "Second"), 2);

        assertEquals("First", puzzle.provideHint());
        assertEquals("Second", puzzle.provideHint());
        assertEquals("All hints have been used.", puzzle.provideHint());
        assertEquals(2, puzzle.getHintsUsed());
    }

    @Test
    public void resetPuzzleClearsStatusAndHintUsage() {
        SimplePuzzle puzzle = new SimplePuzzle(1, "Question?", "Answer", Arrays.asList("Hint"), 1);
        puzzle.trySolve("Answer");
        puzzle.provideHint();

        puzzle.resetPuzzle();
        assertEquals("UNSOLVED", puzzle.getStatus());
        assertEquals(0, puzzle.getHintsUsed());
    }
}
