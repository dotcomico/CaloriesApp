package com.example.calories.ui.views;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.calories.R;

public class BottomSearchMenu {

    private final View rootView;
    private final ImageView searchIcon;
    private final ImageButton barcodeIcon;
    private final TextView searchTextView;
    private final View searchBackground;
    private final LinearLayout buttonsContainer;
    private final ImageView selfSearchIcon, selfAddIcon, catalogIcon;

    private OnMenuClickListener listener;

    public enum State {
        DEFAULT, // When search text is empty
        NUMERIC_INPUT, // When search text is a number
        NO_RESULTS, // When search yields no results
        SEARCH_WITH_RESULTS // When search has results
    }

    public interface OnMenuClickListener {
        void onSearchClick();
        void onBarcodeClick();
        void onCatalogClick();
        void onSelfAddClick();
        void onSelfSearchClick();
    }

    public BottomSearchMenu(View rootView) {
        this.rootView = rootView;

        // Initialize views from the layout with corrected IDs
        searchBackground = rootView.findViewById(R.id.mainSearchView);
        searchIcon = rootView.findViewById(R.id.searchIcon);
        barcodeIcon = rootView.findViewById(R.id.barcodeIcon);
        searchTextView = rootView.findViewById(R.id.searchTextView);
        buttonsContainer = rootView.findViewById(R.id.buttonsContainer);
        selfSearchIcon = rootView.findViewById(R.id.selfSearchIcon);
        selfAddIcon = rootView.findViewById(R.id.selfAddIcon);
        catalogIcon = rootView.findViewById(R.id.catalogIcon);

        setupClickListeners();
        // Set the initial state to default
        setState(State.DEFAULT);
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        this.listener = listener;
    }

    private void setupClickListeners() {
        View.OnClickListener searchClickListener = v -> {
            if (listener != null) {
                listener.onSearchClick();
            }
        };
        searchBackground.setOnClickListener(searchClickListener);
        searchTextView.setOnClickListener(searchClickListener);
        searchIcon.setOnClickListener(searchClickListener);

        barcodeIcon.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBarcodeClick();
            }
        });

        catalogIcon.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCatalogClick();
            }
        });

        selfAddIcon.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelfAddClick();
            }
        });

        selfSearchIcon.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelfSearchClick();
            }
        });
    }

    /**
     * Updates the entire menu's UI to reflect a specific state.
     * The Activity determines the state and calls this method.
     * @param state The state to display.
     */
    public void setState(State state) {
        switch (state) {
            case DEFAULT:
                catalogIcon.setVisibility(View.VISIBLE);
                selfAddIcon.setVisibility(View.GONE);
                selfSearchIcon.setVisibility(View.GONE);
                barcodeIcon.setVisibility(View.VISIBLE);
                searchBackground.setBackgroundResource(R.drawable.search_background);
                break;

            case NUMERIC_INPUT:
                catalogIcon.setVisibility(View.GONE);
                selfAddIcon.setVisibility(View.VISIBLE);
                selfSearchIcon.setVisibility(View.GONE);
                barcodeIcon.setVisibility(View.GONE);
                searchBackground.setBackgroundResource(R.drawable.sty_3_purple);
                break;

            case NO_RESULTS:
                catalogIcon.setVisibility(View.GONE);
                selfAddIcon.setVisibility(View.GONE);
                selfSearchIcon.setVisibility(View.VISIBLE);
                barcodeIcon.setVisibility(View.GONE);
                searchBackground.setBackgroundResource(R.drawable.sty_orang3);
                break;

            case SEARCH_WITH_RESULTS:
                catalogIcon.setVisibility(View.GONE);
                selfAddIcon.setVisibility(View.GONE);
                selfSearchIcon.setVisibility(View.GONE);
                barcodeIcon.setVisibility(View.VISIBLE);
                searchBackground.setBackgroundResource(R.drawable.search_background);
                break;
        }
    }

    /**
     * Updates the text displayed in the search bar.
     * @param text The text to display.
     */
    public void setSearchText(String text) {
        if (searchTextView != null) {
            searchTextView.setText(text);
        }
    }

    /**
     * Updates the text displayed in the search bar using a string resource.
     * @param textResId The resource ID of the text to display.
     */
    public void setSearchText(int textResId) {
        if (searchTextView != null) {
            searchTextView.setText(textResId);
        }
    }

    public View getRootView() {
        return rootView;
    }
}
