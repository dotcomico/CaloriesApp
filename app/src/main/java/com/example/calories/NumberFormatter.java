package com.example.calories;

public class NumberFormatter {

	public static String formatAmount(double number) {
		// Check if the number has no fractional part
		if (number == (long) number) {
			// It's a whole number, format as an integer
			return String.format("%d", (long) number);
		} else {
			// It has a decimal part, return as is (or you can use DecimalFormat for more
			// control)
			return String.valueOf(number);
		}
	}
}
