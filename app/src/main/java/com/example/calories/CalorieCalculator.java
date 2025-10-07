package com.example.calories;

import static com.example.calories.utils.AppConstants.*;


// יצירת מחלקת עזר לחישובי קלוריות
public class CalorieCalculator {

    public static int calculateTotalCalories(double baseCalories, double amount, String unit) {
        if (isWeightVolumeUnit(unit)) {
            return (int) ((baseCalories / 100) * amount);
        } else {
            return (int) (baseCalories * amount);
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