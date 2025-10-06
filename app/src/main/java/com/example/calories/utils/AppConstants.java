package com.example.calories.utils;

public class AppConstants {

    /// שימוש בהגדרה:
    /// import static com.example.calories.utils.AppConstants.*;


    /// ///////
    public static final String GRAM = "גרם";
    public static final String ML = "מל";

    // === יחידות מידה ===
    public static final String UNIT_100_GRAM = "100 " + GRAM;
    public static final String UNIT_100_ML = "100 " +ML;
    public static final String UNIT_UNIT = "יחידה";
    public static final String UNIT_TABLESPOON ="כף";
    public static final String UNIT_TEASPOON ="כפית";
    public static final String UNIT_CUP ="כוס";
    public static final String UNIT_SLICE ="פרוסה";
    public static final String UNIT_CALORIES = "קלוריות";

    // === מצבי חישוב ===
    public static final int CALCULATION_MOD_UNIT = 1;
    public static final int CALCULATION_MOD_WEIGHT_VOLUME = 2;

    // === הגדרות כלליות ===
    public static final int DEFAULT_CALORIES = 100;
    public static final int DEFAULT_AMOUNT = 1;


    // itemState - הגדרת סוג איבר מסוג מוצר product;
    public static final int PRODUCT_STATE_SYSTEM = 0;
    public static final int PRODUCT_STATE_CUSTOM = 1;
    public static final int PRODUCT_STATE_SELF_SEARCH = 999;
    public static final int PRODUCT_STATE_MARKED_FOR_DELETE = 100;

    //אקסטרות מעבר בין מסכים - Extra
    public static final String EXTRA_BARCODE = "barcode";
    public static final String EXTRA_NAME = "name";



    // === מפתחות לשמירה ב-SharedPreferences ===
    public static final String PREF_NAME = "shared preferences";
    public static final String KEY_PRODUCT_LIST = "products";
    public static final String KEY_CONSUMED_PRODUCT_LIST = "consumed_products";
    public static final String KEY_CUSTOM_UNITS = "custom_units";


    ///
//    public static final String DATE_PATTERN_YEAR_FIRST= "yyyy-MM-dd";   //יש לחבר לדפוס אחיד
    public static final String DATE_PATTERN= "dd-MM-yyyy";
    public static final String DATE_PATTERN_FOR_SHOW = "yyyy/MM/dd__HH:mm";


    //
    public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search?q=";
    // === טקסטים חוזרים ===
    public static final String ERROR_INVALID_INPUT = "קלט לא תקין";
    public static final String MESSAGE_SAVED_SUCCESSFULLY = "נשמר בהצלחה";
    public static final String MESSAGE_ITEM_ADDED = "המוצר נוסף בהצלחה";
    public static final String TEXT_NO_RESULT = "אין תוצאה";

}
