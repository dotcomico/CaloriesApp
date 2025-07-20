package com.example.calories.data.models;

import androidx.annotation.NonNull;

public class MeasurementUnit {
    private String id;
    private String name;
    private boolean isCustom;
    private int position;

    public MeasurementUnit(String id, String name, boolean isCustom, int position) {
        this.id = id;
        this.name = name;
        this.isCustom = isCustom;
        this.position = position;
    }

    public MeasurementUnit(String name, boolean isCustom) {
        this.id = generateId();
        this.name = name;
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
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        MeasurementUnit that = (MeasurementUnit) obj;
        return id.equals(that.id);
    }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
}