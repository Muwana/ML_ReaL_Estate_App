package com.ml.mlreal_estate.models;

public class User {
    private String userId;
    private String fullName;
    private String email;
    private String userType;
    private String createdAt;

    public User() {}

    public User(String userId, String fullName, String email, String userType, String createdAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.userType = userType;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getId() {
        return "";
    }
}