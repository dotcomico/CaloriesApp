package com.example.calories.ui.screens;

import static com.example.calories.utils.SystemProducts_Utils.getSystemProductsArr;
import static com.example.calories.utils.Utility.isNumeric;
import static com.example.calories.utils.Utility.startNewActivity;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calories.BaseActivity;
import com.example.calories.ConsumedProductManager;
import com.example.calories.data.models.ConsumedProduct;
import com.example.calories.data.models.Product;
import com.example.calories.data.storage.ProductStorageManager;
import com.example.calories.ui.dialogs.ConsumedProductEditingDialog;
import com.example.calories.ui.dialogs.ProductSelectionDialog;
import com.example.calories.ui.utils.CaptureAct;
import com.example.calories.ui.adapters.ConsumedItemAdapter;
import com.example.calories.ui.adapters.ProductItemAdapter;
import com.example.calories.R;
import com.example.calories.ui.adapters.RecyclerItemClickListener;
import com.example.calories.ui.views.CircularProgressView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.calories.utils.AppConstants.*;

public class MainActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
    ///  יש לפעול לפיצול רשימות- יש רשימה אחת למוצרי מערכת ויש רשימה למוצרים אישיים אך יש לחברם יחד לרשימה כוללה
    /// כרגע מסיבה מסויימת הם מתחברים יחדיו לרשימת מוצרי המערכת מסיבה לא ברורה
    /// ראה ערך בפעולה addCustomProductListToProductCatalog()

    //--------------- CalorieTrackerView ---------------
    private RecyclerView consumedProductsRecyclerView;
    private ConsumedProduct consumedProduct_edit;
    ConsumedProductEditingDialog consumedProductEditingDialog;

    //--------------- ProductCatalogView ---------------
    ProductSelectionDialog productSelectionDialog;
    private ArrayList<Product> systemProductList = new ArrayList<>();//רשימת מוצרים (מערכת)
    private ArrayList<Product> customProducts = new ArrayList<>();//רשימת מוצרים שיצר המשתמש
    private ArrayList<Product> filteredProducts = new ArrayList<>();//רשימת מוצרים מסוננת (לפי חיפוש)
    private RecyclerView productsRecyclerView;
    private EditText searchTextView;
    private Product aProductItem =null;

    //--------------- CustomProductView  ---------------


    //--------------- others  ---------------
    private Calendar calendar;
    private final CaptureAct captureAct = new CaptureAct();

    private ProductStorageManager productStorageManager;
    ConsumedProductManager consumedProductManager;
    private ImageView lastDayBtn, nextDayBtn;

    private ImageView selfSearchIcon, selfAddIcon, iv_goToSelfSearch,
            settingsIcon, barcodeIcon , catalogIcon , backIcon , searchIcon;
    private RelativeLayout notFoundLayout;
    private TextView currentDateText;

    private TextView caloriesViewText;
    private ImageView customListIcon;

    private CircularProgressView calorieProgressView;
    private TextView caloriesDescriptionText;
    private TextView breakfastCalories;
    private TextView lunchCalories;
    private TextView dinnerCalories;
    private TextView collapsedDateText;
    private TextView collapsedCaloriesText;
    private AppBarLayout appBarLayout;
    private RelativeLayout caloriesLayout ,catalogLayout;
    LinearLayout collapsedLayout;
    LinearLayout meals;
     int totalDailyCalories = 2000; // יעד קלוריות יומי
     int consumedCalories = 0; // קלוריות שנצרכו

    private boolean isShowingConsumed = true; // מצב נוכחי - true = צרכנו, false = נותר
    private boolean isAnimating = false; // למנוע אנימציות מרובות במקביל


    // יצירת מאזין למסך הגדרות ובסגירה המיין אקטיביטי צריך להתעדכן
    //recreate(); מעדכן אותו


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        EdgeToEdge.enable(this);
        setContentView( R.layout.activity_main );

        setupSystemUI();
        initManagersAndDialogs();
        calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        initViews();
        setupAppBarListener();
        setupRecyclerListeners();
        setupSearchView();
        setupListeners();

        searchIcon.setVisibility(View.VISIBLE);
        catalogLayout.setVisibility(View.GONE);
        updateMain();

    }
    private void setupListeners() {
        consumedProductEditingDialog.setOnEditCompleteListener(() -> {
            refreshConsumedProductsList();
            updateTotalCalories();
            updateProgressView();
        });

        productSelectionDialog.setOnProductSelectedListener(() -> {
            dismissCatalog();
            refreshConsumedProductsList();
            updateTotalCalories();
            updateProgressView();
        });

        MyProductActivity.setScreenCloseListener(new MyProductActivity.ScreenCloseListener() {
            @Override
            public void onScreenClosed() {
                updateMain();
            }
        });


        ProductStorageManager.setGlobalProductCreatedListener(newProduct -> {
//              productsRecyclerView.setBackgroundColor(Color.RED);
            dismissCatalog();
            updateMain();
            openProductSelectionDialog(newProduct);
        });

    }

    private void setupSearchView() {
        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleSearchTextChange(s.toString());
            }
        });
        // הוספת מאזין לכפתור ה"חיפוש" במקלדת (אופציונלי)
        searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // בודק אם המשתמש לחץ על "חפש", "הבא" וכו' מהמקלדת
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = searchTextView.getText().toString();

                    if (isNumeric(query)) {
                        selfAddActions();
                    } else if (filteredProducts.isEmpty()) {
                        // אם הרשימה ריקה (מוצר לא נמצא) תפתח חיפוש עצמי
                        openCustomProductByName(query);
                    }

                    // הסתרת המקלדת לאחר החיפוש
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);

                    return true; // ציין שהאירוע טופל
                }
                return false;
            }
        });


            ///  פעולה זו מתקיימת בכל עדכון של מצב יום או לילה מסיבה מסויימת.
    }

    private void handleSearchTextChange(String query) {
        /// סגירת החיפוש
        if (query.isEmpty()){ //ללא הבדיקה הזו, בכל מצב של שינוי המצב יום או לילה של האפליקציה אז השורת "פועלת" ולכן הפעולה רצה ופותחת את רשימת המוצרים סתם
            catalogLayout.setVisibility(View.GONE); //הצג קטלוג
            catalogIcon.setVisibility(View.VISIBLE);
            selfAddIcon.setVisibility(View.GONE);
            selfSearchIcon.setVisibility( View.GONE );
            searchIcon.setVisibility(View.VISIBLE);
            backIcon.setVisibility(View.GONE);
            return;
        }
        // עדכון רשימת מזון לפי חיפוש בזמן הקלדה
        backIcon.setVisibility(View.VISIBLE);
        searchIcon.setVisibility(View.GONE);
        searchInFoodList(query);


        /// הוספה עצמית של קלוריות - כשמוקלד מספר
        if (isNumeric( query )){
            catalogIcon.setVisibility(View.GONE);
            selfAddIcon.setVisibility(View.VISIBLE);

            selfSearchIcon.setVisibility( View.GONE );
            catalogLayout.setVisibility(View.GONE);
        }
        ///  הצעה לחיפוש עצמי והוספת מוצר - כשרשימה ריק
        else if (filteredProducts.isEmpty()){  // אם הרשימה ריקה (אין מוצרים)- הצעה לחיפוש עצמי
            catalogIcon.setVisibility(View.GONE);
            selfAddIcon.setVisibility(View.GONE);
            selfSearchIcon.setVisibility( View.VISIBLE );

            catalogLayout.setVisibility(View.VISIBLE);
            notFoundLayout.setVisibility(View.VISIBLE);
        }
        ///  בכל הקלדה
        else {
            catalogIcon.setVisibility(View.VISIBLE);
            selfAddIcon.setVisibility(View.GONE);
            selfSearchIcon.setVisibility( View.GONE );

            catalogLayout.setVisibility(View.VISIBLE);
            notFoundLayout.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerListeners() {
        productsRecyclerView.addOnItemTouchListener( new RecyclerItemClickListener(MainActivity.this, productsRecyclerView
                ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                if (!(filteredProducts.get( position ).getItemState() ==PRODUCT_STATE_SELF_SEARCH)) {
                    aProductItem = filteredProducts.get( position );
                    openProductSelectionDialog(aProductItem);
                }
                else{
                    //פתח מסך הוספת מוצר
                    openCustomProductByName( searchTextView.getText().toString());
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

    }

    private void setupSystemUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appBarLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });
    }
    /// כך ניתן לגרום לשורת חיפוש לרחף מעל מקלדת
    /// עובד כאשר הסטייל במניפסט הוא android:theme="@style/Base.Theme.Calories"
    /// כלומר: <style name="Base.Theme.Calories" parent="Theme.Material3.DayNight.NoActionBar"> </style>
    private void initManagersAndDialogs() {
        productStorageManager = new ProductStorageManager(this);
        consumedProductManager = new ConsumedProductManager(this);
        consumedProductEditingDialog = new ConsumedProductEditingDialog(this);
        productSelectionDialog = new ProductSelectionDialog(this);
    }
    private void openProductSelectionDialog(Product product) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isActive()) {
            // המקלדת פעילה - סוגרים ומחכים
            searchTextView.clearFocus();
            hideKeyboardAndShowDialog(product);
        } else {
            productSelectionDialog.show(product,calendar , consumedProductManager);
        }
    }

    private void hideKeyboardAndShowDialog(Product product) {
        hideKeyboard();
        new Handler(Looper.getMainLooper()).postDelayed(() -> productSelectionDialog.show(product,calendar , consumedProductManager), 200); //
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View view) {
        if (view == caloriesLayout){
            if (!isAnimating) {
                flipCaloriesView();
            }
        }

        if(view== customListIcon){
            startNewActivity(MainActivity.this, MyProductActivity.class);
        }

        if (view == settingsIcon){
//            ly_settings.setVisibility( View.VISIBLE );
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        if (view == catalogIcon){
            openCatalog();
        }

        if (view== selfAddIcon) {
            selfAddActions();
        }

        if(view== barcodeIcon){
            closeCatalog();
            scanCode();}

        if(view== nextDayBtn){
            //יקרה רק אם כל שאר המסכים סגורים
            if (notFoundLayout.getVisibility()==View.GONE){
                calendar.add(Calendar.DAY_OF_MONTH, 1); //Adds a day
                currentDateText.setText( new SimpleDateFormat(DATE_PATTERN).format(calendar.getTime()));
                collapsedDateText.setText(currentDateText.getText());
                refreshConsumedProductsList();
                updateTotalCalories();
                updateProgressView();
            }
        }

        if(view== lastDayBtn){
            //יקרה רק אם כל שאר המסכים סגורים
            if (notFoundLayout.getVisibility()==View.GONE) {
                calendar.add( Calendar.DAY_OF_MONTH , -1 ); //Goes to previous day
                currentDateText.setText( new SimpleDateFormat( DATE_PATTERN ).format( calendar.getTime() ) );
                collapsedDateText.setText(currentDateText.getText());
                refreshConsumedProductsList();
                updateTotalCalories();
                updateProgressView();
            }
        }

        if (view== iv_goToSelfSearch){
            openCustomProductByName( searchTextView.getText().toString());
        }

        if (view== selfSearchIcon){
            openCustomProductByName( searchTextView.getText().toString());
        }

        if (view == backIcon){
            backToMain();
        }

    }
    private void flipCaloriesView() {
        isAnimating = true;

        int remainingCalories = totalDailyCalories - consumedCalories;
        float remainingProgress = (float) remainingCalories / totalDailyCalories;
        float consumedProgress = (float) consumedCalories / totalDailyCalories;

        AnimatorSet growAndHalfFlip = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.grow_and_half_flip);
        growAndHalfFlip.setTarget(caloriesLayout);

        AnimatorSet halfFlipAndShrink = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.half_flip_and_shrink);
        halfFlipAndShrink.setTarget(caloriesLayout);

        growAndHalfFlip.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // מתבצע כשהמסך באמצע (ב־90 מעלות)
                isShowingConsumed = !isShowingConsumed;

                if (isShowingConsumed) {
                    caloriesViewText.setText(String.valueOf(consumedCalories));
                    caloriesDescriptionText.setText(R.string.calories);
                    calorieProgressView.setProgress(consumedProgress);
                } else {
                    caloriesViewText.setText(String.valueOf(remainingCalories));
                    caloriesDescriptionText.setText(R.string.remaining_calories);
                    calorieProgressView.setProgress(remainingProgress);
                }

                // להפעיל את האנימציה השנייה
                halfFlipAndShrink.start();
            }
        });

        halfFlipAndShrink.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }
        });

        growAndHalfFlip.start();
    }
    private void selfAddActions() {
        String calorieText= searchTextView.getText().toString().trim();
        aProductItem = new Product(PRODUCT_STATE_SYSTEM,"הוספת עצמית",UNIT_CALORIES ,"0","");
        aProductItem.setCalorieText("100");
        addConsumedProductToList( Integer.parseInt( calorieText ) , Integer.parseInt( calorieText ) );

        updateTotalCalories();
        updateProgressView();

        searchTextView.setVisibility( View.VISIBLE );
        searchTextView.setText( "" );
//        searchTextView.setIconified( true );
        hideKeyboard();
        backIcon.setVisibility( View.GONE );
        searchIcon.setVisibility(View.VISIBLE);
        if (!consumedProductManager.getConsumedProductsOfDay().isEmpty()) {
            consumedProductsRecyclerView.smoothScrollToPosition( consumedProductManager.getConsumedProductsOfDay().size() - 1 );
        }
        selfAddIcon.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result =IntentIntegrator.parseActivityResult( requestCode,resultCode,data );
        if(result != null){
            if (result.getContents() != null){
                    searchProductByBarcode(result.getContents());}
            else{
                Toast.makeText( this, TEXT_NO_RESULT,Toast.LENGTH_SHORT ).show();
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
                            openCatalog();
                            searchInFoodList( examplel.getName() );

                        }
                    }else{//בדוק על כל אחד
                        String currentString = examplel.getBarcode().trim();
                        String[] separated = currentString.split(",");
                        for (String s : separated) {
                            if (barcode.trim().matches(s.trim())) {//אם תואם לחיפוש
                                temp = true;
                                openCatalog();
                                searchInFoodList(examplel.getName());

                            }
                        }
                    }
                }
                j++;
            }

            if (temp) { //נמצאה התאמה
                openProductSelectionDialog(examplel);
                if (filteredProducts.size() == 2) {
                    closeCatalog();
                }
            } else {//לא נמצא
                //חפש באינטרנט
                openCustomProductByBarcode(barcode.trim());
            }
    }

    private void searchInFoodList(String s) {
        //פעולת חיפוש ועדכון רשימת מזון
        filteredProducts = new ArrayList<>();
        //חפש לפי שייכים לי
        for (int j = 0; j < systemProductList.size(); j++){
            Product example2 = systemProductList.get(j);
            if (example2.getName().toLowerCase().trim().contains( s.toLowerCase().trim() )&&example2.getItemState()==PRODUCT_STATE_CUSTOM ){
                filteredProducts.add(example2);}
        }
        //חפש ברשימה כללית
        for (int i = 0; i < systemProductList.size(); i++){    //הכנס את מי שמתחילים בטקסט שהוקלד
            Product example = systemProductList.get(i);
            if(example.getName().toLowerCase().trim().startsWith( s.toLowerCase().trim() )&&example.getItemState()==PRODUCT_STATE_SYSTEM){
                filteredProducts.add(example);}
        }
        for (int i = 0; i < systemProductList.size(); i++){  //ורק אז הכנס את מי שנשאר ומכיל את הטקסט שהוקלד
            Product example = systemProductList.get(i);
            if (example.getName().toLowerCase().trim().contains( s.toLowerCase().trim() )&&!example.getName().toLowerCase().trim().startsWith( s.toLowerCase().trim() ) && example.getItemState()==PRODUCT_STATE_SYSTEM){
                filteredProducts.add(example);}
            //    sortByAB(  filteredExampleList );
        }
        //הצג הצעה - במקרה של חוסר תוצאות
        if (!filteredProducts.isEmpty()){ filteredProducts.add(new Product( PRODUCT_STATE_SELF_SEARCH, "", "","",""));}
        //עדכן רשימה
        productsRecyclerView.setAdapter(new ProductItemAdapter(filteredProducts));
    }
    private void dismissCatalog() {
        notFoundLayout.setVisibility( View.GONE );
        catalogLayout.setVisibility(View.GONE);
//        mainSearchView.setVisibility( View.VISIBLE );
        searchTextView.setText( "" );
//        searchTextView.setIconified( true );
        hideKeyboard();
        backIcon.setVisibility( View.GONE );
        searchIcon.setVisibility(View.VISIBLE);

    }

    // פתיחת וסגירת מסכים
    private void closeCatalog(){
        backIcon.setVisibility(View.GONE);
        catalogLayout.setVisibility(View.GONE);
    }
    private void openCatalog(){
        catalogIcon.setVisibility(View.VISIBLE);
        selfAddIcon.setVisibility(View.GONE);
        selfSearchIcon.setVisibility( View.GONE );
        backIcon.setVisibility(View.VISIBLE);
        searchIcon.setVisibility(View.GONE);
        catalogLayout.setVisibility(View.VISIBLE);
        cancelEdit();
    }
    private void openCustomProductByBarcode(String barcode){
        cancelEdit();
            Intent i = new Intent(MainActivity.this, ProductCreationActivity.class);
        i.putExtra(EXTRA_BARCODE, barcode );
        startActivity( i );
    }
    private void openCustomProductByName( String name){
        cancelEdit();
        Intent i = new Intent(MainActivity.this, ProductCreationActivity.class);
        i.putExtra(EXTRA_NAME, name );
        startActivity( i );
    }

    private void initViews() {
        customListIcon =findViewById( R.id.customListIcon);
        customListIcon.setOnClickListener( this );
        catalogLayout=findViewById(R.id.catalogLayout);
        calorieProgressView = findViewById(R.id.calorieProgressView);
        caloriesDescriptionText =findViewById(R.id.caloriesDescriptionText);
        breakfastCalories = findViewById(R.id.breakfastCalories);
        lunchCalories = findViewById(R.id.lunchCalories);
        dinnerCalories = findViewById(R.id.dinnerCalories);
        collapsedDateText = findViewById(R.id.collapsedDateText);
        collapsedCaloriesText = findViewById(R.id.collapsedCaloriesText);
        appBarLayout = findViewById(R.id.appBarLayout);
        collapsedLayout = findViewById(R.id.collapsedLayout);
        caloriesLayout = findViewById(R.id.caloriesLayout);
        caloriesLayout.setOnClickListener(this);
        consumedProductsRecyclerView =findViewById( R.id.consumedProductsRecyclerView);
        consumedProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchIcon =findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener( this );
        backIcon =findViewById( R.id.backIcon);
        backIcon.setOnClickListener( this );
        barcodeIcon =findViewById( R.id.barcodeIcon);
        barcodeIcon.setOnClickListener( this );
        catalogIcon=findViewById(R.id.catalogIcon);
        catalogIcon.setOnClickListener(this);
        selfSearchIcon =findViewById( R.id.selfSearchIcon);
        selfSearchIcon.setOnClickListener( this );
        selfAddIcon =findViewById( R.id.selfAddIcon);
        selfAddIcon.setOnClickListener( this );
        settingsIcon =findViewById( R.id.settingsIcon);
        settingsIcon.setOnClickListener( this );
        currentDateText =findViewById( R.id.currentDateText);
        nextDayBtn =findViewById( R.id.nextDayBtn);
        nextDayBtn.setOnClickListener( this );
        lastDayBtn =findViewById( R.id.lastDayBtn);
        lastDayBtn.setOnClickListener( this );
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        notFoundLayout =findViewById(R.id.notFoundLayout);
        iv_goToSelfSearch =findViewById( R.id.iv_goToSelfSearch);
        iv_goToSelfSearch.setOnClickListener( this );
        caloriesViewText =findViewById( R.id.caloriesViewText);
        caloriesViewText.setOnClickListener( this );
        searchTextView = findViewById( R.id.searchTextView);

        // נותן תחושת עומק לסיבוב
        caloriesLayout.setCameraDistance(8000 * getResources().getDisplayMetrics().density);

        // לדאוג ש־pivot יהיה במרכז אחרי שה־View נמדד
        caloriesLayout.post(() -> {
            caloriesLayout.setPivotX(caloriesLayout.getWidth() / 2f);
            caloriesLayout.setPivotY(caloriesLayout.getHeight() / 2f);
        });
    }
    private void setupAppBarListener() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                float collapseRatio = Math.abs((float) verticalOffset / totalScrollRange);

                // התכווצות המד המרכזי
                float scale = 1f - (collapseRatio * 0.7f); // יתכווץ ל-30% מהגודל
                calorieProgressView.setScaleX(scale);
                calorieProgressView.setScaleY(scale);
                calorieProgressView.setAlpha(1f - collapseRatio);

                caloriesViewText.setScaleX(scale);
                caloriesViewText.setScaleY(scale);
                caloriesViewText.setAlpha(1f - collapseRatio);

                caloriesDescriptionText.setScaleX(scale);
                caloriesDescriptionText.setScaleY(scale);
                caloriesDescriptionText.setAlpha(1f - collapseRatio);

