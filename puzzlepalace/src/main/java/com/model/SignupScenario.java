package com.model;

import java.util.List;
import java.util.UUID;

/**
 * Test scenario for creating a new user account.
 * Verifies that a signup works and data is saved properly.
 */
public class SignupScenario {

    private final PuzzlePalaceFacade facade;
    private final String userDataPath;

    /**
     * Creates a signup scenario using a custom file path.
     *
     * @param userDataPath path to the user data file
     */
    public SignupScenario(String userDataPath) {
        this.userDataPath = userDataPath;
        this.facade = new PuzzlePalaceFacade(userDataPath);
    }

        /** Creates a signup scenario with the default user data path. */
    public SignupScenario() {
        this("out/signup-scenario-users.json");
    }

    /**
     * Runs the signup process and checks if it succeeded.
     *
     * @param username username to register
     * @param password password to register
     * @return true if the account was created and saved
     */
    public boolean execute(String username, String password) {
        Player newPlayer = facade.createAccount(username, password);
        if (newPlayer == null) {
            return false;
        }

        boolean inFacadeList = containsUser(facade.getUserList(), username);

        facade.logout();

        List<Player> persistedUsers = DataLoader.loadUsers(userDataPath);
        boolean persisted = containsUser(persistedUsers, username);

        return inFacadeList && persisted;
    }

        /** Checks if a username exists in the player list. */
    private boolean containsUser(List<Player> players, String username) {
        if (players == null || username == null) {
            return false;
        }
        for (Player player : players) {
            if (player != null && player.getUsername() != null
                && player.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
/**
     * Runs a quick test of the signup scenario.
     *
     * @param args optional user data path
     */
    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "out/signup-scenario-users.json";
        SignupScenario scenario = new SignupScenario(path);
        String username = "User-" + UUID.randomUUID().toString().substring(0, 8);
        boolean success = scenario.execute(username, "Password123!");
        System.out.println(success
            ? "Signup scenario succeeded for " + username
            : "Signup scenario failed for " + username);
    }
}