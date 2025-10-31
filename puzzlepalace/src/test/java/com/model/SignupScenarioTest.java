package com.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SignupScenarioTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void executeCreatesUserAndPersistsToFile() throws Exception {
        File file = temp.newFile("users.json");
        SignupScenario scenario = new SignupScenario(file.getAbsolutePath());

        assertTrue(scenario.execute("NewSignup", "Password123!"));

        List<Player> players = DataLoader.loadUsers(file.getAbsolutePath());
        boolean found = players.stream().anyMatch(p -> "NewSignup".equalsIgnoreCase(p.getUsername()));
        assertTrue(found);
    }

    @Test
    public void executeFailsWhenAccountCreationFails() throws Exception {
        File file = temp.newFile("users.json");
        SignupScenario scenario = new SignupScenario(file.getAbsolutePath());

        assertFalse(scenario.execute(null, "Password123!"));
    }
}