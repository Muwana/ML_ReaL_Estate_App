package com.ml.ml_real_estate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ml.ml_real_estate.adapters.DashboardPagerAdapter;
import com.ml.ml_real_estate.utils.SharedPreferencesManager;

public class UserDashboardActivity extends AppCompatActivity {
    private static final String TAG = "UserDashboardActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private SharedPreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dashboard);

        Log.d(TAG, "UserDashboardActivity onCreate started");

        prefManager = new SharedPreferencesManager(this);

        // Check if user data is available
        if (prefManager.getUserName() == null || prefManager.getUserEmail() == null) {
            Log.e(TAG, "User data missing, redirecting to login");
            navigateToLogin();
            return;
        }

        initializeViews();
        setupNavigation();
        setupViewPagerSafeMode(); // Using safe mode
        setupHeader();

        Log.d(TAG, "UserDashboardActivity setup completed");
    }

    private void initializeViews() {
        Log.d(TAG, "Initializing views");

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Debug view initialization
        if (drawerLayout == null) Log.e(TAG, "drawerLayout is null");
        if (navigationView == null) Log.e(TAG, "navigationView is null");
        if (tabLayout == null) Log.e(TAG, "tabLayout is null");
        if (viewPager == null) Log.e(TAG, "viewPager is null");

        Log.d(TAG, "Views initialized successfully");
    }

    private void setupNavigation() {
        Log.d(TAG, "Setting up navigation");

        View toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setOnClickListener(v -> {
                Log.d(TAG, "Toolbar clicked, opening drawer");
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(navigationView);
                }
            });
        } else {
            Log.e(TAG, "Toolbar not found");
        }

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    Log.d(TAG, "Navigation item selected: " + id);

                    if (id == R.id.nav_profile) {
                        Log.d(TAG, "Profile navigation selected");
                        if (viewPager != null) {
                            viewPager.setCurrentItem(3);
                        }
                    } else if (id == R.id.nav_ml_website) {
                        Log.d(TAG, "ML Website navigation selected");
                        connectToMLRealEstate();
                    } else if (id == R.id.nav_settings) {
                        Log.d(TAG, "Settings navigation selected");
                        showToast("Settings - Coming Soon");
                    } else if (id == R.id.nav_logout) {
                        Log.d(TAG, "Logout navigation selected");
                        logout();
                    } else {
                        Log.w(TAG, "Unknown navigation item: " + id);
                    }

                    if (drawerLayout != null) {
                        drawerLayout.closeDrawers();
                    }
                    return true;
                }
            });
        }

        Log.d(TAG, "Navigation setup completed");
    }

    // ADD THIS METHOD TO YOUR UserDashboardActivity CLASS:
    private void setupViewPagerSafeMode() {
        Log.d(TAG, "🚀 setupViewPagerSafeMode() started");

        try {
            // Step 1: Verify critical views exist
            Log.d(TAG, "Step 1: Verifying ViewPager and TabLayout");
            if (viewPager == null) {
                Log.e(TAG, "❌ CRITICAL: viewPager is null - cannot proceed");
                showToast("Dashboard error: ViewPager not found");
                setupEmergencyFallback();
                return;
            }

            if (tabLayout == null) {
                Log.w(TAG, "⚠️ TabLayout is null - will proceed without tabs");
            }

            // Step 2: Create adapter with error handling
            Log.d(TAG, "Step 2: Creating DashboardPagerAdapter");
            DashboardPagerAdapter adapter;
            try {
                adapter = new DashboardPagerAdapter(this);
                Log.d(TAG, "✅ DashboardPagerAdapter created successfully");
            } catch (Exception adapterError) {
                Log.e(TAG, "❌ Error creating adapter: " + adapterError.getMessage(), adapterError);
                showToast("Dashboard loading issue");
                setupEmergencyFallback();
                return;
            }

            // Step 3: Set up ViewPager with the adapter
            Log.d(TAG, "Step 3: Setting up ViewPager with adapter");
            try {
                viewPager.setAdapter(adapter);
                Log.d(TAG, "✅ ViewPager adapter set successfully");
            } catch (Exception viewPagerError) {
                Log.e(TAG, "❌ Error setting ViewPager adapter: " + viewPagerError.getMessage(), viewPagerError);
                showToast("ViewPager setup failed");
                setupEmergencyFallback();
                return;
            }

            // Step 4: Add comprehensive page change listener for debugging
            Log.d(TAG, "Step 4: Setting up page change listener");
            try {
                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                        Log.d(TAG, "📱 Page scrolled - Position: " + position +
                                ", Offset: " + positionOffset);
                    }

                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        String pageName = getPageNameForPosition(position);
                        Log.d(TAG, "🎯 Page selected: " + position + " (" + pageName + ")");

                        // Special handling for each tab
                        switch (position) {
                            case 0:
                                Log.d(TAG, "📊 Properties tab activated");
                                break;
                            case 1:
                                Log.d(TAG, "⭐ Favorites tab activated - monitoring for crashes");
                                break;
                            case 2:
                                Log.d(TAG, "💬 Messages tab activated");
                                break;
                            case 3:
                                Log.d(TAG, "👤 Profile tab activated");
                                break;
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        super.onPageScrollStateChanged(state);
                        String stateName = getScrollStateName(state);
                        Log.d(TAG, "🔄 Page scroll state: " + stateName + " (" + state + ")");
                    }
                });
                Log.d(TAG, "✅ Page change listener registered");
            } catch (Exception listenerError) {
                Log.e(TAG, "⚠️ Error setting page change listener: " + listenerError.getMessage());
                // Non-critical error, continue
            }

            // Step 5: Set up TabLayout with extensive error handling
            Log.d(TAG, "Step 5: Setting up TabLayout");
            if (tabLayout != null) {
                try {
                    new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                        try {
                            Log.d(TAG, "🔖 Configuring tab for position: " + position);
                            String tabText = getTabTextForPosition(position);
                            tab.setText(tabText);
                            Log.d(TAG, "✅ Tab " + position + " configured as: " + tabText);
                        } catch (Exception tabError) {
                            Log.e(TAG, "❌ Error configuring tab " + position + ": " + tabError.getMessage());
                            tab.setText("Tab " + position); // Fallback text
                        }
                    }).attach();
                    Log.d(TAG, "✅ TabLayoutMediator attached successfully");

                    // Add tab selection listener for additional debugging
                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            Log.d(TAG, "👉 Tab selected: " + tab.getPosition() + " - " + tab.getText());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                            Log.d(TAG, "👈 Tab unselected: " + tab.getPosition());
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            Log.d(TAG, "🔁 Tab reselected: " + tab.getPosition());
                        }
                    });

                } catch (Exception tabError) {
                    Log.e(TAG, "❌ Error setting up TabLayout: " + tabError.getMessage(), tabError);
                    tabLayout.setVisibility(View.GONE);
                    showToast("Tabs disabled due to error");
                }
            } else {
                Log.w(TAG, "⚠️ TabLayout is null - skipping tab setup");
            }

            // Step 6: Set initial page with validation
            Log.d(TAG, "Step 6: Setting initial page");
            try {
                int initialPage = 0; // Start with Properties tab
                if (initialPage >= 0 && initialPage < adapter.getItemCount()) {
                    viewPager.setCurrentItem(initialPage, false);
                    Log.d(TAG, "✅ Initial page set to: " + initialPage + " (Properties)");
                } else {
                    Log.e(TAG, "❌ Invalid initial page: " + initialPage);
                    viewPager.setCurrentItem(0, false); // Force to first page
                }
            } catch (Exception pageError) {
                Log.e(TAG, "❌ Error setting initial page: " + pageError.getMessage());
            }

            // Step 7: Verify everything is working
            Log.d(TAG, "Step 7: Final verification");
            if (viewPager.getAdapter() != null) {
                Log.d(TAG, "🎉 SUCCESS - ViewPager setup completed!");
                Log.d(TAG, "📊 Adapter has " + viewPager.getAdapter().getItemCount() + " pages");
                showToast("Dashboard loaded successfully");
            } else {
                Log.e(TAG, "❌ FAILED - ViewPager adapter is null");
                showToast("Dashboard loading failed");
                setupEmergencyFallback();
            }

        } catch (Exception e) {
            Log.e(TAG, "💥 CRITICAL ERROR in setupViewPagerSafeMode: " + e.getMessage(), e);
            showToast("Dashboard error - using basic mode");
            setupEmergencyFallback();
        }

        Log.d(TAG, "🏁 setupViewPagerSafeMode() completed");
    }

    // Helper methods for setupViewPagerSafeMode
    private String getPageNameForPosition(int position) {
        switch (position) {
            case 0: return "Properties";
            case 1: return "Favorites";
            case 2: return "Messages";
            case 3: return "Profile";
            default: return "Unknown";
        }
    }

    private String getTabTextForPosition(int position) {
        switch (position) {
            case 0: return "Properties";
            case 1: return "Favorites";
            case 2: return "Messages";
            case 3: return "Profile";
            default: return "Tab " + position;
        }
    }

    private String getScrollStateName(int state) {
        switch (state) {
            case ViewPager2.SCROLL_STATE_IDLE: return "IDLE";
            case ViewPager2.SCROLL_STATE_DRAGGING: return "DRAGGING";
            case ViewPager2.SCROLL_STATE_SETTLING: return "SETTLING";
            default: return "UNKNOWN";
        }
    }

    private void setupEmergencyFallback() {
        Log.d(TAG, "🆘 Setting up emergency fallback interface");

        try {
            // Hide the original views
            if (tabLayout != null) {
                tabLayout.setVisibility(View.GONE);
                Log.d(TAG, "✅ TabLayout hidden");
            }
            if (viewPager != null) {
                viewPager.setVisibility(View.GONE);
                Log.d(TAG, "✅ ViewPager hidden");
            }

            // Create a simple emergency layout
            LinearLayout emergencyLayout = new LinearLayout(this);
            emergencyLayout.setOrientation(LinearLayout.VERTICAL);
            emergencyLayout.setGravity(Gravity.CENTER);
            emergencyLayout.setPadding(100, 100, 100, 100);
            emergencyLayout.setBackgroundColor(Color.WHITE);

            // Title
            TextView titleText = new TextView(this);
            titleText.setText("🏠 ML Real Estate");
            titleText.setTextSize(24);
            titleText.setTypeface(null, Typeface.BOLD);
            titleText.setGravity(Gravity.CENTER);
            titleText.setTextColor(Color.BLACK);

            // Subtitle
            TextView subtitleText = new TextView(this);
            subtitleText.setText("Emergency Mode");
            subtitleText.setTextSize(16);
            subtitleText.setGravity(Gravity.CENTER);
            subtitleText.setTextColor(Color.GRAY);
            subtitleText.setPadding(0, 10, 0, 30);

            // Message
            TextView messageText = new TextView(this);
            messageText.setText("The dashboard is temporarily unavailable.\n\n" +
                    "Available Features:\n" +
                    "• Properties\n" +
                    "• Favorites\n" +
                    "• Messages\n" +
                    "• Profile\n\n" +
                    "Please restart the app or contact support.");
            messageText.setTextSize(16);
            messageText.setGravity(Gravity.CENTER);
            messageText.setTextColor(Color.DKGRAY);
            messageText.setLineSpacing(1.2f, 1.2f);

            // Refresh button
            Button refreshButton = new Button(this);
            refreshButton.setText("🔄 Retry Dashboard");
            refreshButton.setBackgroundColor(Color.parseColor("#4CAF50"));
            refreshButton.setTextColor(Color.WHITE);
            refreshButton.setPadding(50, 20, 50, 20);
            refreshButton.setOnClickListener(v -> {
                Log.d(TAG, "🔄 Emergency retry button clicked");
                showToast("Retrying dashboard setup...");
                // Restart the activity
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });

            // Add views to layout
            emergencyLayout.addView(titleText);
            emergencyLayout.addView(subtitleText);
            emergencyLayout.addView(messageText);
            emergencyLayout.addView(refreshButton);

            // Replace content
            setContentView(emergencyLayout);
            Log.d(TAG, "✅ Emergency fallback interface setup completed");

        } catch (Exception fallbackError) {
            Log.e(TAG, "💥 CRITICAL: Even emergency fallback failed: " + fallbackError.getMessage());

            // Last resort - show a simple toast
            showToast("Critical app error - Please restart");
        }
    }

    private void setupHeader() {
        Log.d(TAG, "Setting up navigation header");

        try {
            if (navigationView != null) {
                View headerView = navigationView.getHeaderView(0);
                if (headerView == null) {
                    Log.e(TAG, "Navigation header view is null");
                    return;
                }

                TextView tvUserName = headerView.findViewById(R.id.tvUserName);
                TextView tvUserEmail = headerView.findViewById(R.id.tvUserEmail);
                TextView tvUserType = headerView.findViewById(R.id.tvUserType);

                // Set user data from SharedPreferences
                String userName = prefManager.getUserName();
                String userEmail = prefManager.getUserEmail();

                Log.d(TAG, "User data - Name: " + userName + ", Email: " + userEmail);

                if (tvUserName != null) {
                    tvUserName.setText(userName != null ? userName : "User");
                }
                if (tvUserEmail != null) {
                    tvUserEmail.setText(userEmail != null ? userEmail : "user@email.com");
                }
                if (tvUserType != null) {
                    tvUserType.setText("Buyer Account");
                }

                Log.d(TAG, "Navigation header setup completed");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up navigation header: " + e.getMessage(), e);
        }
    }

    private void connectToMLRealEstate() {
        Log.d(TAG, "Connecting to ML Real Estate website");
        try {
            Intent intent = new Intent(this, MLWebViewActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            Log.d(TAG, "MLWebViewActivity started successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error starting MLWebViewActivity: " + e.getMessage(), e);
            showToast("Cannot open website at the moment");
        }
    }

    private void logout() {
        Log.d(TAG, "Initiating logout process");

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Log.d(TAG, "User confirmed logout");

            // Clear user data
            prefManager.logout();
            Log.d(TAG, "User data cleared from SharedPreferences");

            Toast.makeText(UserDashboardActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Navigate to LoginActivity
            try {
                Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Log.d(TAG, "Navigated to LoginActivity");
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to LoginActivity: " + e.getMessage(), e);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            Log.d(TAG, "User cancelled logout");
            dialog.dismiss();
        });
        builder.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Toast shown: " + message);
    }

    private void navigateToLogin() {
        Log.d(TAG, "Navigating to login activity");
        try {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to login: " + e.getMessage(), e);
        }
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(navigationView)) {
            Log.d(TAG, "Back pressed - closing navigation drawer");
            drawerLayout.closeDrawer(navigationView);
        } else {
            Log.d(TAG, "Back pressed - exiting activity");
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "UserDashboardActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "UserDashboardActivity onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "UserDashboardActivity onDestroy");
    }
}