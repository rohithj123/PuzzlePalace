package com.model;

import java.util.List;

/**
 * This tests the DataLoader class.
 * This loads player data from a JSON file and prints the results.
 */

public class DataLoaderTester {
    /**
     * This runs the test for DataLoader.
     * This loads players from a JSON file and displays them in the console.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        String filePath = "data/users.json"; 
        List<Player> players = DataLoader.loadUsers(filePath);

        System.out.println("Loaded " + players.size() + " players:");
        for (Player p : players) {
            System.out.println(" - " + p);
        }
    }
}