package com.example.yourjob;

import android.app.AlertDialog;
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
import android.widget.Toast;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    RecyclerView jobsRecycler;
    JobAdapter adapter;
    List<Job> jobList = new ArrayList<>();
    List<Job> filteredList = new ArrayList<>();
    Set<String> favoriteIds = new HashSet<>(); 
    ProgressBar progressBar;
    EditText searchEditText;
    Spinner cityFilterSpinner, fieldFilterSpinner;
    TextView noResultsText;
    DatabaseReference mDatabase;
    String userId;
    String userCity = "", userField = "", userAge = "";
    
    private ValueEventListener jobsListener;

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

        adapter.setOnItemClickListener(position -> {
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
        });

        adapter.setOnFavoriteClickListener((position, isFav) -> {
            if (position >= 0 && position < filteredList.size()) {
                toggleFavorite(filteredList.get(position).id, isFav);
            }
        });

        adapter.setOnDeleteClickListener(position -> {
            if (position >= 0 && position < filteredList.size()) {
                showDeleteConfirmation(filteredList.get(position).id);
            }
        });

        return view;
    }

    private void showDeleteConfirmation(String jobId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Job")
                .setMessage("Are you sure you want to delete this job posting?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    mDatabase.child("jobs").child(jobId).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Job deleted", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadUserPrefsAndFavorites() {
        if (userId == null) {
            setupJobsListener();
            return;
        }
        
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userCity = snapshot.child("city").getValue(String.class);
                    userField = snapshot.child("field").getValue(String.class);
                    userAge = snapshot.child("age").getValue(String.class);
                    String role = snapshot.child("role").getValue(String.class);

                    if ("admin".equals(role)) {
                        adapter.setAdmin(true);
                    }
                    
                    if (userCity == null) userCity = "";
                    if (userField == null) userField = "";
                    if (userAge == null) userAge = "";

                    adapter.setUserPreferences(userCity, userField, userAge);
                }
                loadFavorites();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { setupJobsListener(); }
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
                adapter.setFavoriteIds(new ArrayList<>(favoriteIds));
                
                if (jobsListener == null) {
                    setupJobsListener();
                } else {
                    sortAndFilter();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void toggleFavorite(String jobId, boolean isFav) {
        if (userId == null) return;
        
        if (isFav) favoriteIds.add(jobId);
        else favoriteIds.remove(jobId);
        
        adapter.setFavoriteIds(new ArrayList<>(favoriteIds));
        sortAndFilter();
        
        DatabaseReference favRef = mDatabase.child("favorites").child(userId).child(jobId);
        if (isFav) favRef.setValue(true);
        else favRef.removeValue();
    }

    private void setupFilterSpinners() {
        if (getContext() == null) return;
        
        String[] citiesArray = getResources().getStringArray(R.array.cities_array);
        List<String> cities = new ArrayList<>();
        cities.add("City"); 
        for (String s : citiesArray) {
            if (!s.contains("Select")) {
                cities.add(s);
            }
        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityFilterSpinner.setAdapter(cityAdapter);

        String[] fieldsArray = getResources().getStringArray(R.array.fields_array);
        List<String> fields = new ArrayList<>();
        fields.add("Field"); 
        for (String s : fieldsArray) {
            if (!s.contains("Select")) {
                fields.add(s);
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

    private void sortAndFilter() {
        Collections.sort(jobList, (j1, j2) -> {
            boolean f1 = favoriteIds.contains(j1.id);
            boolean f2 = favoriteIds.contains(j2.id);
            
            if (f1 && !f2) return -1;
            if (!f1 && f2) return 1;
            
            int match1 = getMatchPercentage(j1);
            int match2 = getMatchPercentage(j2);
            if (match1 != match2) {
                return Integer.compare(match2, match1);
            }
            
            return Long.compare(j2.timestamp, j1.timestamp);
        });
        applyFilters();
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

    private void setupJobsListener() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        if (jobsListener != null) {
            mDatabase.child("jobs").removeEventListener(jobsListener);
        }
        
        jobsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                jobList.clear();
                
                boolean hasApproved = false;
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Job job = ds.getValue(Job.class);
                        if (job != null) {
                            if (job.id == null || job.id.isEmpty()) job.id = ds.getKey();
                            jobList.add(job);
                            if (job.isApproved) hasApproved = true;
                        }
                    }
                }

                if (!hasApproved) {
                    JobStorage.addProfessionalSampleJobs();
                }

                sortAndFilter();
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { if (progressBar != null) progressBar.setVisibility(View.GONE); }
        };
        
        mDatabase.child("jobs").addValueEventListener(jobsListener);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (jobsListener != null) {
            mDatabase.child("jobs").removeEventListener(jobsListener);
        }
    }
}