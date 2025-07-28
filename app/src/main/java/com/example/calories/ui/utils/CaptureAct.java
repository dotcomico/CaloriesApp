package com.example.calories.ui.utils;

import android.app.Activity;
import android.content.Context;

import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.CaptureActivity;

public class CaptureAct extends CaptureActivity {
    public void scanCode(Context context) {
        IntentIntegrator integrator = new IntentIntegrator((Activity) context);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("לחץ על מקשי צליל להפעלת פנס");
        integrator.initiateScan();
    }
}
