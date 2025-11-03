package com.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DataWriterTesterTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void main_createsJsonFile_and_printsMessage() throws Exception {
        File root = temp.getRoot();
        String originalDir = System.getProperty("user.dir");
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        System.setOut(new PrintStream(buffer));

        try {
            System.setProperty("user.dir", root.getAbsolutePath());

            DataWriterTester.main(new String[0]);

        } finally {
            System.setProperty("user.dir", originalDir);
            System.setOut(originalOut);
        }

        File expected = new File(root, "data/users.json");
        assertTrue("Expected users.json file to be created", expected.exists());

        String json = Files.readString(expected.toPath());
        assertNotNull("JSON file should not be empty", json);
        assertTrue("JSON should contain example player data", json.contains("alice"));

        String output = buffer.toString();
        assertTrue("Console output should mention writing players",
                output.contains("Wrote") && output.contains("players"));
    }
}
