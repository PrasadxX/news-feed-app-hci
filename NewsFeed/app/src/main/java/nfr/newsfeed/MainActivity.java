package nfr.newsfeed;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import nfr.newsfeed.helpers.NavigationItem;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}