package com.example.calories;

// יצירת מחלקת עזר לחישובי קלוריות
public class CalorieCalculator {

    private static final String UNIT_100_GRAM = "100 גרם";
    private static final String UNIT_100_ML = "100 מל";
    private static final String UNIT_CALORIES = "קלוריות";

    public static final int CALCULATION_MOD_UNIT = 1;
    public static final int CALCULATION_MOD_WEIGHT_VOLUME = 2;

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

    public static String getDefaultAmount(String unit) {
        return isWeightVolumeUnit(unit) ? "100" : "1";
    }

    private static boolean isWeightVolumeUnit(String unit) {
        return UNIT_100_GRAM.equals(unit) || UNIT_100_ML.equals(unit) || UNIT_CALORIES.equals(unit);
    }
}