package com.example.calories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class MyProductActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, ViewTreeObserver.OnGlobalLayoutListener {
    ArrayList<Integer> typeList = new ArrayList<>();//רשימת תמונות סוג מדד
    List<String> categories;
    //  Spinner s;
    ArrayList<ProductItem> myProductList = new ArrayList<>();//רשימת מוצרים שאני שמרתי
    ProductItem product_Item;
    ProductItemAdapter productItemAdapter;
    RecyclerView mRecyclerView;
    Button btn_save;
    LinearLayout ly_aditProduct;
    ImageView iv_delete, iv_barcode;
    EditText et_food, et_kal;
    Spinner spinner;
    private int menuToChoose = R.menu.pa_menu;

    int lastClickedItem = 0;
    private ProductStorageManager productStorageManager;
    private BarcodeDialogHandler barcodeDialogHandler;
    private ProductItemDeletionHelper deletionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_prodact );

        productStorageManager = new ProductStorageManager(this);
        barcodeDialogHandler = new BarcodeDialogHandler(this);


        ly_aditProduct = findViewById( R.id.layoutEditProduct);
        spinner = findViewById( R.id.spinner );
        spinner = (Spinner) findViewById( R.id.spinner );
        spinner.setOnItemSelectedListener( this );
        spinner.getViewTreeObserver().addOnGlobalLayoutListener( this );
        et_food = findViewById( R.id.et_food );
        et_kal = findViewById( R.id.et_kal );
        btn_save = findViewById( R.id.btn_save );
        btn_save.setOnClickListener( this );
        iv_delete = findViewById( R.id.iv_delete );
        iv_delete.setOnClickListener( this );
        iv_barcode = findViewById( R.id.iv_barcode );
        iv_barcode.setOnClickListener( this );
        mRecyclerView = findViewById( R.id.recyclerView );
        // makeSpinner();

        mRecyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        myProductList= productStorageManager.load();
        productItemAdapter = new ProductItemAdapter( myProductList );
        mRecyclerView.setAdapter(productItemAdapter);

        deletionHelper = new ProductItemDeletionHelper(
                this,
                myProductList,
                productItemAdapter,
                () -> changeMenu(deletionHelper.isInDeleteMode() ? R.menu.delete_item_menu : R.menu.pa_menu)
        );
        //פעולות לחיצה על איברי הרשימה- קלוריות מסך ראשי
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MyProductActivity.this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (deletionHelper.isInDeleteMode()) {
                            deletionHelper.toggleItemSelection(position);
                        } else {
                            // מצב רגיל - עריכת פריט
                            product_Item = myProductList.get(position);
                            lastClickedItem = position;
                            ly_aditProduct.setVisibility(View.VISIBLE);
                            et_food.setText(product_Item.getName());
                            et_kal.setText(product_Item.getCalorieText());
                            barcodeDialogHandler.getBarcodeEditText().setText(
                                    product_Item.getBarcode() != null ? product_Item.getBarcode() : ""
                            );
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        deletionHelper.enterDeleteMode(position);
                    }
                })
        );

        getSupportActionBar().show();
        //     getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        //     getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toast.makeText( this , "לחץ לחיצה ארוכה על פריט לסימון ומחיקה" , Toast.LENGTH_SHORT ).show();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the FAB click
                Intent i = new Intent(MyProductActivity.this, MainActivity.class);
                i.putExtra("selfSearch", true );
                startActivity( i );
            }
        });
    }

    public void changeMenu(int myMenu) {
        menuToChoose = myMenu;
        invalidateOptionsMenu();
    }


    private void dotanAll() {

        myProductList.add( new ProductItem( 0 , 1 , "נס קפה קר,  חלב סויה וניל" , "כוס זכוכית" , "90" , 0 , "" ) );
        myProductList.add( new ProductItem( 0 , 1 , "נס קפה קר" , "כוס זכוכית" , "100" , 0 , "" ) );
        myProductList.add( new ProductItem( 0 , 1 , "ציפס תפוח אדמה בנינגה" , "יחידה" , "8" , 0 , "" ) );
        myProductList.add( new ProductItem( 0 , 1 , "בורקס בשר 250 גרם" , "יחידה" , "625" , 0 , "" ) );
        myProductList.add( new ProductItem( 0 , 1 , "בולגרית יוונית לבן" , "קוביה" , "14" , 0 , "" ) );
        myProductList.add( new ProductItem( 0 , 1 , "בולגרית יוונית לבן" , "100 גרם" , "282" , 0 , "" ) );
        myProductList.add( new ProductItem( 0 , 1 , "טונה ירקות- עגבניה שום בצל פטריות תבלינים" , "יחידה" , "200" , 0 , "" ) );
        myProductList.add( new ProductItem( 0 , 1 , "טונה ירקות של דותן" , "100 גרם" , "91" , 0 , "" ) );
        myProductList.add( new ProductItem( 0 , 1 , "מרק ירקות- תפוד גזר בצל פקק שמן ותבלינים" , "סיר קטן" , "220" , 0 , "" ) );
        myProductList.add( new ProductItem( 0 , 1 , "טוסט גבינה 28 אחוז" , "יחידה" , "250" , 0 , "" ) );
        myProductList.add( new ProductItem( 0 , 1 , "קציצת גונדי" , "יחידה" , "60" , 0 , "" ) );
        myProductList.add( new ProductItem( 0 , 1 , "המבורגר דאבל וציפס" , "מנה" , "900" , 0 , "" ) );
        myProductList.add( new ProductItem( 0 , 1 , "טוסט חיילים-חצי פיתה" , "יחידה" , "350" , 0 , "" ) );
        //הגדרת המספר שמייצג את סוג המדד והתמונה המתאימה ,לפי סוג המדד השמר()
    /*
    for (int i=0; i<myProductList.size(); i++){
        for (int j=0;j<spinner.getAdapter().getCount();j++){
            if (myProductList.get( i ).getmTextType().equals( spinner.getItemAtPosition( j ).toString() )){
                myProductList.get( i ).setmTypePosition( j );
                myProductList.get( i ).setmImageTypeR( typeList.get( j ));
            }
        }
            }
     */

        productStorageManager.save(myProductList);

        //   updateMainList();
        Toast toast = null;
        toast = Toast.makeText( getBaseContext() , "נוסף למערכת! -המוצרים השמורים של דותן" , Toast.LENGTH_SHORT );
        toast.show();
    }

