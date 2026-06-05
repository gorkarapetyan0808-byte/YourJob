package com.example.yourjob;

import android.content.Context;
import android.content.SharedPreferences;
<<<<<<< HEAD
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

<<<<<<< HEAD
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
public class BusinessManager {

    private static final String PREF_NAME = "business_profile";

<<<<<<< HEAD
    public interface SaveCompleteListener {
        void onSaveComplete(boolean success);
    }

    public static void save(final Context context, final String name, final String phone, final String email, Uri logoUri, final String city, final String field, final SaveCompleteListener listener) {
        final String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            if (listener != null) listener.onSaveComplete(false);
            return;
        }

        if (logoUri != null) {
            String uriStr = logoUri.toString();
            if (uriStr.startsWith("data:image")) {
                saveToDatabaseAndLocal(context, userId, name, phone, email, uriStr, city, field, listener);
                return;
            }

            try {
                InputStream inputStream = context.getContentResolver().openInputStream(logoUri);
                if (inputStream == null) {
                    saveToDatabaseAndLocal(context, userId, name, phone, email, "", city, field, listener);
                    return;
                }
                
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                
                if (bitmap == null) {
                    saveToDatabaseAndLocal(context, userId, name, phone, email, "", city, field, listener);
                    return;
                }

                int maxWidth = 500;
                int maxHeight = 500;
                if (bitmap.getWidth() > maxWidth || bitmap.getHeight() > maxHeight) {
                    float ratio = Math.min((float) maxWidth / bitmap.getWidth(), (float) maxHeight / bitmap.getHeight());
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * ratio), (int) (bitmap.getHeight() * ratio), true);
                }

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);
                byte[] bytes = output.toByteArray();

                String base64Image = "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP);
                
                saveToDatabaseAndLocal(context, userId, name, phone, email, base64Image, city, field, listener);

            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) listener.onSaveComplete(false);
            }
        } else {
            saveToDatabaseAndLocal(context, userId, name, phone, email, "", city, field, listener);
        }
    }

    private static void saveToDatabaseAndLocal(Context context, String userId, String name, String phone, String email, String logoData, String city, String field, final SaveCompleteListener listener) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("logo", logoData);
=======
    public static void save(Context context, String name, String phone, String email, String logoUri, String city, String field) {
        // 1. Save locally
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("logo", logoUri);
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        editor.putString("city", city);
        editor.putString("field", field);
        editor.apply();

<<<<<<< HEAD
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();
        
        mDatabase.child("businesses").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Business existing = snapshot.getValue(Business.class);
                boolean approved = existing != null && existing.isApproved;
                String reason = existing != null ? existing.rejectionReason : "";

                Business business = new Business(userId, name, phone, email, logoData, city, field);
                business.isApproved = approved;
                business.rejectionReason = reason;

                mDatabase.child("businesses").child(userId).setValue(business).addOnCompleteListener(task -> {
                    if (listener != null) listener.onSaveComplete(task.isSuccessful());
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { if (listener != null) listener.onSaveComplete(false); }
        });
    }

    public static void loadFromFirebase(final Context context, final Runnable onComplete) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            if (onComplete != null) onComplete.run();
            return;
        }
=======
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
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82

        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();
        mDatabase.child("businesses").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Business business = snapshot.getValue(Business.class);
<<<<<<< HEAD
                    if (business != null) saveLocally(context, business);
                }
                if (onComplete != null) onComplete.run();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { if (onComplete != null) onComplete.run(); }
=======
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
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
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
<<<<<<< HEAD
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference()
                    .child("businesses").child(userId).removeValue();
=======

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();
            mDatabase.child("businesses").child(userId).removeValue();
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        }
    }

    public static String get(Context context, String key) {
<<<<<<< HEAD
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(key, "");
    }
=======
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
    public static String getName(Context context) { return get(context, "name"); }
    public static String getPhone(Context context) { return get(context, "phone"); }
    public static String getEmail(Context context) { return get(context, "email"); }
    public static String getLogo(Context context) { return get(context, "logo"); }
    public static String getCity(Context context) { return get(context, "city"); }
    public static String getField(Context context) { return get(context, "field"); }
<<<<<<< HEAD
}
=======
}
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
