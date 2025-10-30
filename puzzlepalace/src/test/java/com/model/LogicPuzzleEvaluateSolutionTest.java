package com.model;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LogicPuzzleEvaluateSolutionTest {

    private final LogicPuzzleEvaluateSolution evaluator = new LogicPuzzleEvaluateSolution();

    @Test
    public void evaluateSolutionHandlesMissingInputs() {
        assertEquals("No puzzle question provided.", evaluator.evaluateSolution(null, "answer", "guess"));
        assertEquals("No correct answer stored for puzzle.", evaluator.evaluateSolution("question", null, "guess"));
        assertEquals("No answer provided.", evaluator.evaluateSolution("question", "answer", null));
    }

    @Test
    public void evaluateSolutionRecognizesExactMatch() {
        String result = evaluator.evaluateSolution(
                "Re-route the circuits",
                "Connect red to red",
                " connect RED to red!!! "
        );
        assertEquals("✅ Correct! The circuits are now off.", result);
    }

    @Test
    public void evaluateSolutionProvidesPartialFeedback() {
        String result = evaluator.evaluateSolution(
                "Re-route the circuits",
                "Connect red to red",
                "I think the red wire goes somewhere else"
        );
        assertEquals("Almost there! You’re on the right track—check your connections again.", result);
    }

    @Test
    public void evaluateSolutionHandlesIncorrectAnswer() {
        String result = evaluator.evaluateSolution(
                "Re-route the circuits",
                "Connect red to red",
                "Use the blue wire"
        );
        assertEquals("❌ Incorrect. Try again or use a hint.", result);
    }
}