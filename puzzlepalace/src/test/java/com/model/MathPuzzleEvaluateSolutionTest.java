package com.model;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MathPuzzleEvaluateSolutionTest {

    private final MathPuzzleEvaluateSolution evaluator = new MathPuzzleEvaluateSolution();

    @Test
    public void evaluateSolutionWithNumericAnswerHandlesValidAndInvalidInput() {
        assertEquals(
                "Correct! Your answer satisfies the equation.",
                evaluator.evaluateSolution("x + 2 = 5", "3")
        );

        assertEquals(
                "Incorrect. Left = 7.0000, Right = 5.0000",
                evaluator.evaluateSolution("x + 2 = 5", "5")
        );

        assertEquals(
                "Invalid number format for answer.",
                evaluator.evaluateSolution("x + 2 = 5", "three")
        );
    }

    @Test
    public void evaluateSolutionWithVariablesHandlesEdgeCases() {
        assertEquals(
                "No equation provided.",
                evaluator.evaluateSolution(null, Map.of("x", 2.0))
        );

        assertEquals(
                "No variable assignments provided.",
                evaluator.evaluateSolution("x + 2 = 5", Map.of())
        );

        assertEquals(
                "Invalid equation format — must contain '='.",
                evaluator.evaluateSolution("x + 2", Map.of("x", 2.0))
        );

        assertEquals(
                "Error evaluating: Unknown variable: y",
                evaluator.evaluateSolution("x + y = 2", Map.of("x", 1.0))
        );
    }
}