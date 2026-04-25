package com.example.yourjob;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainMenuActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Check if we need to open a specific fragment
        String openFragment = getIntent().getStringExtra("open_fragment");
        Fragment initialFragment = new HomeFragment();
        int initialNavId = R.id.nav_home;

        if ("profile".equals(openFragment)) {
            initialFragment = new ProfileFragment();
            initialNavId = R.id.nav_profile;
        } else if ("business".equals(openFragment)) {
            initialFragment = new MyBusinessFragment();
            initialNavId = R.id.nav_business;
        } else if ("settings".equals(openFragment)) {
            initialFragment = new SettingsFragment();
            initialNavId = R.id.nav_settings;
        }

        // Default screen
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, initialFragment)
                .commit();
        
        bottomNavigationView.setSelectedItemId(initialNavId);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            if(item.getItemId() == R.id.nav_home){
                selectedFragment = new HomeFragment();
            }
            else if(item.getItemId() == R.id.nav_applications){
                selectedFragment = new ApplicationsFragment();
            }
            else if(item.getItemId() == R.id.nav_profile){
                selectedFragment = new ProfileFragment();
            }
            else if(item.getItemId() == R.id.nav_business){
                selectedFragment = new MyBusinessFragment();
            }
            else if(item.getItemId() == R.id.nav_settings){
                selectedFragment = new SettingsFragment();
            }

            if(selectedFragment != null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}
