package com.example.calories.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.calories.ui.utils.CaptureAct;
import com.example.calories.R;
import com.example.calories.ui.screens.MyProductActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import static com.example.calories.utils.AppConstants.*;
public class BarcodeDialogHandler {

    private final Context context;
    private final Dialog dialog;
    private final EditText etBarcode;
    private final ImageView ivScan;

    private final CaptureAct captureAct = new CaptureAct();

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

    private void scanCode(){
        captureAct.scanCode(context);
    }

    public void handleActivityResult(IntentResult result) {
        if (result != null && result.getContents() != null) {
            String barcode = result.getContents();
            addBarcode(barcode);
        } else {
            Toast.makeText(context, TEXT_NO_RESULT, Toast.LENGTH_SHORT).show();
        }
    }
    public  void  addBarcode(String barcode){
        if ( !barcode.isEmpty()) {
            String et_text= etBarcode.getText().toString();
            if (et_text.isEmpty()){
                etBarcode.setText( barcode );
            } else {
                etBarcode.setText( et_text + " , " +barcode  );
            }
    }
    }
}
