package com.model;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DataLoaderTesterTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void mainPrintsLoadedPlayers() throws Exception {
        File root = temp.getRoot();
        File dataDir = new File(root, "data");
        dataDir.mkdirs();
        File file = new File(dataDir, "users.json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("[{\"username\":\"TestUser\"}]");
        }

        String originalDir = System.getProperty("user.dir");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(buffer));
        try {
            System.setProperty("user.dir", root.getAbsolutePath());
            DataLoaderTester.main(new String[0]);
        } finally {
            System.setProperty("user.dir", originalDir);
            System.setOut(originalOut);
        }

        assertTrue(buffer.toString().contains("Loaded 1 players"));
    }
}