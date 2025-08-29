package com.example.calories.ui.dialogs;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.calories.ConsumedProductManager;
import com.example.calories.R;
import com.example.calories.data.models.Product;
import com.example.calories.utils.Utility;
import java.util.Calendar;
import java.util.Objects;

public class ProductSelectionDialog {

    private Dialog dialog;
    private final Context context;
    private Product product;
    ConsumedProductManager consumedProductManager;
    private Calendar calendar;
    private OnProductSelectedListener listener;
    private boolean listenersSetup = false;

    private ImageView expandDetailsImg, increaseEditImg, decreaseEditImg;
    private TextView caloriesHeaderTv, productNameTv, barcodeInfoTv, unitTv;
    private Button saveBtn;
    private EditText amountEt;

    private static final double UNIT_INCREMENT = 1.0;
    private static final double UNIT_HALF = 0.5;
    private static final double UNIT_QUARTER = 0.25;
    private static final double WEIGHT_VOLUME_INCREMENT = 50.0;
    private static final int CALCULATION_MOD_UNIT = 1;
    private static final int CALCULATION_MOD_WEIGHT_VOLUME = 2;

    public interface OnProductSelectedListener {
        void onSaveComplete();  // כשמשתמש שומר
    }
    public void setOnProductSelectedListener(OnProductSelectedListener listener) {
        this.listener = listener;
    }
    public ProductSelectionDialog(Context context ) {
        this.context = context;
        createDialog();
    }

    private void createDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_selection_product);

        Objects.requireNonNull(dialog.getWindow()).setLayout(  ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
        dialog.getWindow().setGravity( Gravity.BOTTOM);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        initViews();

    }
    private void initViews() {
        productNameTv = dialog.findViewById( R.id.tv_selectedProductName);
        productNameTv.setMovementMethod( new ScrollingMovementMethod() );
        caloriesHeaderTv = dialog.findViewById( R.id.tv_caloriesHeader);
        unitTv =dialog.findViewById( R.id.tv_unit);
        saveBtn =dialog.findViewById( R.id.btn_addToConsumption);
        amountEt =dialog.findViewById( R.id.et_addAmount);
        decreaseEditImg =dialog.findViewById( R.id.iv_decreaseAmount);

        increaseEditImg =dialog.findViewById( R.id.iv_increaseAmount);

        barcodeInfoTv =dialog.findViewById( R.id.tv_barcodeInfo);

        expandDetailsImg = dialog.findViewById( R.id.iv_expandProductInfo);

    }

    public void show(Product product, Calendar calendar, ConsumedProductManager consumedProductManager ) {
        this.calendar= calendar;
        this.product = product;
        this.consumedProductManager = consumedProductManager;

        dialog.show();
        setupData();
        setupListeners();
    }
    private void setupData() {
        productNameTv.setText(product.getName());
        caloriesHeaderTv.setText(product.getCalorieText());
        unitTv.setText( product.getUnit());

        String s = product.getBarcode();
        if (s != null && s.isEmpty()){
            s= "לא צורף מידע נוסף";
        }
        barcodeInfoTv.setText( "ברקוד מוצר: " + s );
        barcodeInfoTv.setVisibility(  View.GONE );

        //האדיט טקסט של כמות יעודכן ל1 אם מדובר בכמות וכו ורק אם מדובר ב100 גרם או מל אז יעודכן ל100
        if ( product.getUnit().equals( "100 גרם" ) || product.getUnit().equals( "100 מל" )  ){
            amountEt.setText( "100" );
        }
        else{
            amountEt.setText("1");
        }

    }
    private void setupListeners() {
        if (listenersSetup) return;

        saveBtn.setOnClickListener(v -> {
            String amountText = amountEt.getText().toString().trim();
            if (!isValidAmount(amountText)) {
                return;
            }

            try {
                double newAmount = Double.parseDouble(amountText);
                consumedProductManager.addItem(newAmount, product, calendar);
                if (listener != null) {
                    listener.onSaveComplete();
                }
                dialog.dismiss();
            } catch (NumberFormatException e) {
                // הודעת שגיאה
            }
            ///   add consumption saving method
            if (listener != null) {
                listener.onSaveComplete();
            }
        });


        increaseEditImg.setOnClickListener(v -> adjustAmount(true));
        decreaseEditImg.setOnClickListener(v -> adjustAmount(false));


        expandDetailsImg.setOnClickListener(view ->  {
            if (barcodeInfoTv.getVisibility() == View.GONE )
                barcodeInfoTv.setVisibility(View.VISIBLE );
            else
                barcodeInfoTv.setVisibility(View.GONE );
        });

        productNameTv.setOnClickListener(v -> {
            String productName=   productNameTv.getText().toString().trim();
            Utility.clipData(productName , context);
            Toast.makeText( context, "הועתק שם מוצר",Toast.LENGTH_SHORT).show();
        });

        amountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                String st;
                if ( amountEt.getText().toString().equals( "." )){ amountEt.setText( "" );}
                if ( amountEt.getText().toString().matches("")){
                    st=calcolatyCAL(Double.parseDouble( caloriesHeaderTv.getText().toString() ),0, unitTv.getText().toString());
                }else{
                    st = calcolatyCAL( Double.parseDouble( caloriesHeaderTv.getText().toString() ) ,
                            Double.parseDouble( amountEt.getText().toString() ), unitTv.getText().toString() );

                }
                saveBtn.setText( st+ "\n"+" הוסף " );

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        listenersSetup = true; }

    private void adjustAmount(boolean increase) {
        if (product == null) {
            return;
        }

        String currentText = amountEt.getText().toString().trim();
        double currentAmount = 0;

        if (!currentText.isEmpty()) {
            try {
                currentAmount = Double.parseDouble(currentText);
            } catch (NumberFormatException e) {
                currentAmount = 0; // ערך ברירת מחדל
            }
        }
        String unit = product.getUnit();
        String newAmountText = calculateNewAmount(currentAmount, increase, unit);
        amountEt.setText(newAmountText);
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
    private String calcolatyCAL(double kal, double amount,String type) {
        //פעולה לחישוב קלוריות והוספה לכמות כוללת

        String str_caloria;
        if ( type.equals( "100 גרם" )  || type.equals( "100 מל" )  || type.equals( "קלוריות" ) ){
            str_caloria=(String.format( String.valueOf( (int) ( (kal/100)*amount  /* +temp*/) )));
        }
        else{
            str_caloria = (String.format( String.valueOf( (int) ( kal* amount /* +temp*/) )));
        }
        return str_caloria;
    }
    public void close(){
        dialog.dismiss();
    }
    public boolean isClosed(){
        return !dialog.isShowing();
    }
}