package com.model;

import java.util.List;
import java.util.UUID;

public class EscapeRoom 
{
    private UUID roomID;
    private String name;
    private String description;
    private List<Puzzle> puzzles;
    private List<String> exits;

    public EscapeRoom() 
    {
    }

    public EscapeRoom(UUID roomID, String name, String description, List<Puzzle> puzzles, List<String> exits) 
    {
    }

    public UUID getRoomID() 
    {
        return null;
    }

    public String getName() 
    {
        return null;
    }

    public String getDescription() 
    {
        return null;
    }

    public List<Puzzle> getPuzzles() 
    {
        return null;
    }

    public List<String> getExits() 
    {
        return null;
    }

    public void loadRoom(UUID id) 
    {
    }

    public boolean isCompleted() 
    {
        return false;
    }
}
