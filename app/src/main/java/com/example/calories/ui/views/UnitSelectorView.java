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

// כדאי לבדוק שלא יכניסו תווים אסורים או מדדים באורך לא תקין (למשל " ", או רק סימנים).
// אפשר להוסיף בדיקה פשוטה על אורך או Regex.


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
        initializeViews();
    }

    public UnitSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews();
    }

    public UnitSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews();
    }

    private void initializeViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_unit_selector, this, true);

        spinnerUnit = findViewById(R.id.spinner_unit);
        editCustomUnit = findViewById(R.id.edit_custom_unit);
        iconEdit = findViewById(R.id.icon_edit);

        unitManager = UnitManager.getInstance(getContext());

        setupSpinner();
        setupEditText();
        setupEditIcon();
    }

    /**
     * Initializes the UnitSelectorView by inflating the layout and setting up internal components.
     */
    private void setupSpinner() {
        loadUnits();
        setUpAdapter();
        setupItemSelectedListener();
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
    private void setupItemSelectedListener() {
        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleUnitSelection(position);
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }
    private void handleUnitSelection(int position) {
        String selectedUnitName  = displayItems.get(position);
        if (selectedUnitName .equals(ADD_NEW_OPTION)) {
            showCustomInput();
            return;
        }

        if (listener != null && !isInCustomMode) {
            listener.onUnitSelected(selectedUnitName );
        }
    }
    private void setUpAdapter() {
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, displayItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);
    }
    private void loadUnits() {
        List<Unit> allUnits = unitManager.getAllUnits();
        displayItems = new ArrayList<>();

        for (Unit unit : allUnits) {
            displayItems.add(unit.getName());
        }

        displayItems.add(ADD_NEW_OPTION);
    }
    private void setupEditIcon() {
        iconEdit.setOnClickListener(v -> showEditDialog());
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
        String customUnit = getEditCustomUnitText();

        if (customUnit.isEmpty()) {
            revertToDefaultUnit();
            return;
        }

        if (unitManager.isUnitNameExists(customUnit)) {
            selectExistingUnit(customUnit);
           return;
        }

        addNewCustomUnit(customUnit);
    }
    private void revertToDefaultUnit() {
        hideCustomInput();
        selectDefaultUnit();
    }

    private void selectExistingUnit(String unitName) {
        hideCustomInput();
        selectUnit(unitName);
    }

    private void addNewCustomUnit(String unitName) {
        Unit newUnit = new Unit(unitName, true);
        unitManager.saveCustomUnit(newUnit);

        refreshSpinner();
        selectUnit(unitName);
        hideCustomInput();

        if (listener != null) {
            listener.onUnitSelected(unitName);
        }
    }

    public void selectDefaultUnit() {
        String DEFAULT_UNIT = unitManager.getDefaultUnits().get(0).getName();
        selectUnit(DEFAULT_UNIT);
    }

    private void refreshSpinner() {
        loadUnits();
        adapter.notifyDataSetChanged();
        setUpAdapter();
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
            Utility.makeToast( "אין מדדים מותאמים אישית", getContext());
            return;
        }

        String[] unitNames = extractUnitNames(customUnits);

        new AlertDialog.Builder(getContext())
                .setTitle("מדדים מותאמים אישית")
                .setItems(unitNames, (dialog, which) -> {
                    Unit selectedUnit = customUnits.get(which);
                    showDeleteConfirmation(selectedUnit);
                })
                .setNegativeButton("סגור", null)
                .show();
    }

    private String[] extractUnitNames(List<Unit> units) {
        String[] names = new String[units.size()];
        for (int i = 0; i < units.size(); i++) {
            names[i] = units.get(i).getName();
        }
        return names;
    }

    private void showDeleteConfirmation(Unit unit) {
        String unitName = unit.getName();
        String message = "האם אתה בטוח שברצונך למחוק את המדד \"" + unitName + "\"?";

        new AlertDialog.Builder(getContext())
                .setTitle("מחיקת מדד")
                .setMessage(message)
                .setPositiveButton("מחק", (dialog, which) -> {
                    unitManager.removeCustomUnit(unit);
                    refreshSpinner();
                    Toast.makeText(getContext(), "המדד \"" + unitName + "\" נמחק", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("בטל", null)
                .show();
    }


    // Public methods
    public void setOnUnitSelectedListener(OnUnitSelectedListener listener) {
        this.listener = listener;
    }

    public String getSelectedUnit() {
        Object selectedItem = spinnerUnit.getSelectedItem();

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

    public String getUnit() {

        if (isInCustomMode) {
            handleCustomUnit();
        }

        return getSelectedUnit();

    }
}