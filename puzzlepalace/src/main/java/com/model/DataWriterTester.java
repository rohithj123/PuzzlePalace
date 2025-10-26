package com.model;

import java.util.ArrayList;
import java.util.List;
/**
 * This tests the DataWriter class.
 * This creates sample player data and writes it to a JSON file.
 */

public class DataWriterTester 
{
    /**
     * This runs the test for DataWriter.
     * This creates a few players, saves them to a file, and prints a message.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) 
    {
        String filePath = "data/users.json"; 

        List<Player> players = new ArrayList<>();
        players.add(new Player("alice", "alice@example.com", "password1"));
        players.add(new Player("bob", "bob@example.com", "password2"));
        players.add(new Player("guest", "guest@example.com", "password3"));

        DataWriter.saveUsers(players, filePath);

        System.out.println("Wrote " + players.size() + " players to " + filePath);
    }
}
