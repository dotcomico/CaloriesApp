package com.example.calories.data.models;

import androidx.annotation.NonNull;

public class Unit {
    private String id;
    private String name;
    private boolean isCustom;
    private int position;

    public Unit(String id, String name, boolean isCustom, int position) {
        this.id = id.trim();
        this.name = name.trim();
        this.isCustom = isCustom;
        this.position = position;
    }

    public Unit(String name, boolean isCustom) {
        this.id = generateId();
        this.name = name.trim();
        this.isCustom = isCustom;
        this.position = 0;
    }

    private String generateId() {
        return "unit_" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Unit that = (Unit) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}