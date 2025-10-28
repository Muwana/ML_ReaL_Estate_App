package com.ml.ml_real_estate.models;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private String dashboardUrl;
    private User user;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(boolean success, String message, String token, String dashboardUrl, User user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.dashboardUrl = dashboardUrl;
        this.user = user;
    }

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getDashboardUrl() { return dashboardUrl; }
    public void setDashboardUrl(String dashboardUrl) { this.dashboardUrl = dashboardUrl; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}