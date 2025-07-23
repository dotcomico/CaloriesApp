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

import com.example.calories.data.storage.UnitManager;
import com.example.calories.R;
import com.example.calories.data.models.Unit;
import com.example.calories.utils.Utility;

import java.util.ArrayList;
import java.util.List;
/**
 * A custom view that allows the user to select a predefined unit
 * from a Spinner, or enter a custom unit using an EditText field.
 * <p>
 * Typically used when entering product details with flexible units
 * like "gram", "cup", or "custom unit".
 */

public class UnitSelectorView extends LinearLayout {

    private static final String ADD_NEW_OPTION = "הוסף מדד חדש...";
    private Spinner spinnerUnit;
    private EditText editCustomUnit;
    private ImageView iconEdit;
    private ArrayAdapter<String> adapter;
    private List<String> displayItems;
    private UnitManager unitManager;
    private OnUnitSelectedListener listener;
    private boolean isInCustomMode = false;

    public UnitSelectorView(Context context) {
        super(context);
        init();
    }

    public UnitSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UnitSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        int layoutRid = R.layout.custom_unit_selector;
        LayoutInflater.from(getContext()).inflate(layoutRid, this, true);

        spinnerUnit = findViewById(R.id.spinner_unit);
        editCustomUnit = findViewById(R.id.edit_custom_unit);
        iconEdit = findViewById(R.id.icon_edit);

        unitManager = UnitManager.getInstance(getContext());

        setupSpinner();
        setupEditText();
        setupEditIcon();
    }

    private void setupSpinner() {

        loadUnits();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, displayItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);

        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = displayItems.get(position);
                if (selected.equals(ADD_NEW_OPTION)) {
                    showCustomInput();
                } else {
                    if (listener != null && !isInCustomMode) {
                        //להוסיף יחידת מידה -> לרפרש מידע -> לסמן את המוצר החדש
                        listener.onUnitSelected(selected);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupEditText() {
        editCustomUnit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                handleCustomUnit();
                return true;
            }
            return false;
        });

    }

    private void setupEditIcon() {
        iconEdit.setOnClickListener(v -> showEditDialog());
    }

    private void loadUnits() {
        List<Unit> allUnits = unitManager.getAllUnits();
        displayItems = new ArrayList<>();

        for (Unit unit : allUnits) {
            displayItems.add(unit.getName());
        }

        displayItems.add(ADD_NEW_OPTION);
    }

    private void showCustomInput() {
        isInCustomMode = true;
        spinnerUnit.setVisibility(View.GONE);
        editCustomUnit.setVisibility(View.VISIBLE);
        iconEdit.setVisibility(View.VISIBLE);
        editCustomUnit.requestFocus();
    }

    private void hideCustomInput() {
        isInCustomMode = false;
        spinnerUnit.setVisibility(View.VISIBLE);
        editCustomUnit.setVisibility(View.GONE);
        iconEdit.setVisibility(View.GONE);
        editCustomUnit.setText("");
    }

    public void handleCustomUnit() {
        String customMeasurement = getEditCustomUnitText();

        if (customMeasurement.isEmpty()) {

            hideCustomInput();

            selectDefaultUnit();

            return;
        }

        // צור והוסף יחידת מידה
        if (!unitManager.isUnitNameExists(customMeasurement)) {
            // שמירת המדד החדש
            Unit newUnit = new Unit(customMeasurement, true);
            unitManager.saveCustomUnit(newUnit);
            // עדכון הספינר
            refreshSpinner();
            // בחירת המדד החדש
            selectUnit(customMeasurement);

            // חזרה למצב רגיל
            hideCustomInput();

            // הודעה למאזין
            if (listener != null) {
                listener.onUnitSelected(customMeasurement);
            }
        }
    }

    public void selectDefaultUnit() {
        String DEFAULT_UNIT = unitManager.getDefaultUnits().get(0).getName();
        selectUnit(DEFAULT_UNIT);
    }

    private void refreshSpinner() {
        loadUnits();
        adapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, displayItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);
    }

    private void selectUnit(String measurement) {
        int position = displayItems.indexOf(measurement);
        if (position >= 0) {
            spinnerUnit.setSelection(position);
        }
    }

    private void showEditDialog() {
        List<Unit> customUnits = unitManager.getCustomUnits();

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
            Unit unitToDelete = customUnits.get(which);
            showDeleteConfirmation(unitToDelete);
        });
        builder.setNegativeButton("סגור", null);
        builder.show();
    }

    private void showDeleteConfirmation(Unit unit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("מחיקת מדד");
        builder.setMessage("האם אתה בטוח שברצונך למחוק את המדד \"" + unit.getName() + "\"?");
        builder.setPositiveButton("מחק", (dialog, which) -> {
            unitManager.removeCustomUnit(unit);
            refreshSpinner();
            Toast.makeText(getContext(), "המדד נמחק", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("בטל", null);
        builder.show();
    }

    // Public methods
    public void setOnUnitSelectedListener(OnUnitSelectedListener listener) {
        this.listener = listener;
    }

    public String getSelectedUnit(Context context) {

        Object selectedItem = spinnerUnit.getSelectedItem(); //למה מחזיר  "100 גרם" ?
        Utility.makeToast(spinnerUnit.getSelectedItem().toString(), context);
        if (selectedItem != null && !selectedItem.toString().equals(ADD_NEW_OPTION)) {
            return selectedItem.toString();
        }
        return "";
    }

    public void setSelectedUnit(String unitName) {
        if (!TextUtils.isEmpty(unitName)) {
            selectUnit(unitName);
        }
    }

    public interface OnUnitSelectedListener {
        void onUnitSelected(String unitName);
    }

    public String getEditCustomUnitText() {
        return editCustomUnit.getText().toString().trim();
    }

    public String getUnit(Context context) {

        if (isInCustomMode) {
            handleCustomUnit();
        }

        return getSelectedUnit(context);

    }
}