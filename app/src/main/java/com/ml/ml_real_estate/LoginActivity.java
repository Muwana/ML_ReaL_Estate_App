package com.ml.ml_real_estate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ml.ml_real_estate.api.ApiService;
import com.ml.ml_real_estate.api.RetrofitClient;
import com.ml.ml_real_estate.models.LoginResponse;
import com.ml.ml_real_estate.models.User;
import com.ml.ml_real_estate.utils.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin, btnMLConnect;
    private TextView tvSignUp;
    private ApiService apiService;
    private SharedPreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        prefManager = new SharedPreferencesManager(this);
        if (prefManager.isLoggedIn()) {
            navigateToDashboard(prefManager.getUserType());
            return;
        }

        initializeViews();
        setupApiService();
        setupClickListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnMLConnect = findViewById(R.id.btnMLConnect);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void setupApiService() {
        apiService = RetrofitClient.getApiService();
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvSignUp.setOnClickListener(v -> navigateToSignUp());
        btnMLConnect.setOnClickListener(v -> connectToMLRealEstate());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInputs(email, password)) {
            return;
        }

        // Show loading
        setLoginButtonState(false, "Signing In...");

        // Check network first
        if (!RetrofitClient.isNetworkAvailable(this)) {
            setLoginButtonState(true, "Sign In");
            Toast.makeText(this, "No internet connection. Please check your network and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        // Use real API login
        performApiLogin(email, password);
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Please enter your email");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Please enter your password");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void performApiLogin(String email, String password) {
        ApiService.LoginRequest loginRequest = new ApiService.LoginRequest(email, password);
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoginButtonState(true, "Sign In");

                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessfulLogin(response.body());
                } else {
                    handleFailedLogin(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoginButtonState(true, "Sign In");
                handleNetworkFailure(t);
            }
        });
    }

    private void handleSuccessfulLogin(LoginResponse loginResponse) {
        if (loginResponse.isSuccess()) {
            User user = loginResponse.getUser();
            if (user != null) {
                String userType = user.getUserType();
                prefManager.setUserData(user, loginResponse.getToken());
                prefManager.setLogin(true);

                Toast.makeText(LoginActivity.this,
                        "Login successful! Welcome " + user.getFullName(),
                        Toast.LENGTH_SHORT).show();
                navigateToDashboard(userType);
            } else {
                Toast.makeText(LoginActivity.this,
                        "Invalid user data received from server",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this,
                    loginResponse.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFailedLogin(Response<LoginResponse> response) {
        String errorMessage = "Login failed. Please try again.";

        switch (response.code()) {
            case 401:
                errorMessage = "Invalid email or password";
                break;
            case 404:
                errorMessage = "User not found. Please sign up first.";
                break;
            case 500:
                errorMessage = "Server error. Please try again later.";
                break;
            case 400:
                errorMessage = "Bad request. Please check your input.";
                break;
            default:
                if (response.code() == 0) {
                    errorMessage = "Cannot connect to server. Please check:\n" +
                            "1. Server is running\n" +
                            "2. Correct IP address\n" +
                            "3. Network connection";
                } else {
                    errorMessage = "Login failed. Error code: " + response.code();
                }
                break;
        }

        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Login HTTP Error: " + response.code() + " - " + response.message());

        // Log response body if available
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
        Log.e(TAG, "Login API call failed: " + t.getMessage());

        String errorMessage = "Network error: " + t.getMessage();

        if (t instanceof java.net.SocketTimeoutException) {
            errorMessage = "Connection timeout. Please check:\n" +
                    "• Server is running\n" +
                    "• Network connection is stable\n" +
                    "• Firewall is not blocking the connection";
        } else if (t instanceof java.net.ConnectException) {
            errorMessage = "Cannot connect to server. Please check:\n" +
                    "• Server IP address: " + RetrofitClient.getBaseUrl() + "\n" +
                    "• Server is running\n" +
                    "• Port is not blocked";
        } else if (t instanceof java.net.UnknownHostException) {
            errorMessage = "Server not found. Please check:\n" +
                    "• Internet connection\n" +
                    "• Server URL is correct\n" +
                    "• DNS settings";
        }

        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void setLoginButtonState(boolean enabled, String text) {
        runOnUiThread(() -> {
            btnLogin.setText(text);
            btnLogin.setEnabled(enabled);
        });
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void connectToMLRealEstate() {
        Intent intent = new Intent(this, MLWebViewActivity.class);
        startActivity(intent);
    }

    private void navigateToDashboard(String userType) {
        Intent intent;
        switch (userType.toLowerCase()) {
            case "admin":
                intent = new Intent(this, AdminDashboardActivity.class);
                break;
            case "seller":
                intent = new Intent(this, SellerDashboardActivity.class);
                break;
            case "buyer":
            case "user":
                intent = new Intent(this, BuyerDashboardActivity.class);
                break;
            default:
                intent = new Intent(this, BuyerDashboardActivity.class);
                break;
        }
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}