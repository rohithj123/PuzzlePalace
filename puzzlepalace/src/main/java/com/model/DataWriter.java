package com.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DataWriter {

    private DataWriter() {
    }

    public static void saveUsers(List<Player> players, String filePath) {
        JSONArray playerArray = new JSONArray();

        for (Player p : players) {
            String username = safeGetString(p, "getUsername", "username");
            String email = safeGetString(p, "getEmail", "email");

            JSONObject playerObj = new JSONObject();
            playerObj.put("username", username);
            playerObj.put("email", email);
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
