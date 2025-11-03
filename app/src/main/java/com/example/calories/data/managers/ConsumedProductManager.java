package com.example.calories.data.managers;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.calories.data.models.ConsumedProduct;
import com.example.calories.data.models.Product;
import com.example.calories.data.storage.ConsumedProductsStorageManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import static com.example.calories.utils.AppConstants.*;

public class ConsumedProductManager {

	private ArrayList<ConsumedProduct> consumedProductsOfDay;
	private ArrayList<ConsumedProduct> consumedProductsAll;

	private final ConsumedProductsStorageManager consumedProductsStorageManager;

	public ConsumedProductManager(Context context) {
		this.consumedProductsStorageManager = new ConsumedProductsStorageManager(context);
		this.consumedProductsAll = consumedProductsStorageManager.load();
		this.consumedProductsOfDay = consumedProductsStorageManager.loadToday();
	}

	public void loadItemsData(Calendar calendarDayParameter) {
		consumedProductsAll = consumedProductsStorageManager.load();
		consumedProductsOfDay = consumedProductsStorageManager.loadByDay(calendarDayParameter);
	}

	public void saveItemsData() {
		consumedProductsStorageManager.save(consumedProductsAll);
	}

	public void clearItemsData() {
		consumedProductsStorageManager.clear();
		consumedProductsAll.clear();
	}

	public void addItem(double amount, Product product, Calendar calendarDayParameter) {
		@SuppressLint("SimpleDateFormat")
		SimpleDateFormat date = new SimpleDateFormat(DATE_PATTERN);

		ConsumedProduct listItem = new ConsumedProduct(amount, product, date.format(calendarDayParameter.getTime()));
		consumedProductsAll.add(listItem);

		saveItemsData();
		loadItemsData(calendarDayParameter);
	}

	public void deleteItemById(String targetId) {
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

	public void editItemAmountById(double newAmount, String targetId, Calendar calendarDayParameter) {

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
		// לשקול ממש כאן את הפעולה loadItemsData(calendarDayParameter); ולהפוך אותה
		// לפרטית
		return consumedProductsOfDay;
	}

	public int getConsumedCaloriesOfDay() {
		int calories = 0;
		for (int i = 0; i < consumedProductsOfDay.size(); i++) {
			calories += consumedProductsOfDay.get(i).getTotalCalories();
		}
		return calories;
	}

	public ArrayList<ConsumedProduct> getConsumedProductsAll() {
		return consumedProductsAll;
	}
}
