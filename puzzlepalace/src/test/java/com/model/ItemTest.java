package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ItemTest {

    @Test
    public void toStringDisplaysIdAndName() {
        Item item = new Item(7, "Golden Key");
        assertEquals("Item{id=7, name=Golden Key}", item.toString());
    }

    @Test
    public void playersInventoryTreatsItemsWithSameIdAsMatching() {
        Player player = new Player("Collector", "collector@example.com", "pw");
        Item stored = new Item(101, "Ancient Relic");
        Item probe = new Item(101, "Any Name");

        assertTrue(player.addItem(stored));
        assertTrue(player.hasItem(probe));
    }

    @Test
    public void playersInventoryMatchesItemsWithSameNameWhenIdDiffers() {
        Player player = new Player("Archivist", "archivist@example.com", "pw");
        Item stored = new Item(1, "Mysterious Map");
        Item probe = new Item(2, "Mysterious Map");

        assertTrue(player.addItem(stored));
        assertTrue(player.hasItem(probe));
    }
}