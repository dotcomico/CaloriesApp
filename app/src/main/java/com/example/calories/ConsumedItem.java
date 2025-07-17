package com.example.calories;

import java.time.LocalDateTime;

public class ConsumedItem {

    // Constants for unit types
    private static final String UNIT_100_GRAM = "100 גרם";
    private static final String UNIT_100_ML = "100 מל";
    private static final String UNIT_CALORIES = "קלוריות";
    private static final int STANDARD_UNIT_BASE = 100;

    private double amount; // כמות שנצרכה
    private ProductItem productItem; // המוצר שנצרך
    private String date; // תאריך כטקסט (לצורך תאימות לאחור)
    private int serial; // מזהה פנימי (למשל לשמירה במסד נתונים)
    private LocalDateTime dateTime; // מתי נאכל

    public ConsumedItem() {
    }

    public ConsumedItem(double amount, ProductItem productItem, String date, int serial) {
        this.amount = amount;
        this.productItem = productItem;
        this.date = date;
        this.serial = serial;
    }

    // Getters and Setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public ProductItem getProductItem() {
        return productItem;
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * חישוב סה"כ קלוריות על בסיס כמות וסוג יחידה
     * @return סה"כ קלוריות כמספר שלם
     */
    public int getTotalCalories() {
        if (productItem == null) {
            return 0;
        }

        try {
            double caloriesPerUnit = Double.parseDouble(productItem.getCalorieText());

            if (isStandardUnit()) {
                // יחידה סטנדרטית - חישוב לפי 100 גרם/מ"ל
                return (int) ((caloriesPerUnit / STANDARD_UNIT_BASE) * amount);
            } else {
                // יחידה לא סטנדרטית - חישוב לפי יחידה
                return (int) (caloriesPerUnit * amount);
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * בדיקה האם היחידה היא סטנדרטית (100 גרם/מ"ל)
     * @return true אם היחידה סטנדרטית, false אחרת
     */
    public boolean isStandardUnit() {
        if (productItem == null) {
            return false;
        }

        String unit = productItem.getUnit();
        return UNIT_100_GRAM.equals(unit) ||
                UNIT_100_ML.equals(unit) ||
                UNIT_CALORIES.equals(unit);
    }

    /**
     * יצירת טקסט תצוגה עבור היחידה והכמות
     * @return טקסט מעוצב להצגה
     */
    public String getUnitDisplayText() {
        if (productItem == null) {
            return "";
        }

        String formattedAmount = formatAmount(amount);
        String unit = productItem.getUnit();

        if (isStandardUnit()) {
            return getStandardUnitDisplayText(unit, formattedAmount);
        } else {
            return unit + " × " + formattedAmount;
        }
    }

    private String getStandardUnitDisplayText(String unit, String formattedAmount) {
        if (UNIT_100_GRAM.equals(unit)) {
            return formattedAmount + " גרם";
        } else if (UNIT_100_ML.equals(unit)) {
            return formattedAmount + " מל";
        } else {
            return UNIT_CALORIES;
        }
    }

    private String formatAmount(double amount) {
        return isWholeNumber(amount)
                ? String.valueOf((int) amount)
                : String.valueOf(amount);
    }

    private boolean isWholeNumber(double number) {
        return number % 1 == 0;
    }
}