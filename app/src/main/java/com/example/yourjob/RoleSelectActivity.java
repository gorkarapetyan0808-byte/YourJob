package com.example.yourjob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RoleSelectActivity extends AppCompatActivity {

    private static final String TAG = "RoleSelectActivity";
    View personalBtn, businessBtn;
    Button setupLaterButton;
    DatabaseReference mDatabase;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_select);

        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();
        userId = FirebaseAuth.getInstance().getUid();

        personalBtn = findViewById(R.id.personalProfileClickArea);
        businessBtn = findViewById(R.id.businessProfileClickArea);
        setupLaterButton = findViewById(R.id.setupLaterButton);

        if (personalBtn != null) {
            personalBtn.setOnClickListener(v -> {
<<<<<<< HEAD
=======
                Log.d(TAG, "Personal clicked");
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
                saveRoleAndContinue("personal");
            });
        }

        if (businessBtn != null) {
            businessBtn.setOnClickListener(v -> {
<<<<<<< HEAD
=======
                Log.d(TAG, "Business clicked");
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
                saveRoleAndContinue("business");
            });
        }

        if (setupLaterButton != null) {
            setupLaterButton.setOnClickListener(v -> {
<<<<<<< HEAD
=======
                Log.d(TAG, "Setup later clicked");
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
                saveRoleAndContinue("skipped");
            });
        }
    }

    private void saveRoleAndContinue(String role) {
        if (userId == null) {
            userId = FirebaseAuth.getInstance().getUid();
            if (userId == null) {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
                return;
            }
        }

<<<<<<< HEAD
        mDatabase.child("users").child(userId).child("role").setValue(role)
                .addOnSuccessListener(aVoid -> {
=======
        Log.d(TAG, "Saving role: " + role + " for user: " + userId);
        
        mDatabase.child("users").child(userId).child("role").setValue(role)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Role saved successfully");
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
                    Intent intent;
                    if (role.equals("business")) {
                        intent = new Intent(RoleSelectActivity.this, EditBusinessActivity.class);
                    } else {
                        intent = new Intent(RoleSelectActivity.this, MainMenuActivity.class);
                        if (role.equals("personal")) {
                            intent.putExtra("open_fragment", "profile");
                        }
                    }
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
<<<<<<< HEAD
=======
                    Log.e(TAG, "Failed to save role", e);
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
