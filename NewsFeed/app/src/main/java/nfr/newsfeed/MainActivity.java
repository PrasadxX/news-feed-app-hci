package nfr.newsfeed;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import nfr.newsfeed.helpers.NavigationItem;
import nfr.newsfeed.utils.PreferencesManager;

public class MainActivity extends AppCompatActivity {

    private PreferencesManager preferencesManager;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize preferences manager
        preferencesManager = new PreferencesManager(this);

        // Apply theme based on saved preference
        if (preferencesManager.isDarkModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment
        loadFragment(new HomeFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            for (NavigationItem navigationItem : NavigationItem.values()) {
                if (navigationItem.getId() == item.getItemId()) {
                    switch (navigationItem) {
                        case HOME:
                            selectedFragment = new HomeFragment();
                            break;
                        case CATEGORIES:
                            selectedFragment = new CategoriesFragment();
                            break;
                        case SETTINGS:
                            selectedFragment = new SettingsFragment();
                            break;
                    }
                    break;
                }
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }


    @Override
    public void onBackPressed() {
        // Handle back press to navigate to the previous fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof HomeFragment) {
            super.onBackPressed();
        } else {
            loadFragment(new HomeFragment());
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}