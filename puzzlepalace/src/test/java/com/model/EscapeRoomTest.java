package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

public class EscapeRoomTest {

    private static class SolvedPuzzle extends Puzzle {
        SolvedPuzzle() {
            super(11, "Solved", "SOLVED", new Hint());
        }
    }

    @Test
    public void isCompletedRequiresEveryPuzzleSolved() {
        EscapeRoom room = new EscapeRoom(UUID.randomUUID(), "Vault", "desc",
                java.util.List.of(new SolvedPuzzle(), new SolvedPuzzle()),
                java.util.List.of("North"));

        assertTrue(room.isCompleted());

        Puzzle unsolved = new SolvedPuzzle();
        unsolved.status = "ATTEMPTED";
        EscapeRoom withUnsolved = new EscapeRoom(room.getRoomID(), "Vault", "desc",
                java.util.List.of(unsolved), Collections.singletonList("North"));

        assertFalse(withUnsolved.isCompleted());
    }

    @Test
    public void loadRoomOverwritesIdentifierOnlyWhenNonNull() {
        EscapeRoom room = new EscapeRoom();
        UUID id = UUID.randomUUID();

        room.loadRoom(id);
        assertEquals(id, room.getRoomID());

        room.loadRoom(null);
        assertEquals(id, room.getRoomID());
    }

    @Test
    public void equalsAndHashCodeUseRoomId() {
        UUID shared = UUID.randomUUID();
        EscapeRoom first = new EscapeRoom(shared, "Vault", "desc", Collections.emptyList(), Collections.emptyList());
        EscapeRoom second = new EscapeRoom(shared, "Other", "desc", Collections.emptyList(), Collections.emptyList());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        EscapeRoom different = new EscapeRoom(UUID.randomUUID(), "Vault", "desc", Collections.emptyList(), Collections.emptyList());
        org.junit.Assert.assertNotEquals(first, different);
    }
}