package com.example.yourjob;

import android.content.Context;
import android.content.SharedPreferences;

public class UserProfileManager {

    private static final String PREF_NAME = "user_profile";

    public static void save(Context context, String name, String city, String age, String field) {

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("name", name);
        editor.putString("city", city);
        editor.putString("age", age);
        editor.putString("field", field);

        editor.apply();
    }

    public static String get(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }
}