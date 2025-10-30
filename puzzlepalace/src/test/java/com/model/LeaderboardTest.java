package com.model;

import static org.junit.Assert.assertNull;
import org.junit.Test;

public class LeaderboardTest {

    @Test
    public void stubMethodsReturnDefaults() {
        Leaderboard leaderboard = new Leaderboard();

        leaderboard.addEntry();
        assertNull(leaderboard.displayTopScores());
        org.junit.Assert.assertEquals(0, leaderboard.findUserRank());
    }
}