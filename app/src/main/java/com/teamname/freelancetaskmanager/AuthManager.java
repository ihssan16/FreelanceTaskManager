package com.teamname.freelancetaskmanager;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {

    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_EMAIL = "user_email";

    // ✅ TWO parameters (Context + email)
    public static void login(Context context, String email) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        prefs.edit()
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public static void logout(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        prefs.edit().clear().apply();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return prefs.contains(KEY_EMAIL);
    }
}
