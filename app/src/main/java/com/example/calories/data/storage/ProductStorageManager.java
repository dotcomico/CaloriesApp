package com.example.calories.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.calories.data.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ProductStorageManager {
    private static final String PREF_NAME = "shared preferences";
    private static final String KEY_PRODUCT_LIST = "products";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public ProductStorageManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void save(ArrayList<Product> productList) {
        String json = gson.toJson(productList);
        sharedPreferences.edit().putString(KEY_PRODUCT_LIST, json).apply();
    }

    public ArrayList<Product> load() {
        String json = sharedPreferences.getString(KEY_PRODUCT_LIST, null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        ArrayList<Product> list = gson.fromJson(json, type);
        return (list != null) ? list : new ArrayList<>();
    }

    public void clear() {
        sharedPreferences.edit().remove(KEY_PRODUCT_LIST).apply();
    }

    public Product getLastItem() {
        ArrayList<Product> list = load();
        if (!list.isEmpty()) {
            return list.get(list.size() - 1);
        }
        return null;
    }
}
