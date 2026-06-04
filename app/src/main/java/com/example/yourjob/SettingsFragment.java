package com.example.yourjob;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        Button logoutBtn = view.findViewById(R.id.settingsLogoutBtn);
        Button deleteAccBtn = view.findViewById(R.id.settingsDeleteAccountBtn);
        Button adminPanelBtn = view.findViewById(R.id.adminPanelBtn);

        checkAdminStatus(adminPanelBtn);

        logoutBtn.setOnClickListener(v -> logoutUser());
        deleteAccBtn.setOnClickListener(v -> showDeleteAccountDialog());
        adminPanelBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), AdminActivity.class)));

        return view;
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
        if (getContext() != null) {
            BusinessManager.delete(getContext());
            JobStorage.clearAll(getContext());
        }
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) getActivity().finish();
    }

    private void showDeleteAccountDialog() {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_msg)
                .setPositiveButton(R.string.logout, (dialog, which) -> deleteAccountPermanently())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void deleteAccountPermanently() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference db = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();
        String uid = user.getUid();

        db.child("users").child(uid).removeValue();
        db.child("businesses").child(uid).removeValue();
        
        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                logoutUser();
            } else {
                Toast.makeText(getContext(), "Auth Error. Please re-login first.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
