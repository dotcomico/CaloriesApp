package com.example.calories.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calories.R;
import com.example.calories.data.models.Product;
import com.example.calories.data.storage.UnitManager;

import java.util.ArrayList;
import static com.example.calories.utils.AppConstants.*;
public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.ProductItemViewHolder> {

    private final ArrayList<Product> customProducts;

    public ProductItemAdapter(ArrayList<Product> productList) {
        this.customProducts = productList;
    }

    public static class ProductItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productUnitImageView;
        private final ConstraintLayout mainLayout;
        private final TextView productNameTextView;
        private final TextView productUnitTextView;
        private final TextView productCalorieValueTextView;
        private final TextView productDescriptionTextView;
        private final ConstraintLayout calorieContainer;

        private final LinearLayout productUnitContainer;

        public ProductItemViewHolder(View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.main);
            productUnitImageView = itemView.findViewById(R.id.productUnitImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productUnitTextView = itemView.findViewById(R.id.productUnitTextView);
            productCalorieValueTextView = itemView.findViewById(R.id.productCalorieValueTextView);
            productDescriptionTextView = itemView.findViewById(R.id.productDescriptionTextView);
            calorieContainer = itemView.findViewById(R.id.calorieContainer);
            productUnitContainer = itemView.findViewById(R.id.productUnitContainer);
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
        Product currentItem = customProducts.get(position);

        holder.productUnitImageView.setImageResource(UnitManager.getUnitImageResId(currentItem.getUnit()));
        holder.productNameTextView.setText(currentItem.getName());
        holder.productUnitTextView.setText(currentItem.getUnit());
        holder.productCalorieValueTextView.setText(currentItem.getCalorieText());

        int state = currentItem.getItemState();
        holder.mainLayout.setBackgroundResource(getCustomBackgroundForState(state));
        holder.productCalorieValueTextView.setScaleY(1f);
        holder.productCalorieValueTextView.setTextSize(25);
        holder.productDescriptionTextView.setVisibility(View.GONE);
        holder.productDescriptionTextView.setTextColor(Color.parseColor("#999999"));
        holder.calorieContainer.setBackgroundResource(R.drawable.calories_product_item_display);
        holder.productUnitContainer.setVisibility(View.VISIBLE);

        if (state == PRODUCT_STATE_SELF_SEARCH) {
            holder.productNameTextView.setText("לא מה שחיפשת?");
            holder.productDescriptionTextView.setText("המשך בחיפוש עצמי");
            holder.productCalorieValueTextView.setText("\uD83D\uDD0D");
            applyHighlightStyle(holder);
            holder.productDescriptionTextView.setVisibility(View.VISIBLE);
            holder.productDescriptionTextView.setTextColor(Color.WHITE);
            holder.calorieContainer.setBackground(null);
            holder.productUnitContainer.setVisibility(View.GONE);
        }
    }



    @DrawableRes
    private int getCustomBackgroundForState(int state) {
        switch (state) {
            // message_sty1 , message_sty_delete , message_sty3
            case PRODUCT_STATE_SELF_SEARCH:
                return R.drawable.product_item_background_self_search;
            case PRODUCT_STATE_MARKED_FOR_DELETE:
                return R.drawable.product_item_background_delete;
            case PRODUCT_STATE_CUSTOM:
                return R.drawable.product_item_background_custom;
            case PRODUCT_STATE_SYSTEM:
            default:
                return R.drawable.product_item_background;
        }
    }
    private void applyHighlightStyle(ProductItemViewHolder holder) {
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
        customProducts.get(position).setItemState(PRODUCT_STATE_MARKED_FOR_DELETE);
        notifyItemChanged(position);
    }

    public void unMarkItem(int position) {
        customProducts.get(position).setItemState(PRODUCT_STATE_CUSTOM);
        notifyItemChanged(position);
    }

    public boolean isMarkedForDelete(int position) {
        return customProducts.get(position).getItemState() == PRODUCT_STATE_MARKED_FOR_DELETE;
    }

    public int getDeleteMarkCount() {
        int count = 0;
        for (Product item : customProducts) {
            if (item.getItemState() == PRODUCT_STATE_MARKED_FOR_DELETE) {
                count++;
            }
        }
        return count;
    }

    public void removeMarkedItems() {
        for (int i = customProducts.size() - 1; i >= 0; i--) {
            if (customProducts.get(i).getItemState() == PRODUCT_STATE_MARKED_FOR_DELETE) {
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
