package com.example.calories.data.models;

public class ProductItem {

    // Constants for item states
    public static final int ITEM_STATE_SYSTEM = 0;  // מוצר במערכת
    public static final int ITEM_STATE_PRIVATE = 1; // מוצר פרטי

    private int unitTypeValue; // סוג המדד בצורה מספרית
    private int itemState; // הבחנה בין מוצר פרטי למוצר במערכת (0=מערכת, 1=פרטי)
    private String name; // שם מוצר
    private String unit; // סוג המדד כמחרוזת
    private String calorieText; // קלוריות
    private int unitImageResId; // מיקום התמונה של סוג המדד המתאים בריסורס
    private String barcode;

    public ProductItem() {
        // קונסטרקטור ריק
    }

    public ProductItem(int unitTypeValue, int itemState, String name, String unit,
                       String calorieText, int unitImageResId, String barcode) {
        this.unitTypeValue = unitTypeValue;
        this.itemState = itemState;
        this.name = name;
        this.unit = unit;
        this.calorieText = calorieText;
        this.unitImageResId = unitImageResId;
        this.barcode = barcode;
    }

    // Getters and Setters
    public int getUnitTypeValue() {
        return unitTypeValue;
    }

    public void setUnitTypeValue(int unitTypeValue) {
        this.unitTypeValue = unitTypeValue;
    }

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

    public int getUnitImageResId() {
        return unitImageResId;
    }

    public void setUnitImageResId(int unitImageResId) {
        this.unitImageResId = unitImageResId;
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
        return itemState == ITEM_STATE_SYSTEM;
    }

    /**
     * בדיקה האם המוצר הוא מוצר פרטי
     * @return true אם מוצר פרטי, false אחרת
     */
    public boolean isPrivateItem() {
        return itemState == ITEM_STATE_PRIVATE;
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
        unitTypeValue = 0;
        itemState = ITEM_STATE_SYSTEM;
        name = "";
        unit = "";
        calorieText = "";
        unitImageResId = 0;
        barcode = "";
    }

    @Override
    public String toString() {
        return "ProductItem{" +
                "name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", calories='" + calorieText + '\'' +
                ", itemState=" + itemState +
                '}';
    }
}