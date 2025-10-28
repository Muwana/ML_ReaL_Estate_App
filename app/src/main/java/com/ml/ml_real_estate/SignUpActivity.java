package com.ml.ml_real_estate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ml.ml_real_estate.api.ApiService;
import com.ml.ml_real_estate.api.RetrofitClient;
import com.ml.ml_real_estate.models.LoginResponse;
import com.ml.ml_real_estate.models.User;
import com.ml.ml_real_estate.utils.SharedPreferencesManager;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
    private RadioGroup rgUserType;
    private Button btnSignUp;
    private TextView tvSignIn;
    private ApiService apiService;
    private SharedPreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        prefManager = new SharedPreferencesManager(this);
        initializeViews();
        setupApiService();
        setupClickListeners();
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        rgUserType = findViewById(R.id.rgUserType);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);

        // Set default user type to "user" (Buyer)
        rgUserType.check(R.id.rbUser);
    }

    private void setupApiService() {
        apiService = RetrofitClient.getApiService();
    }

    private void setupClickListeners() {
        btnSignUp.setOnClickListener(v -> attemptSignUp());
        tvSignIn.setOnClickListener(v -> navigateToLogin());

        setupPasswordValidation();
    }

    private void setupPasswordValidation() {
        etConfirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePasswordMatch();
            }
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !TextUtils.isEmpty(etConfirmPassword.getText())) {
                validatePasswordMatch();
            }
        });
    }

    private void validatePasswordMatch() {
        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();

        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) &&
                !password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
        } else {
            etConfirmPassword.setError(null);
        }
    }

    private void attemptSignUp() {
        String fullName = Objects.requireNonNull(etFullName.getText()).toString().trim();
        String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();

        // Get selected user type
        int selectedId = rgUserType.getCheckedRadioButtonId();
        String userType = getUserTypeFromRadioId(selectedId);

        // Validation
        if (!validateForm(fullName, email, password, confirmPassword, userType)) {
            return;
        }

        // Show loading
        btnSignUp.setText("Creating Account...");
        btnSignUp.setEnabled(false);

        // Check network first
        if (!RetrofitClient.isNetworkAvailable(this)) {
            btnSignUp.setText("Create Account");
            btnSignUp.setEnabled(true);
            Toast.makeText(this, "No internet connection. Please check your network and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create registration request
        ApiService.RegisterRequest registerRequest = new ApiService.RegisterRequest(fullName, email, password, userType);
        Call<LoginResponse> call = apiService.register(registerRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnSignUp.setText("Create Account");
                btnSignUp.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        // Save user data and token
                        prefManager.setUserData(loginResponse.getUser(), loginResponse.getToken());
                        prefManager.setLogin(true);

                        Toast.makeText(SignUpActivity.this, "Account created successfully! Welcome " + fullName, Toast.LENGTH_SHORT).show();
                        navigateToDashboard(userType);
                    } else {
                        Toast.makeText(SignUpActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleFailedSignUp(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnSignUp.setText("Create Account");
                btnSignUp.setEnabled(true);
                handleNetworkFailure(t);
            }
        });
    }

    private void handleFailedSignUp(Response<LoginResponse> response) {
        String errorMessage = "Registration failed. Please try again.";

        switch (response.code()) {
            case 400:
                errorMessage = "Email already exists. Please use a different email.";
                break;
            case 500:
                errorMessage = "Server error. Please try again later.";
                break;
            default:
                if (response.code() == 0) {
                    errorMessage = "Cannot connect to server. Please check:\n" +
                            "1. Server is running\n" +
                            "2. Correct IP address\n" +
                            "3. Network connection";
                } else {
                    errorMessage = "Registration failed. Error code: " + response.code();
                }
                break;
        }

        Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Signup HTTP Error: " + response.code() + " - " + response.message());

        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.e(TAG, "Error response body: " + errorBody);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading error body: " + e.getMessage());
        }
    }

    private void handleNetworkFailure(Throwable t) {
        Log.e(TAG, "Signup API call failed: " + t.getMessage());

        String errorMessage = "Network error: " + t.getMessage();

        if (t instanceof java.net.SocketTimeoutException) {
            errorMessage = "Connection timeout. Please check server connection.";
        } else if (t instanceof java.net.ConnectException) {
            errorMessage = "Cannot connect to server at: " + RetrofitClient.getBaseUrl();
        } else if (t instanceof java.net.UnknownHostException) {
            errorMessage = "Server not found. Please check network connection.";
        }

        Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private boolean validateForm(String fullName, String email, String password, String confirmPassword, String userType) {
        boolean isValid = true;

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Please enter your full name");
            etFullName.requestFocus();
            isValid = false;
        } else {
            etFullName.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Please enter your email");
            etEmail.requestFocus();
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            isValid = false;
        } else {
            etEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Please enter your password");
            etPassword.requestFocus();
            isValid = false;
        } else if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            isValid = false;
        } else {
            etPassword.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            isValid = false;
        } else {
            etConfirmPassword.setError(null);
        }

        return isValid;
    }

    private String getUserTypeFromRadioId(int radioId) {
        if (radioId == R.id.rbSeller) {
            return "seller";
        } else if (radioId == R.id.rbAdmin) {
            return "admin";
        } else {
            return "user"; // This will be treated as buyer
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void navigateToDashboard(String userType) {
        try {
            Log.d(TAG, "Navigating to dashboard for user type: " + userType);

            Intent intent;
            switch (userType.toLowerCase()) {
                case "admin":
                    intent = new Intent(this, AdminDashboardActivity.class);
                    break;
                case "seller":
                    intent = new Intent(this, SellerDashboardActivity.class);
                    break;
                case "buyer": // Treat "user" as buyer
                default:
                    intent = new Intent(this, BuyerDashboardActivity.class);
                    break;
            }

            // Add flags to clear the back stack
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to dashboard: " + e.getMessage());
            Toast.makeText(this, "Error opening dashboard: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();

            // Fallback to login screen
            Intent fallbackIntent = new Intent(this, LoginActivity.class);
            startActivity(fallbackIntent);
            finish();
        }
    }

}