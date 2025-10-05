package com.model;

public class Item {
    private int id;
    private String name;

    public Item() {}

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Item{id=" + id + ", name=" + name + "}";
    }
    
}
