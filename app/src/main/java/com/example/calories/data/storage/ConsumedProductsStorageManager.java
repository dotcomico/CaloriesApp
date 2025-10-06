package com.example.calories.data.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.calories.data.models.ConsumedProduct;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import static com.example.calories.utils.AppConstants.*;
public class ConsumedProductsStorageManager {
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

        public ConsumedProductsStorageManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void save(ArrayList<ConsumedProduct> consumedProductList) {
        String json = gson.toJson(consumedProductList);
        sharedPreferences.edit().putString(KEY_CONSUMED_PRODUCT_LIST, json).apply();
    }

    public ArrayList<ConsumedProduct> loadToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());

        return loadByDay(calendar);
    }

    public ArrayList<ConsumedProduct> loadByDay(Calendar calendar_day) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String strDate = sdf.format(calendar_day.getTime());

        ArrayList<ConsumedProduct> consumedProductsAll = load();
        ArrayList<ConsumedProduct> consumedProductsOfDay = new ArrayList<>();

        for (int i = 0; i < consumedProductsAll.size(); i++) {
            //אם זה היום הנכון לפי התאריך
            if (strDate.equals(consumedProductsAll.get(i).getFormattedDate())) {
                consumedProductsOfDay.add(consumedProductsAll.get(i));
            }
        }
        return consumedProductsOfDay;
    }
    public ArrayList<ConsumedProduct> load() {
        String json = sharedPreferences.getString(KEY_CONSUMED_PRODUCT_LIST, null);
        Type type = new TypeToken<ArrayList<ConsumedProduct>>() {}.getType();
        ArrayList<ConsumedProduct> list = gson.fromJson(json, type);
        return (list != null) ? list : new ArrayList<>();
    }

    public void clear() {
        sharedPreferences.edit().remove(KEY_CONSUMED_PRODUCT_LIST).apply();
    }


}
