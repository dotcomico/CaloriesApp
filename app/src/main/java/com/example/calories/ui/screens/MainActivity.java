package com.example.calories.ui.screens;

import static com.example.calories.utils.SystemProducts_Utils.getSystemProductsArr;
import static com.example.calories.utils.Utility.clipData;
import static com.example.calories.utils.Utility.isNumeric;
import static com.example.calories.utils.Utility.makeToast;
import static com.example.calories.utils.Utility.startNewActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calories.ConsumedProductManager;
import com.example.calories.data.models.ConsumedProduct;
import com.example.calories.data.models.Product;
import com.example.calories.data.storage.ProductStorageManager;
import com.example.calories.ui.dialogs.BarcodeDialogHandler;
import com.example.calories.ui.dialogs.EditConsumedProductDialog;
import com.example.calories.ui.utils.CaptureAct;
import com.example.calories.ui.adapters.ConsumedItemAdapter;
import com.example.calories.ui.adapters.ProductItemAdapter;
import com.example.calories.R;
import com.example.calories.ui.adapters.RecyclerItemClickListener;
import com.example.calories.ui.views.UnitSelectorView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher, View.OnTouchListener {

    //--------------- CalorieTrackerView ---------------
    private RecyclerView consumedProductsRecyclerView;
    private RecyclerView.LayoutManager consumedProductsLayoutManager;

    private ConsumedProduct consumedProduct_edit;

    EditConsumedProductDialog editConsumedProductDialog;
    private String lastClickedId;

    //--------------- ProductCatalogView ---------------

    private ArrayList<Product> systemProductList = new ArrayList<>();//רשימת מוצרים (מערכת)
    private ArrayList<Product> customProducts = new ArrayList<>();//רשימת מוצרים שיצר המשתמש
    private ArrayList<Product> filteredProducts = new ArrayList<>();//רשימת מוצרים מסוננת (לפי חיפוש)
    private RecyclerView productsRecyclerView;
    private RecyclerView.Adapter productsAdapter;
    private RecyclerView.LayoutManager productsLayoutManager;
    private LinearLayout ly_productSelectionBottomSheet;
    private ImageView iv_closeBottomSheet , iv_expandProductInfo , iv_increaseAmount , iv_decreaseAmount;
    private TextView tv_caloriesHeader , tv_selectedProductName ,tv_barcodeInfo , tv_unit;
    private SearchView mainSearchView;
    private EditText et_addAmount;
    private Button btn_addToConsumption;
    private Product temp_exampleItem=null;

    //--------------- CustomProductView  ---------------
    private WebView webview;
    private SearchView selfSearchSearchView;
    private Button saveNewProductItemButton, webSearchSuggestion;
    private EditText newProductNameEditText, newProductCaloriesEditText;
    private LinearLayout ly_productCreationForm;
    private LinearLayout ly_customProductBottomSheet;
    private ImageView iv_collapseBottomSheet , saveAndStay;
    private UnitSelectorView unitSelectorView;

    private BarcodeDialogHandler barcodeDialogHandler;


    //--------------- others  ---------------
    private Calendar calendar;
    private final CaptureAct captureAct = new CaptureAct();

    private ProductStorageManager productStorageManager;

    // dialog

    ConsumedProductManager consumedProductManager;
    // top bar
    private Button btn_lastDay,btn_nextDay;

    //  settings
    private LinearLayout ly_settings;


    private ImageView iv_backFromSelfSearchToMain, iv_myProdacts_SS, iv_showSelfSearchBar, iv_selfSearch_round ,  iv_selfAdd, iv_barcodeScan, iv_goToSelfSearch,
            iv_backToMain,iv_settings, iv_startBarcodescan;
    private RelativeLayout rl_selfSearch,rl_top, rl_selfSearchTopBar,rl_mainInformation;
    private TextView tv_date, tv_returnToMainScreen;
    private int calcolaty_mod;


    private TextView tv_clearMainCaloriesList, tv_totalCalories;
    private ImageView iv_myProducts;

    private Animation slide_in_bottom,slide_out_bottom;
    private Dialog dialog ;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );


        calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        findViewAndMore();
        changeBarColor(rl_top);

        barcodeDialogHandler =new BarcodeDialogHandler(this);
        productStorageManager  = new ProductStorageManager(this);
        consumedProductManager = new ConsumedProductManager(this);
        editConsumedProductDialog =new EditConsumedProductDialog(MainActivity.this);
        //יצירת רשימת המוצרים
        updateMainList();
        //מיון לפי א"ב
        //      sortArrayList();



        newProductCaloriesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                if ( newProductCaloriesEditText.getText().toString().equals( "." )){
                    et_addAmount.setText( "" );
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        } );
        et_addAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                String st = null;
                if ( et_addAmount.getText().toString().equals( "." )){ et_addAmount.setText( "" );}
                if ( et_addAmount.getText().toString().matches("")){     st=calcolatyCAL(Double.parseDouble( tv_caloriesHeader.getText().toString() ),0, tv_unit.getText().toString());
                }else{
                    //   if (TextUtils. isDigitsOnly(et_amounttt.getText().toString() )) {
                    st = calcolatyCAL( Double.parseDouble( tv_caloriesHeader.getText().toString() ) ,
                            Double.parseDouble( et_addAmount.getText().toString() ), tv_unit.getText().toString() );
                    //    }

                }
                //     if (TextUtils. isDigitsOnly(et_amounttt.getText().toString() )) {
                btn_addToConsumption.setText( st+ "\n"+" הוסף " );
                      //   }else{  btn_addFood.setText( " הוסף " );}

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //פעולות לחיצה על איברי הרשימה
        productsRecyclerView.addOnItemTouchListener( new RecyclerItemClickListener(MainActivity.this, productsRecyclerView
                ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                if (!(filteredProducts.get( position ).getItemState() ==999)) {
                    temp_exampleItem= filteredProducts.get( position );
                    showFoodDitals(filteredProducts.get( position ));
                    hideKeyboard();
                }
                else{
                    //פתח חיפוש עצמי
                    startWebSearch();
                }
            }



            @Override public void onLongItemClick(View view, int position) {
            }
        }) );
        //פעולות לחיצה על איברי הרשימה- קלוריות מסך ראשי
        consumedProductsRecyclerView.addOnItemTouchListener( new RecyclerItemClickListener(MainActivity.this, consumedProductsRecyclerView
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view , int position)
            {
                //עריכת פריט
                consumedProduct_edit = consumedProductManager.getConsumedProductsOfDay().get( position );
                editConsumedProductDialog.show(consumedProduct_edit, calendar , consumedProductManager);
            }

            @Override
            public void onLongItemClick(View view , int position) {
                if (editConsumedProductDialog.isClose()){
                    String id = consumedProductManager.getConsumedProductsOfDay().get(position).getId();
                    deleteConsumedProductById(id);
                }
            }
        } ) );
        //פעולת חיפוש
        mainSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override//כאשר לוחצים חפש
            public boolean onQueryTextSubmit(String s) {
                if (isNumeric( s )){
                    selfAddActions();
                }else if (filteredProducts.isEmpty()){
                    //אם הרשימה ריקה(מוצר לא נמצא) תפתח חיפוש עצמי
                    startWebSearch(); }

                return false;
            }
            @Override//כאשר החיפוש מתבצע
            public boolean onQueryTextChange(String s) {
                // עדכון רשימת מזון לפי חיפוש בזמן הקלדה
                iv_backToMain.setVisibility(View.VISIBLE);

                searchInFoodList(s);

                if (isNumeric( s )){
                    mainSearchView.setBackgroundResource( R.drawable.sty_3_purple );
                    rl_mainInformation.setVisibility(View.VISIBLE);
                    iv_selfAdd.setVisibility(View.VISIBLE);
                    iv_selfSearch_round.setVisibility( View.GONE );
                    iv_startBarcodescan.setVisibility( View.GONE );
                    rl_selfSearch.setVisibility(View.GONE);
                    productsRecyclerView.setVisibility(View.GONE);
                    iv_backToMain.setImageResource( R.drawable.ic_baseline_arrow_circle_right_purple );

                }
                else if (filteredProducts.isEmpty()){  // אם הרשימה ריקה (אין מוצרים)- הצעה לחיפוש עצמי
                    mainSearchView.setBackgroundResource( R.drawable.sty_orang3);
                    rl_mainInformation.setVisibility(View.GONE);
                    iv_selfAdd.setVisibility(View.GONE);
                    iv_selfSearch_round.setVisibility( View.VISIBLE );
                    iv_startBarcodescan.setVisibility( View.GONE );
                    rl_selfSearch.setVisibility(View.VISIBLE);
                    productsRecyclerView.setVisibility(View.GONE);
                    iv_backToMain.setImageResource( R.drawable.baseline_arrow_circle_right_oreng );

                } else {
                    mainSearchView.setBackgroundResource( R.drawable.sty_3 );
                    rl_mainInformation.setVisibility(View.GONE);
                    iv_selfAdd.setVisibility(View.GONE);
                    iv_selfSearch_round.setVisibility( View.GONE );
                    iv_startBarcodescan.setVisibility( View.VISIBLE );
                    rl_selfSearch.setVisibility(View.GONE);
                    productsRecyclerView.setVisibility(View.VISIBLE);
                    iv_backToMain.setImageResource( R.drawable.ic_baseline_arrow_circle_right_blue );
                }

                return false;
            }
        } );
        selfSearchSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                startinternetWebSearch();
                return  false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        //סגירת מסכים לא נחוצים בכניסה התחלתית למסך
        webview.setVisibility(View.GONE);
        ly_customProductBottomSheet.setVisibility(View.GONE);
        ly_productSelectionBottomSheet.setVisibility(View.GONE);
        iv_backToMain.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.GONE);
        rl_selfSearch.setVisibility(View.GONE);
        rl_mainInformation.setVisibility(View.VISIBLE);

        et_addAmount.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers
        newProductCaloriesEditText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers

        Objects.requireNonNull(getSupportActionBar()).hide();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean isSelfSearch = extras.getBoolean( "selfSearch" );
            if (isSelfSearch) {
                startWebSearch();
                rl_mainInformation.setVisibility(View.GONE);
                closeMain();
            }
        }

        // showDialog();


        editConsumedProductDialog.setOnEditCompleteListener(new EditConsumedProductDialog.OnEditCompleteListener() {
            @Override
            public void onEditComplete() {
                 // רענון הרשימה
                refreshConsumedProductsList();
            }
        });
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View view) {
        if (view == iv_expandProductInfo) {
            if (tv_barcodeInfo.getVisibility() == View.GONE )
                tv_barcodeInfo.setVisibility(  View.VISIBLE );
            else
                tv_barcodeInfo.setVisibility(  View.GONE );
        }

        if(view== webSearchSuggestion){
            startinternetWebSearchDotan(webSearchSuggestion.getText().toString());
            webSearchSuggestion.setVisibility( View.GONE );
            hideKeyboard();
            ly_productCreationForm.setVisibility( View.GONE );
            iv_showSelfSearchBar.setVisibility( View.VISIBLE );
        }

        if(view== iv_myProducts || view == iv_myProdacts_SS){
            startNewActivity(MainActivity.this, MyProductActivity.class);

        }
        if (view== iv_barcodeScan){
            showCustomDialog();
        }
        if (view==iv_selfAdd) {
            selfAddActions();
        }
        if(view== iv_startBarcodescan){
            openMain();
            scanCode();}
        if(view==btn_nextDay){
            //יקרה רק אם כל שאר המסכים סגורים
            if (editConsumedProductDialog.isClose()&& ly_productSelectionBottomSheet.getVisibility()==View.GONE&& ly_customProductBottomSheet.getVisibility()==View.GONE&&rl_selfSearch.getVisibility()==View.GONE){
                calendar.add(Calendar.DAY_OF_MONTH, 1); //Adds a day
                tv_date.setText( new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime()));
                refreshConsumedProductsList();
            }
        }
        if(view==btn_lastDay){
            //יקרה רק אם כל שאר המסכים סגורים
            if (editConsumedProductDialog.isClose()&& ly_productSelectionBottomSheet.getVisibility()==View.GONE&& ly_customProductBottomSheet.getVisibility()==View.GONE&&rl_selfSearch.getVisibility()==View.GONE) {
                calendar.add( Calendar.DAY_OF_MONTH , -1 ); //Goes to previous day
                tv_date.setText( new SimpleDateFormat( "dd-MM-yyyy" ).format( calendar.getTime() ) );
                refreshConsumedProductsList();
            }
        }
        if(view == iv_closeBottomSheet){cancelFoodAdd();
        }
        //  if(view == iv_delete2 || view == iv_backFromSelfSearchToMain){
        if( view == iv_backFromSelfSearchToMain){

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                boolean isSelfSearch = extras.getBoolean( "selfSearch" );
                if (isSelfSearch) {
                    finish();
                }
            }else {
                //ורוד
                cancelNewFoodAdd();
                openMain();
            }


        }
        if (view == iv_showSelfSearchBar){
            ly_productCreationForm.setVisibility( View.VISIBLE );
            iv_showSelfSearchBar.setVisibility( View.GONE );
        }
        if (view == iv_collapseBottomSheet){
            hideKeyboard();
            ly_productCreationForm.setVisibility( View.GONE );
            iv_showSelfSearchBar.setVisibility( View.VISIBLE );

        }

        if (view == btn_addToConsumption) {
            if (!isContainsLetters( et_addAmount.getText().toString() )) {
                if (!et_addAmount.getText().toString().equals( "" ) && !et_addAmount.getText().toString().equals( "0" ) && !(Double.parseDouble( et_addAmount.getText().toString() ) < 0)) { //אם כמות לא כלום או 0
                    //פעולה לחישוב קלוריות והוספה לכמות כוללת
                    double kal = Double.parseDouble( tv_caloriesHeader.getText().toString() );
                    double amount = Double.parseDouble( et_addAmount.getText().toString() );
                    //     double temp=Double.parseDouble( str_caloria );
                    String str_caloria;
                    if (calcolaty_mod == 2) {
                        str_caloria = (String.format( String.valueOf( (int) ((kal / 100) * amount  /* +temp*/) ) ));
                    } else {
                        str_caloria = (String.format( String.valueOf( (int) (kal * amount /* +temp*/) ) ));
                    }
                    addConsumedProductToList( amount , Integer.parseInt( str_caloria ) );
                    updateTotalCalories();
                    mainSearchView.setVisibility( View.VISIBLE );
                    mainSearchView.setQuery( "" , true );
                    mainSearchView.setIconified( true );
                    hideKeyboard();
                    cancelFoodAdd();
                    rl_mainInformation.setVisibility( View.VISIBLE );
                    webview.setVisibility( View.GONE );
                    ly_customProductBottomSheet.setVisibility( View.GONE );
                    ly_productSelectionBottomSheet.setVisibility( View.GONE );
                    iv_backToMain.setVisibility( View.GONE );
                    productsRecyclerView.setVisibility( View.GONE );
                    rl_selfSearch.setVisibility( View.GONE );
                    if (!consumedProductManager.getConsumedProductsOfDay().isEmpty()) {
                        consumedProductsRecyclerView.smoothScrollToPosition( consumedProductManager.getConsumedProductsOfDay().size() - 1 );
                    }
                }
            }
        }

        if (view == saveAndStay){
            saveNewProduct();
        }
        if (view == saveNewProductItemButton){

            saveNewProduct();

            hideKeyboard();
            cancelFoodAdd();
            mainSearchView.setVisibility( View.VISIBLE );
            mainSearchView.setIconified(true);
            showFoodDitals(getLastItem());
            openMain();
        }
        if (view == mainSearchView){
            //  mRecyclerView.setVisibility(View.VISIBLE);
            ///    et_food.setText( "" );
            //   rl_mainInformation.setVisibility(View.GONE);
            mainSearchView.setIconified(false);
        }
        if (view == selfSearchSearchView){
            selfSearchSearchView.setIconified(false);
        }
        if (view== iv_goToSelfSearch){
            startWebSearch();
            /*
               Intent i = new Intent(this, MainActivity.class);
            i.putExtra("selfSearch", true );
            startActivity( i );
             */
        }
        if (view==iv_selfSearch_round){
            startWebSearch();
            /*
               Intent i = new Intent(this, MainActivity.class);
            i.putExtra("selfSearch", true );
            startActivity( i );
             */
        }
        if (view == iv_backToMain){
            backToMain();
        }
        if (view == tv_clearMainCaloriesList){
            //   str_caloria =("0");
            //  SharedPreferences.Editor editor = sp.edit();
            //         editor.putString( "caloria" , str_caloria );
            //     editor.commit();
            //   text.setText( str_caloria );
            tv_totalCalories.setText("0");
            clearConsumedProductData();
            Toast.makeText( getBaseContext(), "רשימת קלוריות שנצרכו נמחקה (מסך ראשי)",Toast.LENGTH_SHORT).show();
            restartApp();
        }
        if (view== tv_returnToMainScreen){
            ly_settings.setVisibility( View.GONE );
        }
        if (view==iv_settings) {
            ly_settings.setVisibility( View.VISIBLE );
        }
        if (view== iv_increaseAmount){
            String st= et_addAmount.getText().toString();
            if (!st.matches( "" )){
                if (calculationMod(tv_unit.getText().toString())==1){
                    if (Double.parseDouble(et_addAmount.getText().toString())>=1){   st= ""+(Double.parseDouble(et_addAmount.getText().toString())+1);}
                    if (Double.parseDouble(et_addAmount.getText().toString())==0.5){ st="1";}
                    if (Double.parseDouble(et_addAmount.getText().toString())==0.25){ st="0.5";}
                    if (Double.parseDouble(et_addAmount.getText().toString())==0){st="0.25";}
                }
                if (calculationMod(tv_unit.getText().toString())==2&&Double.parseDouble(et_addAmount.getText().toString())>=0){st= ""+(Double.parseDouble(et_addAmount.getText().toString())+50);}
            }else {st="0";}
            et_addAmount.setText(st);

        }
        if (view== iv_decreaseAmount){
            String st= et_addAmount.getText().toString();
            if (!st.matches( "" )){
                if (calculationMod(tv_unit.getText().toString())==1 ){
                    if (Double.parseDouble(et_addAmount.getText().toString())-1>=1){
                        st= ""+(Double.parseDouble(et_addAmount.getText().toString())-1); }
                    if (Double.parseDouble(et_addAmount.getText().toString())==1){ st="0.5";}
                    if (Double.parseDouble(et_addAmount.getText().toString())==0.5){ st="0.25";}
                    if (Double.parseDouble(et_addAmount.getText().toString())==0.25){st="0";}
                }
                if (calculationMod(tv_unit.getText().toString())==2&&Double.parseDouble(et_addAmount.getText().toString())-50>=0){st= ""+(Double.parseDouble(et_addAmount.getText().toString())-50);}
            }else {st="0";}
            et_addAmount.setText(st);
        }
        if (view== tv_selectedProductName){
            //העתק טקסט
            clipData(tv_selectedProductName.getText().toString() , this);
            Toast.makeText( getBaseContext(), "הועתק שם מוצר",Toast.LENGTH_SHORT).show();
        }
        if (view== newProductCaloriesEditText ||view== et_addAmount){
            EditText  et_temp= (EditText) view;
            et_temp.setText("");
        }
    }

    private void saveNewProduct() {

        String newProductName = newProductNameEditText.getText().toString();
        String newProductCalories = newProductCaloriesEditText.getText().toString();
        String newProductUnit = unitSelectorView.getUnit();

        if (newProductName.isEmpty()) {
            newProductNameEditText.setBackgroundResource( R.drawable.sty_red );
            return;
        }
        if (newProductCalories.isEmpty()) {
            newProductCaloriesEditText.setBackgroundResource( R.drawable.sty_red );
            return;
        }

        if ( isContainsLetters(newProductCalories)){
            newProductCaloriesEditText.setBackgroundResource( R.drawable.sty_red );
            return;
        }

        addToFoodList();
        saveData();

        newProductNameEditText.setBackgroundResource( R.drawable.sty_2 );
        newProductCaloriesEditText.setBackgroundResource( R.drawable.sty_2 );

        newProductCaloriesEditText.setText( "" );

        updateMainList();

        makeToast( "\"" + newProductName +"\"" +" "+ newProductUnit +" נוסף למערכת!", getBaseContext());

    }

    private void selfAddActions() {

        String str_caloria= mainSearchView.getQuery().toString().trim();
        temp_exampleItem= new Product(0,"הוספת עצמית","קלוריות" ,"0","");
        temp_exampleItem.setCalorieText("100");
        addConsumedProductToList( Integer.parseInt( str_caloria ) , Integer.parseInt( str_caloria ) );
        updateTotalCalories();
        mainSearchView.setVisibility( View.VISIBLE );
        mainSearchView.setQuery( "" , true );
        mainSearchView.setIconified( true );
        hideKeyboard();
        cancelFoodAdd();
        rl_mainInformation.setVisibility( View.VISIBLE );
        webview.setVisibility( View.GONE );
        ly_customProductBottomSheet.setVisibility( View.GONE );
        ly_productSelectionBottomSheet.setVisibility( View.GONE );
        iv_backToMain.setVisibility( View.GONE );
        productsRecyclerView.setVisibility( View.GONE );
        rl_selfSearch.setVisibility( View.GONE );
        if (!consumedProductManager.getConsumedProductsOfDay().isEmpty()) {
            consumedProductsRecyclerView.smoothScrollToPosition( consumedProductManager.getConsumedProductsOfDay().size() - 1 );
        }
        mainSearchView.setBackgroundResource( R.drawable.sty_3 );
        iv_selfAdd.setVisibility(View.GONE);
        iv_startBarcodescan.setVisibility( View.VISIBLE );

    }

    //Function to display the custom dialog.
    public void showCustomDialog() {
        barcodeDialogHandler.showDialog();
    }
    public void closeCustomDialog() {
        barcodeDialogHandler.dismissDialog();
    }

    //פעולה שתציג דיאלוג ספציפי מתחתית המסך
    private void showDialog() {

        dialog.getWindow().setLayout(  ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity( Gravity.BOTTOM);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result =IntentIntegrator.parseActivityResult( requestCode,resultCode,data );
        if(result != null){
            if (result.getContents() != null){
                if (barcodeDialogHandler.isDialogShowing()){
                    searchProductByBarcode(result.getContents(),1);
                }
                else{
                    searchProductByBarcode(result.getContents(),0);}

            }
            else{
                Toast.makeText( this, "אין תוצאה",Toast.LENGTH_SHORT ).show();
            }


        }else{
            //אם אין תוצאה בינתיים
            super.onActivityResult( requestCode, resultCode, data );}
    }
    private void scanCode(){
        captureAct.scanCode(this);
    }
    private void searchProductByBarcode(String barcode , int mode) {
        if(mode==0) {
            Product examplel = new Product();
            boolean temp = false;
            int j = 0;
            while (!temp && j < systemProductList.size()) {
                examplel = systemProductList.get( j );
                //אם מוצא התאמה ברקוד
                if (examplel.getBarcode() != null && !examplel.getBarcode().isEmpty() && !examplel.getBarcode().equals( "null" )) {//אם לא ריק
                    if(!examplel.getBarcode().contains( "," )) {//אם יש ברקוד אחד
                        if (barcode.trim().matches( examplel.getBarcode().trim() )) {//אם תואם לחיפוש
                            temp = true;
                            openFood();
                            searchInFoodList( examplel.getName() );
                        }
                    }else{//בדוק על כל אחד
                        String currentString = examplel.getBarcode().trim();
                        String[] separated = currentString.split(",");
                        for (int i=0;i<separated.length; i++){
                            //Toast.makeText( MainActivity.this,  separated[i].trim(), Toast.LENGTH_SHORT).show();
                            if (barcode.trim().matches(  separated[i].trim() )) {//אם תואם לחיפוש
                                temp = true;
                                openFood();
                                searchInFoodList( examplel.getName() );

                            }
                        }
                    }
                }
                j++;
            }
            if (temp) { //נמצאה התאמה
                hideKeyboard();
                showFoodDitals( examplel );
                if (filteredProducts.size() == 2) {
                    openMain();
                }
            } else {//לא נמצא
                //חפש באינטרנט
                openNewProdact();
                temp_exampleItem = new Product(  0 , "" , "" , "" ,  barcode.trim() );
                startWebSearchForBarcode( barcode.trim() );
            }
        }
        if(mode==1) {
            String temp= barcodeDialogHandler.getBarcodeEditText().getText().toString().trim();
            if (temp.isEmpty()){
                barcodeDialogHandler.getBarcodeEditText().setText(barcode);
            } else {
                barcodeDialogHandler.getBarcodeEditText().setText( temp + " , " +barcode  );
            }
            webSearchSuggestion.setVisibility( View.VISIBLE );
            webSearchSuggestion.setText( barcode );
        }
    }

    //פעולות עדכון רשימות בסיס
    private void updateSystemProductList()   {
        systemProductList = new ArrayList<>();
        systemProductList =getSystemProductsArr();

        // המצב הראשוני של רשימת החיפוש כרשימת המוצרים (ברירת מחדל)
        filteredProducts = systemProductList;

        //עדכון הרשימה הפיזית במסך כרשימת המוצרים
        productsLayoutManager = new LinearLayoutManager(this);
        productsRecyclerView.setLayoutManager(productsLayoutManager);
        productsAdapter = new ProductItemAdapter(systemProductList);
        productsRecyclerView.setAdapter(productsAdapter);
    }
    private void addCustomProductListToProductCatalog() {
        if (customProducts == null) {
            customProducts = new ArrayList<>();
        }else {
            for (int i = 0; i < customProducts.size(); i++){
                //הוספת איבי רשימה שלי לרשימה ראשית
                systemProductList.add( customProducts.get( i ) );
            }
            productsRecyclerView.setAdapter(new ProductItemAdapter(systemProductList));
            // המצב הראשוני של רשימת החיפוש כרשימת המוצרים (ברירת מחדל)
            filteredProducts = systemProductList;
        }
    }
    //פעולות מערכת
    private void searchInFoodList(String s) {
        //פעולת חיפוש ועדכון רשימת מזון
        filteredProducts = new ArrayList<>();
        //חפש לפי שייכים לי
        for (int j = 0; j < systemProductList.size(); j++){
            Product example2 = systemProductList.get(j);
            if (example2.getName().toLowerCase().trim().contains( s.toLowerCase().trim() )&&example2.getItemState()==1 ){
                filteredProducts.add(example2);}
        }
        //חפש ברשימה כללית
        for (int i = 0; i < systemProductList.size(); i++){    //הכנס את מי שמתחילים בטקסט שהוקלד
            Product example = systemProductList.get(i);
            if(example.getName().toLowerCase().trim().startsWith( s.toLowerCase().trim() )&&example.getItemState()==0){
                filteredProducts.add(example);}
        }
        for (int i = 0; i < systemProductList.size(); i++){  //ורק אז הכנס את מי שנשאר ומכיל את הטקסט שהוקלד
            Product example = systemProductList.get(i);
            if (example.getName().toLowerCase().trim().contains( s.toLowerCase().trim() )&&!example.getName().toLowerCase().trim().startsWith( s.toLowerCase().trim() ) &&example.getItemState()==0){
                filteredProducts.add(example);}
            //    sortByAB(  filteredExampleList );
        }
        //הצג הודעה במקרה של חוסר תוצאות
        if (filteredProducts.size()>0){ filteredProducts.add(new Product( 999, "לא מה שחיפשת?", "המשך בחיפוש עצמי","\uD83D\uDD0D",""));}
        //עדכן רשימה
        RecyclerView.Adapter  newAdapter = new ProductItemAdapter(filteredProducts);
        productsRecyclerView.setAdapter(newAdapter);
    }

    private void sortByAB(ArrayList<Product> mExampleList) {
        //מיון לפי אב
        Collections.sort(mExampleList, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        //  a.notifyDataSetChanged();
        /*
        Collections.sort(ExampleList, new Comparator<String>()
        {
            @Override
            public int compare(String text1, String text2)
            {
                return text1.compareToIgnoreCase(text2);
            }
        });

         */
    }

    private void showFoodDitals(Product exampleItem) {
        ly_productSelectionBottomSheet.startAnimation( slide_in_bottom );
        temp_exampleItem=exampleItem ;

        ly_customProductBottomSheet.setVisibility( View.GONE );
        ly_productSelectionBottomSheet.setVisibility( View.VISIBLE );
        mainSearchView.setVisibility( View.GONE );
        tv_selectedProductName.setText(exampleItem.getName());
        tv_caloriesHeader.setText(exampleItem.getCalorieText());
        tv_unit.setText(  exampleItem.getUnit());

        tv_barcodeInfo.setVisibility(  View.GONE );
        String s = exampleItem.getBarcode();
        if (s != null && s.isEmpty()){
            s= "?";
        }
        tv_barcodeInfo.setText( "ברקוד מוצר: " + s );

        //האדיט טקסט של כמות יעודכן ל1 אם מדובר בכמות וכו ורק אם מדובר ב100 גרם או מל אז יעודכן ל100
        if ( exampleItem.getUnit().equals( "100 גרם" ) ||exampleItem.getUnit().equals( "100 מל" )  ){
            et_addAmount.setText( "100" );
            calcolaty_mod=2;
        }
        else{
            et_addAmount.setText("1");
            calcolaty_mod=1;
        }

    }
    private int calculationMod(String string){
        if ( string.equals( "100 גרם" ) ||string.equals( "100 מל" ) || string.equals( "קלוריות" ) ){
            // calcolaty_mod=2;
            return 2;
        }
        else{
            //calcolaty_mod=1;
            return 1;
        }
    }
    private void sortArrayList() {
        Collections.sort(systemProductList, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        productsAdapter = new ProductItemAdapter(systemProductList);
        productsAdapter.notifyDataSetChanged();
    }
    //פעולות שרדפרפרנס רשימת מזון פרטית
    private void addToFoodList(){
        //הוסף מזון לרשימת מוצרים שלי (רק אם אני בחיפוש עצמי או עורך מוצר קיים)
        Product item;
        String unit = unitSelectorView.getUnit();
        item = new Product(   1 , newProductNameEditText.getText().toString().trim() ,
                unit , newProductCaloriesEditText.getText().toString().trim() , barcodeDialogHandler.getBarcodeEditText().getText().toString().trim() );
        customProducts.add(item);
        temp_exampleItem=item;
    }
    private void loadCustomProductListData() {
        customProducts =productStorageManager.load();
    }
    private void saveData(){
        productStorageManager.save(customProducts);
    }
    private void clearData(){
        productStorageManager.clear();
        customProducts = new ArrayList<>();
    }
    private Product getLastItem(){
        return productStorageManager.getLastItem();
    }

    // פתיחת וסגירת מסכים
    private void openMain(){

        rl_mainInformation.setVisibility(View.VISIBLE);
        rl_top.setVisibility( View.VISIBLE );
        rl_selfSearchTopBar.setVisibility( View.GONE );
        changeBarColor(rl_top);

        iv_backToMain.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.GONE);

        webview.setVisibility(View.GONE);

        rl_selfSearch.setVisibility(View.GONE);

        //    ly_addNewPrivetFood.setVisibility(View.GONE);
        //    ly_aditAmount.setVisibility(View.GONE);
        //    ly_addFood.setVisibility(View.GONE);
    }
    private void closeMain(){
        rl_mainInformation.setVisibility(View.GONE);
    }
    private void refreshMain(){ }
    private void openFood(){
        rl_mainInformation.setVisibility(View.GONE);
        rl_top.setVisibility( View.VISIBLE );
        rl_selfSearchTopBar.setVisibility( View.GONE );
        changeBarColor(rl_top);
        iv_backToMain.setVisibility(View.VISIBLE);

        productsRecyclerView.setVisibility(View.VISIBLE);

        webview.setVisibility(View.GONE);

        rl_selfSearch.setVisibility(View.GONE);

        ly_customProductBottomSheet.setVisibility(View.GONE);
        cancelEdit();
        ly_productSelectionBottomSheet.setVisibility(View.GONE);
    }
    private void closeFood(){
        iv_backToMain.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.GONE);
    }
    private void openNewProdact(){

        ly_customProductBottomSheet.startAnimation( slide_in_bottom );
        rl_mainInformation.setVisibility(View.GONE);
        rl_top.setVisibility( View.GONE );
        rl_selfSearchTopBar.setVisibility( View.VISIBLE );
        changeBarColor(rl_selfSearchTopBar);
        //   showDialog();

        iv_backToMain.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.GONE);

        webview.setVisibility(View.VISIBLE);
        ly_customProductBottomSheet.setVisibility(View.VISIBLE);

        rl_selfSearch.setVisibility(View.GONE);

        cancelEdit();
        ly_productSelectionBottomSheet.setVisibility(View.GONE);

    }
    private void closeNewProdact(){
        webview.setVisibility(View.VISIBLE);
        ly_customProductBottomSheet.setVisibility(View.VISIBLE);
    }
    //פעולות קטנות
    private void findViewAndMore() {

        unitSelectorView = findViewById(R.id.unit_selector);

        dialog= new Dialog(this);
        dialog.setContentView(R.layout.new_privet_prodact_sheet);

        tv_barcodeInfo =findViewById( R.id.tv_barcodeInfo);

        iv_expandProductInfo = findViewById( R.id.iv_expandProductInfo);
        iv_expandProductInfo.setOnClickListener( this );

        rl_top=findViewById(R.id.rl_top);
        rl_selfSearchTopBar =findViewById(R.id.rl_selfSearchTopBar);
        iv_myProdacts_SS=findViewById(R.id.iv_myProdacts_SS);
        iv_myProdacts_SS.setOnClickListener( this );
        iv_backFromSelfSearchToMain =findViewById(R.id.iv_backFromSelfSearchToMain);
        iv_backFromSelfSearchToMain.setOnClickListener( this );


        webSearchSuggestion =findViewById( R.id.webSearchSuggestion);
        webSearchSuggestion.setOnClickListener( this );
        slide_in_bottom= AnimationUtils.loadAnimation( this,R.anim.slide_in_bottom );
        slide_out_bottom= AnimationUtils.loadAnimation( this,R.anim.slide_out_bottom );

        iv_myProducts =findViewById( R.id.iv_myProducts);
        iv_myProducts.setOnClickListener( this );

        consumedProductsRecyclerView =findViewById( R.id.consumedProductsRecyclerView);
        consumedProductsLayoutManager = new LinearLayoutManager(this);
        consumedProductsRecyclerView.setLayoutManager(consumedProductsLayoutManager);


        iv_startBarcodescan =findViewById( R.id.iv_barcodeSearch_round );
        iv_startBarcodescan.setOnClickListener( this );

        ly_productCreationForm =findViewById( R.id.ly_productCreationForm);
        iv_showSelfSearchBar =findViewById( R.id.iv_showSelfSearchBar );
        iv_showSelfSearchBar.setOnClickListener( this );
        iv_collapseBottomSheet =findViewById( R.id.iv_collapseBottomSheet);
        iv_collapseBottomSheet.setOnClickListener( this );
        iv_selfSearch_round =findViewById( R.id.iv_selfSearch);
        iv_selfSearch_round.setOnClickListener( this );
        iv_selfAdd =findViewById( R.id.iv_selfAdd);
        iv_selfAdd.setOnClickListener( this );
        iv_barcodeScan =findViewById( R.id.iv_barcodeScan);
        iv_barcodeScan.setOnClickListener( this );

        iv_decreaseAmount =findViewById( R.id.iv_decreaseAmount);
        iv_decreaseAmount.setOnClickListener( this );

        iv_increaseAmount =findViewById( R.id.iv_increaseAmount);
        iv_increaseAmount.setOnClickListener( this );

        saveAndStay =findViewById( R.id.saveAndStay);
        saveAndStay.setOnClickListener( this );

        tv_returnToMainScreen =findViewById( R.id.tv_returnToMainScreen);
        tv_returnToMainScreen.setOnClickListener( this );
        iv_settings=findViewById( R.id.iv_settings);
        iv_settings.setOnClickListener( this );
        ly_settings=findViewById( R.id.ly_settings);

        iv_closeBottomSheet =findViewById( R.id.iv_closeBottomSheet);
        iv_closeBottomSheet.setOnClickListener( this );

        tv_date=findViewById( R.id.tv_date);
        btn_nextDay=findViewById( R.id.btn_nextDay);
        btn_nextDay.setOnClickListener( this );

        btn_lastDay=findViewById( R.id.btn_lastDay);
        btn_lastDay.setOnClickListener( this );
        ;
        rl_mainInformation=findViewById( R.id.rl_mainInformation );
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        tv_clearMainCaloriesList =findViewById( R.id.tv_clearMainCaloriesList);
        tv_clearMainCaloriesList.setOnClickListener( this );
        et_addAmount =findViewById( R.id.et_addAmount);

        iv_backToMain =findViewById( R.id.iv_backToMain );
        iv_backToMain.setOnClickListener( this );
        rl_selfSearch=findViewById(R.id.rl_selfSearch);
        iv_goToSelfSearch =findViewById( R.id.iv_goToSelfSearch);
        iv_goToSelfSearch.setOnClickListener( this );
        newProductNameEditText = findViewById( R.id.newProductNameEditText );
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
                webSearchSuggestion.setText(newProductNameEditText.getText().toString());
                if(!newProductNameEditText.getText().toString().equals( "" )){
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
        newProductCaloriesEditText = findViewById( R.id.newProductCaloriesEditText );
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
        tv_selectedProductName = findViewById( R.id.tv_selectedProductName);
        tv_selectedProductName.setMovementMethod( new ScrollingMovementMethod() );
        tv_selectedProductName.setOnClickListener( this );
        tv_caloriesHeader = findViewById( R.id.tv_caloriesHeader);
        tv_unit =findViewById( R.id.tv_unit);
        saveNewProductItemButton =findViewById( R.id.saveNewProductItemButton);
        saveNewProductItemButton.setOnClickListener( this );
        btn_addToConsumption =findViewById( R.id.btn_addToConsumption);
        btn_addToConsumption.setOnClickListener( this );
        ly_customProductBottomSheet =findViewById( R.id.ly_customProductBottomSheet);
        ly_productSelectionBottomSheet =findViewById( R.id.ly_productSelectionBottomSheet);
        tv_totalCalories =findViewById( R.id.tv_totalCalories);
        tv_totalCalories.setOnClickListener( this );
        webview = findViewById( R.id.webview);
        webview.setWebViewClient( new WebViewClient() );
        WebSettings webSettings=webview.getSettings();
        webSettings.setJavaScriptEnabled( true );
//ההוראה החשובה לעדכון דף באפליקציה עצמה
        //      webSettings.setAppCacheEnabled( true );
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //לא ברור מה אלו
        webSettings.setDatabaseEnabled( true );
        webSettings.setEnableSmoothTransition( true );
        webSettings.setGeolocationEnabled( true );
        webSettings.setDomStorageEnabled(  true);
        webview.setWebViewClient(new MyWebViewClient());
        mainSearchView = findViewById( R.id.mainSearchView);
        mainSearchView.setOnClickListener( this );
        selfSearchSearchView = findViewById( R.id.selfSearchSearchView);
        selfSearchSearchView.setOnClickListener( this );
        newProductCaloriesEditText.setOnTouchListener( this );
        et_addAmount.setOnTouchListener( this );
        //   et_kal.setOnClickListener( this );et_amounttt.setOnClickListener( this );et_spinnerEditT.setOnClickListener( this );et_newAmount.setOnClickListener( this );;
//
    }
    private void updateMainList() {
        updateSystemProductList();
        loadCustomProductListData();
        addCustomProductListToProductCatalog();
        refreshConsumedProductsList();
        //מיון לפי א"ב
        //sortArrayList();
    }
    private void cancelEdit() {
editConsumedProductDialog.close();
    }
    private void cancelFoodAdd() {
        ly_customProductBottomSheet.setVisibility(View.GONE);
        ly_productSelectionBottomSheet.setVisibility(View.GONE);
        mainSearchView.setVisibility(View.VISIBLE);
        iv_backToMain.setVisibility( View.VISIBLE );

    }
    private void cancelNewFoodAdd() {
        hideKeyboard();
        cancelFoodAdd();
        iv_backToMain.setVisibility(View.GONE);
        ly_productCreationForm.setVisibility( View.VISIBLE );
        productsRecyclerView.setVisibility(View.VISIBLE);
        webSearchSuggestion.setVisibility( View.GONE );
        iv_selfSearch_round.setVisibility( View.GONE );
        iv_showSelfSearchBar.setVisibility( View.GONE );
        iv_startBarcodescan.setVisibility( View.VISIBLE );

        mainSearchView.setQuery( "" , false );
        newProductNameEditText.setText( "" );
        newProductCaloriesEditText.setText( "" );

        unitSelectorView.selectDefaultUnit();

        mainSearchView.setBackgroundResource( R.drawable.sty_3 );
        updateMainList();

    }
    private void startinternetWebSearch() {
        if (Build.VERSION.SDK_INT >= 19) {
            webview.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        webview.loadUrl("https://www.google.com/search?q=" + selfSearchSearchView.getQuery().toString() +" "+ "קלוריות");
        //העלם עמודת חיפוש והצג אפשרות ביטול
        //   serchview_internet.setVisibility(View.GONE);
    }
    private void startinternetWebSearchDotan(String str) {
        if (Build.VERSION.SDK_INT >= 19) {
            webview.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        webview.loadUrl("https://www.google.com/search?q=" + str);
    }
    private void startWebSearchForBarcode(String bCod) {
        ly_productSelectionBottomSheet.setVisibility(View.GONE);
        barcodeDialogHandler.getBarcodeEditText().setText(bCod);

        if (Build.VERSION.SDK_INT >= 19) {
            webview.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        webview.loadUrl("https://www.google.com/search?q=" + bCod);

        //העלם עמודת חיפוש והצג אפשרות ביטול
        mainSearchView.setVisibility(View.GONE);
    }
    private void startWebSearch() {
        ly_customProductBottomSheet.startAnimation( slide_in_bottom );
        ly_productCreationForm.setVisibility( View.VISIBLE );
        iv_backToMain.setVisibility( View.GONE );
        webSearchSuggestion.setVisibility( View.GONE );
        rl_mainInformation.setVisibility(View.GONE);
        rl_top.setVisibility( View.GONE );
        rl_selfSearchTopBar.setVisibility( View.VISIBLE );
        changeBarColor(rl_selfSearchTopBar);

        rl_selfSearch.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.GONE);

        rl_selfSearch.setVisibility( View.GONE );
        webview.setVisibility( View.VISIBLE );
        ly_customProductBottomSheet.setVisibility(View.VISIBLE);
        ly_productSelectionBottomSheet.setVisibility(View.GONE);

        barcodeDialogHandler.getBarcodeEditText().setText("");

        if (Build.VERSION.SDK_INT >= 19) {
            webview.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        webview.loadUrl("https://www.google.com/search?q=" + mainSearchView.getQuery().toString() +" "+ "קלוריות");
        newProductNameEditText.setText( mainSearchView.getQuery().toString() );
        webSearchSuggestion.setVisibility( View.GONE ); //זה בכוונה אחרי חיפוש המוצר ואחרי עדעון האדיט טקסט של שם המוצר.
        //העלם עמודת חיפוש והצג אפשרות ביטול
        mainSearchView.setVisibility(View.GONE);
    }

    private void refreshConsumedProductsList(){
        loadConsumedProductData(calendar);
        updateTotalCalories();

        ArrayList<ConsumedProduct>consumedProducts=consumedProductManager.getConsumedProductsOfDay();
        consumedProductsRecyclerView.setAdapter(new ConsumedItemAdapter(consumedProducts));
        if (!consumedProducts.isEmpty()){
            consumedProductsRecyclerView.smoothScrollToPosition(consumedProducts.size()-1);}

    }
    private void backToMain() {
        mainSearchView.setVisibility( View.VISIBLE );
        mainSearchView.setQuery( "",true );
        mainSearchView.setIconified(true);
        hideKeyboard();
        cancelFoodAdd();
        openMain();
    }
    //פעולות כלליות
    public void hideKeyboard(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static boolean isContainsLetters(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }
        for (int i = 0; i < string.length(); ++i) {
            if (Character.isLetter(string.charAt(i))) {
                return true;
            }
        }
        return false;
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeBarColor(RelativeLayout relativeLayout) {

        ColorDrawable viewColor = (ColorDrawable) rl_top.getBackground();
        int colorId = viewColor.getColor();
        getWindow().setStatusBarColor( colorId);
    }
    private void restartApp() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
    private void clipExampleListAsCode(String labal){
        String detailsString= labal;
        detailsString=detailsString+" "+ systemProductList.size()+" items ";
        for(int i = 0; i < systemProductList.size(); i++){
            String itemT= "exampleList.add(new ExampleItem("+0+","+0+","+"\""+ systemProductList.get( i ).getName()+"\"" +","+"\""+ systemProductList.get( i ).getUnit()+"\""+" ,"+"\""+ systemProductList.get( i ).getCalorieText()+"\""+","+0+","+"null"+"));";
            detailsString= detailsString + "\n"+itemT;
        }
        clipData(detailsString , this);
        Toast.makeText( getBaseContext(), "Copied successfully"+ systemProductList.size()+" items ",Toast.LENGTH_SHORT).show();
    }


    //פעולות שרדפרפרנס רשימת הקלוריות המוצגת במסך ראשי

    private void addConsumedProductToList(double amount, int calories){
        consumedProductManager.addItem( amount,temp_exampleItem,calendar);
        consumedProductsRecyclerView.setAdapter(new ConsumedItemAdapter(consumedProductManager.getConsumedProductsOfDay()));
    }
    private void loadConsumedProductData(Calendar calendarDayParameter) {
        consumedProductManager.loadItemsData(calendarDayParameter);    }
    private void saveConsumedProductData(){
        consumedProductManager.saveItemsData();
    }
    private void clearConsumedProductData(){
        consumedProductManager.clearItemsData();
    }
    private void updateTotalCalories(){
        int totalCalories=0;

        for(int i = 0; i < consumedProductManager.getConsumedProductsOfDay().size(); i++){
            totalCalories += consumedProductManager.getConsumedProductsOfDay().get( i ).getTotalCalories();
        }

        if (totalCalories!=0){
            //  text.setText( "  "+totalCalories+"  " );
            tv_totalCalories.setText( ""+totalCalories );
            tv_totalCalories.setBackgroundResource( R.drawable.sty_blue_r ); }
        else{    tv_totalCalories.setText( "");
            tv_totalCalories.setBackgroundResource( R.drawable.sty_blue_r_sercle ); }
    }
    private void deleteConsumedProductById(String targetId){
        consumedProductManager.deleteItemById(targetId);
        updateTotalCalories();
        consumedProductsRecyclerView.setAdapter(new ConsumedItemAdapter(consumedProductManager.getConsumedProductsOfDay()));
    }
    private void editConsumedProductAmountById(String targetId, double newAmount , Calendar calendar){
        consumedProductManager.editItemAmountById(newAmount ,targetId , calendar );
        updateTotalCalories();
        consumedProductsRecyclerView.setAdapter(new ConsumedItemAdapter(consumedProductManager.getConsumedProductsOfDay()));
    }


    // אין לי מושג מה זה ומה שלא יודעים לא כואב ;)  (כל מה שמתחת)
    @Override
    public void onItemSelected(AdapterView<?> adapterView , View view , int i , long l) {
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

    }
    @Override
    public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

    }
    @Override
    public void afterTextChanged(Editable editable) {

    }
    @Override
    public void onBackPressed() {

        if (webview.canGoBack() && webview.getVisibility() == View.VISIBLE) {
            webview.goBack();
        } else {

            if (ly_customProductBottomSheet.getVisibility() == View.VISIBLE) {
                //   cancelNewFoodAdd();
            }
            if (ly_productSelectionBottomSheet.getVisibility() == View.VISIBLE) {
                cancelFoodAdd();
            } else {
                if (productsRecyclerView.getVisibility() == View.VISIBLE) {
                    backToMain();
                } else {
                    if (rl_selfSearch.getVisibility() == View.VISIBLE) {
                        backToMain();
                    }
                }
            }

            if (!editConsumedProductDialog.isClose()) {
                cancelEdit();
            }
            if (ly_settings.getVisibility() == View.VISIBLE) {
                ly_settings.setVisibility( View.GONE );
            }
            super.onBackPressed();
            //השורה למטה אומרת שתלך אחורה. בינתיים זה בעצם סוגר תאפליקציה
            //      super.onBackPressed();
        }
    }
    @Override
    public boolean onTouch(View view , MotionEvent motionEvent) {
        if (view== newProductCaloriesEditText ||view== et_addAmount){
            EditText  et_temp= (EditText) view;
            et_temp.setText("");
            return false;}
        return false;
    }
    private static class MyWebViewClient extends WebViewClient {
        /*
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("www.newsweek.com")) {
                //open url contents in webview
                return false;
            } else {
                //here open external links in external browser or app
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

        }
         */
    }

}