package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This manages all the escape rooms in the game.
 * This handles adding, removing, loading, and tracking rooms.
 */
public class EscapeRoomManager {
    private final List<EscapeRoom> rooms;
    private EscapeRoom currentRoom;
    private int totalRooms;

    private static volatile EscapeRoomManager instance;
    /**
     * This creates a new EscapeRoomManager.
     * This initializes the list of rooms and sets the current room to null.
     */

    private EscapeRoomManager() {
        this.rooms = new ArrayList<>();
        this.currentRoom = null;
        this.totalRooms = 0;
    }
    /**
     * This returns the single instance of EscapeRoomManager.
     *
     * @return the EscapeRoomManager instance
     */
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

    /**
     * This adds a room to the manager.
     *
     * @param room the room to add
     */
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
    /**
     * This removes a room using its ID.
     *
     * @param id the ID of the room to remove
     */
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

    /**
     * This finds and returns a room by its ID.
     *
     * @param id the room ID
     * @return the EscapeRoom with the given ID, or null if not found
     */
    public EscapeRoom getRoom(UUID id) {
        if (id == null || rooms.isEmpty()) return null;
        for (EscapeRoom r : rooms) {
            if (r != null && id.equals(r.getRoomID())) {
                return r;
            }
        }
        return null;
    }

    /**
     * This returns a list of all rooms.
     *
     * @return an unmodifiable list of rooms
     */
    public List<EscapeRoom> getAllRooms() {
        return Collections.unmodifiableList(new ArrayList<>(rooms));
    }

     /**
     * This sets the current room using its ID.
     *
     * @param id the ID of the room to set as current
     */
    public void setCurrentRoom(UUID id) {
        if (id == null) {
            this.currentRoom = null;
            return;
        }
        this.currentRoom = getRoom(id);
    }
    /**
     * This returns the current room.
     *
     * @return the current EscapeRoom
     */
    public EscapeRoom getCurrentRoom() {
        return currentRoom;
    }
    /**
     * This checks if a room is completed.
     *
     * @param id the ID of the room
     * @return true if the room is completed, false otherwise
     */

    public boolean isRoomCompleted(UUID id) {
        EscapeRoom r = getRoom(id);
        if (r == null) return false;
        return r.isCompleted();
    }
    /**
     * This clears all rooms from the manager.
     */
    public void resetRooms() {
        rooms.clear();
        currentRoom = null;
        totalRooms = 0;
    }
    /**
     * This loads rooms from a file.
     *
     * @param filePath the path to the file
     */
    public void loadRoomsFromFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
    }

    /**
     * This saves rooms to a file.
     *
     * @param filePath the path to the file
     */
    public void saveRoomsToFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
    }
    /**
     * This returns the total number of rooms.
     *
     * @return the total number of rooms
     */
    public int getTotalRooms() {
        return totalRooms;
    }
}
