package com.example.calories;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.Locale;
import static com.example.calories.utils.AppConstants.*;

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // טעינת הגדרות לפני יצירת המסך
        loadSettings();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateBaseContextLocale(newBase));
    }

    private Context updateBaseContextLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString(KEY_LANGUAGE, getSystemLanguage());

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // טעינת מצב לילה
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        applyDarkMode(isDarkMode);
    }

    // BaseActivity.java

    // BaseActivity.java

    public void applyDarkMode(boolean isDarkMode) {
        int desiredMode = isDarkMode
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;

        int currentMode = AppCompatDelegate.getDefaultNightMode();

        if (currentMode != desiredMode) {
            AppCompatDelegate.setDefaultNightMode(desiredMode);
        }
    }

    /**
     * מחזיר את שפת המערכת אם לא נבחרה שפה
     */
    private String getSystemLanguage() {
        String systemLang = Locale.getDefault().getLanguage();

        // בדיקה אם השפה נתמכת, אחרת ברירת מחדל לעברית
        if (systemLang.equals("he") || systemLang.equals("en") || systemLang.equals("ar")) {
            return systemLang;
        }
        return "he"; // ברירת מחדל
    }

    /**
     * פונקציה לשמירת שפה (לשימוש בכל האפליקציה)
     */
    protected void saveLanguage(String languageCode) {
        SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();
    }

    /**
     * פונקציה לשמירת מצב לילה (לשימוש בכל האפליקציה)
     */
    protected void saveDarkMode(boolean isDarkMode) {
        SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_DARK_MODE, isDarkMode);
        editor.apply();
    }

    /**
     * פונקציה לקבלת השפה הנוכחית
     */
    protected String getCurrentLanguage() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, getSystemLanguage());
    }

    /**
     * פונקציה לקבלת מצב הלילה הנוכחי
     */
    protected boolean isDarkModeEnabled() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }
}