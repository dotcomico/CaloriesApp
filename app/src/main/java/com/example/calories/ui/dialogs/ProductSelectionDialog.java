package com.example.calories.ui.dialogs;

import static com.example.calories.utils.Utility.clipData;
import static com.example.calories.utils.Utility.hideKeyboardFrom;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calories.ConsumedProductManager;
import com.example.calories.R;
import com.example.calories.data.models.Product;
import com.example.calories.data.storage.ProductStorageManager;
import com.example.calories.utils.Utility;

import java.util.Calendar;
import java.util.Objects;

public class ProductSelectionDialog {

    private Dialog dialog;
    private final Context context;
    private OnProductSelectedListener listener;
    private Product product;
    ConsumedProductManager consumedProductManager;
    private boolean listenersSetup = false;
    private Calendar calendar;

    private LinearLayout ly_productSelectionBottomSheet;
    private ImageView iv_expandProductInfo , iv_increaseAmount , iv_decreaseAmount;
    private TextView tv_caloriesHeader , tv_selectedProductName ,tv_barcodeInfo , tv_unit;
    private Button btn_addToConsumption;
    private EditText et_addAmount;

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
        ly_productSelectionBottomSheet = dialog.findViewById(R.id.ly_productSelectionBottomSheet);
        tv_selectedProductName = dialog.findViewById( R.id.tv_selectedProductName);
        tv_selectedProductName.setMovementMethod( new ScrollingMovementMethod() );
        tv_caloriesHeader = dialog.findViewById( R.id.tv_caloriesHeader);
        tv_unit =dialog.findViewById( R.id.tv_unit);
        btn_addToConsumption =dialog.findViewById( R.id.btn_addToConsumption);
        et_addAmount =dialog.findViewById( R.id.et_addAmount);
        iv_decreaseAmount =dialog.findViewById( R.id.iv_decreaseAmount);

        iv_increaseAmount =dialog.findViewById( R.id.iv_increaseAmount);

        tv_barcodeInfo =dialog.findViewById( R.id.tv_barcodeInfo);

        iv_expandProductInfo = dialog.findViewById( R.id.iv_expandProductInfo);

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
        tv_selectedProductName.setText(product.getName());
        tv_caloriesHeader.setText(product.getCalorieText());
        tv_unit.setText( product.getUnit());

        String s = product.getBarcode();
        if (s != null && s.isEmpty()){
            s= "לא צורף מידע נוסף";
        }
        tv_barcodeInfo.setText( "ברקוד מוצר: " + s );
        tv_barcodeInfo.setVisibility(  View.GONE );

        //האדיט טקסט של כמות יעודכן ל1 אם מדובר בכמות וכו ורק אם מדובר ב100 גרם או מל אז יעודכן ל100
        if ( product.getUnit().equals( "100 גרם" ) || product.getUnit().equals( "100 מל" )  ){
            et_addAmount.setText( "100" );
        }
        else{
            et_addAmount.setText("1");
        }

    }
    private void setupListeners() {
        if (listenersSetup) return;

        btn_addToConsumption.setOnClickListener(v -> {
            String amountText = et_addAmount.getText().toString().trim();
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


        iv_increaseAmount.setOnClickListener(v -> adjustAmount(true));
        iv_decreaseAmount.setOnClickListener(v -> adjustAmount(false));


        iv_expandProductInfo.setOnClickListener( view ->  {
            if (tv_barcodeInfo.getVisibility() == View.GONE )
                tv_barcodeInfo.setVisibility(View.VISIBLE );
            else
                tv_barcodeInfo.setVisibility(View.GONE );
        });

        tv_selectedProductName.setOnClickListener(v -> {
            String productName=   tv_selectedProductName.getText().toString().trim();
            Utility.clipData(productName , context);
            Toast.makeText( context, "הועתק שם מוצר",Toast.LENGTH_SHORT).show();
        });

        et_addAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                String st = null;
                if ( et_addAmount.getText().toString().equals( "." )){ et_addAmount.setText( "" );}
                if ( et_addAmount.getText().toString().matches("")){
                    st=calcolatyCAL(Double.parseDouble( tv_caloriesHeader.getText().toString() ),0, tv_unit.getText().toString());
                }else{
                    st = calcolatyCAL( Double.parseDouble( tv_caloriesHeader.getText().toString() ) ,
                            Double.parseDouble( et_addAmount.getText().toString() ), tv_unit.getText().toString() );

                }
                btn_addToConsumption.setText( st+ "\n"+" הוסף " );

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

        String currentText = et_addAmount.getText().toString().trim();
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
        et_addAmount.setText(newAmountText);
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