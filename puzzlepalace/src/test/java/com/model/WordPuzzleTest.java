package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class WordPuzzleTest {

    @Test
    public void evaluateSolutionRecognizesCorrectAnswerIgnoringCaseAndWhitespace() {
        WordPuzzle puzzle = new WordPuzzle(
                10,
                "Question",
                Arrays.asList("Moon", "Sun"),
                "Moon",
                Arrays.asList("It glows at night")
        );

        String result = puzzle.evaluateSolution("  moon  ");
        assertEquals("Correct! Your answer is correct.", result);
    }

    @Test
    public void evaluateSolutionSignalsIncorrectWhenWordInListButWrong() {
        WordPuzzle puzzle = new WordPuzzle(
                10,
                "Question",
                Arrays.asList("Moon", "Sun"),
                "Moon",
                Arrays.asList("It glows at night")
        );

        String result = puzzle.evaluateSolution("Sun");
        assertEquals("Incorrect. \"Sun\" is not the right word.", result);
    }

    @Test
    public void evaluateSolutionHandlesNullOrMissingAnswers() {
        WordPuzzle puzzle = new WordPuzzle(Arrays.asList("Moon"), Arrays.asList("Hint"));
        puzzle.setCorrectAnswer("Moon");
        assertEquals("No answer provided.", puzzle.evaluateSolution(" "));
    }

    @Test
    public void evaluateSolutionWarnsWhenNoCorrectAnswerConfigured() {
        WordPuzzle puzzle = new WordPuzzle(Arrays.asList("Moon"), Arrays.asList("Hint"));
        puzzle.setCorrectAnswer(null);

        assertEquals("This puzzle has no correct answer configured.", puzzle.evaluateSolution("Moon"));
    }

    @Test
    public void getHintCyclesThroughAvailableHintsThenStops() {
        WordPuzzle puzzle = new WordPuzzle(
                10,
                "Question",
                Arrays.asList("Moon", "Sun"),
                "Moon",
                Arrays.asList("First", "Second")
        );

        assertEquals("First", puzzle.getHint());
        assertEquals("Second", puzzle.getHint());
        assertEquals("All hints have been used.", puzzle.getHint());
        assertEquals(2, puzzle.getHintsUsed());
    }

    @Test
    public void resetPuzzleResetsHintUsageAndMaintainsQuestion() {
        WordPuzzle puzzle = new WordPuzzle(
                10,
                "Choose wisely",
                Collections.singletonList("Moon"),
                "Moon",
                Arrays.asList("First")
        );

        puzzle.getHint();
        puzzle.resetPuzzle();

        assertEquals(0, puzzle.getHintsUsed());
        assertNotNull(puzzle.getQuestion());
        assertTrue(puzzle.getQuestion().contains("Moon"));
    }
}
