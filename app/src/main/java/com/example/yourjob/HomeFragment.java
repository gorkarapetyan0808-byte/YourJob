package com.example.yourjob;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView jobsRecycler;
    JobAdapter adapter;
    List<Job> jobList = new ArrayList<>();
    List<Job> filteredList = new ArrayList<>();
    List<String> favoriteIds = new ArrayList<>();
    ProgressBar progressBar;
    EditText searchEditText;
    Spinner cityFilterSpinner, fieldFilterSpinner;
    TextView noResultsText;
    DatabaseReference mDatabase;
    String userId;
    String userCity = "", userField = "", userAge = "";

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        jobsRecycler = view.findViewById(R.id.jobsRecycler);
        progressBar = view.findViewById(R.id.homeProgressBar);
        searchEditText = view.findViewById(R.id.searchEditText);
        cityFilterSpinner = view.findViewById(R.id.filterCitySpinner);
        fieldFilterSpinner = view.findViewById(R.id.filterFieldSpinner);
        noResultsText = view.findViewById(R.id.noResultsText);
        
        jobsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new JobAdapter(filteredList);
        jobsRecycler.setAdapter(adapter);

        userId = FirebaseAuth.getInstance().getUid();
        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();

        setupFilterSpinners();
        loadUserPrefsAndFavorites();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        adapter.setOnItemClickListener(new JobAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (position >= 0 && position < filteredList.size()) {
                    Job job = filteredList.get(position);
                    Intent intent = new Intent(getContext(), JobDetailsActivity.class);
                    intent.putExtra("id", job.id);
                    intent.putExtra("title", job.title);
                    intent.putExtra("company", job.company);
                    intent.putExtra("description", job.description);
                    intent.putExtra("age", job.age);
                    intent.putExtra("field", job.field);
                    intent.putExtra("contact", job.contact);
                    intent.putExtra("city", job.city);
                    startActivity(intent);
                }
            }
        });

        adapter.setOnFavoriteClickListener(new JobAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavClick(int position, boolean isFav) {
                if (position >= 0 && position < filteredList.size()) {
                    toggleFavorite(filteredList.get(position).id, isFav);
                }
            }
        });

        return view;
    }

    private void loadUserPrefsAndFavorites() {
        if (userId == null) {
            loadJobsFromFirebase();
            return;
        }
        
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userCity = snapshot.child("city").getValue(String.class);
                    userField = snapshot.child("field").getValue(String.class);
                    userAge = snapshot.child("age").getValue(String.class);
                    
                    if (userCity == null) userCity = "";
                    if (userField == null) userField = "";
                    if (userAge == null) userAge = "";

                    adapter.setUserPreferences(userCity, userField, userAge);
                }
                loadFavorites();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { loadJobsFromFirebase(); }
        });
    }

    private void loadFavorites() {
        mDatabase.child("favorites").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteIds.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    favoriteIds.add(ds.getKey());
                }
                adapter.setFavoriteIds(favoriteIds);
                loadJobsFromFirebase();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void toggleFavorite(String jobId, boolean isFav) {
        if (userId == null) return;
        DatabaseReference favRef = mDatabase.child("favorites").child(userId).child(jobId);
        if (isFav) favRef.setValue(true);
        else favRef.removeValue();
    }

    private void setupFilterSpinners() {
        if (getContext() == null) return;
        
        // City Spinner Setup
        String[] citiesArray = getResources().getStringArray(R.array.cities_array);
        List<String> cities = new ArrayList<>();
        cities.add("City"); // Header
        for (int i = 0; i < citiesArray.length; i++) {
            if (!citiesArray[i].contains("Select")) {
                cities.add(citiesArray[i]);
            }
        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityFilterSpinner.setAdapter(cityAdapter);

        // Field Spinner Setup
        String[] fieldsArray = getResources().getStringArray(R.array.fields_array);
        List<String> fields = new ArrayList<>();
        fields.add("Field"); // Header
        for (int i = 0; i < fieldsArray.length; i++) {
            if (!fieldsArray[i].contains("Select")) {
                fields.add(fieldsArray[i]);
            }
        }

        ArrayAdapter<String> fieldAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, fields);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldFilterSpinner.setAdapter(fieldAdapter);

        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { applyFilters(); }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        cityFilterSpinner.setOnItemSelectedListener(filterListener);
        fieldFilterSpinner.setOnItemSelectedListener(filterListener);
    }

    private void applyFilters() {
        String searchText = searchEditText.getText().toString().toLowerCase().trim();
        String selectedCity = cityFilterSpinner.getSelectedItem() != null ? cityFilterSpinner.getSelectedItem().toString() : "";
        String selectedField = fieldFilterSpinner.getSelectedItem() != null ? fieldFilterSpinner.getSelectedItem().toString() : "";

        filteredList.clear();
        for (Job job : jobList) {
            if (!job.isApproved) continue;

            boolean matchesSearch = searchText.isEmpty() || 
                    (job.title != null && job.title.toLowerCase().contains(searchText)) || 
                    (job.company != null && job.company.toLowerCase().contains(searchText));
            
            boolean matchesCity = selectedCity.equals("City") || 
                    (job.city != null && job.city.trim().equalsIgnoreCase(selectedCity.trim()));
            
            boolean matchesField = selectedField.equals("Field") || 
                    (job.field != null && job.field.trim().equalsIgnoreCase(selectedField.trim()));
            
            if (matchesSearch && matchesCity && matchesField) filteredList.add(job);
        }
        adapter.notifyDataSetChanged();
        noResultsText.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadJobsFromFirebase() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        mDatabase.child("jobs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                jobList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Job job = ds.getValue(Job.class);
                        if (job != null) jobList.add(job);
                    }
                }
                
                Collections.sort(jobList, new Comparator<Job>() {
                    @Override
                    public int compare(Job j1, Job j2) {
                        return Integer.compare(getMatchPercentage(j2), getMatchPercentage(j1));
                    }
                });
                
                applyFilters();
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { if (progressBar != null) progressBar.setVisibility(View.GONE); }
        });
    }

    private int getMatchPercentage(Job job) {
        int score = 0;
        if (isValid(userCity) && job.city != null && job.city.trim().equalsIgnoreCase(userCity.trim())) score += 40;
        if (isValid(userField) && job.field != null && job.field.trim().equalsIgnoreCase(userField.trim())) score += 40;
        if (isValid(userAge) && isAgeInRange(userAge, job.age)) score += 20;
        return score;
    }

    private boolean isValid(String v) { 
        return v != null && !v.isEmpty() && !v.contains("Select") && !v.contains("All");
    }

    private boolean isAgeInRange(String userAgeStr, String jobAgeRange) {
        if (userAgeStr == null || jobAgeRange == null || !jobAgeRange.contains("-")) return false;
        try {
            int uAge = Integer.parseInt(userAgeStr.replaceAll("[^0-9]", "").trim());
            String[] parts = jobAgeRange.split("-");
            int min = Integer.parseInt(parts[0].trim());
            int max = Integer.parseInt(parts[1].trim());
            return uAge >= min && uAge <= max;
        } catch (Exception e) { return false; }
    }
}
