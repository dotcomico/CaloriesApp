package com.example.calories;

import android.content.Context;

import com.example.calories.data.models.ConsumedProduct;
import com.example.calories.data.models.Product;
import com.example.calories.data.storage.ConsumedProductStorageManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class ConsumedProductManager {

    private ArrayList<ConsumedProduct> consumedProductsOfDay;
    private ArrayList<ConsumedProduct> consumedProductsAll;

    private final ConsumedProductStorageManager consumedProductStorageManager;

    public ConsumedProductManager( Context context) {
        this.consumedProductStorageManager = new ConsumedProductStorageManager(context);
        this.consumedProductsAll = consumedProductStorageManager.load();
        this.consumedProductsOfDay = consumedProductStorageManager.loadToday();
    }

    public void loadItemsData(Calendar calendarDayParameter) {
        consumedProductsAll = consumedProductStorageManager.load();
        consumedProductsOfDay = consumedProductStorageManager.loadByDay(calendarDayParameter);
    }

    public void saveItemsData(){
        consumedProductStorageManager.save(consumedProductsAll);
    }

    public void clearItemsData(){
        consumedProductStorageManager.clear();
        consumedProductsAll.clear();
    }

    public void addItem(double amount, Product product , Calendar calendarDayParameter){
        //  Calendar c = Calendar.getInstance(); //   SimpleDateFormat hour = new SimpleDateFormat("HH");

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

        ConsumedProduct listItem=new ConsumedProduct(amount,product, date.format(calendarDayParameter.getTime()));
        consumedProductsAll.add(listItem );

        saveItemsData();
        loadItemsData(calendarDayParameter);
    }

    public void deleteItemById(String targetId){
        // הסרה מהרשימה היומית
        Iterator<ConsumedProduct> iterator = consumedProductsOfDay.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(targetId)) {
                iterator.remove();
                break;
            }
        }
// הסרה מהרשימה הראשית
        Iterator<ConsumedProduct> bufferIterator = consumedProductsAll.iterator();
        while (bufferIterator.hasNext()) {
            if (bufferIterator.next().getId().equals(targetId)) {
                bufferIterator.remove();
                break;
            }
        }
        saveItemsData();
    }

    public void editItemAmountById( double newAmount ,String targetId , Calendar calendarDayParameter ){

        for (ConsumedProduct consumedProduct : consumedProductsOfDay) {
            if (consumedProduct.getId().equals(targetId)) {
                consumedProduct.setAmount(newAmount);
                break;
            }
        }

        for (ConsumedProduct consumedProduct : consumedProductsAll) {
            if (consumedProduct.getId().equals(targetId)) {
                consumedProduct.setAmount(newAmount);
                break;
            }
        }
        saveItemsData();
        loadItemsData(calendarDayParameter);
    }


    public ArrayList<ConsumedProduct> getConsumedProductsOfDay() {
        return consumedProductsOfDay;
    }

    public ArrayList<ConsumedProduct> getConsumedProductsAll() {
        return consumedProductsAll;
    }
}
