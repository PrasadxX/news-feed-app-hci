package nfr.newsfeed.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "nfr_newsfeed_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_SELECTED_CATEGORY = "selected_category";
    private static final int DEFAULT_CATEGORY_ID = 36569; // Featured category as default

    private SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isDarkModeEnabled() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkModeEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    public int getSelectedCategoryId() {
        return sharedPreferences.getInt(KEY_SELECTED_CATEGORY, DEFAULT_CATEGORY_ID);
    }

    public void setSelectedCategoryId(int categoryId) {
        sharedPreferences.edit().putInt(KEY_SELECTED_CATEGORY, categoryId).apply();
    }
}