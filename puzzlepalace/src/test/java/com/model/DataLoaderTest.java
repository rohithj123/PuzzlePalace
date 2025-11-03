package com.model;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DataLoaderTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void loadUsers_returnsEmptyList_whenFileMissing() {
        List<Player> players = DataLoader.loadUsers("does-not-exist.json");
        assertNotNull("Expected non-null list from DataLoader", players);
        assertTrue("Expected empty list when file missing", players.isEmpty());
    }

    @Test
    public void loadUsers_parsesValidJsonFileCorrectly() throws Exception {
        File jsonFile = temp.newFile("players.json");

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write("[{\n" +
                    "  \"username\": \"Alice\",\n" +
                    "  \"email\": \"alice@example.com\",\n" +
                    "  \"password\": \"secret\",\n" +
                    "  \"passwordHash\": \"HASHED\",\n" +
                    "  \"score\": {\"points\": 100, \"puzzlesSolved\": 3, \"hintsUsed\": 2, \"timeTaken\": 60},\n" +
                    "  \"progressLog\": [\n" +
                    "    {\"puzzleId\": 1, \"question\": \"Riddle\", \"answer\": \"42\", \"status\": \"SOLVED\", \"lastUpdated\": \"" + LocalDateTime.now() + "\", \"hintsUsed\": [\"Hint 1\"]}\n" +
                    "  ]\n" +
                    "}]");
        }

        List<Player> players = DataLoader.loadUsers(jsonFile.getAbsolutePath());
        assertEquals("Expected exactly one player parsed", 1, players.size());

        Player alice = players.get(0);
        assertEquals("Alice", alice.getUsername());
        assertEquals("alice@example.com", alice.getEmail());
        assertFalse("Player should not be marked as guest", alice.isGuest());

        Score score = alice.getScoreDetails();
        assertNotNull("Expected non-null score object", score);
        assertEquals(100, score.getPoints());
        assertEquals(3, score.getPuzzlesSolved());
        assertEquals(2, score.getHintsUsed());
        assertEquals(60, score.getTimeTaken());

        List<PuzzleProgressSnapshot> snapshots = alice.getPuzzleProgressSnapshots();
        assertEquals("Expected one progress snapshot", 1, snapshots.size());
        assertTrue("Snapshot should indicate solved puzzle", snapshots.get(0).isSolved());
    }
}
