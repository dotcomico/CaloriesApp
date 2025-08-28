package com.example.calories;

public class AmountAdjuster {

    private static final double UNIT_INCREMENT = 1.0;
    private static final double UNIT_HALF = 0.5;
    private static final double UNIT_QUARTER = 0.25;
    private static final double WEIGHT_VOLUME_INCREMENT = 50.0;

    public static String calculateNewAmount(double currentAmount, boolean increase, String unit) {
        if (CalorieCalculator.getCalculationMode(unit) == CalorieCalculator.CALCULATION_MOD_UNIT) {
            return calculateUnitAmount(currentAmount, increase);
        } else {
            return calculateWeightVolumeAmount(currentAmount, increase);
        }
    }

    private static String calculateUnitAmount(double currentAmount, boolean increase) {
        if (increase) {
            if (currentAmount >= UNIT_INCREMENT) {
                return String.valueOf(currentAmount + UNIT_INCREMENT);
            }
            if (currentAmount == UNIT_HALF) return String.valueOf(UNIT_INCREMENT);
            if (currentAmount == UNIT_QUARTER) return String.valueOf(UNIT_HALF);
            if (currentAmount == 0) return String.valueOf(UNIT_QUARTER);
        } else {
            if (currentAmount - UNIT_INCREMENT >= UNIT_INCREMENT) {
                return String.valueOf(currentAmount - UNIT_INCREMENT);
            }
            if (currentAmount == UNIT_INCREMENT) return String.valueOf(UNIT_HALF);
            if (currentAmount == UNIT_HALF) return String.valueOf(UNIT_QUARTER);
            if (currentAmount == UNIT_QUARTER) return "0";
        }
        return String.valueOf(currentAmount);
    }

    private static String calculateWeightVolumeAmount(double currentAmount, boolean increase) {
        if (increase) {
            return String.valueOf(currentAmount + WEIGHT_VOLUME_INCREMENT);
        } else if (currentAmount >= WEIGHT_VOLUME_INCREMENT) {
            return String.valueOf(currentAmount - WEIGHT_VOLUME_INCREMENT);
        }
        return String.valueOf(currentAmount);
    }
}