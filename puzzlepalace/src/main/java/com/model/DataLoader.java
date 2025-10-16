

package com.model;

import java.io.File;
import java.io.FileReader;
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
}
