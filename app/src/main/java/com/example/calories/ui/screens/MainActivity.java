package com.example.calories.ui.screens;

import static com.example.calories.utils.SystemProducts_Utils.getSystemProductsArr;
import static com.example.calories.utils.Utility.clipData;
import static com.example.calories.utils.Utility.isNumeric;
import static com.example.calories.utils.Utility.startNewActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.example.calories.ui.dialogs.ConsumedProductEditingDialog;
import com.example.calories.ui.dialogs.CustomProductDialog;
import com.example.calories.ui.dialogs.ProductSelectionDialog;
import com.example.calories.ui.utils.CaptureAct;
import com.example.calories.ui.adapters.ConsumedItemAdapter;
import com.example.calories.ui.adapters.ProductItemAdapter;
import com.example.calories.R;
import com.example.calories.ui.adapters.RecyclerItemClickListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
    ///  יש לפעול לפיצול רשימות- יש רשימה אחת למוצרי מערכת ויש רשימה למוצרים אישיים אך יש לחברם יחד לרשימה כוללה
    /// כרגע מסיבה מסויימת הם מתחברים יחדיו לרשימת מוצרי המערכת מסיבה לא ברורה
    /// ראה ערך בפעולה addCustomProductListToProductCatalog()

    //--------------- CalorieTrackerView ---------------
    private RecyclerView consumedProductsRecyclerView;
    private RecyclerView.LayoutManager consumedProductsLayoutManager;
    private ConsumedProduct consumedProduct_edit;

    ConsumedProductEditingDialog consumedProductEditingDialog;
    private String lastClickedId;

    //--------------- ProductCatalogView ---------------
    ProductSelectionDialog productSelectionDialog;
    private ArrayList<Product> systemProductList = new ArrayList<>();//רשימת מוצרים (מערכת)
    private ArrayList<Product> customProducts = new ArrayList<>();//רשימת מוצרים שיצר המשתמש
    private ArrayList<Product> filteredProducts = new ArrayList<>();//רשימת מוצרים מסוננת (לפי חיפוש)
    private RecyclerView productsRecyclerView;
    private RecyclerView.Adapter productsAdapter;
    private RecyclerView.LayoutManager productsLayoutManager;
    private SearchView mainSearchView;
    private Product aProductItem =null;

    //--------------- CustomProductView  ---------------
    private WebView webview;
    CustomProductDialog customProductDialog;

    //--------------- others  ---------------
    private Calendar calendar;
    private final CaptureAct captureAct = new CaptureAct();

    private ProductStorageManager productStorageManager;

    // dialog
    ConsumedProductManager consumedProductManager;
    // top bar
    private Button lastDayBtn, nextDayBtn;

    //  settings
    private LinearLayout ly_settings;

    private ImageView iv_backFromSelfSearchToMain, iv_myProducts_SS, iv_showSelfSearchBar, iv_selfSearch_round ,  iv_selfAdd, iv_goToSelfSearch,
            iv_backToMain, settingsIcon, barcodeIcon;
    private RelativeLayout rl_selfSearch,rl_top, rl_selfSearchTopBar,rl_mainInformation;
    private TextView currentDateText, tv_returnToMainScreen;

    private TextView tv_clearMainCaloriesList, caloriesViewText;
    private ImageView customListIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        findViewAndMore();
        changeBarColor(rl_top);

        customProductDialog = new CustomProductDialog(this);
        productStorageManager  = new ProductStorageManager(this);
        consumedProductManager = new ConsumedProductManager(this);
        consumedProductEditingDialog =new ConsumedProductEditingDialog(MainActivity.this);
        productSelectionDialog = new ProductSelectionDialog(MainActivity.this);
        //יצירת רשימת המוצרים
        updateMain();
        //מיון לפי א"ב
        //      sortArrayList();


        productsRecyclerView.addOnItemTouchListener( new RecyclerItemClickListener(MainActivity.this, productsRecyclerView
                ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                if (!(filteredProducts.get( position ).getItemState() ==999)) {
                    aProductItem = filteredProducts.get( position );

                   openProductSelectionDialog(aProductItem);
                }
                else{
                    //פתח חיפוש עצמי
                    startWebSearch();
                }
            }



            @Override public void onLongItemClick(View view, int position) {
            }
        }) );

        consumedProductsRecyclerView.addOnItemTouchListener( new RecyclerItemClickListener(MainActivity.this, consumedProductsRecyclerView
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view , int position)
            {
                //עריכת פריט
                consumedProduct_edit = consumedProductManager.getConsumedProductsOfDay().get( position );
                consumedProductEditingDialog.show(consumedProduct_edit, calendar , consumedProductManager);
            }

            @Override
            public void onLongItemClick(View view , int position) {
                if (consumedProductEditingDialog.isClosed()){
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
                    barcodeIcon.setVisibility( View.GONE );
                    rl_selfSearch.setVisibility(View.GONE);
                    productsRecyclerView.setVisibility(View.GONE);
                    iv_backToMain.setImageResource( R.drawable.ic_baseline_arrow_circle_right_purple );

                }
                else if (filteredProducts.isEmpty()){  // אם הרשימה ריקה (אין מוצרים)- הצעה לחיפוש עצמי
                    mainSearchView.setBackgroundResource( R.drawable.sty_orang3);
                    rl_mainInformation.setVisibility(View.GONE);
                    iv_selfAdd.setVisibility(View.GONE);
                    iv_selfSearch_round.setVisibility( View.VISIBLE );
                    barcodeIcon.setVisibility( View.GONE );
                    rl_selfSearch.setVisibility(View.VISIBLE);
                    productsRecyclerView.setVisibility(View.GONE);
                    iv_backToMain.setImageResource( R.drawable.baseline_arrow_circle_right_oreng );

                } else {
                    mainSearchView.setBackgroundResource( R.drawable.sty_3 );
                    rl_mainInformation.setVisibility(View.GONE);
                    iv_selfAdd.setVisibility(View.GONE);
                    iv_selfSearch_round.setVisibility( View.GONE );
                    barcodeIcon.setVisibility( View.VISIBLE );
                    rl_selfSearch.setVisibility(View.GONE);
                    productsRecyclerView.setVisibility(View.VISIBLE);
                    iv_backToMain.setImageResource( R.drawable.ic_baseline_arrow_circle_right_blue );
                }

                return false;
            }
        } );

        //סגירת מסכים לא נחוצים בכניסה התחלתית למסך
        webview.setVisibility(View.GONE);
        iv_backToMain.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.GONE);
        rl_selfSearch.setVisibility(View.GONE);
        rl_mainInformation.setVisibility(View.VISIBLE);

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


        consumedProductEditingDialog.setOnEditCompleteListener(new ConsumedProductEditingDialog.OnEditCompleteListener() {
            @Override
            public void onEditComplete() {
                // רענון הרשימה
                refreshConsumedProductsList();
            }
        });

        productSelectionDialog.setOnProductSelectedListener(new ProductSelectionDialog.OnProductSelectedListener() {
            @Override
            public void onSaveComplete() {
                refreshConsumedProductsList();
                mainSearchView.setVisibility( View.VISIBLE );
                mainSearchView.setQuery( "" , true );
                mainSearchView.setIconified( true );
                hideKeyboard();
                cancelFoodAdd();
                rl_mainInformation.setVisibility( View.VISIBLE );
                webview.setVisibility( View.GONE );
                iv_backToMain.setVisibility( View.GONE );
                productsRecyclerView.setVisibility( View.GONE );
                rl_selfSearch.setVisibility( View.GONE );
            }
        });
        customProductDialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (webview.getVisibility()==View.VISIBLE){
                    iv_showSelfSearchBar.setVisibility(View.VISIBLE);}
            }
        });

        customProductDialog.setOnCustomProductItemListener(new CustomProductDialog.OnCustomProductItemListener() {
            @Override
            public void onItemCreated(Product customProduct) {
                hideKeyboard();
                cancelFoodAdd();
                mainSearchView.setVisibility( View.VISIBLE );
                mainSearchView.setIconified(true);

                openMain();
                updateMain();
                openProductSelectionDialog(customProduct);
            }

            @Override
            public void onSearch(String suggestion) {
                startInternetWebSearch(suggestion);
            }

            @Override
            public void onSearchSuggestionClicked(String suggestion) {
                startInternetWebSearchDotan(suggestion);
                hideKeyboard();
                customProductDialog.close();
                iv_showSelfSearchBar.setVisibility( View.VISIBLE );
            }

            @Override
            public void onDialogClose() {
            }
        });

        MyProductActivity.setScreenCloseListener(new MyProductActivity.ScreenCloseListener() {
            @Override
            public void onScreenClosed() {
                updateMain();
            }
        });
    }

    private void openProductSelectionDialog(Product product) {
        // פעולה לבדיקת םמקלדת פתוחה, לא עובדת
        // mainSearchView.setQuery("", false);
        //mainSearchView.setIconified(true);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isActive()) {
            // המקלדת פעילה - סוגרים ומחכים
            mainSearchView.clearFocus();
            hideKeyboardAndShowDialog(product);
        } else {
            productSelectionDialog.show(product,calendar , consumedProductManager);
        }
    }

    private void hideKeyboardAndShowDialog(Product product) {
        hideKeyboard();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
            productSelectionDialog.show(product,calendar , consumedProductManager);
        }
    }, 200); //
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View view) {

        if(view== customListIcon || view == iv_myProducts_SS){
            startNewActivity(MainActivity.this, MyProductActivity.class);

        }

        if (view==iv_selfAdd) {
            selfAddActions();
        }

        if(view== barcodeIcon){
            openMain();
            scanCode();}

        if(view== nextDayBtn){
            //יקרה רק אם כל שאר המסכים סגורים
            if (rl_selfSearch.getVisibility()==View.GONE){
                calendar.add(Calendar.DAY_OF_MONTH, 1); //Adds a day
                currentDateText.setText( new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime()));
                refreshConsumedProductsList();
            }
        }

        if(view== lastDayBtn){
            //יקרה רק אם כל שאר המסכים סגורים
            if (rl_selfSearch.getVisibility()==View.GONE) {
                calendar.add( Calendar.DAY_OF_MONTH , -1 ); //Goes to previous day
                currentDateText.setText( new SimpleDateFormat( "dd-MM-yyyy" ).format( calendar.getTime() ) );
                refreshConsumedProductsList();
            }
        }

        if( view == iv_backFromSelfSearchToMain){
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                boolean isSelfSearch = extras.getBoolean( "selfSearch" );
                if (isSelfSearch) {
                    finish();
                }
            }else {
                cancelNewFoodAdd();
                openMain();
            }
        }

        if (view == iv_showSelfSearchBar){
            iv_showSelfSearchBar.setVisibility( View.GONE );
            customProductDialog.show(productStorageManager , "" , "");
        }

        if (view == mainSearchView){
            //  mRecyclerView.setVisibility(View.VISIBLE);
            ///    et_food.setText( "" );
            //   rl_mainInformation.setVisibility(View.GONE);
            if (mainSearchView != null) {
                mainSearchView.setIconified(false);
            }
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
            caloriesViewText.setText("0");
            clearConsumedProductData();
            Toast.makeText( getBaseContext(), "רשימת קלוריות שנצרכו נמחקה (מסך ראשי)",Toast.LENGTH_SHORT).show();
            restartApp();
        }

        if (view== tv_returnToMainScreen){
            ly_settings.setVisibility( View.GONE );
        }

        if (view== settingsIcon) {
            ly_settings.setVisibility( View.VISIBLE );
        }
    }

    private void selfAddActions() {

        String str_caloria= mainSearchView.getQuery().toString().trim();
        aProductItem = new Product(0,"הוספת עצמית","קלוריות" ,"0","");
        aProductItem.setCalorieText("100");
        addConsumedProductToList( Integer.parseInt( str_caloria ) , Integer.parseInt( str_caloria ) );
        updateTotalCalories();
        mainSearchView.setVisibility( View.VISIBLE );
        mainSearchView.setQuery( "" , true );
        mainSearchView.setIconified( true );
        hideKeyboard();
        cancelFoodAdd();
        rl_mainInformation.setVisibility( View.VISIBLE );
        webview.setVisibility( View.GONE );
        iv_backToMain.setVisibility( View.GONE );
        productsRecyclerView.setVisibility( View.GONE );
        rl_selfSearch.setVisibility( View.GONE );
        if (!consumedProductManager.getConsumedProductsOfDay().isEmpty()) {
            consumedProductsRecyclerView.smoothScrollToPosition( consumedProductManager.getConsumedProductsOfDay().size() - 1 );
        }
        mainSearchView.setBackgroundResource( R.drawable.sty_3 );
        iv_selfAdd.setVisibility(View.GONE);
        barcodeIcon.setVisibility( View.VISIBLE );


    }

/// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result =IntentIntegrator.parseActivityResult( requestCode,resultCode,data );
        if(result != null){
            if (result.getContents() != null){
                if (customProductDialog != null && !customProductDialog.isClosed()){
                        customProductDialog.handleBarcodeResult(result);
                }
                else{
                    searchProductByBarcode(result.getContents());}
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
    private void searchProductByBarcode(String barcode) {
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
                            if (barcode.trim().matches(  separated[i].trim() ))     {//אם תואם לחיפוש
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
                openProductSelectionDialog(examplel);
                if (filteredProducts.size() == 2) {
                    openMain();
                }
            } else {//לא נמצא
                //חפש באינטרנט
                openCustomProductForBarcode(barcode.trim());
                aProductItem = new Product(  0 , "" , "" , "" ,  barcode.trim() );
                startWebSearchForBarcode( barcode.trim() );
            }
    }

    //פעולות עדכון רשימות בסיס
    private void updateSystemProductList()   {
        systemProductList = new ArrayList<>();
        systemProductList =getSystemProductsArr();
        // המצב הראשוני של רשימת החיפוש כרשימת המוצרים (ברירת מחדל)
        filteredProducts = systemProductList;
    }
    private void refreshProductCatalog(){
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
            //הוספת איבי רשימה שלי לרשימה ראשית
            systemProductList.addAll(customProducts);
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
        if (!filteredProducts.isEmpty()){ filteredProducts.add(new Product( 999, "", "","",""));}
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
    private void loadCustomProductListData() {
        customProducts = productStorageManager.load();
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
    private void openFood(){
        mainSearchView.setBackgroundResource( R.drawable.sty_3 );

        iv_selfAdd.setVisibility(View.GONE);
        iv_selfSearch_round.setVisibility( View.GONE );
        barcodeIcon.setVisibility( View.VISIBLE );

        rl_mainInformation.setVisibility(View.GONE);
        rl_top.setVisibility( View.VISIBLE );
        rl_selfSearchTopBar.setVisibility( View.GONE );
        changeBarColor(rl_top);
        iv_backToMain.setVisibility(View.VISIBLE);

        productsRecyclerView.setVisibility(View.VISIBLE);

        webview.setVisibility(View.GONE);

        rl_selfSearch.setVisibility(View.GONE);
        cancelEdit();
        productsRecyclerView.setVisibility(View.VISIBLE);
    }
    private void closeFood(){
        iv_backToMain.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.GONE);
    }
    /// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private void openCustomProductForBarcode( String barcode){   // יכול להיות שפעולה זו מיותרת מכיוון שיש כבר פעולה דומה לפתיחת מוצר מעוצב
        rl_selfSearchTopBar.setVisibility( View.VISIBLE );
        webview.setVisibility(View.VISIBLE);
        changeBarColor(rl_selfSearchTopBar);

        rl_mainInformation.setVisibility(View.GONE);
        rl_top.setVisibility( View.GONE );
        iv_backToMain.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.GONE);
        rl_selfSearch.setVisibility(View.GONE);
        barcodeIcon.setVisibility(View.GONE);
        cancelEdit();

        customProductDialog.show(productStorageManager , "" , barcode );
    }
    private void closeNewProduct(){
        webview.setVisibility(View.VISIBLE);
        customProductDialog.close();
    }
    //פעולות קטנות
    private void findViewAndMore() {

        rl_top=findViewById(R.id.rl_top);
        rl_selfSearchTopBar =findViewById(R.id.rl_selfSearchTopBar);
        iv_myProducts_SS =findViewById(R.id.iv_myProdacts_SS);
        iv_myProducts_SS.setOnClickListener( this );
        iv_backFromSelfSearchToMain =findViewById(R.id.iv_backFromSelfSearchToMain);
        iv_backFromSelfSearchToMain.setOnClickListener( this );

        customListIcon =findViewById( R.id.customListIcon);
        customListIcon.setOnClickListener( this );

        consumedProductsRecyclerView =findViewById( R.id.consumedProductsRecyclerView);
        consumedProductsLayoutManager = new LinearLayoutManager(this);
        consumedProductsRecyclerView.setLayoutManager(consumedProductsLayoutManager);

        barcodeIcon =findViewById( R.id.barcodeIcon);
        barcodeIcon.setOnClickListener( this );

        iv_showSelfSearchBar =findViewById( R.id.iv_showSelfSearchBar );
        iv_showSelfSearchBar.setOnClickListener( this );
        iv_selfSearch_round =findViewById( R.id.iv_selfSearch);
        iv_selfSearch_round.setOnClickListener( this );
        iv_selfAdd =findViewById( R.id.iv_selfAdd);
        iv_selfAdd.setOnClickListener( this );

        tv_returnToMainScreen =findViewById( R.id.tv_returnToMainScreen);
        tv_returnToMainScreen.setOnClickListener( this );
        settingsIcon =findViewById( R.id.settingsIcon);
        settingsIcon.setOnClickListener( this );
        ly_settings=findViewById( R.id.ly_settings);
        currentDateText =findViewById( R.id.currentDateText);
        nextDayBtn =findViewById( R.id.nextDayBtn);
        nextDayBtn.setOnClickListener( this );

        lastDayBtn =findViewById( R.id.lastDayBtn);
        lastDayBtn.setOnClickListener( this );
        ;
        rl_mainInformation=findViewById( R.id.rl_mainInformation );
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        tv_clearMainCaloriesList =findViewById( R.id.tv_clearMainCaloriesList);
        tv_clearMainCaloriesList.setOnClickListener( this );

        iv_backToMain =findViewById( R.id.iv_backToMain );
        iv_backToMain.setOnClickListener( this );
        rl_selfSearch=findViewById(R.id.rl_selfSearch);
        iv_goToSelfSearch =findViewById( R.id.iv_goToSelfSearch);
        iv_goToSelfSearch.setOnClickListener( this );

        caloriesViewText =findViewById( R.id.caloriesViewText);
        caloriesViewText.setOnClickListener( this );
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
    }
    private void updateMain() {
        updateCatalogListsAndView();
        refreshConsumedProductsList();
        //מיון לפי א"ב
        //sortArrayList();
    }
    private void updateCatalogListsAndView(){
        updateSystemProductList();
        refreshProductCatalog();
        loadCustomProductListData();
        addCustomProductListToProductCatalog();
    }
    private void cancelEdit() {
        consumedProductEditingDialog.close();
    }
    private void cancelFoodAdd() {
        customProductDialog.close();
        mainSearchView.setVisibility(View.VISIBLE);
        iv_backToMain.setVisibility( View.VISIBLE );

    }
    private void cancelNewFoodAdd() {
        hideKeyboard();
        cancelFoodAdd();
        iv_backToMain.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.VISIBLE);
        iv_selfSearch_round.setVisibility( View.GONE );
        iv_showSelfSearchBar.setVisibility( View.GONE );
        barcodeIcon.setVisibility( View.VISIBLE );
        mainSearchView.setQuery( "" , false );
        mainSearchView.setBackgroundResource( R.drawable.sty_3 );
        updateMain();  /// האם זה בכלל נחוץ? נראה שאין סיבה לעדכן נראות מסך ראשי אם מדובר בביטול פעולה

    }
    private void startInternetWebSearch(String query) {
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.loadUrl("https://www.google.com/search?q=" + query +" "+ "קלוריות");
        //העלם עמודת חיפוש והצג אפשרות ביטול
        //   serchview_internet.setVisibility(View.GONE);
    }
    private void startInternetWebSearchDotan(String SuggestionQuery) {
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.loadUrl("https://www.google.com/search?q=" + SuggestionQuery);
    }
    private void startWebSearchForBarcode(String bCod) {
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.loadUrl("https://www.google.com/search?q=" + bCod);

        //העלם עמודת חיפוש והצג אפשרות ביטול
        mainSearchView.setVisibility(View.GONE);
    }
    private void startWebSearch() {
        iv_backToMain.setVisibility( View.GONE );
        rl_mainInformation.setVisibility(View.GONE);
        rl_top.setVisibility( View.GONE );
        rl_selfSearchTopBar.setVisibility( View.VISIBLE );
        changeBarColor(rl_selfSearchTopBar);
        rl_selfSearch.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.GONE);
        rl_selfSearch.setVisibility( View.GONE );
        webview.setVisibility( View.VISIBLE );

        customProductDialog.show(productStorageManager , mainSearchView.getQuery().toString() , "" );

        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.loadUrl("https://www.google.com/search?q=" + mainSearchView.getQuery().toString() +" "+ "קלוריות");
         //העלם עמודת חיפוש והצג אפשרות ביטול
        mainSearchView.setVisibility(View.GONE);
        iv_selfSearch_round.setVisibility( View.GONE );
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
        consumedProductManager.addItem( amount, aProductItem,calendar);
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
            caloriesViewText.setText( ""+totalCalories );
            caloriesViewText.setBackgroundResource( R.drawable.sty_blue_r ); }
        else{    caloriesViewText.setText( "");
            caloriesViewText.setBackgroundResource( R.drawable.sty_blue_r_sercle ); }
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

            if (!consumedProductEditingDialog.isClosed()) {
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