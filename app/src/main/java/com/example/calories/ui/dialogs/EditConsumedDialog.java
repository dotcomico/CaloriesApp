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


public class EditConsumedDialog {

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
    public interface OnEditCompleteListener {
        void onEditComplete();  // כשמשתמש שומר
    }

    public void setOnEditCompleteListener(OnEditCompleteListener listener) {
        this.listener = listener;
    }
    public EditConsumedDialog(Context context ) {
        this.context = context;
        createDialog();
    }

    private void createDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_consumed_edit);

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
        this.calendar = cld;
        this.consumedProduct = consumedProduct;
        this.consumedProductManager=manager;

        dialog.show();
        setupData();
        setupListeners();
    }
    private void setupData() {
        consumedProductNameTv.setText(consumedProduct.getProductItem().getName());
        consumedProductNewAmountTv.setText(String.valueOf(consumedProduct.getAmount()));
    }
    private void setupListeners() {
        saveEdit.setOnClickListener(v -> {
            String amountText = consumedProductNewAmountTv.getText().toString().trim();
            if (!amountText.isEmpty()) {
                double newAmount = Double.parseDouble(amountText);
                consumedProductManager.editItemAmountById(newAmount, consumedProduct.getId(), calendar);

                if (listener != null) {
                    listener.onEditComplete(); // קוראים למתודה
                }

                dialog.dismiss();
            }
        });

        increaseEditImg.setOnClickListener(v -> adjustAmount(true));
        decreaseEditImg.setOnClickListener(v -> adjustAmount(false));

        // ניקוי טקסט בלחיצה על EditText
        consumedProductNewAmountTv.setOnClickListener(v ->
                consumedProductNewAmountTv.setText(""));
    }

    private void adjustAmount(boolean increase) {
        String currentText = consumedProductNewAmountTv.getText().toString().trim();
        double currentAmount = currentText.isEmpty() ? 0 : Double.parseDouble(currentText);

        String unit = consumedProduct.getProductItem().getUnit();
        String newAmountText = calculateNewAmount(currentAmount, increase, unit);
        consumedProductNewAmountTv.setText(newAmountText);
    }
    private String calculateNewAmount(double currentAmount, boolean increase, String unit) {
        if (calculationMod(unit) == 1) {
            if (increase) {
                if (currentAmount >= 1) return String.valueOf(currentAmount + 1);
                if (currentAmount == 0.5) return "1";
                if (currentAmount == 0.25) return "0.5";
                if (currentAmount == 0) return "0.25";
            } else {
                if (currentAmount - 1 >= 1) return String.valueOf(currentAmount - 1);
                if (currentAmount == 1) return "0.5";
                if (currentAmount == 0.5) return "0.25";
                if (currentAmount == 0.25) return "0";
            }
        } else if (calculationMod(unit) == 2) {
            if (increase && currentAmount >= 0) return String.valueOf(currentAmount + 50);
            if (!increase && currentAmount - 50 >= 0) return String.valueOf(currentAmount - 50);
        }
        return String.valueOf(currentAmount);
    }
    private int calculationMod(String unit) {
        return (unit.equals("100 גרם") || unit.equals("100 מל") || unit.equals("קלוריות")) ? 2 : 1;
    }

    public void close(){
        dialog.dismiss();
    }
    public boolean isClose(){
        return !dialog.isShowing();
    }
}