package com.example.calories;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class BarcodeDialogHandler {

    private final Context context;
    private final Dialog dialog;
    private final EditText etBarcode;
    private final ImageView ivScan;

    public BarcodeDialogHandler(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.scan_barcod_dialog);
        dialog.setCancelable(true);

        etBarcode = dialog.findViewById(R.id.et_d_enter_code);
        ivScan = dialog.findViewById(R.id.iv_d_code_scan);
        ivScan.setOnClickListener(v -> scanCode());
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public EditText getBarcodeEditText() {
        return etBarcode;
    }


    public boolean isDialogShowing() {
        return dialog.isShowing();
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator((MyProductActivity) context); // אם תשתמש באחר — שנה את זה
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("לחץ על מקשי צליל להפעלת פנס");
        integrator.initiateScan();
    }

    public void handleActivityResult(IntentResult result) {
        if (result != null && result.getContents() != null) {
            String barcode = result.getContents();

            String et_text= etBarcode.getText().toString();
            if (et_text.isEmpty()){
                etBarcode.setText( barcode );
            } else {
                etBarcode.setText( et_text + " , " +barcode  );
            }

            // etBarcode.setText(barcode);
        } else {
            Toast.makeText(context, "אין תוצאה", Toast.LENGTH_SHORT).show();
        }
    }
}
