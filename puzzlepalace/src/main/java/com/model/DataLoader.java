package com.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class DataLoader {

    private final String filePath;

    public DataLoader(String filePath) {
        this.filePath = filePath;
    }

   
    public List<Player> loadUsers() {
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

                String username = null;
                String email = null;

                Object u = jo.get("username");
                if (u != null) username = u.toString();

                Object e = jo.get("email");
                if (e != null) email = e.toString();

                // Use existing Player constructor (username, email). Fallback to "guest" if username missing.
                Player p = new Player(username == null ? "guest" : username, email);
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
