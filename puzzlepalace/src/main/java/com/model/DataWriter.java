package com.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * This class saves player data to a JSON file.
 * This helps store user progress, scores, and account information.
 */
public class DataWriter {
    /**
     * This prevents creating an instance of DataWriter.
     */
    private DataWriter() {
    }
    /**
     * This saves all player data to a given JSON file path.
     *
     * @param players the list of players to save
     * @param filePath the path of the JSON file to write to
     */
    public static void saveUsers(List<Player> players, String filePath) {
        JSONArray playerArray = new JSONArray();

        for (Player p : players) {
            String username = safeGetString(p, "getUsername", "username");
            String email = safeGetString(p, "getEmail", "email");
            String playerId = safeGetString(p, "getPlayerID", "playerID");
            String passwordHash = safeGetString(p, "getPasswordHash", "passwordHash");
            String guestFlag = safeGetString(p, "isGuest", "guest");

            JSONObject playerObj = new JSONObject();
            if (!playerId.isEmpty()) {
                playerObj.put("playerID", playerId);
            }
            playerObj.put("username", username);
            playerObj.put("email", email);
            if (!passwordHash.isEmpty()) {
                playerObj.put("passwordHash", passwordHash);
            }
            if (!guestFlag.isEmpty()) {
                playerObj.put("isGuest", Boolean.parseBoolean(guestFlag));
            }
            
            Score score = p.getScoreDetails();
            if (score != null) {
                JSONObject scoreObj = new JSONObject();
                scoreObj.put("points", score.getPoints());
                scoreObj.put("puzzlesSolved", score.getPuzzlesSolved());
                scoreObj.put("hintsUsed", score.getHintsUsed());
                scoreObj.put("timeTaken", score.getTimeTaken());
                playerObj.put("score", scoreObj);
            }

            JSONArray historyArray = new JSONArray();
            for (PuzzleProgressSnapshot snapshot : p.getPuzzleProgressSnapshots()) {
                if (snapshot == null) {
                    continue;
                }
                JSONObject entry = new JSONObject();
                entry.put("puzzleId", snapshot.getPuzzleId());
                entry.put("question", snapshot.getQuestion());
                entry.put("status", snapshot.getStatus());
                entry.put("answer", snapshot.getAnswer());
                entry.put("lastUpdated", snapshot.getLastUpdated().toString());
                JSONArray hintsArray = new JSONArray();
                for (String hint : snapshot.getHintsUsed()) {
                    hintsArray.add(hint);
                }
                entry.put("hintsUsed", hintsArray);
                historyArray.add(entry);
            }
            playerObj.put("progressLog", historyArray);

            playerArray.add(playerObj);
        }

        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(playerArray.toJSONString());
            writer.flush();
            System.out.println("DataWriter: wrote " + players.size() + " players to " + filePath);
        } catch (IOException e) {
            System.out.println("DataWriter: IO error while writing file: " + e.getMessage());
        }
    }
    /**
     * This safely gets a string value from a method or field.
     *
     * @param obj the object to inspect
     * @param methodName the name of the getter method
     * @param fieldName the name of the field
     * @return the value as a string, or an empty string if not found
     */
    private static String safeGetString(Object obj, String methodName, String fieldName) {
        if (obj == null) return "";

        try {
            Method m = obj.getClass().getMethod(methodName);
            Object val = m.invoke(obj);
            return val == null ? "" : val.toString();
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
        }

        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Object val = f.get(obj);
            return val == null ? "" : val.toString();
        } catch (NoSuchFieldException nsf) {
            try {
                Field alt = obj.getClass().getDeclaredField("_" + fieldName);
                alt.setAccessible(true);
                Object val = alt.get(obj);
                return val == null ? "" : val.toString();
            } catch (Exception ignored) {}
        } catch (Exception e) {
        }

        return "";
    }
}
