package com.example.calories.data.models;
import static com.example.calories.utils.AppConstants.*;

public class Product {
    /// לשנות calorieText לInt
    private int itemState; // הבחנה בין מוצר פרטי למוצר במערכת (0=מערכת, 1=פרטי)
    private String name; // שם מוצר
    private String unit; // סוג המדד כמחרוזת
    private String calorieText; /// קלוריות
    private String barcode;

    public Product() {
        // קונסטרקטור ריק
    }

    public Product(int itemState, String name, String unit,
                   String calorieText, String barcode) {

        this.itemState = itemState;
        this.name = name;
        this.unit = unit;
        this.calorieText = calorieText;
        this.barcode = barcode;
    }

    // Getters and Setters

    public int getItemState() {
        return itemState;
    }

    public void setItemState(int itemState) {
        this.itemState = itemState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCalorieText() {
        return calorieText;
    }

    public void setCalorieText(String calorieText) {
        this.calorieText = calorieText;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * בדיקה האם המוצר הוא מוצר מערכת
     * @return true אם מוצר מערכת, false אחרת
     */
    public boolean isSystemItem() {
        return itemState == PRODUCT_STATE_SYSTEM;
    }

    /**
     * בדיקה האם המוצר הוא מוצר פרטי
     * @return true אם מוצר פרטי, false אחרת
     */
    public boolean isPrivateItem() {
        return itemState == PRODUCT_STATE_CUSTOM;
    }

    /**
     * בדיקה האם יש לקלורי טקסט תקין
     * @return true אם יש ערך קלוריות תקין, false אחרת
     */
    public boolean hasValidCalories() {
        if (calorieText == null || calorieText.trim().isEmpty()) {
            return false;
        }

        try {
            double calories = Double.parseDouble(calorieText);
            return calories >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * איפוס כל השדות למצב ברירת מחדל
     */
    public void clear() {
        itemState = PRODUCT_STATE_SYSTEM;
        name = "";
        unit = "";
        calorieText = "";
        barcode = "";
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", calories='" + calorieText + '\'' +
                ", itemState=" + itemState +
                '}';
    }
}