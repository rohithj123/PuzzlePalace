package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class MathChallengePuzzleTest {

    @Test
    public void trySolveMatchesNumericAnswer() {
        MathChallengePuzzle puzzle = new MathChallengePuzzle(99, "prompt", 42.0,
                Arrays.asList("Hint1", "Hint2"));

        assertFalse(puzzle.trySolve("41"));
        assertEquals("ATTEMPTED", puzzle.getStatus());

        assertTrue(puzzle.trySolve("42"));
        assertEquals("SOLVED", puzzle.getStatus());
        assertEquals(42.0, puzzle.getSolution(), 0.0);
    }

    @Test
    public void trySolveHandlesWhitespaceAndInvalidNumbers() {
        MathChallengePuzzle puzzle = new MathChallengePuzzle(100, "prompt", 3.14, "only hint");

        assertFalse(puzzle.trySolve(""));
        assertFalse(puzzle.trySolve("abc"));
        assertTrue(puzzle.trySolve(" 3.14 "));
    }
}