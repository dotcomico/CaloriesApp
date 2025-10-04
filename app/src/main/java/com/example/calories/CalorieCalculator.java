package com.example.calories;

import static com.example.calories.utils.AppConstants.*;


// יצירת מחלקת עזר לחישובי קלוריות
public class CalorieCalculator {

    public static String calculateCalories(double caloriesPer100, double amount, String unit) {
        if (isWeightVolumeUnit(unit)) {
            return String.valueOf((int) ((caloriesPer100 / 100) * amount));
        } else {
            return String.valueOf((int) (caloriesPer100 * amount));
        }
    }

    public static int getCalculationMode(String unit) {
        return isWeightVolumeUnit(unit) ? CALCULATION_MOD_WEIGHT_VOLUME : CALCULATION_MOD_UNIT;
    }

    public static int getDefaultAmount(String unit) {
        return isWeightVolumeUnit(unit) ? DEFAULT_CALORIES : DEFAULT_AMOUNT;
    }

    private static boolean isWeightVolumeUnit(String unit) {
        return UNIT_100_GRAM.equals(unit) || UNIT_100_ML.equals(unit) || UNIT_CALORIES.equals(unit);
    }
}