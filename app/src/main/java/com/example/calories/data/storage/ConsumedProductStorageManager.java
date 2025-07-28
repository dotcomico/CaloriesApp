package com.example.calories.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.calories.data.models.ConsumedProduct;
import com.example.calories.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ConsumedProductStorageManager {
    private static final String PREF_NAME = "shared preferences";
    private static final String KEY_PRODUCT_LIST = "eat list";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public ConsumedProductStorageManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void save(ArrayList<ConsumedProduct> consumedProductList) {
        String json = gson.toJson(consumedProductList);
        sharedPreferences.edit().putString(KEY_PRODUCT_LIST, json).apply();
    }

    public ArrayList<ConsumedProduct> load() {
        String json = sharedPreferences.getString(KEY_PRODUCT_LIST, null);
        Type type = new TypeToken<ArrayList<ConsumedProduct>>() {}.getType();
        ArrayList<ConsumedProduct> list = gson.fromJson(json, type);
        return (list != null) ? list : new ArrayList<>();
    }
    public ArrayList<ConsumedProduct> loadToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());

        return loadByDay(calendar);
    }
    public ArrayList<ConsumedProduct> loadByDay(Calendar calendar_day) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(calendar_day.getTime());

        ArrayList<ConsumedProduct> consumedProductsBuffer = load();
        ArrayList<ConsumedProduct> consumedProducts = new ArrayList<>();

            for (int i = 0; i < consumedProductsBuffer.size(); i++) {
                //תעדכן מספר סידורי
                consumedProductsBuffer.get(i).setSerial(i);
                //אם זה היום הנכון לפי התאריך
                if (strDate.equals(consumedProductsBuffer.get(i).getDate())) {
//פה זה מתקן את התמונה של כמות, בבוא הזמן נשנה אותה לתמונות של בוקר צהריים או ערב, השוני יקבע לפי השעה שהוספנו את המזון לרשימה

                    consumedProducts.add(consumedProductsBuffer.get(i));
                }
            }
        return consumedProducts;
    }
    public void clear() {
        sharedPreferences.edit().remove(KEY_PRODUCT_LIST).apply();
    }

}
