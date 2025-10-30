package com.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class LogicPuzzleTest {

    @Test
    public void evaluateSolutionDelegatesToEvaluator() {
        LogicPuzzle puzzle = new LogicPuzzle(1, "Question", "Correct", Arrays.asList("Hint1"));
        String result = puzzle.evaluateSolution("correct");
        assertEquals("✅ Correct! The circuits are now off.", result);
    }

    @Test
    public void getHintReturnsHintsSequentially() {
        LogicPuzzle puzzle = new LogicPuzzle(1, "Question", "Answer", Arrays.asList("First", "Second"));
        assertEquals("First", puzzle.getHint());
        assertEquals("Second", puzzle.getHint());
        assertEquals("All hints have been used.", puzzle.getHint());
    }

    @Test
    public void resetPuzzleResetsHintCounter() {
        LogicPuzzle puzzle = new LogicPuzzle(1, "Question", "Answer", Arrays.asList("First"));
        puzzle.getHint();
        puzzle.resetPuzzle();
        assertEquals("First", puzzle.getHint());
    }
}