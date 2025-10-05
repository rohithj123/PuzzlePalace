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

    public EscapeRoom(UUID roomID, String name, String description,
            List<Puzzle> puzzles, List<String> exits) 
    {
        this.roomID = roomID;
        this.name = name;
        this.description = description;
        this.puzzles = puzzles;
        this.exits = exits;
    }

    public UUID getRoomID() 
    {
        return roomID;
    }

    public String getName() 
    {
        return name;
    }

    public String getDescription() 
    {
        return description;
    }

    public List<Puzzle> getPuzzles() 
    {
        return puzzles;
    }

    public List<String> getExits() 
    {
        return exits;
    }

    public void loadRoom(UUID id) 
    {
        System.out.println("loadRoom() called with id: " + id + " (stub)");
    }

    public boolean isCompleted() 
    {
        System.out.println("isCompleted() called (stub)");
        return false;
    }

    @Override
    public String toString() 
    {
        return "EscapeRoom{" +
                "roomID=" + roomID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", puzzles=" + (puzzles != null ? puzzles.size() : 0) +
                ", exits=" + exits +
                '}';
    }
}
