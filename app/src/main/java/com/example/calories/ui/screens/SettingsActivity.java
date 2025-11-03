package com.example.calories.ui.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.calories.BaseActivity;
import com.example.calories.R;

import static com.example.calories.utils.AppConstants.*;

public class SettingsActivity extends BaseActivity {

	private SwitchCompat darkModeSwitch;
	private CardView languageCard;
	private CardView aboutCard;
	private TextView selectedLanguageText;
	private ImageButton backButton;

	private SharedPreferences prefs;

	private static SettingsChangeListener settingsChangeListener;

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

		prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

		initializeViews();
		setupListeners();
		updateUI();
	}

	public interface SettingsChangeListener {
		void onThemeChange();

		void onLanguageChange();
	}

	public static void setSettingsChangeListener(SettingsChangeListener listener) {
		settingsChangeListener = listener;
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
			if (settingsChangeListener != null) {
				settingsChangeListener.onThemeChange();
			}
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
		String[] languages = { getString(R.string.hebrew), getString(R.string.english), getString(R.string.arabic) };

		String[] languageCodes = { "he", "en", "ar" };

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
			dialog.dismiss();

			// Recreate the activity to apply the new language
			recreate();
			if (settingsChangeListener != null) {
				settingsChangeListener.onLanguageChange();
			}
		});

		builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

		AlertDialog dialog = builder.create();
		dialog.show();
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