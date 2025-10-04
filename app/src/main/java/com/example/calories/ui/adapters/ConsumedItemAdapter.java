package com.example.calories.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calories.R;
import com.example.calories.data.models.ConsumedProduct;
import com.example.calories.data.models.Product;
import com.example.calories.data.storage.UnitManager;
import java.util.ArrayList;

public class ConsumedItemAdapter extends RecyclerView.Adapter<ConsumedItemAdapter.ConsumedItemViewHolder> {

    private final ArrayList<ConsumedProduct> consumedProducts;

    public static class ConsumedItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView unitImageView;
        private final TextView productNameTextView;
        private final TextView unitTextView;
        private final TextView calorieValueTextView;

        public ConsumedItemViewHolder(View itemView) {
            super(itemView);
            unitImageView = itemView.findViewById(R.id.productUnitImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            unitTextView = itemView.findViewById(R.id.productUnitTextView);
            calorieValueTextView = itemView.findViewById(R.id.productCalorieValueTextView);
        }
    }

    public ConsumedItemAdapter(ArrayList<ConsumedProduct> consumedProducts) {
        this.consumedProducts = consumedProducts;
    }

    @NonNull
    @Override
    public ConsumedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.consumed_item, parent, false);
        return new ConsumedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsumedItemViewHolder holder, int position) {
        ConsumedProduct currentItem = consumedProducts.get(position);

        bindProductInfo(holder, currentItem);
        bindCalorieInfo(holder, currentItem);
        itemStyle(holder);
    }

    private void bindProductInfo(ConsumedItemViewHolder holder, ConsumedProduct item) {
        Product product = item.getProductItem();

        holder.unitImageView.setImageResource(UnitManager.getUnitImageResId(product.getUnit()));
        holder.productNameTextView.setText(product.getName());
    }

    private void bindCalorieInfo(ConsumedItemViewHolder holder, ConsumedProduct item) {
        // Use ConsumedProduct's calculation logic
        int totalCalories = item.getTotalCalories();
        String unitDisplayText = item.getUnitDisplayText();

        holder.calorieValueTextView.setText(String.valueOf(totalCalories));
        holder.unitTextView.setText(unitDisplayText);
    }
    private void itemStyle(ConsumedItemViewHolder holder) {
        holder.calorieValueTextView.setScaleY(1.0f);
        holder.calorieValueTextView.setTextSize(30);
    }
    @Override
    public int getItemCount() {
        return consumedProducts.size();
    }
}