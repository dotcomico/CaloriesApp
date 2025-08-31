package com.example.calories.ui.dialogs;

import static com.example.calories.utils.Utility.makeToast;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.example.calories.R;
import com.example.calories.data.models.Product;
import com.example.calories.data.storage.ProductStorageManager;
import com.example.calories.ui.views.UnitSelectorView;

import java.util.Objects;

public class CustomProductDialog {

    private Dialog dialog;
    private final Context context;
    ProductStorageManager productStorageManager;
    private OnCustomProductItemListener listener;
    private boolean listenersSetup = false;

    // בראש המחלקה CustomProductDialog, לפני הבנאי (constructor)

    private LinearLayout ly_productCreationForm;
    private LinearLayout ly_customProductBottomSheet;
    private ImageView iv_collapseBottomSheet;
    private EditText newProductNameEditText;
    private EditText newProductCaloriesEditText;
    private SearchView selfSearchSearchView;
    private Button saveNewProductItemButton, webSearchSuggestion;
    private ImageView   saveAndStay;
    private UnitSelectorView unitSelectorView;
    private BarcodeDialogHandler barcodeDialogHandler;


    public interface OnCustomProductItemListener {
        void onItemCreated();  // כשמשתמש שומר
        void onSearchSuggestionClicked(String suggestion);
    }
    public void OnCustomProductCreatedListener(OnCustomProductItemListener listener) {
        this.listener = listener;
    }
    public CustomProductDialog(Context context ) {
        this.context = context;
        createDialog();
    }

    private void createDialog() {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_product_custom);

            Objects.requireNonNull(dialog.getWindow()).setLayout(  ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
            dialog.getWindow().setGravity( Gravity.BOTTOM);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            initViews();
}

    private void initViews() {
        ly_productCreationForm =dialog.findViewById( R.id.ly_productCreationForm);
        ly_customProductBottomSheet = dialog.findViewById( R.id.ly_customProductBottomSheet);
        iv_collapseBottomSheet =dialog.findViewById( R.id.iv_collapseBottomSheet);
        newProductNameEditText = dialog.findViewById( R.id.newProductNameEditText );
        newProductCaloriesEditText = dialog.findViewById( R.id.newProductCaloriesEditText );
        selfSearchSearchView = dialog.findViewById( R.id.selfSearchSearchView);
        saveNewProductItemButton =dialog.findViewById( R.id.saveNewProductItemButton);
        webSearchSuggestion =dialog.findViewById( R.id.webSearchSuggestion);
        saveAndStay =dialog.findViewById( R.id.saveAndStay);
        unitSelectorView = dialog.findViewById(R.id.unit_selector);
    }

    public void show(ProductStorageManager productStorageManager) {
        this.productStorageManager= productStorageManager;
        barcodeDialogHandler =new BarcodeDialogHandler(context);

        dialog.show();
        setupData();
        setupListeners();
    }
    private void setupData() {

    }
    private void setupListeners() {
        if (listenersSetup) return;

        saveNewProductItemButton.setOnClickListener(this::saveNewProduct);

        saveAndStay.setOnClickListener(this::saveNewProduct);

        webSearchSuggestion.setOnClickListener( view -> {
            String searchSuggestion = webSearchSuggestion.getText().toString().trim();
            if (searchSuggestion.isEmpty()){
                return;
            }

            if (listener != null) {
                listener.onSearchSuggestionClicked(searchSuggestion);
            }
            webSearchSuggestion.setVisibility( View.GONE );
        } );

        selfSearchSearchView.setOnClickListener( view -> {selfSearchSearchView.setIconified(false);} );

        newProductNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                newProductNameEditText.setBackgroundResource( R.drawable.sty_2 );

            }
            @Override
            public void afterTextChanged(Editable editable) {
                String searchQuery = newProductNameEditText.getText().toString().trim();
                webSearchSuggestion.setText(searchQuery);
                if(!searchQuery.isEmpty()){
                    webSearchSuggestion.setVisibility( View.VISIBLE );
                    webSearchSuggestion.setText( newProductNameEditText.getText().toString() + " " + "קלוריות" );
                }else{
                    webSearchSuggestion.setVisibility( View.GONE );
                }
                /* if (webview.getUrl().toString().equals( "https://www.google.com/search?q=" + et_food.getText().toString() +" "+ "קלוריות")){
                    moreSerch.setVisibility( View.GONE );
                }
                */

            }
        });

        newProductCaloriesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                newProductCaloriesEditText.setBackgroundResource( R.drawable.sty_2 );
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

        listenersSetup = true;
    }

    private void saveNewProduct(View v) {

        String newProductName = newProductNameEditText.getText().toString().trim();
        String newProductCalories = newProductCaloriesEditText.getText().toString().trim();
        String newProductUnit = unitSelectorView.getUnit();

        if (newProductName.isEmpty()) {
            newProductNameEditText.setBackgroundResource( R.drawable.sty_red );
            return;
        }
        if (newProductCalories.isEmpty()) {
            newProductCaloriesEditText.setBackgroundResource( R.drawable.sty_red );
            return;
        }

        if ( isValidAmount(newProductCalories)){
            newProductCaloriesEditText.setBackgroundResource( R.drawable.sty_red );
            return;
        }

        addToFoodList();

        newProductNameEditText.setBackgroundResource( R.drawable.sty_2 );
        newProductCaloriesEditText.setBackgroundResource( R.drawable.sty_2 );
        newProductCaloriesEditText.setText( "" );

        if (listener != null && v==saveNewProductItemButton) {
            listener.onItemCreated();
        }

        makeToast( "\"" + newProductName +"\"" +" "+ newProductUnit +" נוסף למערכת!", context);

    }
    private boolean isValidAmount(String amountText) {
        return !amountText.isEmpty() && amountText.matches("\\d+(\\.\\d+)?");
    }
    private void addToFoodList(){
        //הוסף מזון לרשימת מוצרים שלי (רק אם אני בחיפוש עצמי או עורך מוצר קיים)
        Product item;
        String name = newProductNameEditText.getText().toString().trim();
        String unit = unitSelectorView.getUnit();
        String calories = newProductCaloriesEditText.getText().toString().trim();
        String barcode = barcodeDialogHandler.getBarcodeEditText().getText().toString().trim();

        item = new Product(   1 , name , unit , calories , barcode );

        productStorageManager.addProductAndSave(item);
    }
    public void close(){
        dialog.dismiss();
    }
    public boolean isClosed(){
        return !dialog.isShowing();
    }
}
