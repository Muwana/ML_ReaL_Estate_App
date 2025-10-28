package com.ml.ml_real_estate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ml.ml_real_estate.adapters.SellerPagerAdapter;
import com.ml.ml_real_estate.utils.SharedPreferencesManager;

public class SellerDashboardActivity extends AppCompatActivity {

    private static final String TAG = "SellerDashboardActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private SharedPreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_seller_dashboard);
            Log.d(TAG, "SellerDashboardActivity created successfully");

            prefManager = new SharedPreferencesManager(this);
            initializeViews();
            setupNavigationDrawer();
            setupViewPagerAndTabs();
            setupHeader();

        } catch (Exception e) {
            Log.e(TAG, "Error in SellerDashboardActivity onCreate: " + e.getMessage());
            Toast.makeText(this, "Error loading seller dashboard", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            // Go back to login on error
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initializeViews() {
        try {
            drawerLayout = findViewById(R.id.drawerLayout);
            navigationView = findViewById(R.id.navigationView);
            tabLayout = findViewById(R.id.tabLayout);
            viewPager = findViewById(R.id.viewPager);

            // Set up toolbar
            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
                getSupportActionBar().setTitle("Seller Dashboard");
            }

            Log.d(TAG, "All views initialized successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            throw e; // Re-throw to be caught in onCreate
        }
    }

    private void setupNavigationDrawer() {
        if (navigationView == null) {
            Log.e(TAG, "NavigationView is null");
            return;
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                // Already on dashboard
            } else if (id == R.id.nav_add_property) {
                navigateToAddProperty();
            } else if (id == R.id.nav_my_properties) {
                if (viewPager != null) viewPager.setCurrentItem(0);
            } else if (id == R.id.nav_inquiries) {
                if (viewPager != null) viewPager.setCurrentItem(2);
            } else if (id == R.id.nav_statistics) {
                if (viewPager != null) viewPager.setCurrentItem(1);
            } else if (id == R.id.nav_profile) {
                if (viewPager != null) viewPager.setCurrentItem(3);
            } else if (id == R.id.nav_ml_website) {
                connectToMLRealEstate();
            } else if (id == R.id.nav_settings) {
                openSettings();
            } else if (id == R.id.nav_logout) {
                logout();
            }

            if (drawerLayout != null) {
                drawerLayout.closeDrawer(navigationView);
            }
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout != null) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPagerAndTabs() {
        try {
            SellerPagerAdapter adapter = new SellerPagerAdapter(this);
            viewPager.setAdapter(adapter);

            // Set up tab titles and icons
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText("Listings");
                        tab.setIcon(R.drawable.ic_properties);
                        break;
                    case 1:
                        tab.setText("Stats");
                        tab.setIcon(R.drawable.ic_statictics);
                        break;
                    case 2:
                        tab.setText("Inquiries");
                        tab.setIcon(R.drawable.ic_message);
                        break;
                    case 3:
                        tab.setText("Profile");
                        tab.setIcon(R.drawable.ic_profile);
                        break;
                }
            }).attach();

        } catch (Exception e) {
            Log.e(TAG, "Error setting up view pager: " + e.getMessage());
            Toast.makeText(this, "Error setting up tabs", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupHeader() {
        if (navigationView == null) return;

        try {
            View headerView = navigationView.getHeaderView(0);
            if (headerView != null) {
                TextView tvUserName = headerView.findViewById(R.id.tvUserName);
                TextView tvUserEmail = headerView.findViewById(R.id.tvUserEmail);
                TextView tvUserType = headerView.findViewById(R.id.tvUserType);

                if (tvUserName != null) {
                    String userName = prefManager.getUserName();
                    tvUserName.setText(userName != null ? userName : "Seller");
                }
                if (tvUserEmail != null) {
                    String userEmail = prefManager.getUserEmail();
                    tvUserEmail.setText(userEmail != null ? userEmail : "seller@example.com");
                }
                if (tvUserType != null) {
                    tvUserType.setText("Professional Seller");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up header: " + e.getMessage());
        }
    }

    private void navigateToAddProperty() {
        try {
            Intent intent = new Intent(this, AddPropertyActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to AddProperty: " + e.getMessage());
            Toast.makeText(this, "Error opening add property", Toast.LENGTH_SHORT).show();
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

    private void openSettings() {
        Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> performLogout());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void performLogout() {
        try {
            prefManager.clearAll();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("LOGOUT", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: " + e.getMessage());
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }
}