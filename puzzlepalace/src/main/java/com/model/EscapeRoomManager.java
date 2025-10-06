package com.model;

import java.util.List;
import java.util.UUID;

public class EscapeRoomManager 
{
    private List<EscapeRoom> rooms;
    private EscapeRoom currentRoom;
    private int totalRooms;

    private static EscapeRoomManager instance;

    private EscapeRoomManager()
    {
    }

    public static EscapeRoomManager getInstance() 
    {
        return null;
    }

    public void addRoom(EscapeRoom room) 
    {
    }

    public void removeRoom(UUID id) 
    {
    }

    public EscapeRoom getRoom(UUID id) 
    {
        return null;
    }

    public List<EscapeRoom> getAllRooms() 
    {
        return null;
    }

    public void setCurrentRoom(UUID id) 
    {
    }

    public EscapeRoom getCurrentRoom() 
    {
        return null;
    }

    public boolean isRoomCompleted(UUID id) 
    {
        return false;
    }

    public void resetRooms() 
    {
    }

    public void loadRoomsFromFile(String filePath) 
    {
    }

    public void saveRoomsToFile(String filePath) 
    {
    }
}

