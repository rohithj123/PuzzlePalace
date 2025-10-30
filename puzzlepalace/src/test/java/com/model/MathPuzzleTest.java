package com.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class MathPuzzleTest {

    @Test
    public void checkAnswerEvaluatesStoredAnswerString() {
        MathPuzzle puzzle = new MathPuzzle(1, "2*x=10", null);
        puzzle.setAnswer("x=5");

        assertTrue(puzzle.checkAnswer());
    }

    @Test
    public void checkAnswerUsingMapDetectsIncorrectSolutions() {
        MathPuzzle puzzle = new MathPuzzle(1, "x + y = 10", null);
        Map<String, Double> values = new HashMap<>();
        values.put("x", 2.0);
        values.put("y", 3.0);

        assertFalse(puzzle.checkAnswer(values));
    }

    @Test
    public void checkAnswerStringUpdatesAnswerAndEvaluates() {
        MathPuzzle puzzle = new MathPuzzle(1, "x^2 = 16", null);
        assertTrue(puzzle.checkAnswer("x=4"));
        assertFalse(puzzle.checkAnswer("x=2"));
    }

    @Test
    public void getHintReturnsHintsSequentiallyThenStops() {
        MathPuzzle puzzle = new MathPuzzle(1, "question", null);
        List<String> hints = Arrays.asList("First", "Second");
        puzzle.setHints(hints);

        assertEquals("First", puzzle.getHint());
        assertEquals("Second", puzzle.getHint());
        assertEquals("All hints have been used.", puzzle.getHint());
    }

    @Test
    public void resetPuzzleClearsAnswerAndHintUsage() {
        MathPuzzle puzzle = new MathPuzzle(1, "question", null);
        puzzle.setHints(Arrays.asList("First"));
        puzzle.setAnswer("x=1");
        puzzle.getHint();

        puzzle.resetPuzzle();
        assertEquals(null, puzzle.getAnswer());
        assertEquals("First", puzzle.getHint());
    }

    @Test
    public void evaluateSolutionSupportsNumericInput() {
        MathPuzzle puzzle = new MathPuzzle(1, "x + 3 = 7", null);
        String feedback = puzzle.evaluateSolution("4");
        assertEquals("Correct! Your answer satisfies the equation.", feedback);
    }

    @Test
    public void evaluateSolutionSupportsVariableAssignments() {
        MathPuzzle puzzle = new MathPuzzle(1, "x + y = 7", null);
        String feedback = puzzle.evaluateSolution("x=3,y=4");
        assertEquals("Correct! Your answer satisfies the equation.", feedback);
    }

    @Test
    public void evaluateSolutionReportsMissingAssignments() {
        MathPuzzle puzzle = new MathPuzzle(1, "x + y = 7", null);
        String feedback = puzzle.evaluateSolution("x=foo,y=bar");
        assertEquals("No variable assignments provided.", feedback);
    }
}
