

package com.model;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DataLoader {

    // Static method so you can call DataLoader.loadUsers("file.json")
    public static List<Player> loadUsers(String filePath) {
        List<Player> players = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("DataLoader: file not found at " + filePath + " -> returning empty list");
            return players;
        }

        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(f)) {
            Object parsed = parser.parse(reader);
            if (!(parsed instanceof JSONArray)) {
                System.out.println("DataLoader: JSON root is not an array -> returning empty list");
                return players;
            }

            JSONArray arr = (JSONArray) parsed;
            for (Object o : arr) {
                if (!(o instanceof JSONObject)) continue;
                JSONObject jo = (JSONObject) o;

                String username = jo.get("username") != null ? jo.get("username").toString() : "guest";
                String email = jo.get("email") != null ? jo.get("email").toString() : null;

                String rawPassword = jo.get("password") != null ? jo.get("password").toString() : null;
                String passwordHash = jo.get("passwordHash") != null ? jo.get("passwordHash").toString() : null;

                Player p = new Player(username, email, rawPassword);
                if (passwordHash != null && !passwordHash.isEmpty()) {
                    p.setStoredPasswordHash(passwordHash);
                }

                JSONObject scoreObj = jo.get("score") instanceof JSONObject ? (JSONObject) jo.get("score") : null;
                if (scoreObj != null) {
                    int points = parseInt(scoreObj.get("points"));
                    int puzzlesSolved = parseInt(scoreObj.get("puzzlesSolved"));
                    int hintsUsed = parseInt(scoreObj.get("hintsUsed"));
                    int timeTaken = parseInt(scoreObj.get("timeTaken"));
                    p.applyScoreData(points, puzzlesSolved, hintsUsed, timeTaken);
                }

                JSONArray historyArray = jo.get("progressLog") instanceof JSONArray ? (JSONArray) jo.get("progressLog") : null;
                if (historyArray != null) {
                    List<PuzzleProgressSnapshot> snapshots = new ArrayList<>();
                    for (Object entryObj : historyArray) {
                        if (!(entryObj instanceof JSONObject)) {
                            continue;
                        }
                        JSONObject entry = (JSONObject) entryObj;
                        int puzzleId = parseInt(entry.get("puzzleId"));
                        String question = entry.get("question") != null ? entry.get("question").toString() : "";
                        String status = entry.get("status") != null ? entry.get("status").toString() : null;
                        String answer = entry.get("answer") != null ? entry.get("answer").toString() : "";
                        LocalDateTime lastUpdated = parseDateTime(entry.get("lastUpdated"));
                        List<String> hints = new ArrayList<>();
                        Object hintsObj = entry.get("hintsUsed");
                        if (hintsObj instanceof JSONArray) {
                            JSONArray hintsArray = (JSONArray) hintsObj;
                            for (Object hint : hintsArray) {
                                if (hint != null) {
                                    hints.add(hint.toString());
                                }
                            }
                        }
                        snapshots.add(new PuzzleProgressSnapshot(puzzleId, question, answer, status, hints, lastUpdated));
                    }
                    if (!snapshots.isEmpty()) {
                        p.replaceProgressHistory(snapshots);
                        Score score = p.getScoreDetails();
                        if (score != null) {
                            score.setPuzzlesSolved(Math.max(score.getPuzzlesSolved(), p.getSolvedPuzzleCountFromHistory()));
                            score.setHintsUsed(Math.max(score.getHintsUsed(), p.getTotalHintsUsedFromHistory()));
                        }
                    }
                }
                players.add(p);
            }

            System.out.println("DataLoader: loaded " + players.size() + " players from " + filePath);
        } catch (ParseException pe) {
            System.out.println("DataLoader: parse error: " + pe.getMessage());
        } catch (Exception e) {
            System.out.println("DataLoader: IO error: " + e.getMessage());
        }

        return players;
    }

    private static int parseInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static LocalDateTime parseDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        try {
            return LocalDateTime.parse(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
