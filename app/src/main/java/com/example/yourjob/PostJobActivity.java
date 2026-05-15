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

        setupSpinners();

        String name = BusinessManager.getName(this);
        String phone = BusinessManager.getPhone(this);
        String email = BusinessManager.getEmail(this);
        String contact = phone + " | " + email;

        businessInfo.setText(getString(R.string.post_job) + ": " + name + "\n" + getString(R.string.contact) + ": " + contact);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJobToFirebase(name, contact);
            }
        });
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this,
                R.array.cities_array, R.layout.spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        ArrayAdapter<CharSequence> fieldAdapter = ArrayAdapter.createFromResource(this,
                R.array.fields_array, R.layout.spinner_item);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldSpinner.setAdapter(fieldAdapter);

        List<String> ages = new ArrayList<>();
        ages.add(getString(R.string.age));
        for (int i = 16; i <= 70; i++) {
            ages.add(String.valueOf(i));
        }

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

        String selectField = getResources().getStringArray(R.array.fields_array)[0];
        String selectCity = getResources().getStringArray(R.array.cities_array)[0];
        String selectAge = getString(R.string.age);

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) ||
                field.equals(selectField) || city.equals(selectCity) || 
                ageMin.equals(selectAge) || ageMax.equals(selectAge)) {
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
                            Toast.makeText(PostJobActivity.this, getString(R.string.success_post), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            saveButton.setEnabled(true);
                            Toast.makeText(PostJobActivity.this, "Failed to post job", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
