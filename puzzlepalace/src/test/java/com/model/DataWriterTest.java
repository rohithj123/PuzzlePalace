package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DataWriterTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void saveUsersWritesFullPlayerDetailsIncludingScoreAndProgress() throws IOException, ParseException {
        Player player = new Player("Explorer", "explorer@example.com", "secret");
        player.applyScoreData(250, 4, 1, 360);

        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 10, 15, 30);
        PuzzleProgressSnapshot snapshot = new PuzzleProgressSnapshot(
                12,
                "Solve the riddle",
                "Answer",
                "SOLVED",
                Arrays.asList("First hint", "Second hint"),
                timestamp
        );
        player.replaceProgressHistory(Arrays.asList(snapshot));

        File output = temp.newFile("players.json");
        DataWriter.saveUsers(Arrays.asList(player), output.getAbsolutePath());

        JSONArray root = parseJson(output);
        assertEquals(1, root.size());

        JSONObject entry = (JSONObject) root.get(0);
        assertEquals(player.getPlayerID().toString(), entry.get("playerID"));
        assertEquals("Explorer", entry.get("username"));
        assertEquals("explorer@example.com", entry.get("email"));
        assertFalse((Boolean) entry.get("isGuest"));

        JSONObject scoreObj = (JSONObject) entry.get("score");
        assertNotNull(scoreObj);
        assertEquals(250L, scoreObj.get("points"));
        assertEquals(4L, scoreObj.get("puzzlesSolved"));
        assertEquals(1L, scoreObj.get("hintsUsed"));
        assertEquals(360L, scoreObj.get("timeTaken"));

        JSONArray history = (JSONArray) entry.get("progressLog");
        assertEquals(1, history.size());
        JSONObject historyEntry = (JSONObject) history.get(0);
        assertEquals(12L, historyEntry.get("puzzleId"));
        assertEquals("Solve the riddle", historyEntry.get("question"));
        assertEquals("Answer", historyEntry.get("answer"));
        assertEquals("SOLVED", historyEntry.get("status"));
        assertEquals(timestamp.toString(), historyEntry.get("lastUpdated"));

        JSONArray hintsUsed = (JSONArray) historyEntry.get("hintsUsed");
        assertEquals(Arrays.asList("First hint", "Second hint"), new ArrayList<>(hintsUsed));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void saveUsersFallsBackToFieldsWhenGettersFailAndCreatesDirectories() throws Exception {
        Player player = new Player("Fallback", "", null) {
            @Override
            public String getEmail() {
                throw new RuntimeException("email getter failure");
            }
        };
        player.setEmail("fallback@example.com");
        player.setStoredPasswordHash("hash-value");
        player.applyScoreData(10, 1, 0, 45);

        // Inject null snapshot and a valid snapshot directly into the progress map
        Field progressField = Player.class.getDeclaredField("puzzleProgress");
        progressField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Integer, PuzzleProgressSnapshot> progress = (Map<Integer, PuzzleProgressSnapshot>) progressField.get(player);
        progress.put(1, null);
        PuzzleProgressSnapshot keptSnapshot = new PuzzleProgressSnapshot(
                2,
                "Door code",
                "1234",
                "ATTEMPTED",
                Arrays.asList("Look around"),
                LocalDateTime.of(2023, 5, 6, 7, 8, 9)
        );
        progress.put(2, keptSnapshot);

        File nested = new File(temp.getRoot(), "nested/directory/players.json");
        DataWriter.saveUsers(Arrays.asList(player), nested.getAbsolutePath());

        assertTrue("Parent directories should be created", nested.getParentFile().exists());
        assertTrue("Output file should be created", nested.exists());

        JSONArray root = parseJson(nested);
        assertEquals(1, root.size());

        JSONObject entry = (JSONObject) root.get(0);
        assertEquals("Fallback", entry.get("username"));
        assertEquals("fallback@example.com", entry.get("email"));
        assertEquals("hash-value", entry.get("passwordHash"));
        assertFalse((Boolean) entry.get("isGuest"));

        JSONArray history = (JSONArray) entry.get("progressLog");
        assertEquals(1, history.size());
        JSONObject historyEntry = (JSONObject) history.get(0);
        assertEquals(2L, historyEntry.get("puzzleId"));
        assertEquals("Door code", historyEntry.get("question"));
        assertEquals("1234", historyEntry.get("answer"));
        assertEquals("ATTEMPTED", historyEntry.get("status"));
        assertEquals(keptSnapshot.getLastUpdated().toString(), historyEntry.get("lastUpdated"));
    }

    private JSONArray parseJson(File file) throws IOException, ParseException {
        try (FileReader reader = new FileReader(file)) {
            Object parsed = new JSONParser().parse(reader);
            return (JSONArray) parsed;
        }
    }
}