package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        return new EscapeRoom(id, "Room-" + id, "desc",
                new ArrayList<>(), Collections.singletonList("Exit"));
    }

    @Test
    public void addRoom_replacesExistingId_and_tracksCount() {
        UUID id = UUID.randomUUID();
        EscapeRoom first = buildRoom(id);
        manager.addRoom(first);
        assertEquals("Should have one room after first add", 1, manager.getTotalRooms());
        assertSame("getRoom should return same instance", first, manager.getRoom(id));

        EscapeRoom replacement = buildRoom(id);
        manager.addRoom(replacement);
        assertEquals("Should still have one room after replacement", 1, manager.getTotalRooms());
        assertSame("getRoom should now return replacement instance", replacement, manager.getRoom(id));
    }

    @Test
    public void removeRoom_clearsCurrentRoom_whenRemoved() {
        UUID id = UUID.randomUUID();
        EscapeRoom room = buildRoom(id);
        manager.addRoom(room);
        manager.setCurrentRoom(id);
        assertSame("Current room should be set", room, manager.getCurrentRoom());

        manager.removeRoom(id);
        assertNull("Current room should be cleared", manager.getCurrentRoom());
        assertEquals("Room count should be zero after removal", 0, manager.getTotalRooms());
    }

    @Test
    public void getAllRooms_returnsIndependentSnapshot() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        manager.addRoom(buildRoom(firstId));
        manager.addRoom(buildRoom(secondId));

        List<EscapeRoom> snapshot = new ArrayList<>(manager.getAllRooms());
        assertEquals("Snapshot should contain both rooms", 2, snapshot.size());

        snapshot.clear(); // modifying our copy, not the manager’s list
        assertEquals("Manager’s internal list should remain unchanged",
                2, manager.getAllRooms().size());
    }

    @Test
    public void isRoomCompleted_delegatesToRoomStatus() {
        UUID id = UUID.randomUUID();
        EscapeRoom room = buildRoom(id);
        manager.addRoom(room);
        assertFalse("Empty room should not be completed", manager.isRoomCompleted(id));

        Puzzle solved = new Puzzle(1, "desc", "SOLVED", new Hint()) {};
        EscapeRoom solvedRoom = new EscapeRoom(id, room.getName(), "desc",
                List.of(solved), Collections.singletonList("Exit"));
        manager.addRoom(solvedRoom);
        assertTrue("Room with solved puzzle should be completed", manager.isRoomCompleted(id));
    }
}
