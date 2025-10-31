package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class RoomTest {

    private static class SolvedPuzzle extends Puzzle {
        SolvedPuzzle(int id, String status) {
            super(id, "desc", status, new Hint());
        }
    }

    @Test
    public void constructorCopiesListsAndIsImmutable() {
        Room room = new Room("R1", "Vault", "desc", "Easy", 10,
                Arrays.asList(new SolvedPuzzle(1, "SOLVED")), Arrays.asList("North"), null);

        assertEquals(1, room.getPuzzles().size());
        try {
            room.getPuzzles().add(new SolvedPuzzle(2, "SOLVED"));
            org.junit.Assert.fail("Puzzles list should be unmodifiable");
        } catch (UnsupportedOperationException expected) {
            // expected
        }
        assertEquals(1, room.getExits().size());
    }

    @Test
    public void addPuzzleAndExitAffectState() {
        Room room = new Room();
        room.addPuzzle(new SolvedPuzzle(5, "SOLVED"));
        room.addExit("East");

        assertEquals(1, room.getPuzzles().size());
        assertEquals(1, room.getExits().size());
    }

    @Test
    public void isCompletedRequiresAllSolved() {
        Room room = new Room("R2", "Vault", "desc", "Easy", 10,
                Arrays.asList(new SolvedPuzzle(10, "SOLVED"), new SolvedPuzzle(11, "ATTEMPTED")),
                Collections.emptyList(), null);

        assertFalse(room.isCompleted());

        room.setPuzzles(Arrays.asList(new SolvedPuzzle(10, "SOLVED")));
        assertTrue(room.isCompleted());
    }

    @Test
    public void getPuzzleByIdFindsMatchingPuzzle() {
        SolvedPuzzle puzzle = new SolvedPuzzle(20, "SOLVED");
        Room room = new Room("R3", "Vault", "desc", "Easy", 10,
                Arrays.asList(puzzle), Collections.emptyList(), null);

        assertEquals(puzzle, room.getPuzzleById(20));
        org.junit.Assert.assertNull(room.getPuzzleById(99));
    }
}