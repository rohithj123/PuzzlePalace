package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class ScoreTest {

    @Test
    public void calculateScore_accountsForPenaltiesAndBonuses() {
        Score score = new Score(200, 3, 2, 180);
        int expected = 200 + (3 * 100) - (2 * 15) - ((180 / 60) * 2);
        int total = score.calculateScore();
        assertEquals(expected, total);
    }

    @Test
    public void compare_throwsWhenOtherScoreIsNull() {
        Score score = new Score();
        try {
            score.compare(null);
            fail("Expected IllegalArgumentException when comparing with null");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void setters_clampNegativeValuesToZero() {
        Score score = new Score();
        score.setPoints(-10);
        score.setPuzzlesSolved(-5);
        score.setHintsUsed(-2);
        score.setTimeTaken(-100);
        score.setFreeHintTokens(-8);

        assertEquals(0, score.getPoints());
        assertEquals(0, score.getPuzzlesSolved());
        assertEquals(0, score.getHintsUsed());
        assertEquals(0, score.getTimeTaken());
        assertEquals(0, score.getFreeHintTokens());
    }

    @Test
    public void calculateScore_handlesZeroValuesSafely() {
        Score score = new Score(0, 0, 0, 0);
        assertEquals(0, score.calculateScore());
    }

    @Test
    public void compare_returnsPositiveWhenThisHigher() {
        Score a = new Score(300, 2, 1, 100);
        Score b = new Score(200, 1, 1, 100);
        assertTrue(a.compare(b) > 0);
    }

    @Test
    public void compare_returnsNegativeWhenThisLower() {
        Score a = new Score(100, 1, 1, 50);
        Score b = new Score(200, 3, 0, 20);
        assertTrue(a.compare(b) < 0);
    }
}
