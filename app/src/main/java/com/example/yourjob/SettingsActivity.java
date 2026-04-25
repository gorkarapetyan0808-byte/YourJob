package com.example.yourjob;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton backBtn = findViewById(R.id.backBtn);
        TextView aboutBtn = findViewById(R.id.aboutBtn);
        Button logoutBtn = findViewById(R.id.settingsLogoutBtn);

        backBtn.setOnClickListener(v -> finish());

        aboutBtn.setOnClickListener(v -> {
            Toast.makeText(this, "YourJob App - Find your dream job easily!", Toast.LENGTH_SHORT).show();
        });

        logoutBtn.setOnClickListener(v -> {
            // 1. Sign out from Firebase
            FirebaseAuth.getInstance().signOut();
            
            // 2. Clear local data
            BusinessManager.delete(this);
            JobStorage.clearAll(this);
            
            // 3. Redirect to Login Activity
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
