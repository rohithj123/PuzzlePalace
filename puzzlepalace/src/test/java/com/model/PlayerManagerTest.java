package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PlayerManagerTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void addPlayerRejectsDuplicateUsernames() {
        PlayerManager manager = new PlayerManager();
        assertTrue(manager.addPlayer(new Player("Riley", null, "pw")));
        assertFalse(manager.addPlayer(new Player("Riley", null, "pw2")));
    }

    @Test
    public void authenticateVerifiesPassword() {
        PlayerManager manager = new PlayerManager();
        Player player = new Player("Jordan", null, "topsecret");
        manager.addPlayer(player);

        assertNull(manager.authenticate("Jordan", "wrong"));
        assertNotNull(manager.authenticate("Jordan", "topsecret"));
    }

    @Test
    public void updatePlayerReplacesExistingInstance() {
        PlayerManager manager = new PlayerManager();
        Player original = new Player("Morgan", null, "pw");
        manager.addPlayer(original);

        Player replacement = new Player("Morgan", null, "pw");
        // force same ID via reflection
        UUID id = original.getPlayerID();
        try {
            java.lang.reflect.Field idField = Player.class.getDeclaredField("playerID");
            idField.setAccessible(true);
            idField.set(replacement, id);
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        assertTrue(manager.updatePlayer(replacement));
        assertEquals(replacement, manager.getPlayerById(id));
    }

    @Test
    public void loadPlayersFromFileReplacesCurrentList() throws Exception {
        PlayerManager manager = new PlayerManager();
        manager.addPlayer(new Player("Existing", null, "pw"));

        File file = temp.newFile("players.json");
        List<Player> players = List.of(new Player("Taylor", null, "pw"));
        DataWriter.saveUsers(players, file.getAbsolutePath());

        List<Player> loaded = manager.loadPlayersFromFile(file.getAbsolutePath());
        assertEquals(1, loaded.size());
        assertNotNull(manager.getPlayerByUsername("Taylor"));
    }
}
