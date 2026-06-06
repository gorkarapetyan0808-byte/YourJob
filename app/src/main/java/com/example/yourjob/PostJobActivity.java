package com.example.yourjob;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostJobActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, fieldInput, cityInput, ageInput, contactInput;
    private Button postButton;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getUid();

        titleInput = findViewById(R.id.postJobTitle);
        descriptionInput = findViewById(R.id.postJobDescription);
        fieldInput = findViewById(R.id.postJobField);
        cityInput = findViewById(R.id.postJobCity);
        ageInput = findViewById(R.id.postJobAge);
        contactInput = findViewById(R.id.postJobContact);
        postButton = findViewById(R.id.postJobButton);
        progressBar = findViewById(R.id.postJobProgressBar);

        postButton.setOnClickListener(v -> postJob());
    }

    private void postJob() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String field = fieldInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String age = ageInput.getText().toString().trim();
        String contact = contactInput.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(field)) {
            Toast.makeText(this, "Title, Description and Field are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        postButton.setEnabled(false);

        String jobId = mDatabase.child("jobs").push().getKey();
        String companyName = BusinessManager.getBusiness() != null ? BusinessManager.getBusiness().name : "Unknown Company";
        
        Job job = new Job(jobId, title, companyName, description, age, field, contact, city, userId);
        job.isApproved = true;

        if (jobId != null) {
            mDatabase.child("jobs").child(jobId).setValue(job)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        postButton.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(PostJobActivity.this, "Job posted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(PostJobActivity.this, "Failed to post job", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
