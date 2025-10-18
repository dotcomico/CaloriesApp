package com.example.calories.ui.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.calories.BaseActivity;
import com.example.calories.R;

import java.util.Locale;
import java.util.Objects;

import static com.example.calories.utils.AppConstants.*;

public class SettingsActivity extends BaseActivity {

    private SwitchCompat darkModeSwitch;
    private CardView languageCard;
    private CardView aboutCard;
    private TextView selectedLanguageText;
    private ImageButton backButton;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });


        // טעינת הגדרות לפני setContentView
        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);


        setContentView(R.layout.activity_settings);

        initializeViews();
        setupListeners();
        updateUI();
    }
    private void initializeViews() {
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        languageCard = findViewById(R.id.languageCard);
        aboutCard = findViewById(R.id.aboutCard);
        selectedLanguageText = findViewById(R.id.selectedLanguage);
        backButton = findViewById(R.id.backButton);
    }

    private void setupListeners() {
        // כפתור חזור
        backButton.setOnClickListener(v -> finish());

        // מתג מצב לילה
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveDarkMode(isChecked);
            applyDarkMode(isChecked);
        });

        // בחירת שפה
        languageCard.setOnClickListener(v -> showLanguageDialog());

        // אודות האפליקציה
        aboutCard.setOnClickListener(v -> showAboutDialog());
    }

    private void updateUI() {
        // עדכון מצב המתג
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        darkModeSwitch.setChecked(isDarkMode);

        // עדכון שפה מוצגת
        String language = prefs.getString(KEY_LANGUAGE, "he");
        updateLanguageText(language);
    }



    private void showLanguageDialog() {
        String[] languages = {
                getString(R.string.hebrew),
                getString(R.string.english),
                getString(R.string.arabic)
        };

        String[] languageCodes = {"he", "en", "ar"};

        String currentLanguage = prefs.getString(KEY_LANGUAGE, "he");
        int selectedIndex = 0;
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLanguage)) {
                selectedIndex = i;
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_a_language);
        builder.setSingleChoiceItems(languages, selectedIndex, (dialog, which) -> {
            String selectedLanguage = languageCodes[which];
            saveLanguage(selectedLanguage);
            setLocale(selectedLanguage, true);
            updateLanguageText(selectedLanguage);
            dialog.dismiss();

            // איפוס האקטיביטי כדי להחיל את השפה
            recreate();
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setLocale(String languageCode, boolean restart) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        if (restart) {
            recreate();
        }
    }

    private void updateLanguageText(String languageCode) {
        String languageName;
        switch (languageCode) {
            case "he":
                languageName = getString(R.string.hebrew);
                break;
            case "en":
                languageName = getString(R.string.english);
                break;
            case "ar":
                languageName = getString(R.string.arabic);
                break;
            default:
                languageName = getString(R.string.hebrew);
        }
        selectedLanguageText.setText(languageName);
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.about_the_app);
        builder.setMessage(R.string.about_message);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}