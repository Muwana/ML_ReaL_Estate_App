package com.ml.mlreal_estate.api;

import com.ml.mlreal_estate.models.LoginResponse;
import com.ml.mlreal_estate.models.PropertiesResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    // Authentication endpoints
    @POST("register.php")
    Call<LoginResponse> register(@Body RegisterRequest registerRequest);

    @POST("login.php")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // Property endpoints
    @GET("properties.php")
    Call<PropertiesResponse> getProperties(@Header("Authorization") String authorization);

    // Favorites endpoints - REMOVED DUPLICATE METHODS
    @POST("favorites.php")
    Call<Void> addToFavorites(@Header("Authorization") String authorization, @Body FavoriteRequest request);

    @DELETE("favorites.php")
    Call<Void> removeFromFavorites(@Header("Authorization") String authorization, @Body FavoriteRequest request);

    Call<Void> addToFavorites(String authorization, int id);

    Call<Void> removeFromFavorites(String authorization, int id);


    // Request classes
    class RegisterRequest {
        private String fullName;
        private String email;
        private String password;
        private String userType;

        public RegisterRequest(String fullName, String email, String password, String userType) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
            this.userType = userType;
        }

        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getUserType() { return userType; }
    }

    class LoginRequest {
        private String email;
        private String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }

    class FavoriteRequest {
        private String propertyId;

        public FavoriteRequest(String propertyId) {
            this.propertyId = propertyId;
        }

        public String getPropertyId() { return propertyId; }
    }
}