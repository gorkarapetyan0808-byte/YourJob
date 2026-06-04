package com.example.yourjob;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button logoutBtn = findViewById(R.id.settingsLogoutBtn);
        Button deleteAccBtn = findViewById(R.id.settingsDeleteAccountBtn);
        Button adminPanelBtn = findViewById(R.id.adminPanelBtn);

        checkAdminStatus(adminPanelBtn);

        logoutBtn.setOnClickListener(v -> logoutUser());
        
        if (deleteAccBtn != null) {
            deleteAccBtn.setOnClickListener(v -> showDeleteAccountDialog());
        }
        
        if (adminPanelBtn != null) {
            adminPanelBtn.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, AdminActivity.class)));
        }
    }

    private void checkAdminStatus(Button adminBtn) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/")
                .getReference("users").child(uid).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String role = snapshot.getValue(String.class);
                        if ("admin".equals(role)) {
                            adminBtn.setVisibility(View.VISIBLE);
                        } else {
                            adminBtn.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        BusinessManager.delete(this);
        JobStorage.clearAll(this);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_msg)
                .setPositiveButton(R.string.logout, (dialog, which) -> deleteAccountPermanently())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void deleteAccountPermanently() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference()
                .child("users").child(uid).removeValue();
        FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference()
                .child("businesses").child(uid).removeValue();

        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                logoutUser();
            } else {
                Toast.makeText(this, "Auth Error. Please re-login first.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
