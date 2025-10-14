package com.example.calories.ui.screens;

import static com.example.calories.utils.Utility.startNewActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.calories.BaseActivity;
import com.example.calories.R;
import com.example.calories.data.models.Product;
import com.example.calories.data.storage.ProductStorageManager;
import com.example.calories.ui.dialogs.CustomProductDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import static com.example.calories.utils.AppConstants.*;
import java.util.Objects;

public class ProductCreationActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_selfSearch, topBar;
    private ImageView backBtn, showDialogSheet;
    private WebView webview;
    WebSettings webSettings;
    CustomProductDialog customProductDialog;
    private ProductStorageManager productStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_creation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();

        initViews();
        productStorageManager  = new ProductStorageManager(this);
        customProductDialog = new CustomProductDialog(this);
        customProductDialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (webview.getVisibility()== View.VISIBLE){
                    showDialogSheet.setVisibility(View.VISIBLE);}
            }
        });
        customProductDialog.setOnCustomProductItemListener(new CustomProductDialog.OnCustomProductItemListener() {
            @Override
            public void onItemCreated(Product customProduct) {
                hideKeyboard();
                cancelFoodAdd();
               finish();
            }

            @Override
            public void onSearch(String suggestion) {
                searchForCalories(suggestion);
            }

            @Override
            public void onSearchSuggestionClicked(String suggestion) {
                searchForSuggestion(suggestion);
                hideKeyboard();
                customProductDialog.close();
                showDialogSheet.setVisibility( View.VISIBLE );
            }

            @Override
            public void onDialogClose() {
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String barcode = extras.getString( EXTRA_BARCODE);
            if (barcode != null) {
                customProductDialog.show(productStorageManager , "" , barcode);
                searchOnWeb(barcode);
            }
            String name = extras.getString( EXTRA_NAME );
            if (name != null) {
                customProductDialog.show(productStorageManager , name , "");
                searchForCalories(name);
            }
        }
    }
    private void initViews() {
        topBar =findViewById(R.id.topBar);
        backBtn =findViewById(R.id.backBtn);
        backBtn.setOnClickListener( this );
        showDialogSheet =findViewById( R.id.showDialogSheet);
        showDialogSheet.setOnClickListener( this );
        webview = findViewById( R.id.webview);
        webview.setWebViewClient( new WebViewClient() );
        webSettings=webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //לא ברור מה אלו
        webSettings.setDatabaseEnabled( true );
        webSettings.setEnableSmoothTransition( true );
        webSettings.setGeolocationEnabled( true );
        webSettings.setDomStorageEnabled(  true);

        if (isDarkModeEnabled()) {
            View overlay = new View(this);
            overlay.setBackgroundColor(Color.parseColor("#66000000")); // שכבה שחורה שקופה
            ((ViewGroup) webview.getParent()).addView(overlay,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));

            overlay.bringToFront();
        }

//        boolean isDarkMode = isDarkModeEnabled(); // פונקציה שקיימת ב-BaseActivity
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            webSettings.setForceDark(isDarkMode
//                    ? WebSettings.FORCE_DARK_ON
//                    : WebSettings.FORCE_DARK_OFF);
//        }
//        webview.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//
//                boolean isDarkMode = isDarkModeEnabled();
//                if (isDarkMode) {
//                    String darkCss =
//                            "document.body.style.backgroundColor = '#121212';" +
//                                    "document.body.style.color = 'white';";
//                    view.evaluateJavascript(darkCss, null);
//                }
//            }
//        });
    }

    private void searchOnWeb(String query){
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.loadUrl(GOOGLE_SEARCH_URL + query);
    }
    private void searchForCalories(String caloriesQuery) {
        searchOnWeb(caloriesQuery + " "+ UNIT_CALORIES );
    }
    private void searchForSuggestion(String SuggestionQuery) {
      searchOnWeb(SuggestionQuery);
    }
    public void hideKeyboard(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void cancelFoodAdd() {
        customProductDialog.close();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult( requestCode,resultCode,data );
        if(result != null){
            if (result.getContents() != null){

                customProductDialog.handleBarcodeResult(result);

            }else{
                Toast.makeText( this, TEXT_NO_RESULT,Toast.LENGTH_SHORT ).show();
            }

        }else{
            super.onActivityResult( requestCode, resultCode, data );}
    }

    @Override
    public void onClick(View view) {
        if( view == backBtn){
            finish();
        }

        if (view == showDialogSheet){
            showDialogSheet.setVisibility( View.GONE );
            customProductDialog.show(productStorageManager , "" , "");
        }
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack() && webview.getVisibility() == View.VISIBLE) {
            webview.goBack();
        } else if (customProductDialog.isClosed()){
            customProductDialog.show(productStorageManager , "" , "");
        }else {
        super.onBackPressed();}
    }
}