//                // הזזת מספר הקלוריות והתאריך לצד
//                float translationX = collapseRatio * -150; // הזזה שמאלה
//                caloriesViewText.setTranslationX(translationX);
//                caloriesViewText.setScaleX(1f - (collapseRatio * 0.5f));
//                caloriesViewText.setScaleY(1f - (collapseRatio * 0.5f));
//
//                currentDateText.setTranslationX(translationX);

                // העלמת הכותרת והארוחות
                TextView title = findViewById(R.id.titleText); // תוסיף id לכותרת
                title.setAlpha(1f - collapseRatio);
                meals = findViewById(R.id.meals);
                // העלמת סיכום הארוחות
                meals.setAlpha(1f - collapseRatio);

//                int remainingCalories = totalDailyCalories - consumedCalories;

                // הצג את collapsedLayout רק כשכמעט לגמרי מכווץ
                if (collapseRatio > 0.9f) {
                    collapsedLayout.setVisibility(View.VISIBLE); // הוסף שורה זו!
                    collapsedLayout.setAlpha((collapseRatio - 0.9f) / 0.1f);
                } else {
                    collapsedLayout.setAlpha(0f);
                    if (collapseRatio < 0.85f) {
                        collapsedLayout.setVisibility(View.INVISIBLE); // הסתר רק כשממש רחוק
                    }
                }
            }
        });
    }

    private void updateMain() {
        updateCatalogListsAndView();
        refreshConsumedProductsList();
        // עיכוב קטן לפני הצגת האנימציות
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateInformation();
            }
        }, 300);
    }
    private void updateInformation() {
        updateTotalCalories();
        animateCaloriesText(consumedCalories , 2000);
        updateProgressView();
        caloriesDescriptionText.setText(R.string.calories);

        // עדכון קלוריות הארוחות
        breakfastCalories.setText(R.string.blank_);
        lunchCalories.setText(R.string.blank_);
        dinnerCalories.setText(R.string.blank_);
        collapsedDateText.setText(currentDateText.getText());
        collapsedCaloriesText.setText(getDisplayCaloriesString());
    }

    private String getDisplayCaloriesString() {
        String cal = String.valueOf(consumedCalories);
        String caloriesWord = getString(R.string.calories);
        return cal + " " + caloriesWord;
    }

    private void updateProgressView(){
        // עדכון המד המרכזי
        if (!isShowingConsumed){
            flipCaloriesView();
        }else {
            float progress = (float) consumedCalories / totalDailyCalories;
            calorieProgressView.setProgress(progress);
        }
    }
    private void animateCaloriesText(int targetCalories , long time ) {
        ValueAnimator animator = ValueAnimator.ofInt(0, targetCalories);
        animator.setDuration(time);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                int animatedValue = (Integer) animation.getAnimatedValue();
                caloriesViewText.setText(String.valueOf(animatedValue));
            }
        });

        animator.start();
    }
    private void updateCatalogListsAndView(){
        updateSystemProductList();
        refreshProductCatalog();
        loadCustomProductListData();
        addCustomProductListToProductCatalog();
    }
    private void updateSystemProductList()   {
        systemProductList = new ArrayList<>();
        systemProductList =getSystemProductsArr();
        // המצב הראשוני של רשימת החיפוש כרשימת המוצרים (ברירת מחדל)
        filteredProducts = systemProductList;
    }
    private void refreshProductCatalog(){
        //עדכון הרשימה הפיזית במסך כרשימת המוצרים
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter( new ProductItemAdapter(systemProductList));
    }
    private void loadCustomProductListData() {
        customProducts = productStorageManager.load();
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
    private void cancelEdit() {
        consumedProductEditingDialog.close();
    }
    private void refreshConsumedProductsList(){
        loadConsumedProductData(calendar);
        ArrayList<ConsumedProduct>consumedProducts=consumedProductManager.getConsumedProductsOfDay();
        consumedProductsRecyclerView.setAdapter(new ConsumedItemAdapter(consumedProducts));
//        if (!consumedProducts.isEmpty()){
//            consumedProductsRecyclerView.smoothScrollToPosition(consumedProducts.size()-1);}
    }
    private void updateTotalCalories(){
        consumedCalories = consumedProductManager.getConsumedCaloriesOfDay();
        caloriesViewText.setText( String.valueOf(consumedCalories));
        collapsedCaloriesText.setText(getDisplayCaloriesString());
    }
    private void backToMain() {
        searchTextView.setVisibility( View.VISIBLE );
        searchTextView.setText( "" );
        searchIcon.setVisibility(View.VISIBLE);
        backIcon.setVisibility(View.GONE);
//        searchTextView.setIconified(true);
        hideKeyboard();
        closeCatalog();
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
    private void restartApp() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
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

    private void deleteConsumedProductById(String targetId){
        consumedProductManager.deleteItemById(targetId);
        updateTotalCalories();
        updateProgressView();
        consumedProductsRecyclerView.setAdapter(new ConsumedItemAdapter(consumedProductManager.getConsumedProductsOfDay()));
    }

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
            if (!consumedProductEditingDialog.isClosed()) {
                cancelEdit();
                return;
            }
            super.onBackPressed();
    }


}