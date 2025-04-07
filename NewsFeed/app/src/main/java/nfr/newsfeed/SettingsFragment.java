package nfr.newsfeed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nfr.newsfeed.api.NewsApiService;
import nfr.newsfeed.api.RetrofitClient;
import nfr.newsfeed.models.Category;
import nfr.newsfeed.utils.PreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private SwitchMaterial darkModeSwitch;
    private Spinner categorySpinner;
    private MaterialCardView aboutCard;
    private MaterialCardView shareCard;
    private MaterialCardView rateCard;
    private TextView versionTextView;

    private PreferencesManager preferencesManager;
    private Map<String, Integer> categoryMap = new HashMap<>();
    private List<String> categoryNames = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize preferences manager
        preferencesManager = new PreferencesManager(requireContext());

        // Initialize views
        darkModeSwitch = view.findViewById(R.id.switch_dark_mode);
        categorySpinner = view.findViewById(R.id.category_spinner);
        aboutCard = view.findViewById(R.id.about_card);
        shareCard = view.findViewById(R.id.share_card);
        rateCard = view.findViewById(R.id.rate_card);
        versionTextView = view.findViewById(R.id.version_text);

        // Set up dark mode switch
        setupDarkModeSwitch();

        // Load categories for spinner
        loadCategories();

        // Set up about card
        aboutCard.setOnClickListener(v -> showAboutDialog());

        // Set up share card
        shareCard.setOnClickListener(v -> shareApp());

        // Set up rate card
        rateCard.setOnClickListener(v -> rateApp());

        // Set app version
        try {
            String versionName = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0).versionName;
            versionTextView.setText("Version " + versionName);
        } catch (Exception e) {
            versionTextView.setText("Version 1.0");
        }

        return view;
    }

    private void setupDarkModeSwitch() {
        // Set initial state based on saved preference
        darkModeSwitch.setChecked(preferencesManager.isDarkModeEnabled());

        // Set listener for changes
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save preference
            preferencesManager.setDarkModeEnabled(isChecked);

            // Apply theme
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    private void loadCategories() {
        NewsApiService apiService = RetrofitClient.getClient().create(NewsApiService.class);
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Clear previous data
                    categoryMap.clear();
                    categoryNames.clear();

                    // Add categories to map and list
                    for (Category category : response.body()) {
                        categoryMap.put(category.getName(), category.getId());
                        categoryNames.add(category.getName());
                    }

                    // Set up spinner adapter
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            categoryNames
                    );
                    categorySpinner.setAdapter(adapter);

                    // Set selected category
                    int selectedCategoryId = preferencesManager.getSelectedCategoryId();
                    for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
                        if (entry.getValue() == selectedCategoryId) {
                            int position = categoryNames.indexOf(entry.getKey());
                            if (position >= 0) {
                                categorySpinner.setSelection(position);
                            }
                            break;
                        }
                    }

                    // Set listener for selection changes
                    categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String categoryName = categoryNames.get(position);
                            Integer categoryId = categoryMap.get(categoryName);
                            if (categoryId != null) {
                                preferencesManager.setSelectedCategoryId(categoryId);
                                Toast.makeText(
                                        requireContext(),
                                        "Home screen will show " + categoryName + " news",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Do nothing
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(
                        requireContext(),
                        "Failed to load categories: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void showAboutDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.about_app)
                .setMessage(getString(R.string.about_content) + "\n\n" + getString(R.string.developer))
                .setIcon(R.drawable.ic_info)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this News Feed app!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out News Feed, a great way to stay updated with the latest news! Download it now: [App Store Link]");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void rateApp() {
        // In a real app, this would point to the Play Store listing
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + requireContext().getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + requireContext().getPackageName())));
        }
    }
}