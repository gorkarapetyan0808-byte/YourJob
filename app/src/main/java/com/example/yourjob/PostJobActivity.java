package com.example.yourjob;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PostJobActivity extends AppCompatActivity {

    EditText titleInput, descInput;
    Spinner fieldSpinner, citySpinner, ageMinSpinner, ageMaxSpinner;
    Button saveButton;
    TextView businessInfo;
    ProgressBar progressBar;

    String[] cities;
    List<String> ages;
    String[] fields;

    DatabaseReference mDatabase;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();
        userId = FirebaseAuth.getInstance().getUid();

        titleInput = findViewById(R.id.titleInput);
        descInput = findViewById(R.id.descInput);
        fieldSpinner = findViewById(R.id.fieldSpinner);
        citySpinner = findViewById(R.id.citySpinner);
        ageMinSpinner = findViewById(R.id.ageMinSpinner);
        ageMaxSpinner = findViewById(R.id.ageMaxSpinner);
        saveButton = findViewById(R.id.saveJobButton);
        businessInfo = findViewById(R.id.businessInfo);
        progressBar = new ProgressBar(this);

        setupSpinners();

        String name = BusinessManager.getName(this);
        String phone = BusinessManager.getPhone(this);
        String email = BusinessManager.getEmail(this);
        String contact = phone + " | " + email;

        businessInfo.setText("Posting as: " + name + "\nContact: " + contact);

        saveButton.setOnClickListener(v -> saveJobToFirebase(name, contact));
    }

    private void setupSpinners() {
        cities = new String[]{
                "Ընտրել քաղաք", "Երևան", "Գյումրի", "Վանաձոր", "Աբովյան", "Հրազդան", "Կապան",
                "Արտաշատ", "Արմավիր", "Գորիս", "Մասիս", "Չարենցավան", "Իջևան", "Սևան", "Վեդի",
                "Եղեգնաձոր", "Ալավերդի", "Դիլիջան", "Սիսիան", "Սպիտակ", "Մարտունի", "Աշտարակ", "Թալին"
        };

        ages = new ArrayList<>();
        ages.add("Տարիք");
        for (int i = 16; i <= 70; i++) {
            ages.add(String.valueOf(i));
        }

        fields = new String[]{
                "Ընտրել ոլորտ", "Ծրագրավորում / IT", "Frontend Developer", "Backend Developer",
                "Mobile Developer", "QA / Testing", "DevOps", "UI/UX Design", "Graphic Design",
                "Marketing", "SMM", "SEO", "Sales", "Customer Support", "Finance", "Accounting",
                "HR", "Education", "Medicine"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        ArrayAdapter<String> fAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, fields);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldSpinner.setAdapter(fAdapter);

        ArrayAdapter<String> aAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, ages);
        aAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageMinSpinner.setAdapter(aAdapter);
        ageMaxSpinner.setAdapter(aAdapter);
    }

    private void saveJobToFirebase(String companyName, String contact) {
        String title = titleInput.getText().toString().trim();
        String desc = descInput.getText().toString().trim();
        String field = fieldSpinner.getSelectedItem().toString();
        String city = citySpinner.getSelectedItem().toString();
        String ageMin = ageMinSpinner.getSelectedItem().toString();
        String ageMax = ageMaxSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) ||
                field.equals("Ընտրել ոլորտ") || city.equals("Ընտրել քաղաք") || 
                ageMin.equals("Տարիք") || ageMax.equals("Տարիք")) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String ageRange = ageMin + "-" + ageMax;
        saveButton.setEnabled(false);
        String jobId = mDatabase.child("jobs").push().getKey();

        Job job = new Job(jobId, title, companyName, desc, ageRange, field, contact, city, userId);

        if (jobId != null) {
            mDatabase.child("jobs").child(jobId).setValue(job)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PostJobActivity.this, "Job posted successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            saveButton.setEnabled(true);
                            Toast.makeText(PostJobActivity.this, "Failed to post job", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
