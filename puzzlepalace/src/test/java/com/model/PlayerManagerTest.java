package com.model;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PlayerManagerTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void addPlayer_rejectsDuplicateUsernames() {
        PlayerManager manager = new PlayerManager();
        assertTrue(manager.addPlayer(new Player("Riley", null, "pw")));
        assertFalse(manager.addPlayer(new Player("Riley", null, "pw2")));
    }

    @Test
    public void authenticate_verifiesPasswordCorrectly() {
        PlayerManager manager = new PlayerManager();
        Player player = new Player("Jordan", null, "topsecret");
        manager.addPlayer(player);

        assertNull(manager.authenticate("Jordan", "wrong"));
        assertNotNull(manager.authenticate("Jordan", "topsecret"));
    }

    @Test
    public void updatePlayer_replacesExistingInstance() {
        PlayerManager manager = new PlayerManager();
        Player original = new Player("Morgan", null, "pw");
        manager.addPlayer(original);

        Player replacement = new Player("Morgan", null, "pw");
        UUID id = original.getPlayerID();
        try {
            java.lang.reflect.Field idField = Player.class.getDeclaredField("playerID");
            idField.setAccessible(true);
            idField.set(replacement, id);
        } catch (Exception e) {
            fail("Reflection to copy player ID failed: " + e.getMessage());
        }

        assertTrue(manager.updatePlayer(replacement));
        assertEquals(replacement, manager.getPlayerById(id));
    }

    @Test
    public void loadPlayersFromFile_replacesCurrentList() throws Exception {
        PlayerManager manager = new PlayerManager();
        manager.addPlayer(new Player("Existing", null, "pw"));

        File file = temp.newFile("players.json");
        List<Player> players = List.of(new Player("Taylor", null, "pw"));
        DataWriter.saveUsers(players, file.getAbsolutePath());

        List<Player> loaded = manager.loadPlayersFromFile(file.getAbsolutePath());
        assertEquals(1, loaded.size());
        assertNotNull(manager.getPlayerByUsername("Taylor"));
    }

    @Test
    public void getPlayerByUsername_returnsCorrectPlayer() {
        PlayerManager manager = new PlayerManager();
        Player a = new Player("Alex", null, "123");
        Player b = new Player("Jamie", null, "123");
        manager.addPlayer(a);
        manager.addPlayer(b);

        assertEquals(a, manager.getPlayerByUsername("Alex"));
        assertEquals(b, manager.getPlayerByUsername("Jamie"));
        assertNull(manager.getPlayerByUsername("Unknown"));
    }

    @Test
    public void removePlayer_removesCorrectlyById() {
        PlayerManager manager = new PlayerManager();
        Player player = new Player("Sam", null, "pw");
        manager.addPlayer(player);

        UUID id = player.getPlayerID();
        assertNotNull(manager.getPlayerById(id));

        Player playerToRemove = manager.getPlayerById(id);
        manager.removePlayer(playerToRemove);
        assertNull(manager.getPlayerById(id));
    }
}
