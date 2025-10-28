package com.ml.mlreal_estate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.ml.mlreal_estate.utils.PermissionUtils;
import com.ml.mlreal_estate.R;
import java.util.ArrayList;
import java.util.List;

public class AddPropertyActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private EditText etTitle, etDescription, etPrice, etLocation, etBedrooms, etBathrooms, etArea;
    private Spinner spinnerPropertyType;
    private Button btnAddImages, btnTakePhoto, btnSubmitProperty;
    private ImageView ivPropertyPreview;

    private List<Uri> selectedImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        initializeViews();
        setupSpinner();
        setupClickListeners();
    }

    private void initializeViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etLocation = findViewById(R.id.etLocation);
        etBedrooms = findViewById(R.id.etBedrooms);
        etBathrooms = findViewById(R.id.etBathrooms);
        etArea = findViewById(R.id.etArea);
        spinnerPropertyType = findViewById(R.id.spinnerPropertyType);
        btnAddImages = findViewById(R.id.btnAddImages);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSubmitProperty = findViewById(R.id.btnSubmitProperty);
        ivPropertyPreview = findViewById(R.id.ivPropertyPreview);
    }

    private void setupSpinner() {
        String[] propertyTypes = {"Apartment", "House", "Condo", "Townhouse", "Villa", "Commercial"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, propertyTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPropertyType.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnAddImages.setOnClickListener(v -> openGallery());
        btnTakePhoto.setOnClickListener(v -> openCamera());
        btnSubmitProperty.setOnClickListener(v -> submitProperty());
    }

    private void openGallery() {
        if (PermissionUtils.hasStoragePermission(this)) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else {
            PermissionUtils.requestStoragePermission(this);
        }
    }

    private void openCamera() {
        if (PermissionUtils.hasCameraPermission(this)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST);
        } else {
            PermissionUtils.requestCameraPermission(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                if (data.getClipData() != null) {
                    // Multiple images selected
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        selectedImages.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    // Single image selected
                    Uri imageUri = data.getData();
                    selectedImages.add(imageUri);
                }
                updateImagePreview();

            } else if (requestCode == CAMERA_REQUEST && data != null) {
                // Camera image captured
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    selectedImages.add(imageUri);
                    updateImagePreview();
                }
            }
        }
    }

    private void updateImagePreview() {
        if (!selectedImages.isEmpty()) {
            ivPropertyPreview.setImageURI(selectedImages.get(0));
            btnAddImages.setText("Add More Images (" + selectedImages.size() + ")");
        }
    }

    private void submitProperty() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String bedroomsStr = etBedrooms.getText().toString().trim();
        String bathroomsStr = etBathrooms.getText().toString().trim();
        String areaStr = etArea.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Please enter property title");
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Please enter property price");
            return;
        }

        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Please enter property location");
            return;
        }

        // Validate and create property
        try {
            double price = Double.parseDouble(priceStr);
            int bedrooms = Integer.parseInt(bedroomsStr);
            int bathrooms = Integer.parseInt(bathroomsStr);
            double area = Double.parseDouble(areaStr);

            // TODO: Upload images and create property via API
            Toast.makeText(this, "Property submitted successfully!", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionUtils.handlePermissionResult(requestCode, permissions, grantResults)) {
            switch (requestCode) {
                case PermissionUtils.CAMERA_PERMISSION_CODE:
                    openCamera();
                    break;
                case PermissionUtils.STORAGE_PERMISSION_CODE:
                    openGallery();
                    break;
            }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
