package com.example.calories;

import static com.example.calories.utils.AppConstants.*;

public class AmountAdjuster {

	public static String getNewAmountFormatedText(double currentAmount, boolean increase, String unit) {
		double newAmount = getNewAmount(currentAmount, increase, unit);
		return NumberFormatter.formatAmount(newAmount);
	}

	public static double getNewAmount(double currentAmount, boolean increase, String unit) {
		if (CalorieCalculator.getCalculationMode(unit) == CALCULATION_MOD_UNIT) {
			return calculateUnitAmount(currentAmount, increase);
		} else {
			return calculateWeightVolumeAmount(currentAmount, increase);
		}
	}

	private static double calculateUnitAmount(double currentAmount, boolean increase) {
		if (increase) {
			if (currentAmount >= UNIT_INCREMENT)
				return currentAmount + UNIT_INCREMENT;
			if (currentAmount == UNIT_HALF)
				return UNIT_INCREMENT;
			if (currentAmount == UNIT_QUARTER)
				return UNIT_HALF;
			if (currentAmount == 0)
				return UNIT_QUARTER;
		} else {
			if (currentAmount - UNIT_INCREMENT >= UNIT_INCREMENT)
				return currentAmount - UNIT_INCREMENT;
			if (currentAmount == UNIT_INCREMENT)
				return UNIT_HALF;
			if (currentAmount == UNIT_HALF)
				return UNIT_QUARTER;
			if (currentAmount == UNIT_QUARTER)
				return 0;
		}
		return currentAmount;
	}

	private static double calculateWeightVolumeAmount(double currentAmount, boolean increase) {
		if (increase) {
			return currentAmount + WEIGHT_VOLUME_INCREMENT;
		} else if (currentAmount >= WEIGHT_VOLUME_INCREMENT) {
			return currentAmount - WEIGHT_VOLUME_INCREMENT;
		}
		return currentAmount;
	}
}