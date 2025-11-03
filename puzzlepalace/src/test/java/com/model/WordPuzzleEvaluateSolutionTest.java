package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class WordPuzzleEvaluateSolutionTest {

    private final WordPuzzleEvaluateSolution evaluator = new WordPuzzleEvaluateSolution();

    @Test
    public void evaluateSolutionHandlesMissingInputs() {
        assertEquals("No answer provided.", evaluator.evaluateSolution(null, "correct", Arrays.asList("correct")));
        assertEquals("No correct answer configured for this puzzle.", evaluator.evaluateSolution("answer", "", Arrays.asList("correct")));
        assertEquals("Error: puzzle not provided.", evaluator.evaluateSolution((WordPuzzle) null, "answer"));
    }

    @Test
    public void evaluateSolutionProvidesDetailedFeedback() {
        assertEquals(
                "Correct! You selected the right word.",
                evaluator.evaluateSolution("word", "word", Arrays.asList("word"))
        );

        assertEquals(
                "Incorrect. \"wrong\" is not the correct word.",
                evaluator.evaluateSolution("wrong", "word", Arrays.asList("word", "wrong"))
        );

        assertEquals(
                "Incorrect answer. The correct word was \"word\".",
                evaluator.evaluateSolution("unknown", "word", Arrays.asList("word"))
        );
    }

    @Test
    public void isCorrectComparesAnswersCaseInsensitively() {
        assertFalse(evaluator.isCorrect(null, "answer"));
        assertFalse(evaluator.isCorrect("answer", null));
        assertTrue(evaluator.isCorrect("Word", " word "));
    }
}