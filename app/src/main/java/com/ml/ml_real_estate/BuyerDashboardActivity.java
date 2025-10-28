package com.ml.ml_real_estate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ml.ml_real_estate.adapters.PropertyAdapter;
import com.ml.ml_real_estate.api.ApiService;
import com.ml.ml_real_estate.api.RetrofitClient;
import com.ml.ml_real_estate.models.PropertiesResponse;
import com.ml.ml_real_estate.models.Property;
import com.ml.ml_real_estate.models.User;
import com.ml.ml_real_estate.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyerDashboardActivity extends AppCompatActivity {
    private static final String TAG = "BuyerDashboardActivity";

    private TextView tvWelcome;
    private Button btnViewFavorites, btnSearch, btnMLConnect;
    private RecyclerView rvProperties;
    private PropertyAdapter propertyAdapter;
    private List<Property> propertyList;
    private ApiService apiService;
    private SharedPreferencesManager prefManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dashboard);

        try {
            Log.d(TAG, "BuyerDashboardActivity started");
            initializeViews();
            setupApiService();
            loadUserData();
            setupRecyclerView();
            setupClickListeners();
            loadProperties();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error loading dashboard", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        }
    }

    private void initializeViews() {
        try {
            tvWelcome = findViewById(R.id.tvWelcome);
            btnViewFavorites = findViewById(R.id.btnViewFavorites);
            btnSearch = findViewById(R.id.btnSearch);
            btnMLConnect = findViewById(R.id.btnMLConnect);
            rvProperties = findViewById(R.id.rvProperties);

            Log.d(TAG, "All views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            throw e;
        }
    }

    private void setupApiService() {
        try {
            apiService = RetrofitClient.getApiService();
            Log.d(TAG, "API service initialized. Base URL: " + RetrofitClient.getBaseUrl());
        } catch (Exception e) {
            Log.e(TAG, "Error setting up API service: " + e.getMessage());
            Toast.makeText(this, "API connection error", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData() {
        prefManager = new SharedPreferencesManager(this);
        currentUser = prefManager.getUser();

        if (currentUser != null) {
            tvWelcome.setText("Welcome, " + currentUser.getFullName() + "!");
            Log.d(TAG, "User loaded: " + currentUser.getFullName() + " (" + currentUser.getUserType() + ")");
        } else {
            tvWelcome.setText("Welcome, Buyer!");
            Log.w(TAG, "User data is null in SharedPreferences");
        }
    }

    private void setupRecyclerView() {
        propertyList = new ArrayList<>();
        propertyAdapter = new PropertyAdapter(propertyList, this, false); // false = not favorite fragment
        rvProperties.setLayoutManager(new LinearLayoutManager(this));
        rvProperties.setAdapter(propertyAdapter);
        Log.d(TAG, "RecyclerView setup completed");
    }

    private void setupClickListeners() {
        btnViewFavorites.setOnClickListener(v -> viewFavorites());
        btnSearch.setOnClickListener(v -> searchProperties());
        btnMLConnect.setOnClickListener(v -> connectToMLRealEstate());
    }

    private void loadProperties() {
        Log.d(TAG, "Starting to load properties...");

        // Check if user is logged in
        if (prefManager.getToken() == null || prefManager.getToken().isEmpty()) {
            Log.e(TAG, "No authentication token found");
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        String authToken = "Bearer " + prefManager.getToken();
        Log.d(TAG, "Auth token: " + authToken.substring(0, Math.min(authToken.length(), 20)) + "...");

        // Show loading state
        Toast.makeText(this, "Loading properties...", Toast.LENGTH_SHORT).show();

        Call<PropertiesResponse> call = apiService.getProperties(authToken);
        call.enqueue(new Callback<PropertiesResponse>() {
            @Override
            public void onResponse(Call<PropertiesResponse> call, Response<PropertiesResponse> response) {
                Log.d(TAG, "API Response received. Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    PropertiesResponse propertiesResponse = response.body();
                    Log.d(TAG, "Response success: " + propertiesResponse.isSuccess());

                    if (propertiesResponse.isSuccess()) {
                        List<Property> newProperties = propertiesResponse.getProperties();
                        Log.d(TAG, "Received " + newProperties.size() + " properties");

                        propertyList.clear();
                        propertyList.addAll(newProperties);
                        propertyAdapter.notifyDataSetChanged();

                        if (propertyList.isEmpty()) {
                            Toast.makeText(BuyerDashboardActivity.this,
                                    "No properties available at the moment", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "No properties found in response");
                        } else {
                            Toast.makeText(BuyerDashboardActivity.this,
                                    "Loaded " + propertyList.size() + " properties", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Successfully loaded " + propertyList.size() + " properties");
                        }
                    } else {
                        String errorMsg = propertiesResponse.getMessage();
                        Toast.makeText(BuyerDashboardActivity.this,
                                "Failed to load properties: " + errorMsg, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "API returned error: " + errorMsg);
                    }
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<PropertiesResponse> call, Throwable t) {
                Log.e(TAG, "API Call failed: " + t.getMessage());
                t.printStackTrace();

                String errorMessage = "Network error: " + t.getMessage();
                if (t instanceof java.net.ConnectException) {
                    errorMessage = "Cannot connect to server. Please check:\n" +
                            "• XAMPP is running\n" +
                            "• Server URL is correct\n" +
                            "• Network connection";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMessage = "Connection timeout. Server is not responding.";
                }

                Toast.makeText(BuyerDashboardActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                // Show sample data for testing
                showSampleData();
            }
        });
    }

    private void handleApiError(Response<PropertiesResponse> response) {
        String errorMessage = "Error loading properties";
        Log.e(TAG, "HTTP Error: " + response.code() + " - " + response.message());

        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.e(TAG, "Error response body: " + errorBody);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading error body: " + e.getMessage());
        }

        switch (response.code()) {
            case 401:
                errorMessage = "Session expired. Please login again.";
                logout();
                break;
            case 404:
                errorMessage = "Properties API not found. Check server configuration.";
                break;
            case 500:
                errorMessage = "Server error. Please try again later.";
                break;
            case 0:
                errorMessage = "Cannot connect to server. Check if XAMPP is running.";
                break;
            default:
                errorMessage = "Error " + response.code() + ": " + response.message();
                break;
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();

        // Show sample data for testing
        showSampleData();
    }

    private void showSampleData() {
        Log.d(TAG, "Showing sample data for testing");

        // Create sample properties for testing
        List<Property> sampleProperties = new ArrayList<>();

        Property sample1 = new Property();
        sample1.setId(1);
        sample1.setTitle("Sample Luxury Villa");
        sample1.setDescription("Beautiful sample property for testing");
        sample1.setPrice(500000.00);
        sample1.setPropertyType("villa");
        sample1.setListingType("sale");
        sample1.setBedrooms(4);
        sample1.setBathrooms(3.5);
        sample1.setAreaSqft(2500.00);

        Property.Address address1 = new Property.Address();
        address1.setCity("Sample City");
        address1.setState("Sample State");
        sample1.setAddress(address1);

        Property sample2 = new Property();
        sample2.setId(2);
        sample2.setTitle("Sample Modern Apartment");
        sample2.setDescription("Another sample property for testing");
        sample2.setPrice(250000.00);
        sample2.setPropertyType("apartment");
        sample2.setListingType("sale");
        sample2.setBedrooms(2);
        sample2.setBathrooms(2.0);
        sample2.setAreaSqft(1200.00);

        Property.Address address2 = new Property.Address();
        address2.setCity("Test City");
        address2.setState("Test State");
        sample2.setAddress(address2);

        sampleProperties.add(sample1);
        sampleProperties.add(sample2);

        propertyList.clear();
        propertyList.addAll(sampleProperties);
        propertyAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Showing sample data (Server connection issue)", Toast.LENGTH_LONG).show();
    }

    // Create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    // Handle menu item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_profile) {
            viewProfile();
            return true;
        } else if (id == R.id.menu_favorites) {
            viewFavorites();
            return true;
        } else if (id == R.id.menu_settings) {
            openSettings();
            return true;
        } else if (id == R.id.menu_logout) {
            logout();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void viewProfile() {
        try {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening profile: " + e.getMessage());
            Toast.makeText(this, "Error opening profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewFavorites() {
        try {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening favorites: " + e.getMessage());
            Toast.makeText(this, "Error opening favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSettings() {
        Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void searchProperties() {
        try {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening search: " + e.getMessage());
            Toast.makeText(this, "Error opening search", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectToMLRealEstate() {
        try {
            Intent intent = new Intent(this, MLWebViewActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error connecting to ML: " + e.getMessage());
            Toast.makeText(this, "Error opening website", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        try {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("Yes", (dialog, which) -> performLogout());
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing logout dialog: " + e.getMessage());
            performLogout(); // Fallback to direct logout
        }
    }

    private void performLogout() {
        try {
            Log.d(TAG, "Starting logout process...");

            // Clear user data from shared preferences
            if (prefManager != null) {
                prefManager.clearAll();
                Log.d(TAG, "User data cleared from SharedPreferences");
            } else {
                Log.w(TAG, "prefManager is null");
            }

            // Show logout message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Navigate to login activity with logout flag
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("LOGOUT", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            Log.d(TAG, "Logout process completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error during logout: " + e.getMessage());
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();

            // Fallback: try to navigate to login anyway
            try {
                Intent fallbackIntent = new Intent(this, LoginActivity.class);
                fallbackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(fallbackIntent);
                finish();
            } catch (Exception ex) {
                Log.e(TAG, "Critical error during fallback: " + ex.getMessage());
            }
        }
    }

    private void navigateToLogin() {
        try {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to login: " + e.getMessage());
        }
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        try {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Exit App");
            builder.setMessage("Are you sure you want to exit?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                try {
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error finishing activity: " + e.getMessage());
                }
            });
            builder.setNegativeButton("No", null);
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing exit dialog: " + e.getMessage());
            super.onBackPressed();
        }
    }
}