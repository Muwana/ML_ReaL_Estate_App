package com.ml.ml_real_estate;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.ml.ml_real_estate.models.User;
import com.ml.ml_real_estate.utils.SharedPreferencesManager;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvUserType, tvMemberSince;
    private SharedPreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        initializeViews();
        loadUserData();
    }

    private void initializeViews() {
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvUserType = findViewById(R.id.tvUserType);
        tvMemberSince = findViewById(R.id.tvMemberSince);
        prefManager = new SharedPreferencesManager(this);
    }

    private void loadUserData() {
        User user = prefManager.getUser();
        if (user != null) {
            tvName.setText(user.getFullName());
            tvEmail.setText(user.getEmail());
            tvUserType.setText(user.getUserType());
            tvMemberSince.setText("Member since: " + user.getCreatedAt());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}