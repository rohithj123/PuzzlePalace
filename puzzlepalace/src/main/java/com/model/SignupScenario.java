package com.model;

import java.util.List;
import java.util.UUID;

public class SignupScenario {

    private final PuzzlePalaceFacade facade;
    private final String userDataPath;

    public SignupScenario(String userDataPath) {
        this.userDataPath = userDataPath;
        this.facade = new PuzzlePalaceFacade(userDataPath);
    }

    public SignupScenario() {
        this("out/signup-scenario-users.json");
    }

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