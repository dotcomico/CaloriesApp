package com.example.calories.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.calories.ConsumedProductManager;
import com.example.calories.R;
import com.example.calories.data.models.ConsumedProduct;

import java.util.Calendar;
import java.util.Objects;


public class ConsumedProductEditingDialog {

    private Dialog dialog;
    private final Context context;
    private ConsumedProduct consumedProduct;
    private ConsumedProductManager consumedProductManager;
    private Calendar calendar;

    // UI Components
    private TextView consumedProductNameTv;
    private EditText consumedProductNewAmountTv;
    private ImageView increaseEditImg, decreaseEditImg;
    private Button saveEdit;
    private OnEditCompleteListener listener;
    private boolean listenersSetup = false;

    private static final double UNIT_INCREMENT = 1.0;
    private static final double UNIT_HALF = 0.5;
    private static final double UNIT_QUARTER = 0.25;
    private static final double WEIGHT_VOLUME_INCREMENT = 50.0;
    private static final int CALCULATION_MOD_UNIT = 1;
    private static final int CALCULATION_MOD_WEIGHT_VOLUME = 2;

    public interface OnEditCompleteListener {
        void onEditComplete();  // כשמשתמש שומר
    }

    public void setOnEditCompleteListener(OnEditCompleteListener listener) {
        this.listener = listener;
    }
    public ConsumedProductEditingDialog(Context context ) {
        this.context = context;
        createDialog();
    }

    private void createDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_editing_product_consumed);

        Objects.requireNonNull(dialog.getWindow()).setLayout(  ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
        dialog.getWindow().setGravity( Gravity.BOTTOM);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        initViews();

    }
    private void initViews() {
        consumedProductNameTv = dialog.findViewById(R.id.consumedProductNameTv);
        consumedProductNewAmountTv = dialog.findViewById(R.id.consumedProductNewAmountTv);
        increaseEditImg = dialog.findViewById(R.id.increaseEditImg);
        decreaseEditImg = dialog.findViewById(R.id.decreaseEditImg);
        saveEdit = dialog.findViewById(R.id.saveEdit);
    }

    public void show(ConsumedProduct consumedProduct, Calendar cld ,  ConsumedProductManager manager) {
        if (consumedProduct == null || manager == null || cld == null) {
            return;
        }
        this.calendar = cld;
        this.consumedProduct = consumedProduct;
        this.consumedProductManager=manager;

        dialog.show();
        setupData();
        setupListeners();
    }
    private void setupData() {
        if (consumedProduct != null && consumedProduct.getProductItem() != null) {
            consumedProductNameTv.setText(consumedProduct.getProductItem().getName());
            consumedProductNewAmountTv.setText(String.valueOf(consumedProduct.getAmount()));
        }
    }
    private void setupListeners() {
        if (listenersSetup) return;

        saveEdit.setOnClickListener(v -> {
            String amountText = consumedProductNewAmountTv.getText().toString().trim();
            if (!isValidAmount(amountText)) {
                return;
            }

            try {
                double newAmount = Double.parseDouble(amountText);
                consumedProductManager.editItemAmountById(newAmount, consumedProduct.getId(), calendar);
                if (listener != null) {
                    listener.onEditComplete();
                }
                dialog.dismiss();
            } catch (NumberFormatException e) {
                // הודעת שגיאה
            }
        });

        increaseEditImg.setOnClickListener(v -> adjustAmount(true));
        decreaseEditImg.setOnClickListener(v -> adjustAmount(false));

        // ניקוי טקסט בלחיצה על EditText
        consumedProductNewAmountTv.setOnClickListener(v ->
                consumedProductNewAmountTv.setText(""));

        listenersSetup = true; }

    private void adjustAmount(boolean increase) {
        if (consumedProduct == null || consumedProduct.getProductItem() == null) {
            return;
        }

        String currentText = consumedProductNewAmountTv.getText().toString().trim();
        double currentAmount = 0;

        if (!currentText.isEmpty()) {
            try {
                currentAmount = Double.parseDouble(currentText);
            } catch (NumberFormatException e) {
                currentAmount = 0; // ערך ברירת מחדל
            }
        }
        String unit = consumedProduct.getProductItem().getUnit();
        String newAmountText = calculateNewAmount(currentAmount, increase, unit);
        consumedProductNewAmountTv.setText(newAmountText);
    }
    private String calculateNewAmount(double currentAmount, boolean increase, String unit) {
        if (calculationMod(unit) == CALCULATION_MOD_UNIT) {
            return calculateUnitAmount(currentAmount, increase);

        } else if (calculationMod(unit) == CALCULATION_MOD_WEIGHT_VOLUME) {
            return calculateWeightVolumeAmount(currentAmount, increase);
        }
        return String.valueOf(currentAmount);
    }



    private String calculateUnitAmount(double currentAmount, boolean increase) {
        if (increase) {
            if (currentAmount >= UNIT_INCREMENT) return String.valueOf(currentAmount + UNIT_INCREMENT);
            if (currentAmount == UNIT_HALF) return String.valueOf(UNIT_INCREMENT);
            if (currentAmount == UNIT_QUARTER) return String.valueOf(UNIT_HALF);
            if (currentAmount == 0) return String.valueOf(UNIT_QUARTER);
        } else {
            if (currentAmount - UNIT_INCREMENT >= UNIT_INCREMENT) return String.valueOf(currentAmount - UNIT_INCREMENT);
            if (currentAmount == UNIT_INCREMENT) return String.valueOf(UNIT_HALF);
            if (currentAmount == UNIT_HALF) return String.valueOf(UNIT_QUARTER);
            if (currentAmount == UNIT_QUARTER) return "0";
        }
        return String.valueOf(currentAmount);
    }
    private String calculateWeightVolumeAmount(double currentAmount, boolean increase) {
        if (increase) {
            return String.valueOf(currentAmount + WEIGHT_VOLUME_INCREMENT);
        } else if (currentAmount >= WEIGHT_VOLUME_INCREMENT) {
            return String.valueOf(currentAmount - WEIGHT_VOLUME_INCREMENT);
        }
        return String.valueOf(currentAmount);
    }

    private int calculationMod(String unit) {
        return (unit.equals("100 גרם") || unit.equals("100 מל") || unit.equals("קלוריות")) ? 2 : 1;
    }
    private boolean isValidAmount(String amountText) {
        return !amountText.isEmpty() && amountText.matches("\\d+(\\.\\d+)?");
    }

    public void close(){
        dialog.dismiss();
    }
    public boolean isClosed(){
        return !dialog.isShowing();
    }
}