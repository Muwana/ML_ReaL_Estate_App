package com.ml.ml_real_estate.api;

public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String userType;

    // Default constructor
    public RegisterRequest() {
    }

    // Parameterized constructor
    public RegisterRequest(String fullName, String email, String password, String userType) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    // Getters and setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}