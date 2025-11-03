package com.model;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PlayerTest {

    private static class SolvablePuzzle extends Puzzle {
        private final String expected;

        SolvablePuzzle(int id, String expected) {
            super(id, "Solve me", "UNSOLVED", new Hint(List.of("Try the obvious"), 3));
            this.expected = expected;
        }

        @Override
        public boolean trySolve(String attempt) {
            boolean solved = expected.equals(attempt);
            this.status = solved ? "SOLVED" : "ATTEMPTED";
            return solved;
        }
    }

    @Test
    public void guestLogin_assignsCredentialsProperly() {
        Player guest = new Player();
        String alias = guest.getUsername();
        assertTrue(guest.isGuest());
        assertTrue(alias.startsWith("Guest-"));

        assertTrue(guest.login("Alex", "Secret123"));
        assertEquals("Alex", guest.getUsername());
        assertFalse(guest.isGuest());
        assertTrue(guest.verifyPassword("Secret123"));
    }

    @Test
    public void inventoryOperations_matchByIdOrName() {
        Player player = new Player("Dana", "d@example.com", "pw");
        Item keycard = new Item(101, "Keycard");
        player.addItem(keycard);

        assertTrue(player.hasItem(new Item(101, "Anything")));
        assertTrue(player.hasItem(new Item(999, "Keycard")));
        assertTrue(player.useItem(new Item(101, "Keycard"), null));
        assertFalse(player.hasItem(keycard));
    }

    @Test
    public void recordPuzzleCompletion_createsSnapshotsAndCounts() {
        Player player = new Player("Casey", "c@example.com", "pass");
        SolvablePuzzle puzzle = new SolvablePuzzle(7, "answer");

        assertTrue(player.recordPuzzleCompletion(puzzle, "answer"));
        assertEquals(1, player.getPuzzleProgressSnapshots().size());
        assertEquals(1, player.getSolvedPuzzleCountFromHistory());

        assertFalse(player.recordPuzzleCompletion(puzzle, "answer"));
        assertEquals(1, player.getSolvedPuzzleCountFromHistory());
    }

    @Test
    public void recordHintUsed_aggregatesTotals() {
        Player player = new Player("Evan", "e@example.com", "pw");
        SolvablePuzzle puzzle = new SolvablePuzzle(8, "open");

        player.recordHintUsed(puzzle, " First hint ");
        player.recordHintUsed(puzzle, null);

        assertEquals(1, player.getTotalHintsUsedFromHistory());
        assertEquals(1, player.getScoreDetails().getHintsUsed());
    }

    @Test
    public void tokenAndFreezeTimer_managementWorks() {
        Player player = new Player("Fran", "f@example.com", "pw");

        assertFalse(player.hasFreeHintTokens());
        player.addFreeHintToken();
        assertTrue(player.consumeFreeHintToken());
        assertFalse(player.consumeFreeHintToken());

        assertFalse(player.hasFreezeTimerCharges());
        assertTrue(player.addFreezeTimerCharge());
        assertTrue(player.hasFreezeTimerCharges());
        assertTrue(player.consumeFreezeTimerCharge());
        assertFalse(player.consumeFreezeTimerCharge());
    }

    @Test
    public void recordTimeSpent_accumulatesAndClampsProperly() {
        Player player = new Player("Gale", "g@example.com", "pw");
        player.recordTimeSpent(5);
        assertEquals(5, player.getScoreDetails().getTimeTaken());

        player.recordTimeSpent(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, player.getScoreDetails().getTimeTaken());
    }

    @Test
    public void solvePuzzle_delegatesAndUpdatesScore() {
        Player player = new Player("Harper", "h@example.com", "pw");
        SolvablePuzzle puzzle = new SolvablePuzzle(15, "code");

        assertFalse(player.solvePuzzle(puzzle, "wrong"));
        assertTrue(player.solvePuzzle(puzzle, "code"));
        assertEquals(1, player.getScoreDetails().getPuzzlesSolved());
    }
}
