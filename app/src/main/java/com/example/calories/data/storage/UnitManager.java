package com.example.calories.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.calories.R;
import com.example.calories.data.models.Unit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import static com.example.calories.utils.AppConstants.*;

public class UnitManager {
	private static UnitManager instance;
	private final SharedPreferences sharedPreferences;
	private final Gson gson;

	private static final int GRAMS_100_RID = R.drawable.t_grame;
	private static final int UNIT_RID = R.drawable.t_single;
	private static final int TABLESPOON_RID = R.drawable.t_tablespoon;
	private static final int TEASPOON_RID = R.drawable.t_teaspoon;
	private static final int CUP_RID = R.drawable.t_cup;
	private static final int SLICE_RID = R.drawable.t_slice;;

	private UnitManager(Context context) {
		sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		gson = new Gson();
	}

	public static synchronized UnitManager getInstance(Context context) {
		if (instance == null) {
			instance = new UnitManager(context.getApplicationContext());
		}
		return instance;
	}

	public List<Unit> getDefaultUnits() {
		List<Unit> defaultUnits = new ArrayList<>();
		defaultUnits.add(new Unit("default_1", UNIT_100_GRAM, false, 0));
		defaultUnits.add(new Unit("default_2", UNIT_UNIT, false, 1));
		defaultUnits.add(new Unit("default_3", UNIT_TABLESPOON, false, 2));
		defaultUnits.add(new Unit("default_4", UNIT_TEASPOON, false, 3));
		defaultUnits.add(new Unit("default_5", UNIT_CUP, false, 4));
		defaultUnits.add(new Unit("default_6", UNIT_SLICE, false, 5));
		return defaultUnits;
	}

	public List<Unit> getCustomUnits() {
		String json = sharedPreferences.getString(KEY_CUSTOM_UNITS, "[]");
		Type type = new TypeToken<List<Unit>>() {
		}.getType();
		List<Unit> customUnits = gson.fromJson(json, type);
		return customUnits != null ? customUnits : new ArrayList<>();
	}

	public List<Unit> getAllUnits() {
		List<Unit> allUnits = new ArrayList<>();
		allUnits.addAll(getDefaultUnits());
		allUnits.addAll(getCustomUnits());
		return allUnits;
	}

	public static int getUnitImageResId(String unit) {
		switch (unit) {
		case UNIT_100_GRAM:
			return GRAMS_100_RID;
		case UNIT_UNIT:
			return UNIT_RID;
		case UNIT_TABLESPOON:
			return TABLESPOON_RID;
		case UNIT_TEASPOON:
			return TEASPOON_RID;
		case UNIT_CUP:
			return CUP_RID;
		case UNIT_SLICE:
			return SLICE_RID;
		}

		return 0;
	}

	public void saveCustomUnit(Unit unit) {
		List<Unit> customUnits = getCustomUnits();
		// בדיקה אם המדד כבר קיים
		for (Unit existing : customUnits) {
			if (existing.getName().equals(unit.getName())) {
				return; // כבר קיים
			}
		}

		unit.setPosition(getDefaultUnits().size() + customUnits.size());
		customUnits.add(unit);
		saveCustomUnits(customUnits);
	}

	public void removeCustomUnit(Unit unit) {
		List<Unit> customUnits = getCustomUnits();
		customUnits.removeIf(u -> u.getId().equals(unit.getId()));
		saveCustomUnits(customUnits);
	}

	private void saveCustomUnits(List<Unit> customUnits) {
		String json = gson.toJson(customUnits);
		sharedPreferences.edit().putString(KEY_CUSTOM_UNITS, json).apply();
	}

	public boolean isUnitNameExists(String name) {
		List<Unit> allUnits = getAllUnits();
		for (Unit unit : allUnits) {
			if (unit.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
}