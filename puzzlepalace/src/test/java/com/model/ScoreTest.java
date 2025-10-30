package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class ScoreTest {

    @Test
    public void calculateScoreAccountsForPenalties() {
        Score score = new Score(200, 3, 2, 180);
        int total = score.calculateScore();
        // Base 200 + (3 * 100) - (2 * 15) - ((180 / 60) * 2)
        assertEquals(200 + 300 - 30 - 6, total);
    }

    @Test
    public void compareThrowsWhenOtherScoreIsNull() {
        Score score = new Score();
        assertThrows(IllegalArgumentException.class, () -> score.compare(null));
    }

    @Test
    public void settersClampNegativeValuesToZero() {
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
}
