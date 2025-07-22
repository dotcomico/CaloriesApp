package com.example.calories.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calories.R;
import com.example.calories.data.models.ConsumedItem;
import com.example.calories.data.models.ProductItem;
import com.example.calories.data.storage.MeasurementManager;

import java.util.ArrayList;

public class ConsumedItemAdapter extends RecyclerView.Adapter<ConsumedItemAdapter.ConsumedItemViewHolder> {

    // Constants for UI styling
    private static final float HIGHLIGHTED_SCALE_Y = 1.0f;
    private static final int HIGHLIGHTED_TEXT_SIZE = 30;

    private final ArrayList<ConsumedItem> consumedItems;

    public static class ConsumedItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView unitImageView;
        private final ConstraintLayout main;
        private final TextView productNameTextView;
        private final TextView unitTextView;
        private final TextView calorieValueTextView;
        private final TextView calorieLabelTextView;

        public ConsumedItemViewHolder(View itemView) {
            super(itemView);
            unitImageView = itemView.findViewById(R.id.productUnitImageView);
            main = itemView.findViewById(R.id.main);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            unitTextView = itemView.findViewById(R.id.productUnitTextView);
            calorieValueTextView = itemView.findViewById(R.id.productCalorieValueTextView);
            calorieLabelTextView = itemView.findViewById(R.id.calorieLabelTextView);
        }
    }

    public ConsumedItemAdapter(ArrayList<ConsumedItem> consumedItems) {
        this.consumedItems = consumedItems;
    }

    @NonNull
    @Override
    public ConsumedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.consumed_item, parent, false);
        return new ConsumedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConsumedItemViewHolder holder, int position) {
        ConsumedItem currentItem = consumedItems.get(position);

        bindProductInfo(holder, currentItem);
        bindCalorieInfo(holder, currentItem);
        applyItemStateStyle(holder, currentItem);
    }

    private void bindProductInfo(ConsumedItemViewHolder holder, ConsumedItem item) {
        ProductItem product = item.getProductItem();

        holder.unitImageView.setImageResource(MeasurementManager.getUnitImageResId(product.getUnit()));
        holder.productNameTextView.setText(product.getName());
    }

    private void bindCalorieInfo(ConsumedItemViewHolder holder, ConsumedItem item) {
        // Use ConsumedItem's calculation logic
        int totalCalories = item.getTotalCalories();
        String unitDisplayText = item.getUnitDisplayText();

        holder.calorieValueTextView.setText(String.valueOf(totalCalories));
        holder.unitTextView.setText(unitDisplayText);
    }

    private void applyItemStateStyle(ConsumedItemViewHolder holder, ConsumedItem item) {
        // Apply special styling for system items (itemState == 0)
        if (item.getProductItem().getItemState() == ProductItem.ITEM_STATE_SYSTEM) {
            holder.main.setBackgroundResource(R.drawable.message_sty1);
            holder.calorieLabelTextView.setVisibility(View.VISIBLE);
            holder.calorieValueTextView.setScaleY(HIGHLIGHTED_SCALE_Y);
            holder.calorieValueTextView.setTextSize(HIGHLIGHTED_TEXT_SIZE);
        } else {
            // Reset to default styling for private items
            resetItemStyle(holder);
        }
    }

    private void resetItemStyle(ConsumedItemViewHolder holder) {
        holder.main.setBackgroundResource(R.drawable.message_sty1); // או רקע ברירת מחדל
        holder.calorieLabelTextView.setVisibility(View.VISIBLE);
        holder.calorieValueTextView.setScaleY(1.0f);
        holder.calorieValueTextView.setTextSize(30); // גודל ברירת מחדל
    }

    @Override
    public int getItemCount() {
        return consumedItems.size();
    }
}