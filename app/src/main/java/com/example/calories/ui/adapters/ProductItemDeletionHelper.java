package com.example.calories.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.example.calories.data.models.Product;

import java.util.ArrayList;
import static com.example.calories.utils.AppConstants.*;
public class ProductItemDeletionHelper {

    private final Context context;
    private final ProductItemAdapter adapter;
    private final ArrayList<Product> productList;
    private boolean isInDeleteMode = false;
    private final Runnable onStateChanged; // עדכון UI (למשל שינוי תפריט)

    public ProductItemDeletionHelper(Context context, ArrayList<Product> productList, ProductItemAdapter adapter, Runnable onStateChanged) {
        this.context = context;
        this.adapter = adapter;
        this.productList = productList;
        this.onStateChanged = onStateChanged;
    }

    public boolean isInDeleteMode() {
        return isInDeleteMode;
    }

    public void toggleItemSelection(int position) {
        adapter.markItemToDelete(position);

        if (adapter.getDeleteMarkCount() == 0) {
            exitDeleteMode();
        } else {
            isInDeleteMode = true;
            onStateChanged.run(); // תפריט מחיקה
        }
    }

    public void enterDeleteMode(int position) {
        adapter.markItemToDelete(position);
        isInDeleteMode = true;
        onStateChanged.run(); // שינוי תפריט למחיקה
    }

    public void exitDeleteMode() {
        for (int i = 0; i < productList.size(); i++) {
            adapter.unMarkItem(i);
        }
        isInDeleteMode = false;
        onStateChanged.run(); // חזרה לתפריט רגיל
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteSelectedItems() {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getItemState() == PRODUCT_STATE_MARKED_FOR_DELETE) {
                productList.remove(i);
                i--; // חשוב לא לדלג על איברים
            }
        }
        adapter.notifyDataSetChanged();
        exitDeleteMode();
        Toast.makeText(context, "פריטים נמחקו", Toast.LENGTH_SHORT).show();
    }

    public void toggleSelectAll() {
        if (adapter.getDeleteMarkCount() == adapter.getItemCount()) {
            exitDeleteMode();
        } else {
            for (int i = 0; i < productList.size(); i++) {
                adapter.markItem(i);
            }
            isInDeleteMode = true;
            onStateChanged.run();
        }
    }
}
