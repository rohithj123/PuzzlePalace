package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EscapeRoomManager {
    private final List<EscapeRoom> rooms;
    private EscapeRoom currentRoom;
    private int totalRooms;

    private static volatile EscapeRoomManager instance;

    private EscapeRoomManager() {
        this.rooms = new ArrayList<>();
        this.currentRoom = null;
        this.totalRooms = 0;
    }

    public static EscapeRoomManager getInstance() {
        if (instance == null) {
            synchronized (EscapeRoomManager.class) {
                if (instance == null) {
                    instance = new EscapeRoomManager();
                }
            }
        }
        return instance;
    }

    public void addRoom(EscapeRoom room) {
        if (room == null) return;

        UUID id = room.getRoomID();
        if (id == null) {
            return;
        }

        for (int i = 0; i < rooms.size(); i++) {
            EscapeRoom r = rooms.get(i);
            if (r != null && id.equals(r.getRoomID())) {
                rooms.set(i, room);
                return;
            }
        }

        rooms.add(room);
        totalRooms = rooms.size();
    }
    public void removeRoom(UUID id) {
        if (id == null || rooms.isEmpty()) return;

        for (int i = 0; i < rooms.size(); i++) {
            EscapeRoom r = rooms.get(i);
            if (r != null && id.equals(r.getRoomID())) {
                rooms.remove(i);
                if (currentRoom != null && id.equals(currentRoom.getRoomID())) {
                    currentRoom = null;
                }
                totalRooms = rooms.size();
                return;
            }
        }
    }

    public EscapeRoom getRoom(UUID id) {
        if (id == null || rooms.isEmpty()) return null;
        for (EscapeRoom r : rooms) {
            if (r != null && id.equals(r.getRoomID())) {
                return r;
            }
        }
        return null;
    }

    public List<EscapeRoom> getAllRooms() {
        return Collections.unmodifiableList(new ArrayList<>(rooms));
    }

    public void setCurrentRoom(UUID id) {
        if (id == null) {
            this.currentRoom = null;
            return;
        }
        this.currentRoom = getRoom(id);
    }

    public EscapeRoom getCurrentRoom() {
        return currentRoom;
    }

    public boolean isRoomCompleted(UUID id) {
        EscapeRoom r = getRoom(id);
        if (r == null) return false;
        return r.isCompleted();
    }

    public void resetRooms() {
        rooms.clear();
        currentRoom = null;
        totalRooms = 0;
    }

    public void loadRoomsFromFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
    }

    public void saveRoomsToFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
    }

    public int getTotalRooms() {
        return totalRooms;
    }
}
