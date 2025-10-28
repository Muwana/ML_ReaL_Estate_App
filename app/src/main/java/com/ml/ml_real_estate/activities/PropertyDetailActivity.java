package com.ml.ml_real_estate.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ml.ml_real_estate.R;

public class PropertyDetailActivity extends AppCompatActivity {

    private ImageView ivPropertyImage, ivFavorite;
    private TextView tvTitle, tvPrice, tvLocation, tvDescription, tvBedrooms, tvBathrooms, tvArea;
    private Button btnContactSeller, btnScheduleTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        initializeViews();
        loadPropertyData();
        setupClickListeners();
    }

    private void initializeViews() {
        ivPropertyImage = findViewById(R.id.ivPropertyImage);
        ivFavorite = findViewById(R.id.ivFavorite);
        tvTitle = findViewById(R.id.tvTitle);
        tvPrice = findViewById(R.id.tvPrice);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescription = findViewById(R.id.tvDescription);
        tvBedrooms = findViewById(R.id.tvBedrooms);
        tvBathrooms = findViewById(R.id.tvBathrooms);
        tvArea = findViewById(R.id.tvArea);
        btnContactSeller = findViewById(R.id.btnContactSeller);
        btnScheduleTour = findViewById(R.id.btnScheduleTour);
    }

    private void loadPropertyData() {
        // Get data from intent
        String title = getIntent().getStringExtra("property_title");
        double price = getIntent().getDoubleExtra("property_price", 0);
        String location = getIntent().getStringExtra("property_location");
        String description = getIntent().getStringExtra("property_description");
        int bedrooms = getIntent().getIntExtra("property_bedrooms", 0);
        int bathrooms = getIntent().getIntExtra("property_bathrooms", 0);
        double area = getIntent().getDoubleExtra("property_area", 0);

        // Set data to views
        tvTitle.setText(title);
        tvPrice.setText(String.format("$%,.0f", price));
        tvLocation.setText(location);
        tvDescription.setText(description);
        tvBedrooms.setText(String.valueOf(bedrooms));
        tvBathrooms.setText(String.valueOf(bathrooms));
        tvArea.setText(String.format("%.0f sq ft", area));

        // Set favorite icon (you can load from shared preferences)
        ivFavorite.setImageResource(R.drawable.ic_favorite_border);
    }

    private void setupClickListeners() {
        ivFavorite.setOnClickListener(v -> {
            // Toggle favorite
            Toast.makeText(this, "Favorite clicked", Toast.LENGTH_SHORT).show();
        });

        btnContactSeller.setOnClickListener(v -> {
            Toast.makeText(this, "Contact seller feature coming soon", Toast.LENGTH_SHORT).show();
        });

        btnScheduleTour.setOnClickListener(v -> {
            Toast.makeText(this, "Schedule tour feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }
}
