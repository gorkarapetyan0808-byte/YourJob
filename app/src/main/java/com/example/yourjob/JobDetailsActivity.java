package com.example.yourjob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class JobDetailsActivity extends AppCompatActivity {

    TextView title, company, description, age, field, contact, city;
    TextView applicantInfoDisplay, cvFileNameDisplay;
    EditText messageInput;
    Button uploadCvBtn, applyButton;
    ProgressBar progressBar;

    String selectedCvName = "";
    Uri selectedCvUri = null;
    String applicantName, applicantAge, applicantCity;
    
    DatabaseReference mDatabase;
    String userId;
    String jobId;

    private final ActivityResultLauncher<String[]> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        selectedCvUri = uri;
                        selectedCvName = getFileName(uri);
                        cvFileNameDisplay.setText("CV: " + selectedCvName);
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to select file", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

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

        cvFileNameDisplay.setText("No CV attached");

        if (userId != null) {
            loadUserProfile();
            checkIfAlreadyApplied();
        }

        uploadCvBtn.setOnClickListener(v -> {
            String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
            filePickerLauncher.launch(mimeTypes);
        });

        applyButton.setOnClickListener(v -> validateAndApply(jobTitleStr));
    }

    private void checkIfAlreadyApplied() {
        mDatabase.child("applications").orderByChild("applicantId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String appliedJobId = ds.child("jobId").getValue(String.class);
                            if (jobId.equals(appliedJobId)) {
                                applyButton.setEnabled(false);
                                applyButton.setText("Already Applied");
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                User user = snapshot.getValue(User.class);
                if (user != null && !TextUtils.isEmpty(user.name)) {
                    applicantName = user.name;
                    applicantAge = user.age;
                    applicantCity = user.city;
                    applicantInfoDisplay.setText("Name: " + applicantName + ", Age: " + applicantAge);
                } else {
                    applicantInfoDisplay.setText("Please complete your profile first!");
                    applyButton.setEnabled(false);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { progressBar.setVisibility(View.GONE); }
        });
    }

    private void validateAndApply(String jobTitle) {
        String message = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            messageInput.setError("Message is mandatory");
            return;
        }
        if (selectedCvUri == null) {
            Toast.makeText(this, "Attaching a CV is mandatory", Toast.LENGTH_LONG).show();
            return;
        }
        convertCvToBase64AndSave(jobTitle, message);
    }

    private void convertCvToBase64AndSave(String jobTitle, String message) {
        progressBar.setVisibility(View.VISIBLE);
        applyButton.setEnabled(false);

        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedCvUri);
            if (inputStream == null) throw new Exception("File error");

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            byte[] bytes = output.toByteArray();
            inputStream.close();

            String base64Cv = Base64.encodeToString(bytes, Base64.DEFAULT);
            
            if (bytes.length > 1.5 * 1024 * 1024) {
                throw new Exception("File is too large! Please select a PDF smaller than 1.5MB.");
            }

            saveApplication(jobTitle, message, base64Cv);

        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            applyButton.setEnabled(true);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveApplication(String jobTitle, String message, String cvData) {
        String appId = mDatabase.child("applications").push().getKey();
        Application app = new Application(appId, jobId, jobTitle, userId, applicantName, applicantAge, applicantCity, message, selectedCvName, cvData);

        if (appId != null) {
            mDatabase.child("applications").child(appId).setValue(app).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Applied Successfully!", Toast.LENGTH_SHORT).show();
                    applyButton.setText("Already Applied");
                    applyButton.setEnabled(false);
                    finish();
                } else {
                    applyButton.setEnabled(true);
                    Toast.makeText(this, "Failed to save application", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) result = cursor.getString(nameIndex);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "file.pdf";
    }
}
