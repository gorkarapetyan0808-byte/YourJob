package com.example.yourjob;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BusinessManager {

    private static final String PREF_NAME = "business_profile";

    public static void save(Context context, String name, String phone, String email, String logoUri, String city, String field) {
        // 1. Save locally
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("logo", logoUri);
        editor.putString("city", city);
        editor.putString("field", field);
        editor.apply();

        // 2. Save to Firebase
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();
            Business business = new Business(userId, name, phone, email, logoUri, city, field);
            mDatabase.child("businesses").child(userId).setValue(business);
        }
    }

    public static void loadFromFirebase(Context context, Runnable onComplete) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();
        mDatabase.child("businesses").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Business business = snapshot.getValue(Business.class);
                    if (business != null) {
                        saveLocally(context, business);
                    }
                }
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (onComplete != null) onComplete.run();
            }
        });
    }

    private static void saveLocally(Context context, Business b) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", b.name);
        editor.putString("phone", b.phone);
        editor.putString("email", b.email);
        editor.putString("logo", b.logoUri);
        editor.putString("city", b.city);
        editor.putString("field", b.field);
        editor.apply();
    }

    public static void delete(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();
            mDatabase.child("businesses").child(userId).removeValue();
        }
    }

    public static String get(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static String getName(Context context) { return get(context, "name"); }
    public static String getPhone(Context context) { return get(context, "phone"); }
    public static String getEmail(Context context) { return get(context, "email"); }
    public static String getLogo(Context context) { return get(context, "logo"); }
    public static String getCity(Context context) { return get(context, "city"); }
    public static String getField(Context context) { return get(context, "field"); }
}
