package com.model;

import java.util.List;

public class DataLoaderTester {
    public static void main(String[] args) {
        String filePath = "data/users.json"; // adjust path for your project
        List<Player> players = DataLoader.loadUsers(filePath);

        System.out.println("Loaded " + players.size() + " players:");
        for (Player p : players) {
            System.out.println(" - " + p);
        }
    }
}