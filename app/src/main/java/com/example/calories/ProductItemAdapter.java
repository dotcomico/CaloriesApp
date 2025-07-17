package com.example.calories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.ProductItemViewHolder> {

    // קבועים ברורים במקום מספרים קסומים
    private static final int STATE_NORMAL = 0;
    private static final int STATE_CUSTOM = 1;
    private static final int STATE_MARKED_FOR_DELETE = 100;
    private static final int STATE_HIGHLIGHTED = 999;

    private final ArrayList<ProductItem> customProducts;

    public ProductItemAdapter(ArrayList<ProductItem> productList) {
        this.customProducts = productList;
    }

    public static class ProductItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productUnitImageView;
        private final ConstraintLayout mainLayout;
        private final TextView productNameTextView;
        private final TextView productUnitTextView;
        private final TextView productCalorieValueTextView;
        private final TextView calorieLabelTextView;

        public ProductItemViewHolder(View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.main);
            productUnitImageView = itemView.findViewById(R.id.productUnitImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productUnitTextView = itemView.findViewById(R.id.productUnitTextView);
            productCalorieValueTextView = itemView.findViewById(R.id.productCalorieValueTextView);
            calorieLabelTextView = itemView.findViewById(R.id.calorieLabelTextView);
        }
    }

    @NonNull
    @Override
    public ProductItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductItemViewHolder holder, int position) {
        ProductItem currentItem = customProducts.get(position);

        holder.productUnitImageView.setImageResource(currentItem.getUnitImageResId());
        holder.productNameTextView.setText(currentItem.getName());
        holder.productUnitTextView.setText(currentItem.getUnit());
        holder.productCalorieValueTextView.setText(currentItem.getCalorieText());

        int state = currentItem.getItemState();
        holder.mainLayout.setBackgroundResource(getCustomBackgroundForState(state));
        holder.calorieLabelTextView.setVisibility(View.VISIBLE);
        holder.productCalorieValueTextView.setScaleY(1.0f);
        holder.productCalorieValueTextView.setTextSize(30);

        if (state == STATE_HIGHLIGHTED) {
            applyHighlightStyle(holder);
        }
    }
    @DrawableRes
    private int getCustomBackgroundForState(int state) {
        switch (state) {
            case STATE_HIGHLIGHTED:
                return R.drawable.message_sty3;
            case STATE_MARKED_FOR_DELETE:
                return R.drawable.message_sty_delete;
            case STATE_CUSTOM:
                return R.drawable.message_sty2;
            case STATE_NORMAL:
            default:
                return R.drawable.message_sty1;
        }
    }
    private void applyHighlightStyle(ProductItemViewHolder holder) {
        holder.calorieLabelTextView.setVisibility(View.GONE);
        holder.productCalorieValueTextView.setScaleY(1f);
        holder.productCalorieValueTextView.setTextSize(35);
    }


    @Override
    public int getItemCount() {
        return customProducts.size();
    }

    public void markItemToDelete(int position) {
        if (isMarkedForDelete(position)) {
            unMarkItem(position);
        } else {
            markItem(position);
        }
    }

    public void markItem(int position) {
        customProducts.get(position).setItemState(STATE_MARKED_FOR_DELETE);
        notifyItemChanged(position);
    }

    public void unMarkItem(int position) {
        customProducts.get(position).setItemState(STATE_CUSTOM);
        notifyItemChanged(position);
    }

    public boolean isMarkedForDelete(int position) {
        return customProducts.get(position).getItemState() == STATE_MARKED_FOR_DELETE;
    }

    public int getDeleteMarkCount() {
        int count = 0;
        for (ProductItem item : customProducts) {
            if (item.getItemState() == STATE_MARKED_FOR_DELETE) {
                count++;
            }
        }
        return count;
    }

    public void removeMarkedItems() {
        for (int i = customProducts.size() - 1; i >= 0; i--) {
            if (customProducts.get(i).getItemState() == STATE_MARKED_FOR_DELETE) {
                customProducts.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void removeAt(int position) {
        customProducts.remove(position);
        notifyItemRemoved(position);
    }
}
