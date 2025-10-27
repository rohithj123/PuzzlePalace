package com.model;

/**
 * This represents an item in the game.
 * This stores the item's ID and name.
 */
public class Item {
    private int id;
    private String name;

    /**
     * This creates a blank item.
     */
    public Item() {}

    /**
     * This creates an item with an ID and name.
     *
     * @param id the ID of the item
     * @param name the name of the item
     */
    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * This returns the ID of the item.
     *
     * @return the item ID
     */
    public int getId() {
        return id;
    }

    /**
     * This returns the name of the item.
     *
     * @return the item name
     */
    public String getName() {
        return name;
    }
    /**
     * This returns a string version of the item.
     *
     * @return a string with item details
     */

    @Override
    public String toString() {
        return "Item{id=" + id + ", name=" + name + "}";
    }
    
}
