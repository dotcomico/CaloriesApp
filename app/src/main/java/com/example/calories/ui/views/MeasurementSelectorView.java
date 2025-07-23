package com.example.calories.ui.views;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.calories.data.storage.MeasurementManager;
import com.example.calories.R;
import com.example.calories.data.models.MeasurementUnit;
import com.example.calories.utils.Utility;

import java.util.ArrayList;
import java.util.List;
/**
 * A custom view that allows the user to select a predefined unit of measurement
 * from a Spinner, or enter a custom unit using an EditText field.
 *
 * Typically used when entering product details with flexible measurement units
 * like "gram", "cup", or "custom unit".
 */

public class MeasurementSelectorView extends LinearLayout {

    private static final String ADD_NEW_OPTION = "הוסף מדד חדש...";

    private Spinner spinnerMeasurement;
    private EditText editCustomMeasurement;
    private ImageView iconEdit;
    private ArrayAdapter<String> adapter;
    private List<String> displayItems;
    private List<MeasurementUnit> allUnits;
    private MeasurementManager measurementManager;
    private OnMeasurementSelectedListener listener;
    private boolean isInCustomMode = false;

    public MeasurementSelectorView(Context context) {
        super(context);
        init();
    }

    public MeasurementSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MeasurementSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_measurement_selector, this, true);

        spinnerMeasurement = findViewById(R.id.spinner_measurement);
        editCustomMeasurement = findViewById(R.id.edit_custom_measurement);
        iconEdit = findViewById(R.id.icon_edit);

        measurementManager = MeasurementManager.getInstance(getContext());

        setupSpinner();
        setupEditText();
        setupEditIcon();
    }

    private void setupSpinner() {
        loadMeasurementUnits();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, displayItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeasurement.setAdapter(adapter);

        spinnerMeasurement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = displayItems.get(position);
                if (selected.equals(ADD_NEW_OPTION)) {
                    showCustomInput();
                } else {
                    if (listener != null && !isInCustomMode) {
                        //להוסיף יחידת מידה -> לרפרש מידע -> לסמן את המוצר החדש
                        listener.onMeasurementSelected(selected);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupEditText() {
        editCustomMeasurement.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                handleCustomMeasurement();
                return true;
            }
            return false;
        });

    }

    private void setupEditIcon() {
        iconEdit.setOnClickListener(v -> showEditDialog());
    }

    private void loadMeasurementUnits() {
        allUnits = measurementManager.getAllUnits();
        displayItems = new ArrayList<>();

        for (MeasurementUnit unit : allUnits) {
            displayItems.add(unit.getName());
        }

        displayItems.add(ADD_NEW_OPTION);
    }

    private void showCustomInput() {
        isInCustomMode = true;
        spinnerMeasurement.setVisibility(View.GONE);
        editCustomMeasurement.setVisibility(View.VISIBLE);
        iconEdit.setVisibility(View.VISIBLE);
        editCustomMeasurement.requestFocus();
// add this line to show the keyboard automatically
    }

    private void hideCustomInput() {
        isInCustomMode = false;
        spinnerMeasurement.setVisibility(View.VISIBLE);
        editCustomMeasurement.setVisibility(View.GONE);
        iconEdit.setVisibility(View.GONE);
        editCustomMeasurement.setText("");
        //add this line to close the keyboard
    }

    public void handleCustomMeasurement() {
        //צור והוסף יחידת מידה -> לרפרש מידע -> לסמן את המוצר החדש

        String customMeasurement = getEditCustomMeasurementText();

        //אם הטקסט ריק
        if (TextUtils.isEmpty(customMeasurement)) {
            // אם ריק, חזרה לספינר
            hideCustomInput();

            selectDefaultMeasurement();
            return;
        }

        // צור והוסף יחידת מידה
        if (!measurementManager.isUnitNameExists(customMeasurement)) {
            // שמירת המדד החדש
            MeasurementUnit newUnit = new MeasurementUnit(customMeasurement, true);
            measurementManager.saveCustomUnit(newUnit);
            // עדכון הספינר
            refreshSpinner();
            // בחירת המדד החדש
            selectMeasurement(customMeasurement);

            // חזרה למצב רגיל
            hideCustomInput();

            // הודעה למאזין
            if (listener != null) {
                listener.onMeasurementSelected(customMeasurement);
            }
        }
    }

    public void selectDefaultMeasurement() {
        String DEFAULT_MEASUREMENT = measurementManager.getDefaultUnits().get(0).getName();
        selectMeasurement(DEFAULT_MEASUREMENT);
    }

    private void refreshSpinner() {
        loadMeasurementUnits();
        adapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, displayItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeasurement.setAdapter(adapter);
    }

    private void selectMeasurement(String measurement) {
        int position = displayItems.indexOf(measurement);
        if (position >= 0) {
            spinnerMeasurement.setSelection(position);
        }
    }

    private void showEditDialog() {
        List<MeasurementUnit> customUnits = measurementManager.getCustomUnits();

        if (customUnits.isEmpty()) {
            Toast.makeText(getContext(), "אין מדדים מותאמים אישית", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] items = new String[customUnits.size()];
        for (int i = 0; i < customUnits.size(); i++) {
            items[i] = customUnits.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("מדדים מותאמים אישית");
        builder.setItems(items, (dialog, which) -> {
            MeasurementUnit unitToDelete = customUnits.get(which);
            showDeleteConfirmation(unitToDelete);
        });
        builder.setNegativeButton("סגור", null);
        builder.show();
    }

    private void showDeleteConfirmation(MeasurementUnit unit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("מחיקת מדד");
        builder.setMessage("האם אתה בטוח שברצונך למחוק את המדד \"" + unit.getName() + "\"?");
        builder.setPositiveButton("מחק", (dialog, which) -> {
            measurementManager.removeCustomUnit(unit);
            refreshSpinner();
            Toast.makeText(getContext(), "המדד נמחק", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("בטל", null);
        builder.show();
    }

    // Public methods
    public void setOnMeasurementSelectedListener(OnMeasurementSelectedListener listener) {
        this.listener = listener;
    }

    public String getSelectedMeasurement(Context context) {

        Object selectedItem = spinnerMeasurement.getSelectedItem(); //למה מחזיר  "100 גרם" ?
        Utility.makeToast(spinnerMeasurement.getSelectedItem().toString(), context);
        if (selectedItem != null && !selectedItem.toString().equals(ADD_NEW_OPTION)) {
            return selectedItem.toString();
        }
        return "";
    }

    public void setSelectedMeasurement(String measurement) {
        if (!TextUtils.isEmpty(measurement)) {
            selectMeasurement(measurement);
        }
    }

    public interface OnMeasurementSelectedListener {
        void onMeasurementSelected(String measurement);
    }

    public String getEditCustomMeasurementText() {
        return editCustomMeasurement.getText().toString().trim();
    }

    public String getMeasurement(Context context) {

        if (isInCustomMode) {
            handleCustomMeasurement();
        }

        return getSelectedMeasurement(context);

    }
}