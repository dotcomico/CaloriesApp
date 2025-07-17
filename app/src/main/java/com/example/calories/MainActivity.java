package com.example.calories;

import static com.example.calories.SystemProducts_Utils.getSystemProductsArr;
import static com.example.calories.Utility.clipData;
import static com.example.calories.Utility.isNumeric;
import static com.example.calories.Utility.startNewActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, ViewTreeObserver.OnGlobalLayoutListener, TextWatcher, View.OnTouchListener {
    private WebView webview;
    private Calendar cal;
    private SearchView searchview,serchview_internet;
    private EditText et_food,et_kal,et_amounttt,et_spinnerEditT,et_newAmount, et_d_enter_code;
    private Button btn_addNeePrivetFood,btn_addFood,btn_lastDay,btn_nextDay,btn_aditdFood;
    private LinearLayout ly_selfSearch_bar, ly_addNewPrivetFood,ly_addFood,ly_settings,ly_aditAmount;
    private ImageView  iv_back_SS_to_M, iv_myProdacts_SS, iv_showSelfSearchBar, iv_hideSelfSearchBar, iv_selfSearch_round ,  iv_selfAdd, iv_barcode,iv_search,
            iv_backToMain,iv_delete1,iv_settings,iv_anther,iv_returnToSpinner,plus_z,minus_z,more_z,iv_deleteAdit,minus_adit,plus_adit, iv_barcodeSearch_round;
    private RelativeLayout rl_selfSearch,rl_top,rl_top_ss,rl_mainInformation,llll_spinnerEdit;
    private String foodKaloria_str="";
    private String foodName_str="";
    private TextView tv_food,tv_kal,tv_Type ,text,tv_date,t_see,tv_foodname;
    private int calcolaty_mod,mainL_position=-1;
    private ProductItem temp_exampleItem=null;
    float type_x,type_y,amounttt_x,amounttt_y,p_x,m_x,pa_x,ma_x;
    List<String> categories;
    Button moreSerch;
    //רשימת מוצרים
    private RecyclerView mRecyclerView;
    private RecyclerView myListRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager,myList_LayoutManager;
    private RecyclerView.Adapter myListAdapter;
    ArrayList<ProductItem> exampleList = new ArrayList<>();//רשימת מוצרים (מערכת)
    ArrayList<ProductItem> filteredExampleList = new ArrayList<>();//רשימת מוצרים המתעדכנת לפי חיפוש
    ArrayList<ProductItem> myPrivetFoodlList = new ArrayList<>();//רשימת מוצרים שאני שמרתי
    ArrayList<ConsumedItem> myList = new ArrayList<>();//רשימת מוצרים שצרכתי
    ArrayList<ConsumedItem> myList_temp = new ArrayList<>();
    ConsumedItem consumedItem_adit;
    ArrayList<Integer> typeList = new ArrayList<>();//רשימת תמונות סוג מדד
    Spinner spinner;
    SharedPreferences sp;
    TextView textttttdtghfd,ffffffdtghfd,t_sdfdfss;
    ImageView iv_d_code_scan,iv_myProdacts;
    Dialog QrCode_dialog;
    Animation slide_in_bottom,slide_out_bottom;
    TextView tv_qr_information;
    ImageView iv_expend_more;
    Dialog dialog ;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        cal = Calendar.getInstance();
        cal.setTime(cal.getTime());
        findViewAndMore();
        changeBarColor(rl_top);
        makeSpinner();
        //יצירת רשימת המוצרים
        updateMainList();
        //מיון לפי א"ב
        //      sortArrayList();



        et_kal.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                if ( et_kal.getText().toString().equals( "." )){ et_amounttt.setText( "" );}

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        } );
        et_amounttt.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                String st = null;
                if ( et_amounttt.getText().toString().equals( "." )){ et_amounttt.setText( "" );}
                if ( et_amounttt.getText().toString().matches("")){     st=calcolatyCAL(Double.parseDouble( tv_kal.getText().toString() ),0,tv_Type.getText().toString());
                }else{
                    //   if (TextUtils. isDigitsOnly(et_amounttt.getText().toString() )) {
                    st = calcolatyCAL( Double.parseDouble( tv_kal.getText().toString() ) , Double.parseDouble( et_amounttt.getText().toString() ),tv_Type.getText().toString() );
                    //    }

                }
                //     if (TextUtils. isDigitsOnly(et_amounttt.getText().toString() )) {
                btn_addFood.setText( ""+st+""+""+ "\n"+" הוסף " );
                //   }else{  btn_addFood.setText( " הוסף " );}

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //פעולות לחיצה על איברי הרשימה
        mRecyclerView.addOnItemTouchListener( new RecyclerItemClickListener(MainActivity.this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                if (!(filteredExampleList.get( position ).getItemState() ==999)) {
                    temp_exampleItem=filteredExampleList.get( position );
                    showFoodDitals(filteredExampleList.get( position ));
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
        myListRecyclerView.addOnItemTouchListener( new RecyclerItemClickListener(MainActivity.this, myListRecyclerView , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view , int position)
            {
                //            Toast.makeText(getBaseContext(), "position:"+position+ "\n"+"  מספר ברשימה כללית:"+myList.get( position).getSerial()+"", Toast.LENGTH_SHORT).show();
                //עריכת פריט
                ly_aditAmount.startAnimation( slide_in_bottom );
                ly_aditAmount.setVisibility( View.VISIBLE );
                consumedItem_adit =myList.get( position );
                tv_foodname.setText( consumedItem_adit.getProductItem().getName() );
                et_newAmount.setText( ""+ consumedItem_adit.getAmount()+"");
                mainL_position=position;
            }

            @Override
            public void onLongItemClick(View view , int position) {
                if (ly_aditAmount.getVisibility()==View.GONE){
                    deleteFromCalList(position);}
            }
        } ) );
        //פעולת חיפוש
        searchview.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override//כאשר לוחצים חפש
            public boolean onQueryTextSubmit(String s) {
                if (isNumeric( s )){
                    selfAddActions();
                }else if (filteredExampleList.size()==0){       //אם הרשימה ריקה(מוצר לא נמצא) תפתח חיפוש עצמי
                    startWebSearch(); }
                else { //אחרת סגור את מסך האתר
                //   webview.setVisibility(View.GONE);
                }
                return false;
            }
            @Override//כאשר החיפוש מתבצע
            public boolean onQueryTextChange(String s) {
                // עדכון רשימת מזון לפי חיפוש בזמן הקלדה
                iv_backToMain.setVisibility(View.VISIBLE);
              //  mRecyclerView.setVisibility(View.VISIBLE);

                searchInFoodList(s);

                if (isNumeric( s )){
                    searchview.setBackgroundResource( R.drawable.sty_3_purple );
                    rl_mainInformation.setVisibility(View.VISIBLE);
                    iv_selfAdd.setVisibility(View.VISIBLE);
                    iv_selfSearch_round.setVisibility( View.GONE );
                    iv_barcodeSearch_round.setVisibility( View.GONE );
                    rl_selfSearch.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                     iv_backToMain.setImageResource( R.drawable.ic_baseline_arrow_circle_right_purple );

                }
                else if (filteredExampleList.size()==0){  // אם הרשימה ריקה (אין מוצרים)- הצעה לחיפוש עצמי
                    searchview.setBackgroundResource( R.drawable.sty_3_oreng );
                    rl_mainInformation.setVisibility(View.GONE);
                    iv_selfAdd.setVisibility(View.GONE);
                    iv_selfSearch_round.setVisibility( View.VISIBLE );
                    iv_barcodeSearch_round.setVisibility( View.GONE );
                    rl_selfSearch.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    iv_backToMain.setImageResource( R.drawable.baseline_arrow_circle_right_oreng );

                } else {
                    searchview.setBackgroundResource( R.drawable.sty_3 );
                    rl_mainInformation.setVisibility(View.GONE);
                    iv_selfAdd.setVisibility(View.GONE);
                    iv_selfSearch_round.setVisibility( View.GONE );
                    iv_barcodeSearch_round.setVisibility( View.VISIBLE );
                    rl_selfSearch.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    iv_backToMain.setImageResource( R.drawable.ic_baseline_arrow_circle_right_blue );
                }

                return false;
            }
        } );
        serchview_internet.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                startinternetWebSearch();
                return  false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //      if (!serchview_internet.getQuery().toString().matches( "")||!serchview_internet.getQuery().toString().matches( " קלוריות")||serchview_internet.getQuery().toString().matches( "קלוריות") )
                //   serchview_internet.setQuery( serchview_internet.getQuery().toString()+" קלוריות" );
                return false;
            }
        });
        //סגירת מסכים לא נחוצים בכניסה התחלתית למסך
        webview.setVisibility(View.GONE);
        ly_addNewPrivetFood.setVisibility(View.GONE);
        ly_addFood.setVisibility(View.GONE);
        iv_backToMain.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        rl_selfSearch.setVisibility(View.GONE);
        ly_aditAmount.setVisibility(View.GONE);
        rl_mainInformation.setVisibility(View.VISIBLE);
        //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //  String strDate = sdf.format(c.getTime());

        //    sp = getSharedPreferences( "MyUserPref" , Context.MODE_PRIVATE );//צור שמירת נתונים פרטית
        //אם הקוד כבר נשמר במכשיר תעדכן את טקסט הקוד במסך
        //   if (!sp.getString( "caloria" , "" ).equals( "" )) {
        //    str_caloria = sp.getString( "caloria" , "" );
        //      text.setText( str_caloria );
        //  }
        et_amounttt.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers
        et_kal.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers
        et_newAmount.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean isSelfSearch = extras.getBoolean( "selfSearch" );
            if (isSelfSearch) {
                startWebSearch();
                rl_mainInformation.setVisibility(View.GONE);
                closeMain();
            }
        }
    }


    @Override
    public void onClick(View view) {
        if (view == iv_expend_more) {
            if (tv_qr_information.getVisibility() == View.GONE )
            tv_qr_information.setVisibility(  View.VISIBLE );
            else
                tv_qr_information.setVisibility(  View.GONE );
        }

        if(view==moreSerch){
            startinternetWebSearchDotan(moreSerch.getText().toString());
            moreSerch.setVisibility( View.GONE );
            hideKeyboard();
            ly_selfSearch_bar.setVisibility( View.GONE );
            iv_showSelfSearchBar.setVisibility( View.VISIBLE );
        }

        if(view==iv_myProdacts || view == iv_myProdacts_SS){
            startNewActivity(MainActivity.this, MyProductActivity.class);

        }
        if (view== iv_d_code_scan){
            scanCode();
        }
        if (view==iv_barcode){
            showCustomDialog();
        }
        if (view==iv_selfAdd) {
          selfAddActions();
        }
        if(view== iv_barcodeSearch_round){
            openMain();
            scanCode();}
        if(view==btn_nextDay){
            //יקרה רק אם כל שאר המסכים סגורים
            if (ly_aditAmount.getVisibility()==View.GONE&&ly_addFood.getVisibility()==View.GONE&&ly_addNewPrivetFood.getVisibility()==View.GONE&&rl_selfSearch.getVisibility()==View.GONE){
                cal.add(Calendar.DAY_OF_MONTH, 1); //Adds a day
                tv_date.setText( new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
                updateKlist();
            }
        }
        if(view==btn_lastDay){
            //יקרה רק אם כל שאר המסכים סגורים
            if (ly_aditAmount.getVisibility()==View.GONE&&ly_addFood.getVisibility()==View.GONE&&ly_addNewPrivetFood.getVisibility()==View.GONE&&rl_selfSearch.getVisibility()==View.GONE) {
                cal.add( Calendar.DAY_OF_MONTH , -1 ); //Goes to previous day
                tv_date.setText( new SimpleDateFormat( "dd-MM-yyyy" ).format( cal.getTime() ) );
                updateKlist();
            }
        }
        if(view == iv_delete1){cancelFoodAdd();
        }
        //  if(view == iv_delete2 || view == iv_back_SS_to_M){
        if( view == iv_back_SS_to_M){

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
            ly_selfSearch_bar.setVisibility( View.VISIBLE );
            iv_showSelfSearchBar.setVisibility( View.GONE );
        }
        if (view == iv_hideSelfSearchBar){
            hideKeyboard();
            ly_selfSearch_bar.setVisibility( View.GONE );
            iv_showSelfSearchBar.setVisibility( View.VISIBLE );

        }

        if (view == btn_addFood) {
            if (!isContainsLetters( et_amounttt.getText().toString() )) {
                if (!et_amounttt.getText().toString().equals( "" ) && !et_amounttt.getText().toString().equals( "0" ) && !(Double.parseDouble( et_amounttt.getText().toString() ) < 0)) { //אם כמות לא כלום או 0
                    //פעולה לחישוב קלוריות והוספה לכמות כוללת
                    double kal = Double.parseDouble( tv_kal.getText().toString() );
                    double amount = Double.parseDouble( et_amounttt.getText().toString() );
                    //     double temp=Double.parseDouble( str_caloria );
                    String str_caloria;
                    if (calcolaty_mod == 2) {
                        str_caloria = (String.format( String.valueOf( (int) ((kal / 100) * amount  /* +temp*/) ) ));
                    } else {
                        str_caloria = (String.format( String.valueOf( (int) (kal * amount /* +temp*/) ) ));
                    }
                    addToList( amount , Integer.parseInt( str_caloria ) );
                    update_kaloriesSum_k();
                    searchview.setVisibility( View.VISIBLE );
                    searchview.setQuery( "" , true );
                    searchview.setIconified( true );
                    hideKeyboard();
                    cancelFoodAdd();
                    rl_mainInformation.setVisibility( View.VISIBLE );
                    webview.setVisibility( View.GONE );
                    ly_addNewPrivetFood.setVisibility( View.GONE );
                    ly_addFood.setVisibility( View.GONE );
                    iv_backToMain.setVisibility( View.GONE );
                    mRecyclerView.setVisibility( View.GONE );
                    rl_selfSearch.setVisibility( View.GONE );
                    if (myList.size() != 0) {
                        myListRecyclerView.smoothScrollToPosition( myList.size() - 1 );
                    }
                }
            }
        }
        if (view== btn_addNeePrivetFood || view==iv_anther){
            foodName_str = et_food.getText().toString();
            foodKaloria_str = et_kal.getText().toString();
            if (!foodName_str.isEmpty() && !foodKaloria_str.isEmpty()&&!isContainsLetters( foodKaloria_str )) {
                if ((llll_spinnerEdit.getVisibility()==View.VISIBLE&& !et_spinnerEditT.getText().toString().matches( "" ))||spinner.getVisibility()==View.VISIBLE){
                    addToFoodList();
                    saveData();
                    et_food.setBackgroundResource( R.drawable.sty_2 );
                    et_kal.setBackgroundResource( R.drawable.sty_2 );
                    updateMainList();

                    Toast toast = null;
                    if (spinner.getVisibility()==View.VISIBLE)
                        toast =   Toast.makeText(getBaseContext(), ""+"\"" +et_food.getText().toString()+"\"" +" "+spinner.getSelectedItem()+" נוסף למערכת!", Toast.LENGTH_SHORT);
                    if (llll_spinnerEdit.getVisibility()==View.VISIBLE)
                        toast =   Toast.makeText(getBaseContext(), ""+"\"" +et_food.getText().toString()+"\"" +" "+et_spinnerEditT.getText().toString()+" נוסף למערכת!", Toast.LENGTH_SHORT);

                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    //        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.START, 0, 0);
                    toast.show();

                    if (view==btn_addNeePrivetFood){
                        hideKeyboard();
                        cancelFoodAdd();
                        searchview.setVisibility( View.VISIBLE );
                        searchview.setIconified(true);
                        showFoodDitals(getLastItem());
                        openMain();
                    }
                    et_kal.setText( "" );

                }} else {
                if (foodName_str.isEmpty()) {
                    et_food.setBackgroundResource( R.drawable.sty_red );
                }
                if (foodKaloria_str.isEmpty()) {
                    et_kal.setBackgroundResource( R.drawable.sty_red );
                }
            }
        }
        if (view == searchview){
            //  mRecyclerView.setVisibility(View.VISIBLE);
            ///    et_food.setText( "" );
            //   rl_mainInformation.setVisibility(View.GONE);
            searchview.setIconified(false);
        }
        if (view == serchview_internet){
            serchview_internet.setIconified(false);
        }
        if (view==iv_search){
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
        if (view ==t_sdfdfss){
            clearData();
            Toast.makeText( getBaseContext(), "פריטים שמורים (רשימת חיפוש מאכלים) נמחקו",Toast.LENGTH_SHORT).show();
            restartApp();
        }
        if (view ==textttttdtghfd){
            //   str_caloria =("0");
            //  SharedPreferences.Editor editor = sp.edit();
            //         editor.putString( "caloria" , str_caloria );
            //     editor.commit();
            //   text.setText( str_caloria );
            text.setText("0");
            clearData_K();
            Toast.makeText( getBaseContext(), "רשימת קלוריות שנצרכו נמחקה (מסך ראשי)",Toast.LENGTH_SHORT).show();
            restartApp();
        }
        if(view==ffffffdtghfd){
            String detailsString="_myPrivetFoodlList_";
            detailsString=detailsString+" "+myPrivetFoodlList.size()+" items ";
            for(int i=0; i <myPrivetFoodlList.size();i++){
                String b= myPrivetFoodlList.get( i ).getBarcode();
                if (b== null || b=="0" || b=="" ){b="";}
                String itemT= "exampleList.add(new ExampleItem("+0+","+0+","+"\""+ myPrivetFoodlList.get( i ).getName()+"\"" +","+"\""+ myPrivetFoodlList.get( i ).getUnit()+"\""+" ,"+"\""+ myPrivetFoodlList.get( i ).getCalorieText()+"\""+","+0+"," +"\""+ b+"\"" +"));";
                detailsString=detailsString.toString()+ "\n"+itemT;
            }
            clipData(detailsString , this);
            Toast.makeText( getBaseContext(), "Copied successfully"+myPrivetFoodlList.size()+" items ",Toast.LENGTH_SHORT).show();
        }
        if (view==t_see){
            ly_settings.setVisibility( View.GONE );
        }
        if (view==iv_settings) {
            ly_settings.setVisibility( View.VISIBLE );
        }
        if (view==iv_returnToSpinner){
            spinner.setVisibility( View.VISIBLE );
            spinner.setSelection( 6 );
            llll_spinnerEdit.setVisibility( View.GONE );
        }
        if (view==plus_z){
            String st=et_amounttt.getText().toString();
            if (!st.matches( "" )){
                if (calculationMod(tv_Type.getText().toString())==1){
                    if (Double.parseDouble(et_amounttt.getText().toString())>=1){   st= ""+(Double.parseDouble(et_amounttt.getText().toString())+1) +"";}
                    if (Double.parseDouble(et_amounttt.getText().toString())==0.5){ st="1";}
                    if (Double.parseDouble(et_amounttt.getText().toString())==0.25){ st="0.5";}
                    if (Double.parseDouble(et_amounttt.getText().toString())==0){st="0.25";}
                }
                if (calculationMod(tv_Type.getText().toString())==2&&Double.parseDouble(et_amounttt.getText().toString())>=0){st=""+(Double.parseDouble(et_amounttt.getText().toString())+50)+"";}
            }else {st="0";}
            et_amounttt.setText(st);

        }
        if (view==minus_z){
            String st=et_amounttt.getText().toString();
            if (!st.matches( "" )){
                if (calculationMod(tv_Type.getText().toString())==1 ){
                    if (Double.parseDouble(et_amounttt.getText().toString())-1>=1){
                        st= ""+(Double.parseDouble(et_amounttt.getText().toString())-1) +""; }
                    if (Double.parseDouble(et_amounttt.getText().toString())==1){ st="0.5";}
                    if (Double.parseDouble(et_amounttt.getText().toString())==0.5){ st="0.25";}
                    if (Double.parseDouble(et_amounttt.getText().toString())==0.25){st="0";}
                }
                if (calculationMod(tv_Type.getText().toString())==2&&Double.parseDouble(et_amounttt.getText().toString())-50>=0){st=""+(Double.parseDouble(et_amounttt.getText().toString())-50)+"";}
            }else {st="0";}
            et_amounttt.setText(st);
        }
        if (view==more_z){}
        if (view==iv_deleteAdit){
            cancelAdit();
        }
        if (view==btn_aditdFood){
            if (!et_newAmount.getText().toString().matches( "" )){
                aditItemFromCalList(mainL_position, Double.parseDouble( et_newAmount.getText().toString() ) );
                hideKeyboard();
                ly_aditAmount.setVisibility( View.GONE );
               }
        }
        if (view==plus_adit){
            String st=et_newAmount.getText().toString();
            if (!st.matches( "" )){
                if (calculationMod(consumedItem_adit.getProductItem().getUnit())==1){
                    if (Double.parseDouble(et_newAmount.getText().toString())>=1){   st= ""+(Double.parseDouble(et_newAmount.getText().toString())+1) +"";}
                    if (Double.parseDouble(et_newAmount.getText().toString())==0.5){ st="1";}
                    if (Double.parseDouble(et_newAmount.getText().toString())==0.25){ st="0.5";}
                    if (Double.parseDouble(et_newAmount.getText().toString())==0){st="0.25";}
                }
                if (calculationMod(consumedItem_adit.getProductItem().getUnit())==2&&Double.parseDouble(et_newAmount.getText().toString())>=0){st=""+(Double.parseDouble(et_newAmount.getText().toString())+50)+"";}
            }else {st="0";}
            et_newAmount.setText(st);
        }
        if (view==minus_adit){
            String st=et_newAmount.getText().toString();
            if (!st.matches( "" )){
                if (calculationMod(consumedItem_adit.getProductItem().getUnit())==1 ){
                    if (Double.parseDouble(et_newAmount.getText().toString())-1>=1){
                        st= ""+(Double.parseDouble(et_newAmount.getText().toString())-1) +""; }
                    if (Double.parseDouble(et_newAmount.getText().toString())==1){ st="0.5";}
                    if (Double.parseDouble(et_newAmount.getText().toString())==0.5){ st="0.25";}
                    if (Double.parseDouble(et_newAmount.getText().toString())==0.25){st="0";}
                }
                if (calculationMod(consumedItem_adit.getProductItem().getUnit())==2&&Double.parseDouble(et_newAmount.getText().toString())-50>=0){
                    st=""+(Double.parseDouble(et_newAmount.getText().toString())-50)+"";}
            }else {st="0";}
            et_newAmount.setText(st);
        }
        if (view==tv_food){
            //העתק טקסט
            clipData(tv_food.getText().toString() , this);
            Toast.makeText( getBaseContext(), "הועתק שם מוצר",Toast.LENGTH_SHORT).show();
        }
        if (view==et_kal||view==et_amounttt||view==et_spinnerEditT||view==et_newAmount){
            EditText  et_temp= (EditText) view;
            et_temp.setText("");
        }
        /*    if (view ==iv_plus){
            isPluse=!isPluse;
            if (isPluse==true){

            serchview.setVisibility(View.GONE);
            et_food.setVisibility(View.VISIBLE);
            et_kal.setVisibility(View.VISIBLE);
            btn_plus.setVisibility(View.VISIBLE);
            iv_plus.setImageResource( R.drawable.orengdelite );
            if (serchview.getQuery().toString()!= null){
            et_food.setText( serchview.getQuery().toString());
            }
            }
            else {  serchview.setVisibility(View.VISIBLE);
                et_food.setVisibility(View.GONE);
                et_kal.setVisibility(View.GONE);
                btn_plus.setVisibility(View.GONE);
                iv_plus.setImageResource( R.drawable.orengplus_icon );}
        }



     */
    }

    private void selfAddActions() {

        String str_caloria= searchview.getQuery().toString().trim();
        temp_exampleItem= new ProductItem(0,0,"הוספת עצמית","קלוריות" ,"0",0,"");
        temp_exampleItem.setCalorieText("100");
      addToList( Integer.parseInt( str_caloria ) , Integer.parseInt( str_caloria ) );
        update_kaloriesSum_k();
        searchview.setVisibility( View.VISIBLE );
        searchview.setQuery( "" , true );
        searchview.setIconified( true );
        hideKeyboard();
        cancelFoodAdd();
        rl_mainInformation.setVisibility( View.VISIBLE );
        webview.setVisibility( View.GONE );
        ly_addNewPrivetFood.setVisibility( View.GONE );
        ly_addFood.setVisibility( View.GONE );
        iv_backToMain.setVisibility( View.GONE );
        mRecyclerView.setVisibility( View.GONE );
        rl_selfSearch.setVisibility( View.GONE );
        if (myList.size() != 0) {
            myListRecyclerView.smoothScrollToPosition( myList.size() - 1 );
        }
        searchview.setBackgroundResource( R.drawable.sty_3 );
        iv_selfAdd.setVisibility(View.GONE);
        iv_barcodeSearch_round.setVisibility( View.VISIBLE );

    }

    //Function to display the custom dialog.
    public void showCustomDialog() {
        /*
        Dialog dialog = new Dialog( MainActivity.this );
        //We have added a title in the custom layout. So let's disable the default title.
        //   dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable( true ); dialog.setContentView( R.layout.custom_dialog );
        //Mention the name of the layout of your custom dialog.


        //Initializing the views of the dialog.
        et_d= dialog.findViewById( R.id.et_d );
        iv_scan=dialog.findViewById( R.id.iv_scan);
        iv_scan.setOnClickListener( this );
        btn_bsave=dialog.findViewById(R.id.btn_bsave);
        btn_bsave.setOnClickListener( this );
       // et_d.setText( temp_exampleItem.getmBarcode() );

         */
        QrCode_dialog.show();
    }
    public void closeCustomDialog() {
        QrCode_dialog.dismiss();
    }

    //פעולה שתציג דיאלוג ספציפי מתחתית המסך
    private void showDialog() {
        dialog.show();
        dialog.getWindow().setLayout(  ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity( Gravity.BOTTOM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result =IntentIntegrator.parseActivityResult( requestCode,resultCode,data );
        if(result != null){
            if (result.getContents() != null){
                //     searchview.setQuery( ""+result.getContents() ,false);
                if (QrCode_dialog.isShowing()){  serchByBC(""+result.getContents(),1);}
                else{
                    serchByBC(""+result.getContents(),0);}
                /*
                AlertDialog.Builder builder =new AlertDialog.Builder(this);
                builder.setMessage( result.getContents() );
                builder.setTitle( "תוצאות סריקה" );
                builder.setPositiveButton( "סרוק שוב" , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogI , int which) {
scanCode();
                    }
                } ).setNegativeButton( "סיום" , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog , int which) {
finish();
                    }
                } );
                AlertDialog dialog =builder.create();
                dialog.show();
                */
            }
            else{
                Toast.makeText( this, "אין תוצאה",Toast.LENGTH_SHORT ).show();
            }


        }else{
            //אם אין תוצאה בינתיים
            super.onActivityResult( requestCode, resultCode, data );}
    }
    private void scanCode(){
        IntentIntegrator integrator=new IntentIntegrator( this );
        integrator.setCaptureActivity( CaptureAct.class );
        integrator.setOrientationLocked( false );
        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
        integrator.setPrompt("לחץ על מקשי צליל להפעלת פנס");
        integrator.initiateScan();
    }
    private void serchByBC(String barcode , int mode) {
        if(mode==0) {
            ProductItem examplel = new ProductItem();
            boolean temp = false;
            int j = 0;
            while (temp == false && j < exampleList.size()) {
                examplel = exampleList.get( j );
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
            if (temp == true) { //נמצאה התאמה
                hideKeyboard();
                showFoodDitals( examplel );
                if (filteredExampleList.size() == 2) {
                    openMain();
                }
            } else {//לא נמצא
                //חפש באינטרנט
                openNewProdact();
                temp_exampleItem = new ProductItem( 0 , 0 , "" , "" , "" , 0 , barcode.trim() );
                startWebSearchForBarcode( barcode.trim() );
            }
        }
        if(mode==1) {
            String temp= et_d_enter_code.getText().toString();
            if (temp.isEmpty()){
            et_d_enter_code.setText( ""+barcode );
            } else {
                et_d_enter_code.setText( temp + " , " +barcode  );
            }
            moreSerch.setVisibility( View.VISIBLE );
            moreSerch.setText( barcode );
        }
    }

    //פעולות עדכון רשימות בסיס
    private void apdateFoodList()   {
        exampleList = new ArrayList<>();
        exampleList=getSystemProductsArr();

        // המצב הראשוני של רשימת החיפוש כרשימת המוצרים (ברירת מחדל)
        filteredExampleList=exampleList;
        //הגדרת המספר שמייצג את סוג המדד והתמונה המתאימה ,לפי סוג המדד השמר(כי אין לי כוח לשנות בעצמי)
        for (int i=0; i<exampleList.size(); i++){
            for (int j=0;j<spinner.getAdapter().getCount();j++){
                if (exampleList.get( i ).getUnit().equals( spinner.getItemAtPosition( j ).toString() )){
                    exampleList.get( i ).setUnitTypeValue( j );
                    exampleList.get( i ).setUnitImageResId( typeList.get( j ));
                }
            }
        }
//עדכון הרשימה הפיזית במסך כרשימת המוצרים
        //    mRecyclerView.setHasFixedSize(true);
        // mRecyclerView.setFitsSystemWindows( false );
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ProductItemAdapter(exampleList);
        mRecyclerView.setAdapter(mAdapter);
    }
    private void makeSpinner() {

        categories = new ArrayList<String>();
        categories.add("100 גרם"); typeList.add( R.drawable.t_grame );
        categories.add("100 מל"); typeList.add( R.drawable.t_grame );
        categories.add("כף");         typeList.add( R.drawable.t_tablespoon );
        categories.add("כפית");      typeList.add( R.drawable.t_teaspoon );
        categories.add("כוס");        typeList.add( R.drawable.t_glass );
        categories.add("פרוסה");    typeList.add( R.drawable.t_slice );
        categories.add("יחידה");     typeList.add( R.drawable.single );
        categories.add("אחר");     typeList.add( R.drawable.single );
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,   R.layout.spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView , View view , int position , long l) {
                String str;
                str= categories.get(position);
                if (position==7){
                    spinner.setVisibility( View.GONE );
                    llll_spinnerEdit.setVisibility( View.VISIBLE );
                    //      et_spinnerEditT.setFocusable( 0 );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        } );
    }
    private void addPrivetFoodListToFoodList() {
        if (myPrivetFoodlList == null) {
            myPrivetFoodlList = new ArrayList<>();
        }else {
            for (int i = 0; i < myPrivetFoodlList.size();i++){
                //הוספת איבי רשימה שלי לרשימה ראשית
                exampleList.add( myPrivetFoodlList.get( i ) );
            }
            mRecyclerView.setAdapter(new ProductItemAdapter(exampleList));
            // המצב הראשוני של רשימת החיפוש כרשימת המוצרים (ברירת מחדל)
            filteredExampleList=exampleList;
        }
    }
    //פעולות מערכת
    private void searchInFoodList(String s) {
        //פעולת חיפוש ועדכון רשימת מזון
        filteredExampleList = new ArrayList<>();
        //חפש לפי שייכים לי
        for (int j = 0; j <exampleList.size(); j++){
            ProductItem example2 = exampleList.get(j);
            if (example2.getName().toLowerCase().trim().contains( s.toLowerCase().trim() )&&example2.getItemState()==1 ){
                filteredExampleList.add(example2);}
        }
        //חפש ברשימה כללית
        for (int i = 0; i <exampleList.size(); i++){    //הכנס את מי שמתחילים בטקסט שהוקלד
            ProductItem example = exampleList.get(i);
            if(example.getName().toLowerCase().trim().startsWith( s.toLowerCase().trim() )&&example.getItemState()==0){
                filteredExampleList.add(example);}
        }
        for (int i = 0; i <exampleList.size(); i++){  //ורק אז הכנס את מי שנשאר ומכיל את הטקסט שהוקלד
            ProductItem example = exampleList.get(i);
            if (example.getName().toLowerCase().trim().contains( s.toLowerCase().trim() )&&!example.getName().toLowerCase().trim().startsWith( s.toLowerCase().trim() ) &&example.getItemState()==0){
                filteredExampleList.add(example);}
            //    sortByAB(  filteredExampleList );
        }
        //הצג הודעה במקרה של חוסר תוצאות
        if (filteredExampleList.size()>0){ filteredExampleList.add(new ProductItem(0, 999, "לא מה שחיפשת?", "המשך בחיפוש עצמי","\uD83D\uDD0D",0,null));}
        //עדכן רשימה
        RecyclerView.Adapter  newAdapter = new ProductItemAdapter(filteredExampleList);
        mRecyclerView.setAdapter(newAdapter);
    }

    private void sortByAB(ArrayList<ProductItem> mExampleList) {
        //מיון לפי אב
        Collections.sort(mExampleList, new Comparator<ProductItem>() {
            @Override
            public int compare(ProductItem o1, ProductItem o2) {
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

    private void showFoodDitals(ProductItem exampleItem) {
        ly_addFood.startAnimation( slide_in_bottom );
        temp_exampleItem=exampleItem ;

        ly_addNewPrivetFood.setVisibility( View.GONE );
        ly_addFood.setVisibility( View.VISIBLE );
        searchview.setVisibility( View.GONE );
        tv_food.setText( exampleItem.getName() + "" );
        tv_kal.setText( exampleItem.getCalorieText() + "" );
        tv_Type.setText(  exampleItem.getUnit());

        tv_qr_information.setVisibility(  View.GONE );
        String s = exampleItem.getBarcode();
        if (s != null && s.isEmpty()){
            s= "?";
        }
        tv_qr_information.setText( "ברקוד מוצר: " + s );

        //האדיט טקסט של כמות יעודכן ל1 אם מדובר בכמות וכו ורק אם מדובר ב100 גרם או מל אז יעודכן ל100
        if ( exampleItem.getUnit().equals( "100 גרם" ) ||exampleItem.getUnit().equals( "100 מל" )  ){
            et_amounttt.setText( "100" );
            calcolaty_mod=2;
        }
        else{
            et_amounttt.setText("1");
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
        Collections.sort(exampleList, new Comparator<ProductItem>() {
            @Override
            public int compare(ProductItem o1, ProductItem o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        mAdapter = new ProductItemAdapter(exampleList);
        mAdapter.notifyDataSetChanged();
    }
    //פעולות שרדפרפרנס רשימת מזון פרטית
    private void addToFoodList(){
        //הוסף מזון לרשימת מוצרים שלי (רק אם אני בחיפוש עצמי או עורך מוצר קיים)
        ProductItem item = null;
        if (spinner.getVisibility()==View.VISIBLE) {
            item = new ProductItem( spinner.getSelectedItemPosition() , 1 , et_food.getText().toString().trim() , spinner.getSelectedItem().toString() , et_kal.getText().toString().trim() , 0, et_d_enter_code.getText().toString() );
            myPrivetFoodlList.add(item);
            temp_exampleItem=item;
        }
        if (llll_spinnerEdit.getVisibility()==View.VISIBLE){
            item = new ProductItem( 0 , 1 , et_food.getText().toString().trim() , et_spinnerEditT.getText().toString().trim() , et_kal.getText().toString().trim() , 0, et_d_enter_code.getText().toString() );
            myPrivetFoodlList.add(item);
            temp_exampleItem=item;
        }
        ProductItem exampleItem;
        //exampleItem.getClass()
        //ככה נבדוק תקינות לפני שסורקים את הרשימה של המוצרים שלי. כאשר אנחנו פותחים את האפליקציה יש את הפעולה מקבלת את רשימת המוצרים הפרטית שלי מהשרד פרפרנס. אם במקרה הוספתי תכונה ללמחלקת מוצר, ואני נכנס לאפליקציה לאחר העדכון. אז בזמן שמוסיפים לרשימה הראשית את הרשימה השמורה בעצם מכניסים איברים ממחלקה ישנה לרשימה ממחלקה עדכנית
        //לכן אנחנו נבדוק אם הקלאסים שלהם שווים ואם לא אז ניצור אותם מחדש
    }
    private void loadPrivetFoodListData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<ProductItem>>() {}.getType();
        myPrivetFoodlList = gson.fromJson(json, type);
        if (myPrivetFoodlList == null) {
            myPrivetFoodlList = new ArrayList<>();
        }else {
            for (int i = 0; i < myPrivetFoodlList.size(); i++) {
                String unit = myPrivetFoodlList.get(i).getUnit();

                for (int j = 0; j < spinner.getAdapter().getCount(); j++) {
                    Object spinnerItem = spinner.getItemAtPosition(j);

                    if (unit != null && spinnerItem != null &&
                            unit.trim().equalsIgnoreCase(spinnerItem.toString().trim())) {

                        myPrivetFoodlList.get(i).setUnitImageResId(typeList.get(j));
                        break; // אם מצאת התאמה - אפשר לצאת מהלולאה הפנימית
                    }
                }
            }

        }}
    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myPrivetFoodlList);
        editor.putString("task list", json);
        editor.apply();
    }
    private void clearData(){
        if (myPrivetFoodlList == null) {
            myPrivetFoodlList = new ArrayList<>();
        }else {
            SharedPreferences sharedPreferences = getSharedPreferences( "shared preferences" , MODE_PRIVATE );
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson( null );

            editor.putString( "task list" , json );
            editor.apply();
            myPrivetFoodlList = new ArrayList<>();
            //mRecyclerView.setAdapter( new ExampleAdapter( exampleList ) );
        }
    }
    private ProductItem getLastItem(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<ProductItem>>() {}.getType();
        ArrayList<ProductItem> arrayList = new ArrayList<>();
        arrayList = gson.fromJson(json, type);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }else {
            ProductItem exampleItem=new ProductItem();
            exampleItem=   arrayList.get( arrayList.size()-1 );
            return exampleItem;
        }
        return null;
    }

    // פתיחת וסגירת מסכים
    private void openMain(){

        rl_mainInformation.setVisibility(View.VISIBLE);
        rl_top.setVisibility( View.VISIBLE );
        rl_top_ss.setVisibility( View.GONE );
        changeBarColor(rl_top);

        iv_backToMain.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

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
        rl_top_ss.setVisibility( View.GONE );
        changeBarColor(rl_top);
        iv_backToMain.setVisibility(View.VISIBLE);

        mRecyclerView.setVisibility(View.VISIBLE);

        webview.setVisibility(View.GONE);

        rl_selfSearch.setVisibility(View.GONE);

        ly_addNewPrivetFood.setVisibility(View.GONE);
        ly_aditAmount.setVisibility(View.GONE);
        ly_addFood.setVisibility(View.GONE);
    }
    private void closeFood(){
        iv_backToMain.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }
    private void openNewProdact(){

        ly_addNewPrivetFood.startAnimation( slide_in_bottom );
        rl_mainInformation.setVisibility(View.GONE);
        rl_top.setVisibility( View.GONE );
        rl_top_ss.setVisibility( View.VISIBLE );
        changeBarColor(rl_top_ss);
        //   showDialog();

        iv_backToMain.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

        webview.setVisibility(View.VISIBLE);
        ly_addNewPrivetFood.setVisibility(View.VISIBLE);

        rl_selfSearch.setVisibility(View.GONE);

        ly_aditAmount.setVisibility(View.GONE);
        ly_addFood.setVisibility(View.GONE);

    }
    private void closeNewProdact(){
        webview.setVisibility(View.VISIBLE);
        ly_addNewPrivetFood.setVisibility(View.VISIBLE);
    }
    //פעולות קטנות
    private void findViewAndMore() {
        dialog= new Dialog(this);
        dialog.setContentView(R.layout.new_privet_prodact_sheet);

        tv_qr_information=findViewById( R.id.tv_qr_information );

        iv_expend_more = findViewById( R.id.iv_expend_more );
        iv_expend_more.setOnClickListener( this );

        rl_top=findViewById(R.id.rl_top);
        rl_top_ss=findViewById(R.id.rl_top_ss);
        iv_myProdacts_SS=findViewById(R.id.iv_myProdacts_SS);
        iv_myProdacts_SS.setOnClickListener( this );
        iv_back_SS_to_M=findViewById(R.id.iv_back_SS_to_M);
        iv_back_SS_to_M.setOnClickListener( this );


        moreSerch=findViewById( R.id.moreSerch);
        moreSerch.setOnClickListener( this );
        slide_in_bottom= AnimationUtils.loadAnimation( this,R.anim.slide_in_bottom );
        slide_out_bottom= AnimationUtils.loadAnimation( this,R.anim.slide_out_bottom );
        QrCode_dialog = new Dialog( MainActivity.this );

        QrCode_dialog.setContentView( R.layout.scan_barcod_dialog );
        QrCode_dialog.setCancelable( true );
        et_d_enter_code = QrCode_dialog.findViewById( R.id.et_d_enter_code );
        iv_d_code_scan = QrCode_dialog.findViewById( R.id.iv_d_code_scan );
        iv_d_code_scan.setOnClickListener( this );

        iv_myProdacts=findViewById( R.id.iv_myProdacts );
        iv_myProdacts.setOnClickListener( this );

        myListRecyclerView=findViewById( R.id.myListRecyclerView );
        myList_LayoutManager = new LinearLayoutManager(this);
        myListRecyclerView.setLayoutManager(myList_LayoutManager);
        plus_adit=findViewById( R.id.plus_adit );
        plus_adit.setOnClickListener( this );
        plus_adit.getViewTreeObserver().addOnGlobalLayoutListener(this);
        minus_adit=findViewById( R.id.minus_adit );
        minus_adit.setOnClickListener( this );
        minus_adit.getViewTreeObserver().addOnGlobalLayoutListener(this);
        btn_aditdFood=findViewById( R.id.btn_aditdFood );
        btn_aditdFood.setOnClickListener( this );

        iv_barcodeSearch_round =findViewById( R.id.iv_barcodeSearch_round );
        iv_barcodeSearch_round.setOnClickListener( this );

        ly_selfSearch_bar=findViewById( R.id.ly_selfSearch_bar );
        iv_showSelfSearchBar =findViewById( R.id.iv_showSelfSearchBar );
        iv_showSelfSearchBar.setOnClickListener( this );
        iv_hideSelfSearchBar =findViewById( R.id.iv_hideSelfSearchBar );
        iv_hideSelfSearchBar.setOnClickListener( this );
        iv_selfSearch_round =findViewById( R.id.iv_selfSearch_round );
        iv_selfSearch_round.setOnClickListener( this );
        iv_selfAdd =findViewById( R.id.iv_selfAdd_round );
        iv_selfAdd.setOnClickListener( this );
        iv_barcode =findViewById( R.id.iv_barcode );
        iv_barcode.setOnClickListener( this );
        iv_deleteAdit=findViewById( R.id.iv_deleteAdit );
        iv_deleteAdit.setOnClickListener( this );
        et_newAmount=findViewById( R.id.et_newAmount );
        tv_foodname=findViewById( R.id.tv_foodname );
        ly_aditAmount=findViewById( R.id.ly_aditAmount );
        minus_z=findViewById( R.id.minus_z );
        minus_z.setOnClickListener( this );
        minus_z.getViewTreeObserver().addOnGlobalLayoutListener(this);
        plus_z=findViewById( R.id.plus_z );
        plus_z.setOnClickListener( this );
        plus_z.getViewTreeObserver().addOnGlobalLayoutListener(this);
        llll_spinnerEdit=findViewById( R.id.layoutSpinnerEdit);
        iv_returnToSpinner=findViewById( R.id.iv_returnToSpinner );
        iv_returnToSpinner.setOnClickListener( this );
        et_spinnerEditT=findViewById( R.id.et_spinnerEditT );
        iv_anther=findViewById( R.id.iv_anther);
        iv_anther.setOnClickListener( this );
        t_sdfdfss=findViewById( R.id.t_sdfdfss);
        t_sdfdfss.setOnClickListener( this );
        t_see=findViewById( R.id.t_see);
        t_see.setOnClickListener( this );
        iv_settings=findViewById( R.id.iv_settings);
        iv_settings.setOnClickListener( this );
        ly_settings=findViewById( R.id.ly_settings);

        iv_delete1=findViewById( R.id.iv_delete1);
        iv_delete1.setOnClickListener( this );

        tv_date=findViewById( R.id.tv_date);
        btn_nextDay=findViewById( R.id.btn_nextDay);
        btn_nextDay.setOnClickListener( this );
        btn_nextDay.getViewTreeObserver().addOnGlobalLayoutListener(this);
        btn_lastDay=findViewById( R.id.btn_lastDay);
        btn_lastDay.setOnClickListener( this );
        btn_lastDay.getViewTreeObserver().addOnGlobalLayoutListener(this);
        ffffffdtghfd=findViewById( R.id.ffffffdtghfd);
        ffffffdtghfd.setOnClickListener( this );
        rl_mainInformation=findViewById( R.id.rl_mainInformation );
        mRecyclerView = findViewById(R.id.recyclerView);
        textttttdtghfd=findViewById( R.id.textttttdtghfd );
        textttttdtghfd.setOnClickListener( this );
        et_amounttt=findViewById( R.id.et_amounttt );
        et_amounttt.getViewTreeObserver().addOnGlobalLayoutListener(this);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        spinner.getViewTreeObserver().addOnGlobalLayoutListener(this);
        iv_backToMain =findViewById( R.id.iv_backToMain );
        iv_backToMain.setOnClickListener( this );
        rl_selfSearch=findViewById(R.id.rl_selfSearch);
        iv_search=findViewById( R.id.iv_search );
        iv_search.setOnClickListener( this );
        et_food= findViewById( R.id.et_food );
        et_food.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                et_food.setBackgroundResource( R.drawable.sty_2 );

            }
            @Override
            public void afterTextChanged(Editable editable) {
                moreSerch.setText( et_food.getText().toString() + "" );
                if(!et_food.getText().toString().equals( "" )){
                    moreSerch.setVisibility( View.VISIBLE );
                    moreSerch.setText( et_food.getText().toString() + " " + "קלוריות" );
                }else{
                    moreSerch.setVisibility( View.GONE );
                }
                /* if (webview.getUrl().toString().equals( "https://www.google.com/search?q=" + et_food.getText().toString() +" "+ "קלוריות")){
                    moreSerch.setVisibility( View.GONE );
                }

                */

            }
        });
        et_kal= findViewById( R.id.et_kal );
        et_kal.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                et_kal.setBackgroundResource( R.drawable.sty_2 );
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }

        });
        tv_food = findViewById( R.id.tv_food );
        tv_food.setMovementMethod( new ScrollingMovementMethod() );
        tv_food.setOnClickListener( this );
        tv_kal= findViewById( R.id.tv_kal );
        tv_Type=findViewById( R.id.tv_Type);
        tv_Type.getViewTreeObserver().addOnGlobalLayoutListener(this);
        btn_addNeePrivetFood =findViewById( R.id.btn_addNewPrivetFood );
        btn_addNeePrivetFood.setOnClickListener( this );
        btn_addFood =findViewById( R.id.btn_addFood );
        btn_addFood.setOnClickListener( this );
        ly_addNewPrivetFood =findViewById( R.id.ly_addNewPrivetFood );
        ly_addFood=findViewById( R.id.ly_addFood );
        text=findViewById( R.id.text );  text.setOnClickListener( this );
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
        webview.setWebViewClient( new MyWebViewClient() );
        searchview = findViewById( R.id.serchview );
        searchview.setOnClickListener( this );
        serchview_internet = findViewById( R.id.serchview_internet );
        serchview_internet.setOnClickListener( this );
        et_kal.setOnTouchListener( this );et_amounttt.setOnTouchListener( this );et_spinnerEditT.setOnTouchListener( this );et_newAmount.setOnTouchListener( this );;
        //   et_kal.setOnClickListener( this );et_amounttt.setOnClickListener( this );et_spinnerEditT.setOnClickListener( this );et_newAmount.setOnClickListener( this );;
//
    }
    private void updateMainList() {
        apdateFoodList();
        loadPrivetFoodListData();
        addPrivetFoodListToFoodList();
        updateKlist();
        //מיון לפי א"ב
        //sortArrayList();
    }
    private void cancelAdit() {

        ly_aditAmount.setVisibility( View.GONE );
    }
    private void cancelFoodAdd() {
        ly_addNewPrivetFood.setVisibility(View.GONE);
        ly_addFood.setVisibility(View.GONE);
        searchview.setVisibility(View.VISIBLE);
        iv_backToMain.setVisibility( View.VISIBLE );

    }
    private void cancelNewFoodAdd() {
        hideKeyboard();
        cancelFoodAdd();
        iv_backToMain.setVisibility(View.GONE);
        ly_selfSearch_bar.setVisibility( View.VISIBLE );
        mRecyclerView.setVisibility(View.VISIBLE);
        moreSerch.setVisibility( View.GONE );
        iv_selfSearch_round.setVisibility( View.GONE );
        iv_showSelfSearchBar.setVisibility( View.GONE );
        iv_barcodeSearch_round.setVisibility( View.VISIBLE );

        searchview.setQuery( "" , false );
        et_food.setText( "" );
        et_kal.setText( "" );
        spinner.setSelection( 6 );
        searchview.setBackgroundResource( R.drawable.sty_3 );
        updateMainList();

    }
    private void startinternetWebSearch() {
        if (Build.VERSION.SDK_INT >= 19) {
            webview.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        webview.loadUrl("https://www.google.com/search?q=" + serchview_internet.getQuery().toString() +" "+ "קלוריות");
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
        //  et_food.setText( searchview.getQuery().toString() );

        //   rl_selfSearch.setVisibility(View.GONE);
        //  mRecyclerView.setVisibility(View.GONE);
        //  rl_selfSearch.setVisibility( View.GONE );
        //  webview.setVisibility( View.VISIBLE );
        //     ly_addNewPrivetFood.setVisibility(View.VISIBLE);

        ly_addFood.setVisibility(View.GONE);


        et_d_enter_code = QrCode_dialog.findViewById( R.id.et_d_enter_code );
        et_d_enter_code.setText(""+bCod.toString()+"");

        if (Build.VERSION.SDK_INT >= 19) {
            webview.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        webview.loadUrl("https://www.google.com/search?q=" + bCod.toString());

        //העלם עמודת חיפוש והצג אפשרות ביטול
        searchview.setVisibility(View.GONE);
    }
    private void startWebSearch() {
        ly_addNewPrivetFood.startAnimation( slide_in_bottom );
        ly_selfSearch_bar.setVisibility( View.VISIBLE );
        iv_backToMain.setVisibility( View.GONE );
        moreSerch.setVisibility( View.GONE );
        rl_mainInformation.setVisibility(View.GONE);
        rl_top.setVisibility( View.GONE );
        rl_top_ss.setVisibility( View.VISIBLE );
        changeBarColor(rl_top_ss);

        rl_selfSearch.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

        rl_selfSearch.setVisibility( View.GONE );
        webview.setVisibility( View.VISIBLE );
        ly_addNewPrivetFood.setVisibility(View.VISIBLE);
        ly_addFood.setVisibility(View.GONE);

        et_d_enter_code = QrCode_dialog.findViewById( R.id.et_d_enter_code );
        et_d_enter_code.setText("");

        if (Build.VERSION.SDK_INT >= 19) {
            webview.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        webview.loadUrl("https://www.google.com/search?q=" + searchview.getQuery().toString() +" "+ "קלוריות");
        et_food.setText( searchview.getQuery().toString() );
        moreSerch.setVisibility( View.GONE ); //זה בכוונה אחרי חיפוש המוצר ואחרי עדעון האדיט טקסט של שם המוצר.
        //העלם עמודת חיפוש והצג אפשרות ביטול
        searchview.setVisibility(View.GONE);
    }

    private void updateKlist(){
        loadFoodListData_k();
        myListRecyclerView.setAdapter(new ConsumedItemAdapter(myList));
        if (myList.size()!=0){
            myListRecyclerView.smoothScrollToPosition(myList.size()-1);}
        update_kaloriesSum_k();
    }
    private void backToMain() {
        searchview.setVisibility( View.VISIBLE );
        searchview.setQuery( "",true );
        searchview.setIconified(true);
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
    private void switchPlaces() {
        /*   spinner_x=spinner.getX();
        spinner_y=spinner.getY();
        amount_x=et_amount.getX();
        amount_y=et_amount.getY();
        if(spinner_x>amount_x){
            spinner.setX( amount_x );
            et_amount.setX( spinner_x );
        }
      */
        type_x=tv_Type.getX();
        amounttt_x=et_amounttt.getX();
        if(type_x>amounttt_x){
            tv_Type.setX( amounttt_x );
            et_amounttt.setX( type_x );
        }

        p_x=plus_z.getX();
        m_x=minus_z.getX();
        if(p_x>m_x){
            plus_z.setX( m_x );
            minus_z.setX( p_x );
        }

        pa_x=plus_adit.getX();
        ma_x=minus_adit.getX();
        if(pa_x>ma_x){
            plus_adit.setX( ma_x );
            minus_adit.setX( pa_x );
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
        String detailsString=""+labal+"";
        detailsString=detailsString+" "+exampleList.size()+" items ";
        for(int i=0; i <exampleList.size();i++){
            String itemT= "exampleList.add(new ExampleItem("+0+","+0+","+"\""+ exampleList.get( i ).getName()+"\"" +","+"\""+ exampleList.get( i ).getUnit()+"\""+" ,"+"\""+ exampleList.get( i ).getCalorieText()+"\""+","+0+","+"null"+"));";
            detailsString=detailsString.toString()+ "\n"+itemT;
        }
        clipData(detailsString , this);
        Toast.makeText( getBaseContext(), "Copied successfully"+exampleList.size()+" items ",Toast.LENGTH_SHORT).show();
    }
    //פעולות שרדפרפרנס רשימת הקלוריות המוצגת במסך ראשי
    private void addToList( double amount,int kals){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat hour = new SimpleDateFormat("HH");

        //עדכון רשימת מסך ראשי

        ConsumedItem listItem=new ConsumedItem(amount,temp_exampleItem,date.format(cal.getTime()).toString() , 0);
        myList_temp.add(listItem );
        // שמירה בטלפון
        saveData_K();
        // עדכון רשימה פיזית
        loadFoodListData_k();
        myListRecyclerView.setAdapter(new ConsumedItemAdapter(myList));
    }
    private void loadFoodListData_k() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(cal.getTime());

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("eat list", null);
        Type type = new TypeToken<ArrayList<ConsumedItem>>() {}.getType();
        myList_temp = gson.fromJson(json, type);
        myList = new ArrayList<>();
        if (myList_temp == null) {
            myList_temp= new ArrayList<>();
            myList = new ArrayList<>();
        }else {
            for (int i = 0; i < myList_temp.size(); i++) {
                //תעדכן מספר סידורי
                myList_temp.get( i ).setSerial( i );
                //אם זה היום הנכון לפי התאריך
                if(strDate.toString().equals(myList_temp.get( i ).getDate().toString())){
//פה זה מתקן את התמונה של כמות, בבוא הזמן נשנה אותה לתמונות של בוקר צהריים או ערב, השוני יקבע לפי השעה שהוספנו את המזון לרשימה
                    for (int j = 0; j < spinner.getAdapter().getCount(); j++) {
                        if (myList_temp.get( i ).getProductItem().getUnit().equals( spinner.getItemAtPosition( j ).toString() )) {
                            // exampleList.get( i ).setmTypePosition( j );
                            myList_temp.get( i ).getProductItem().setUnitImageResId( typeList.get( j ) );
                        }  }

                    myList.add( myList_temp.get( i ) );
                }

            }
        }}
    private void saveData_K(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myList_temp);
        editor.putString("eat list", json);
        editor.apply();
    }
    private void clearData_K(){
        if (myList_temp == null) {
            myList_temp = new ArrayList<>();
        }else {
            SharedPreferences sharedPreferences = getSharedPreferences( "shared preferences" , MODE_PRIVATE );
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson( null );

            editor.putString( "eat list" , json );
            editor.apply();
            myList_temp = new ArrayList<>();
            //mRecyclerView.setAdapter( new ExampleAdapter( exampleList ) );
        }
    }
    private void update_kaloriesSum_k(){
        int kaloriesSum=0;
        for( int i = 0; i < myList.size(); i++){
            kaloriesSum+= myList.get( i ).getTotalCalories();
        }

        if (kaloriesSum!=0){
            //  text.setText( "  "+kaloriesSum+"  " );
            text.setText( ""+kaloriesSum+"" );
            text.setBackgroundResource( R.drawable.sty_blue_r ); }
        else{    text.setText( "");
            text.setBackgroundResource( R.drawable.sty_blue_r_sercle ); }
    }
    private void deleteFromCalList(int position){
        boolean finished = false;
        int q=0;
        while (finished==false){
            //אם המספר הסידורי של איבר ברשימה הגדולה שווה למספר הסידורי של האיבר שרציתי למחוק מהרשימה של היום הכללי (כלומר אם זה האיבר שאני רוצה למחוק)
            if (myList_temp.get( q ).getSerial()==myList.get( position).getSerial()){
                //מחק אותו וסיים
                myList_temp.remove( q);
                //רק אחרי שמחקנו את האיבר תמחק המרשימה הקטנה!! אחרת נוצרות בעיות
                myList.remove( position );
                //סיים משימה
                finished = true;
            }
            q++;
        }
//myList_temp.remove( myList.get( position).getSerial());
        saveData_K();
        update_kaloriesSum_k();
        myListRecyclerView.setAdapter(new ConsumedItemAdapter(myList));
    }

    private void aditItemFromCalList(int position,double newAmount){
        boolean finished = false;
        int q=0;
        while (finished==false){
            //אם המספר הסידורי של איבר ברשימה הגדולה שווה למספר הסידורי של האיבר שרציתי למחוק מהרשימה של היום הכללי (כלומר אם זה האיבר שאני רוצה לשנוצ)
            if (myList_temp.get( q ).getSerial()==myList.get( position).getSerial()){
                //שנה כמות
                myList_temp.get( q ).setAmount( newAmount );
               // String  st = calcolatyCAL( Double.parseDouble(   myList_temp.get( q ).getProductItem().getCalorieText()) , myList_temp.get( q ).getAmount() ,myList_temp.get( q ).getProductItem().getUnit());
               // myList_temp.get( q ).setS_kal( Integer.parseInt( st ) );
                //סיים משימה
                finished = true;
            }
            q++;
        }
        saveData_K();
        loadFoodListData_k();
        update_kaloriesSum_k();
        myListRecyclerView.setAdapter(new ConsumedItemAdapter(myList));
    }

    // אין לי מושג מה זה ומה שלא יודעים לא כואב ;)  (כל מה שמתחת)
    @Override
    public void onItemSelected(AdapterView<?> adapterView , View view , int i , long l) {
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
    @Override
    public void onGlobalLayout() {
        switchPlaces();
        switchPlaces();
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

            if (ly_addNewPrivetFood.getVisibility() == View.VISIBLE) {
                //   cancelNewFoodAdd();
            }
            if (ly_addFood.getVisibility() == View.VISIBLE) {
                cancelFoodAdd();
            } else {
                if (mRecyclerView.getVisibility() == View.VISIBLE) {
                    backToMain();
                } else {
                    if (rl_selfSearch.getVisibility() == View.VISIBLE) {
                        backToMain();
                    }
                }
            }

            if (ly_aditAmount.getVisibility() == View.VISIBLE) {
                cancelAdit();
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
        if (view==et_kal||view==et_amounttt||view==et_spinnerEditT||view==et_newAmount){
            EditText  et_temp= (EditText) view;
            et_temp.setText("");
            return false;}
        return false;
    }
    private class MyWebViewClient extends WebViewClient {
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