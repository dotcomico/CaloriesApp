package com.example.calories.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.calories.data.models.ConsumedProduct;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ConsumedProductStorageManager {
    private static final String PREF_NAME = "shared preferences";
    private static final String KEY_PRODUCT_LIST = "consumed_products";

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

    public ArrayList<ConsumedProduct> loadByDay(Calendar calendar_day) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(calendar_day.getTime());

        ArrayList<ConsumedProduct> consumedProductsAll = load();
        ArrayList<ConsumedProduct> consumedProductsOfDay = new ArrayList<>();

        for (int i = 0; i < consumedProductsAll.size(); i++) {
            //אם זה היום הנכון לפי התאריך
            if (strDate.equals(consumedProductsAll.get(i).getDate())) {
                consumedProductsOfDay.add(consumedProductsAll.get(i));
            }
        }
        return consumedProductsOfDay;
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
    public void clear() {
        sharedPreferences.edit().remove(KEY_PRODUCT_LIST).apply();
    }


}
