package com.example.yourjob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JobDetailsActivity extends AppCompatActivity {

    TextView title, company, description, age, field, contact, city;
    TextView applicantInfoDisplay, cvFileNameDisplay;
    EditText messageInput;
    Button uploadCvBtn, applyButton;
    ProgressBar progressBar;

    String selectedCvName = "No CV attached";
    String selectedCvUri = "";
    String applicantName, applicantAge, applicantCity;
    
    DatabaseReference mDatabase;
    String userId;
    String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();
        userId = FirebaseAuth.getInstance().getUid();

        title = findViewById(R.id.titleText);
        company = findViewById(R.id.companyText);
        description = findViewById(R.id.descriptionText);
        age = findViewById(R.id.ageText);
        field = findViewById(R.id.fieldText);
        contact = findViewById(R.id.contactText);
        city = findViewById(R.id.cityText);
        
        applicantInfoDisplay = findViewById(R.id.applicantInfoDisplay);
        cvFileNameDisplay = findViewById(R.id.cvFileNameDisplay);
        messageInput = findViewById(R.id.messageInput);
        uploadCvBtn = findViewById(R.id.uploadCvBtn);
        applyButton = findViewById(R.id.applyButton);
        progressBar = findViewById(R.id.detailsProgressBar);

        jobId = getIntent().getStringExtra("id");
        String jobTitleStr = getIntent().getStringExtra("title");
        
        title.setText(jobTitleStr);
        company.setText(getIntent().getStringExtra("company"));
        description.setText(getIntent().getStringExtra("description"));
        age.setText(getIntent().getStringExtra("age"));
        field.setText(getIntent().getStringExtra("field"));
        contact.setText(getIntent().getStringExtra("contact"));
        city.setText(getIntent().getStringExtra("city"));

        if (userId != null) {
            loadUserProfile();
        }

        uploadCvBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(intent, 101);
        });

        applyButton.setOnClickListener(v -> applyForJob(jobTitleStr));
    }

    private void loadUserProfile() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.name != null && !user.name.isEmpty()) {
                        applicantName = user.name;
                        applicantAge = user.age;
                        applicantCity = user.city;
                        applicantInfoDisplay.setText("Name: " + applicantName + "\n" +
                                                   "Age: " + applicantAge + "\n" +
                                                   "City: " + applicantCity);
                        applyButton.setEnabled(true);
                    } else {
                        applicantInfoDisplay.setText("Please complete your profile first.");
                        applyButton.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void applyForJob(String jobTitle) {
        String message = messageInput.getText().toString().trim();
        
        if (userId == null) return;

        applyButton.setEnabled(false);
        String appId = mDatabase.child("applications").push().getKey();
        
        Application app = new Application(
                appId,
                jobId,
                jobTitle,
                userId,
                applicantName,
                applicantAge,
                applicantCity,
                message,
                selectedCvName,
                selectedCvUri
        );

        if (appId != null) {
            mDatabase.child("applications").child(appId).setValue(app)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // 🔥 LOCAL STORAGE SYNC: Add to local list and save to SharedPreferences
                            JobStorage.loadApplications(JobDetailsActivity.this);
                            JobStorage.applications.add(app);
                            JobStorage.saveApplications(JobDetailsActivity.this);

                            Toast.makeText(JobDetailsActivity.this, "Applied Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            applyButton.setEnabled(true);
                            Toast.makeText(JobDetailsActivity.this, "Failed to apply", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                selectedCvUri = uri.toString();
                selectedCvName = getFileName(uri);
                cvFileNameDisplay.setText("CV: " + selectedCvName);
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }
}
