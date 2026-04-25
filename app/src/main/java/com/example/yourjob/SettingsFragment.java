package com.example.yourjob;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        // Hide back button because it's now a fragment in bottom nav
        View backBtn = view.findViewById(R.id.backBtn);
        if (backBtn != null) backBtn.setVisibility(View.GONE);

        TextView aboutBtn = view.findViewById(R.id.aboutBtn);
        Button logoutBtn = view.findViewById(R.id.settingsLogoutBtn);

        aboutBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "YourJob App - Find your dream job easily!", Toast.LENGTH_SHORT).show();
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            BusinessManager.delete(getContext());
            JobStorage.clearAll(getContext());
            
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}
