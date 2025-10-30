package com.model;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LoginScenarioTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void executeReturnsFalseWhenAuthenticationFails() throws Exception {
        String path = temp.newFile("users.json").getAbsolutePath();
        LoginScenario scenario = new LoginScenario(path);

        assertFalse(scenario.execute("Nope", "bad"));
    }

    @Test
    public void containsUserHandlesNullsAndMatchesIgnoringCase() throws Exception {
        String path = temp.newFile("users.json").getAbsolutePath();
        LoginScenario scenario = new LoginScenario(path);

        Method containsUser = LoginScenario.class.getDeclaredMethod("containsUser", Iterable.class, String.class);
        containsUser.setAccessible(true);

        assertFalse((boolean) containsUser.invoke(scenario, null, "user"));
        assertTrue((boolean) containsUser.invoke(scenario,
                Arrays.asList(new Player("Casey", null, "pw")), "casey"));
        assertFalse((boolean) containsUser.invoke(scenario, Collections.singletonList(null), "casey"));
    }

    @Test
    public void progressFileLocatorUsesPlayerIdentifier() throws Exception {
        Class<?> locator = Class.forName("com.model.LoginScenario$ProgressFileLocator");
        Method forPlayer = locator.getDeclaredMethod("forPlayer", Player.class);
        forPlayer.setAccessible(true);

        Player player = new Player("Terry", "t@example.com", "secret");
        Path specific = (Path) forPlayer.invoke(null, player);
        assertTrue(specific.toString().contains(player.getPlayerID().toString()));

        Path defaultPath = (Path) forPlayer.invoke(null, (Object) null);
        assertEquals("data/progress-default.txt", defaultPath.toString());
    }
}