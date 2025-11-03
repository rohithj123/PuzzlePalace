package com.model;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class LogicPuzzleTest {

    @Test
    public void evaluateSolution_returnsCorrectMessage() {
        LogicPuzzle puzzle = new LogicPuzzle(1, "Question", "Correct", Arrays.asList("Hint1"));
        String result = puzzle.evaluateSolution("correct");
        assertEquals("✅ Correct! The circuits are now off.", result);
    }

    @Test
    public void getHint_returnsHintsSequentially() {
        LogicPuzzle puzzle = new LogicPuzzle(1, "Question", "Answer", Arrays.asList("First", "Second"));
        assertEquals("First", puzzle.getHint());
        assertEquals("Second", puzzle.getHint());
        assertEquals("All hints have been used.", puzzle.getHint());
    }

    @Test
    public void resetPuzzle_resetsHintCounter() {
        LogicPuzzle puzzle = new LogicPuzzle(1, "Question", "Answer", Arrays.asList("First"));
        puzzle.getHint();
        puzzle.resetPuzzle();
        assertEquals("First", puzzle.getHint());
    }

    @Test
    public void evaluateSolution_handlesIncorrectAnswers() {
        LogicPuzzle puzzle = new LogicPuzzle(2, "Question", "Yes", Arrays.asList("Try again"));
        String result = puzzle.evaluateSolution("No");
        assertTrue(result.contains("Incorrect"));
    }

    @Test
    public void evaluateSolution_isCaseInsensitive() {
        LogicPuzzle puzzle = new LogicPuzzle(3, "Question", "Correct", Arrays.asList("Hint"));
        String result = puzzle.evaluateSolution("cOrReCt");
        assertEquals("✅ Correct! The circuits are now off.", result);
    }
}
