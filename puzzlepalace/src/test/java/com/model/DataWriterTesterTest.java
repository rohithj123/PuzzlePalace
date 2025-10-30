package com.model;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DataWriterTesterTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void mainCreatesJsonFileAndPrintsMessage() throws Exception {
        File root = temp.getRoot();
        String originalDir = System.getProperty("user.dir");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(buffer));
        try {
            System.setProperty("user.dir", root.getAbsolutePath());
            DataWriterTester.main(new String[0]);
        } finally {
            System.setProperty("user.dir", originalDir);
            System.setOut(originalOut);
        }

        File expected = new File(root, "data/users.json");
        assertTrue(expected.exists());
        String json = Files.readString(expected.toPath());
        assertTrue(json.contains("alice"));
        assertTrue(buffer.toString().contains("Wrote 3 players"));
    }
}