package com.example.calories.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.calories.data.models.MeasurementUnit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MeasurementManager {
    private static final String PREF_NAME = "measurement_units";
    private static final String KEY_CUSTOM_UNITS = "custom_units";
    private static MeasurementManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    private MeasurementManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized MeasurementManager getInstance(Context context) {
        if (instance == null) {
            instance = new MeasurementManager(context.getApplicationContext());
        }
        return instance;
    }

    public List<MeasurementUnit> getDefaultUnits() {
        List<MeasurementUnit> defaultUnits = new ArrayList<>();
        defaultUnits.add(new MeasurementUnit("default_1", "100 גרם", false, 0));
        defaultUnits.add(new MeasurementUnit("default_2", "יחידה", false, 1));
        defaultUnits.add(new MeasurementUnit("default_3", "כף", false, 2));
        defaultUnits.add(new MeasurementUnit("default_4", "כפית", false, 3));
        defaultUnits.add(new MeasurementUnit("default_5", "כוס", false, 4));
        defaultUnits.add(new MeasurementUnit("default_6", "פרוסה", false, 5));
        return defaultUnits;
    }

    public List<MeasurementUnit> getCustomUnits() {
        String json = sharedPreferences.getString(KEY_CUSTOM_UNITS, "[]");
        Type type = new TypeToken<List<MeasurementUnit>>(){}.getType();
        List<MeasurementUnit> customUnits = gson.fromJson(json, type);
        return customUnits != null ? customUnits : new ArrayList<>();
    }

    public List<MeasurementUnit> getAllUnits() {
        List<MeasurementUnit> allUnits = new ArrayList<>();
        allUnits.addAll(getDefaultUnits());
        allUnits.addAll(getCustomUnits());
        return allUnits;
    }

    public void saveCustomUnit(MeasurementUnit unit) {
        List<MeasurementUnit> customUnits = getCustomUnits();
        // בדיקה אם המדד כבר קיים
        for (MeasurementUnit existing : customUnits) {
            if (existing.getName().equals(unit.getName())) {
                return; // כבר קיים
            }
        }

        unit.setPosition(getDefaultUnits().size() + customUnits.size());
        customUnits.add(unit);
        saveCustomUnits(customUnits);
    }

    public void removeCustomUnit(MeasurementUnit unit) {
        List<MeasurementUnit> customUnits = getCustomUnits();
        customUnits.removeIf(u -> u.getId().equals(unit.getId()));
        saveCustomUnits(customUnits);
    }

    private void saveCustomUnits(List<MeasurementUnit> customUnits) {
        String json = gson.toJson(customUnits);
        sharedPreferences.edit().putString(KEY_CUSTOM_UNITS, json).apply();
    }

    public boolean isUnitNameExists(String name) {
        List<MeasurementUnit> allUnits = getAllUnits();
        for (MeasurementUnit unit : allUnits) {
            if (unit.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}