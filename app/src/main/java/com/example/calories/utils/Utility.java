package com.example.calories.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static com.example.calories.utils.AppConstants.*;

import com.example.calories.R;

public class Utility {

    public  static void makeToast(String string ,Context context){
        //      makeToast("ההעתקה בוצעה בהצלחה" ,  this );
        Toast.makeText( context, string ,Toast.LENGTH_SHORT).show();
    }

    public  static void  clipData(String textString , Context context){
        //clipData( detailsString , this );

        //העתק טקסט
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService( Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", textString);
        clipboard.setPrimaryClip(clip);
    }

    @SuppressLint("IntentReset")
    public static void emailSend(String detailsString , Context context) {
        //       emailSend( detailsString , this );

        SimpleDateFormat sdf = new SimpleDateFormat( DATE_PATTERN_FOR_SHOW , Locale.getDefault() );
        String currentDateAndTime = sdf.format( new Date() );

        Intent i = new Intent( Intent.ACTION_SEND );
        i.setData( Uri.parse( "email" ) );
        String[] s = {"dotanst100@gmail.com" ,};
        i.putExtra( Intent.EXTRA_EMAIL , s );
        i.putExtra( Intent.EXTRA_SUBJECT , R.string.custom_products + currentDateAndTime);
        i.putExtra( Intent.EXTRA_TEXT , detailsString );
        i.setType( "message/rfc822" );
        Intent chooser = Intent.createChooser( i , "Launch Email" );
       context.startActivity( chooser );
    }
    
    public static void startNewActivity(Context context, Class<?> activityClass) {
        //            startNewActivity(MainActivity.this, customProductActivity.class);
        context.startActivity(new Intent(context, activityClass));
    }

    // שיטה סטטית לבדוק אם מחרוזת היא מספר
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService( Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
