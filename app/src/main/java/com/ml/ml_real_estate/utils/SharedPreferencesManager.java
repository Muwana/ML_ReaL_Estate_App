package com.ml.ml_real_estate.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ml.ml_real_estate.models.User;

public class SharedPreferencesManager {

    private static final String TAG = "SharedPreferencesManager";
    private static final String PREF_NAME = "MLRealEstatePref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_TOKEN = "token";

    private final SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private SharedPreferences sharedPreferences;

    public SharedPreferencesManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.commit();
    }

    public void setUserData(User user, String token) {
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getFullName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_TYPE, user.getUserType());
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    public String getUserType() {
        return pref.getString(KEY_USER_TYPE, "user");
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public void clearUserData() {
        editor.clear();
        editor.commit();
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }

    public User getUser() {
        return null;
    }

    public boolean isLoggedIn() {
        try {
            return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        } catch (Exception e) {
            Log.e(TAG, "Error in isLoggedIn(): " + e.getMessage());
            return false;
        }
    }

    public void clearAll() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(KEY_IS_LOGGED_IN);
            editor.remove(KEY_USER_ID);
            editor.remove(KEY_USER_NAME);
            editor.remove(KEY_USER_EMAIL);
            editor.remove(KEY_USER_TYPE);
            editor.remove(KEY_TOKEN);
            boolean success = editor.commit(); // Use commit() for immediate write

            Log.d(TAG, "clearAll() - Success: " + success);
            Log.d(TAG, "After clear - isLoggedIn: " + isLoggedIn());

        } catch (Exception e) {
            Log.e(TAG, "Error in clearAll(): " + e.getMessage());
            e.printStackTrace();
        }
    }

}

