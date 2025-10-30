package com.model;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class EscapeRoomManagerTest {

    private EscapeRoomManager manager;

    @Before
    public void setUp() {
        manager = EscapeRoomManager.getInstance();
        manager.resetRooms();
    }

    private EscapeRoom buildRoom(UUID id) {
        return new EscapeRoom(id, "Room-" + id, "desc", Collections.emptyList(), Collections.singletonList("Exit"));
    }

    @Test
    public void addRoomReplacesExistingIdAndTracksCount() {
        UUID id = UUID.randomUUID();
        EscapeRoom first = buildRoom(id);
        manager.addRoom(first);
        assertEquals(1, manager.getTotalRooms());
        assertSame(first, manager.getRoom(id));

        EscapeRoom replacement = buildRoom(id);
        manager.addRoom(replacement);
        assertEquals(1, manager.getTotalRooms());
        assertSame(replacement, manager.getRoom(id));
    }

    @Test
    public void removeRoomClearsCurrentWhenRemoved() {
        UUID id = UUID.randomUUID();
        EscapeRoom room = buildRoom(id);
        manager.addRoom(room);
        manager.setCurrentRoom(id);
        assertSame(room, manager.getCurrentRoom());

        manager.removeRoom(id);
        assertNull(manager.getCurrentRoom());
        assertEquals(0, manager.getTotalRooms());
    }

    @Test
    public void getAllRoomsReturnsSnapshot() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        manager.addRoom(buildRoom(firstId));
        manager.addRoom(buildRoom(secondId));

        java.util.List<EscapeRoom> snapshot = manager.getAllRooms();
        assertEquals(2, snapshot.size());
        snapshot.clear();

        assertEquals(2, manager.getAllRooms().size());
    }

    @Test
    public void isRoomCompletedDelegatesToRoom() {
        UUID id = UUID.randomUUID();
        EscapeRoom room = buildRoom(id);
        manager.addRoom(room);

        assertFalse(manager.isRoomCompleted(id));

        Puzzle solved = new Puzzle(1, "desc", "SOLVED", new Hint()) {};
        EscapeRoom solvedRoom = new EscapeRoom(id, room.getName(), "desc",
                java.util.List.of(solved), Collections.singletonList("Exit"));
        manager.addRoom(solvedRoom);
        assertTrue(manager.isRoomCompleted(id));
    }
}