package com.ml.ml_real_estate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.ml.ml_real_estate.utils.PermissionUtils;
import com.ml.ml_real_estate.utils.SharedPreferencesManager;
import com.ml.ml_real_estate.R;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private EditText etFullName, etEmail, etPhone, etBio;
    private Button btnUpdateProfile, btnChangePhoto;
    private ImageView ivProfilePhoto;

    private SharedPreferencesManager prefManager;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        prefManager = new SharedPreferencesManager(this);
        initializeViews();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etBio = findViewById(R.id.etBio);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
    }

    private void loadUserData() {
        etFullName.setText(prefManager.getUserName());
        etEmail.setText(prefManager.getUserEmail());
        // Load other user data from API/database
    }

    private void setupClickListeners() {
        btnChangePhoto.setOnClickListener(v -> showImageSourceDialog());
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    private void showImageSourceDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Photo");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    openCamera();
                    break;
                case 1:
                    openGallery();
                    break;
            }
        });
        builder.show();
    }

    private void openGallery() {
        if (PermissionUtils.hasStoragePermission(this)) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                selectedImageUri = data.getData();
                ivProfilePhoto.setImageURI(selectedImageUri);

            } else if (requestCode == CAMERA_REQUEST && data != null) {
                selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    ivProfilePhoto.setImageURI(selectedImageUri);
                }
            }
        }
    }

    private void updateProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Please enter your full name");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Please enter your email");
            return;
        }

        // TODO: Update profile via API
        // Upload profile photo if selected
        if (selectedImageUri != null) {
            // Upload image to server
        }

        // Update local preferences
        // prefManager.updateUserData(updatedUser);

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
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