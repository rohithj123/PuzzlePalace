package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DataLoaderTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void loadUsersReturnsEmptyListWhenFileMissing() {
        List<Player> players = DataLoader.loadUsers("non-existent-file.json");
        assertTrue(players.isEmpty());
    }

    @Test
    public void loadUsersParsesPlayerScoreAndProgress() throws IOException {
        File jsonFile = temp.newFile("players.json");
        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write("[\n" +
                    "  {\n" +
                    "    \"username\": \"Alice\",\n" +
                    "    \"email\": \"alice@example.com\",\n" +
                    "    \"password\": \"secret\",\n" +
                    "    \"passwordHash\": \"HASHED\",\n" +
                    "    \"score\": {\"points\": 50, \"puzzlesSolved\": 2, \"hintsUsed\": 1, \"timeTaken\": 120},\n" +
                    "    \"progressLog\": [\n" +
                    "      {\n" +
                    "        \"puzzleId\": 1,\n" +
                    "        \"question\": \"Riddle\",\n" +
                    "        \"answer\": \"42\",\n" +
                    "        \"status\": \"SOLVED\",\n" +
                    "        \"lastUpdated\": \"" + LocalDateTime.now().toString() + "\",\n" +
                    "        \"hintsUsed\": [\"Hint A\", \"Hint B\"]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"puzzleId\": 2,\n" +
                    "        \"question\": \"Puzzle\",\n" +
                    "        \"answer\": \"\",\n" +
                    "        \"status\": \"UNSOLVED\",\n" +
                    "        \"lastUpdated\": \"not-a-date\",\n" +
                    "        \"hintsUsed\": [\"Hint C\"]\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "]");
        }

        List<Player> players = DataLoader.loadUsers(jsonFile.getAbsolutePath());
        assertEquals(1, players.size());

        Player alice = players.get(0);
        assertEquals("Alice", alice.getUsername());
        assertEquals("alice@example.com", alice.getEmail());
        assertFalse(alice.isGuest());

        Score score = alice.getScoreDetails();
        assertNotNull(score);
        assertEquals(50, score.getPoints());
        assertEquals(2, score.getPuzzlesSolved());
        // Progress history should increase hints used to 3 (two from puzzle 1 and one from puzzle 2)
        assertEquals(3, score.getHintsUsed());
        assertEquals(120, score.getTimeTaken());

        List<PuzzleProgressSnapshot> history = alice.getPuzzleProgressSnapshots();
        assertEquals(2, history.size());
        assertTrue(history.stream().anyMatch(PuzzleProgressSnapshot::isSolved));
        assertEquals(3, alice.getTotalHintsUsedFromHistory());
    }
}
