package com.example.yourjob;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView jobsRecycler;
    JobAdapter adapter;
    List<Job> jobList;
    ProgressBar progressBar;
    DatabaseReference mDatabase;
    String userId;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        jobsRecycler = view.findViewById(R.id.jobsRecycler);
        progressBar = view.findViewById(R.id.homeProgressBar);
        
        jobsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        jobList = new ArrayList<>();
        adapter = new JobAdapter(jobList);
        jobsRecycler.setAdapter(adapter);

        userId = FirebaseAuth.getInstance().getUid();
        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();

        loadUserPrefsAndJobs();

        // CLICK
        adapter.setOnItemClickListener(position -> {
            Job job = jobList.get(position);
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
        });

        return view;
    }

    private void loadUserPrefsAndJobs() {
        if (userId == null) return;
        
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String city = snapshot.child("city").getValue(String.class);
                    String field = snapshot.child("field").getValue(String.class);
                    adapter.setUserPreferences(city != null ? city : "", field != null ? field : "");
                    loadJobsFromFirebase(city, field);
                } else {
                    loadJobsFromFirebase("", "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadJobsFromFirebase(String userCity, String userField) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        mDatabase.child("jobs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear();
                
                if (!snapshot.exists()) {
                    // Sample jobs for testing Match system
                    jobList.add(new Job("s1", "Android Developer", "IT Solutions", "Perfect match test", "20-30", "IT / Programming", "123", "Yerevan", "system"));
                    jobList.add(new Job("s2", "Sales Manager", "Shop Center", "Partial match test (City)", "20-40", "Sales", "123", "Yerevan", "system"));
                    jobList.add(new Job("s3", "Accountant", "Finance Bank", "No match test", "25-45", "Finance", "123", "Gyumri", "system"));
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Job job = dataSnapshot.getValue(Job.class);
                    if (job != null) jobList.add(job);
                }

                // Sorting: Matches first
                Collections.sort(jobList, (j1, j2) -> {
                    int score1 = getMatchScore(j1, userCity, userField);
                    int score2 = getMatchScore(j2, userCity, userField);
                    return Integer.compare(score2, score1); // Descending
                });

                adapter.notifyDataSetChanged();
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });
    }

    private int getMatchScore(Job job, String city, String field) {
        int score = 0;
        if (job.city != null && job.city.equalsIgnoreCase(city)) score += 2;
        if (job.field != null && job.field.equalsIgnoreCase(field)) score += 2;
        return score;
    }
}
