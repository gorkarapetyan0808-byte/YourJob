package com.example.yourjob;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JobApplicationsActivity extends AppCompatActivity {

    RecyclerView appsRecycler;
    ApplicationAdapter adapter;
    List<Application> appList;
    ProgressBar progressBar;
    TextView noAppsText;

    DatabaseReference mDatabase;
    String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_applications);

        jobId = getIntent().getStringExtra("jobId");
        String jobTitle = getIntent().getStringExtra("jobTitle");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Applications: " + jobTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        appsRecycler = findViewById(R.id.appsRecycler);
        progressBar = findViewById(R.id.appsProgressBar);
        noAppsText = findViewById(R.id.noAppsText);

        appsRecycler.setLayoutManager(new LinearLayoutManager(this));
        appList = new ArrayList<>();
        // Passing true because this is the Employer view
        adapter = new ApplicationAdapter(appList, true);
        appsRecycler.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference().child("applications");

        loadApplicationsFromFirebase();
    }

    private void loadApplicationsFromFirebase() {
        if (jobId == null) return;
        progressBar.setVisibility(View.VISIBLE);

        mDatabase.orderByChild("jobId").equalTo(jobId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Application app = ds.getValue(Application.class);
                            if (app != null) appList.add(app);
                        }
                        
                        if (appList.isEmpty()) {
                            noAppsText.setVisibility(View.VISIBLE);
                        } else {
                            noAppsText.setVisibility(View.GONE);
                        }
                        
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(JobApplicationsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
