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

<<<<<<< HEAD
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Applications: " + jobTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
=======
        setTitle("Applications for: " + jobTitle);
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82

        appsRecycler = findViewById(R.id.appsRecycler);
        progressBar = findViewById(R.id.appsProgressBar);
        noAppsText = findViewById(R.id.noAppsText);

        appsRecycler.setLayoutManager(new LinearLayoutManager(this));
        appList = new ArrayList<>();
<<<<<<< HEAD
        adapter = new ApplicationAdapter(appList, true);
=======
        adapter = new ApplicationAdapter(appList);
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
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
<<<<<<< HEAD

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
}
