package com.model;

import java.nio.file.Files;
import java.nio.file.Path;


public class LoginScenario {

    private final PuzzlePalaceFacade facade;
    private final String userDataPath;

    public LoginScenario(String userDataPath) {
        this.userDataPath = userDataPath;
        this.facade = new PuzzlePalaceFacade(userDataPath);
    }

    public LoginScenario() {
        this("json/users.json");
    }

    /**
     * should execute login path.
     *
     * @param username login username
     * @param password login password
     * @return {@code true} when login works, progress is saves and the
     *         player can log in again after logging out
     */
    public boolean execute(String username, String password) {
        Player loggedIn = facade.login(username, password);
        if (loggedIn == null) {
            return false;
        }

        facade.saveCurrentPlayerProgress();

        Path progressFile = ProgressFileLocator.forPlayer(loggedIn);
        boolean progressSaved = Files.exists(progressFile);

        facade.logout();

        Player secondLogin = facade.login(username, password);
        boolean canRelog = secondLogin != null && secondLogin.getUsername() != null
                && secondLogin.getUsername().equalsIgnoreCase(username);

        facade.logout();

        boolean persisted = containsUser(DataLoader.loadUsers(userDataPath), username);

        return progressSaved && canRelog && persisted;
    }

    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "json/users.json";
        String username = args.length > 1 ? args[1] : "PlayerOne";
        String password = args.length > 2 ? args[2] : "SecretPass1!";

        LoginScenario scenario = new LoginScenario(path);
        boolean success = scenario.execute(username, password);
        if (success) {
            System.out.println("Login/logout scenario succeeded for " + username);
        } else {
            System.out.println("Login/logout scenario failed for " + username);
        }
    }

    /**
     * Helper to produce the  save path without duplicating the logic in
     * {@link Progress}.
     */
    private static final class ProgressFileLocator {
        private ProgressFileLocator() {
        }

        static Path forPlayer(Player player) {
            String fileName = "data/progress-default.txt";
            if (player != null && player.getPlayerID() != null) {
                fileName = "data/progress-" + player.getPlayerID() + ".txt";
            }
            return Path.of(fileName);
        }
    }

    private boolean containsUser(Iterable<Player> players, String username) {
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
}
