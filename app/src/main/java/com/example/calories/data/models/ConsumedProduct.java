package com.example.calories.data.models;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.UUID;
import static com.example.calories.utils.AppConstants.*;

import android.annotation.SuppressLint;

public class ConsumedProduct {
//יש להוסיף שמירה של שעה או הגדרה של סוג הארוחה כבוקר צהריים או ערב.
    private static final int STANDARD_UNIT_BASE = 100;

    private String id; // מזהה פנימי (למשל לשמירה במסד נתונים)
    private double amount; // כמות שנצרכה
    private Product product; // המוצר שנצרך
    private String formattedDate; // תאריך כטקסט (לצורך תאימות לאחור)
    private long timestamp; // מתי נאכל

    public ConsumedProduct() {
    }

    public ConsumedProduct(double amount, Product product, String formattedDate) {
        this.amount = amount;
        this.product = product;
        this.formattedDate = formattedDate.trim();
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis(); // שומר את הזמן הנוכחי
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
//        this.formattedDate = sdf.format(Calendar.getInstance().getTime()); // שומר רק תאריך עכשיווי ולא תאריך שמתקבל.
    }

//    public LocalDateTime getDateTime() {
//        return Instant.ofEpochMilli(timestamp)
//                .atZone(ZoneId.systemDefault())
//                .toLocalDateTime();
//    }
//
//    // פעולת עזר שמחזירה תאריך בפורמט יפה להצגה
//    public String getFormattedDate() {
//        Date date = new Date(timestamp);
//        @SuppressLint("SimpleDateFormat")
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//        return sdf.format(date);
//    }

    // Getters and Setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Product getProductItem() {
        return product;
    }

    public void setProductItem(Product product) {
        this.product = product;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate.trim();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.trim();
    }

    /**
     * חישוב סה"כ קלוריות על בסיס כמות וסוג יחידה
     * @return סה"כ קלוריות כמספר שלם
     */
    public int getTotalCalories() {
        if (product == null) {
            return 0;
        }

        try {
            double caloriesPerUnit = Double.parseDouble(product.getCalorieText());

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
        if (product == null) {
            return false;
        }

        String unit = product.getUnit();
        return UNIT_100_GRAM.equals(unit) ||
                UNIT_100_ML.equals(unit) ||
                UNIT_CALORIES.equals(unit);
    }

    /**
     * יצירת טקסט תצוגה עבור היחידה והכמות
     * @return טקסט מעוצב להצגה
     */
    public String getUnitDisplayText() {
        if (product == null) {
            return "";
        }

        String formattedAmount = formatAmount(amount);
        String unit = product.getUnit();

        if (isStandardUnit()) {
            return getStandardUnitDisplayText(unit, formattedAmount);
        } else {
            return unit + " × " + formattedAmount;
        }
    }

    private String getStandardUnitDisplayText(String unit, String formattedAmount) {
        if (UNIT_100_GRAM.equals(unit)) {
            return formattedAmount + " " + GRAM;
        } else if (UNIT_100_ML.equals(unit)) {
            return formattedAmount + " " + ML;
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