// העתק מוצרים שמורים של משתמש והצע שליחה למייל
    /*
    private void printAll() {
        ArrayList<ProductItem> myProductListttt = new ArrayList<>();
        myProductListttt=sortProductArrByOrder();

        String detailsString = "_myProductList_";
        detailsString = detailsString + " " + myProductListttt.size() + " items ";

        for (int i = 0; i < myProductListttt.size(); i++) {
            String b = myProductListttt.get( i ).getBarcode();
            if (b == null || b == "0" || b == "") {
                b = "";
            }
            String itemT = "SystemProductArr.add(new ProductItem(" + 0 + "," + 0 + "," + "\"" + myProductListttt.get( i ).getName() + "\"" + "," + "\"" + myProductListttt.get( i ).getUnit() + "\"" + " ," + "\"" + myProductListttt.get( i ).getCalorieText() + "\"" + "," + 0 + "," + "\"" + b + "\"" + "));";
            detailsString = detailsString.toString() + "\n" + itemT;
        }
        clipData( detailsString , this );
        emailSend( detailsString , this );
        Toast.makeText( getBaseContext() , "Copied successfully" + myProductListttt.size() + " items " , Toast.LENGTH_SHORT ).show();
    }

     */
    private void printAll() {
        ProductExporter exporter = new ProductExporter(this);
        exporter.export(myProductList);
    }


    @Override
    public void onClick(View view) {
        if (view == btn_save) {
            myProductList.get( lastClickedItem ).setCalorieText( et_kal.getText().toString().trim() );
            myProductList.get( lastClickedItem ).setName( et_food.getText().toString().trim() );
            myProductList.get( lastClickedItem ).setBarcode( barcodeDialogHandler.getBarcodeEditText().getText().toString().trim() );
            new ProductItemAdapter( myProductList );
            mRecyclerView.setAdapter(productItemAdapter);
            productStorageManager.save(myProductList);
            ly_aditProduct.setVisibility( View.GONE );

        }

        if (view == iv_barcode) {
            barcodeDialogHandler.showDialog();

        }
        if (view == iv_delete) {
            cancelEdit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode , int resultCode , Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (barcodeDialogHandler != null && result != null) {
            barcodeDialogHandler.handleActivityResult(result);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void cancelEdit() {
        ly_aditProduct.setVisibility( View.GONE );
    }

    private void clearDataAndList() {
        productStorageManager.clear();
        myProductList = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.delete_item_menu, menu);
        //  getMenuInflater().inflate(R.menu.delete_item_menu, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( menuToChoose , menu );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.menu1_back) {
                finish();
                return true;
            }

            if (item.getItemId() == R.id.menu1_dotan) {
                dotanAll();
                mRecyclerView.setAdapter(productItemAdapter);
                return true;
            }

            if (item.getItemId() == R.id.menu1_print_all) {
                printAll();
                return true;
            }

            if (item.getItemId() == R.id.menu_abortDelete) {
                deletionHelper.exitDeleteMode();
                return true;
            }

            if (item.getItemId() == R.id.menu_delete) {
                deletionHelper.deleteSelectedItems();
                productStorageManager.save(myProductList);
                return true;
            }

            if (item.getItemId() == R.id.menu_select_all) {
                deletionHelper.toggleSelectAll();
                return true;
            }

            return super.onOptionsItemSelected(item);

        }




    @Override
    public void onItemSelected(AdapterView<?> adapterView , View view , int i , long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onGlobalLayout() {

    }
}