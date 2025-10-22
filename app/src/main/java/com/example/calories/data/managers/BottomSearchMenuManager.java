package com.example.calories.data.managers;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.calories.R;

public class BottomSearchMenuManager {

    // Views
    private ScrollView bottomActionMenu;
    private EditText searchTextView;
    private ImageView searchIcon, backIcon, catalogIcon;
    private ImageView selfAddIcon, selfSearchIcon, barcodeIcon;
    private LinearLayout buttonsContainer;
    private View separator;

    private Context context;
    private Activity activity;

    // Listeners
    private OnSearchListener onSearchListener;
    private OnActionClickListener onActionClickListener;

    // Constructor
    public BottomSearchMenuManager(Activity activity) {
        this.activity = activity;
        this.context = activity;
        initViews();
        setupListeners();
    }

    // ============= Initialization =============

    private void initViews() {
        bottomActionMenu = activity.findViewById(R.id.bottomActionMenu);
        searchTextView = activity.findViewById(R.id.searchTextView);
        searchIcon = activity.findViewById(R.id.searchIcon);
        backIcon = activity.findViewById(R.id.backIcon);
        catalogIcon = activity.findViewById(R.id.catalogIcon);
        selfAddIcon = activity.findViewById(R.id.selfAddIcon);
        selfSearchIcon = activity.findViewById(R.id.selfSearchIcon);
        barcodeIcon = activity.findViewById(R.id.barcodeIcon);
        buttonsContainer = activity.findViewById(R.id.buttonsContainer);
        separator = activity.findViewById(R.id.separator);

        // Initial state
        setInitialState();
    }

    private void setupListeners() {
        // Search text changes
        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleSearchTextChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Editor action (search button on keyboard)
        searchTextView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (onActionClickListener != null) {
                    onActionClickListener.onSearchSubmit(searchTextView.getText().toString());
                }
                hideKeyboard();
                return true;
            }
            return false;
        });

        // Icon clicks
        searchIcon.setOnClickListener(v -> {
            requestSearchFocus();
        });

        backIcon.setOnClickListener(v -> {
            if (onActionClickListener != null) {
                onActionClickListener.onBackClick();
            }
            resetToInitialState();
        });

        catalogIcon.setOnClickListener(v -> {
            if (onActionClickListener != null) {
                onActionClickListener.onCatalogClick();
            }
            showCatalogMode();
        });

        selfAddIcon.setOnClickListener(v -> {
            if (onActionClickListener != null) {
                onActionClickListener.onSelfAddClick(searchTextView.getText().toString());
            }
        });

        selfSearchIcon.setOnClickListener(v -> {
            if (onActionClickListener != null) {
                onActionClickListener.onSelfSearchClick(searchTextView.getText().toString());
            }
        });

        barcodeIcon.setOnClickListener(v -> {
            if (onActionClickListener != null) {
                onActionClickListener.onBarcodeClick();
            }
        });
    }

    // ============= State Management =============

    private void setInitialState() {
        searchIcon.setVisibility(View.VISIBLE);
        backIcon.setVisibility(View.GONE);
        catalogIcon.setVisibility(View.VISIBLE);
        selfAddIcon.setVisibility(View.GONE);
        selfSearchIcon.setVisibility(View.GONE);
    }

    public void resetToInitialState() {
        searchTextView.setText("");
        setInitialState();
        hideKeyboard();
        clearSearchFocus();
    }

    private void handleSearchTextChange(String query) {
        if (query.isEmpty()) {
            setInitialState();
            if (onSearchListener != null) {
                onSearchListener.onSearchCleared();
            }
            return;
        }

        // Show back button when typing
        backIcon.setVisibility(View.VISIBLE);
        searchIcon.setVisibility(View.GONE);

        // Notify listener
        if (onSearchListener != null) {
            onSearchListener.onSearchQueryChanged(query);
        }
    }

    // ============= Public State Methods =============

    public void showNumericMode() {
        catalogIcon.setVisibility(View.GONE);
        selfAddIcon.setVisibility(View.VISIBLE);
        selfSearchIcon.setVisibility(View.GONE);
    }

    public void showSelfSearchMode() {
        catalogIcon.setVisibility(View.GONE);
        selfAddIcon.setVisibility(View.GONE);
        selfSearchIcon.setVisibility(View.VISIBLE);
    }

    public void showNormalMode() {
        catalogIcon.setVisibility(View.VISIBLE);
        selfAddIcon.setVisibility(View.GONE);
        selfSearchIcon.setVisibility(View.GONE);
    }

    public void showCatalogMode() {
        backIcon.setVisibility(View.VISIBLE);
        searchIcon.setVisibility(View.GONE);
    }

    // ============= Utility Methods =============

    public void clearSearch() {
        searchTextView.setText("");
    }

    public String getSearchQuery() {
        return searchTextView.getText().toString().trim();
    }

    public void setSearchQuery(String query) {
        searchTextView.setText(query);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);
    }

    public void requestSearchFocus() {
        searchTextView.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchTextView, InputMethodManager.SHOW_IMPLICIT);
    }
    public void clearSearchFocus() {
        searchTextView.clearFocus();
    }

    // ============= Listener Interfaces =============

    public interface OnSearchListener {
        void onSearchQueryChanged(String query);
        void onSearchCleared();
    }

    public interface OnActionClickListener {
        void onBackClick();
        void onCatalogClick();
        void onSelfAddClick(String calorieValue);
        void onSelfSearchClick(String productName);
        void onBarcodeClick();
        void onSearchSubmit(String query);
    }

    // ============= Setters for Listeners =============

    public void setOnSearchListener(OnSearchListener listener) {
        this.onSearchListener = listener;
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        this.onActionClickListener = listener;
    }
}