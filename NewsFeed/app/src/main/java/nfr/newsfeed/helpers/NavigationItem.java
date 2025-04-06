package nfr.newsfeed.helpers;

import nfr.newsfeed.R;

public enum NavigationItem {
    HOME(R.id.navigation_home),
    CATEGORIES(R.id.navigation_categories),
    SETTINGS(R.id.navigation_settings);

    private final int id;

    NavigationItem(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}