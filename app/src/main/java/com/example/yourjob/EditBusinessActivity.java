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

public class EditBusinessActivity extends AppCompatActivity {

    private EditText nameInput, fieldInput, cityInput, descriptionInput, contactInput;
    private Button saveButton;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getUid();

        nameInput = findViewById(R.id.editBusinessName);
        fieldInput = findViewById(R.id.editBusinessField);
        cityInput = findViewById(R.id.editBusinessCity);
        descriptionInput = findViewById(R.id.editBusinessDescription);
        contactInput = findViewById(R.id.editBusinessContact);
        saveButton = findViewById(R.id.saveBusinessButton);
        progressBar = findViewById(R.id.editBusinessProgressBar);

        loadBusinessData();

        saveButton.setOnClickListener(v -> saveBusinessData());
    }

    private void loadBusinessData() {
        Business business = BusinessManager.getBusiness();
        if (business != null) {
            nameInput.setText(business.name);
            fieldInput.setText(business.field);
            cityInput.setText(business.city);
            descriptionInput.setText(business.description);
            contactInput.setText(business.contact);
        }
    }

    private void saveBusinessData() {
        String name = nameInput.getText().toString().trim();
        String field = fieldInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String contact = contactInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(field)) {
            Toast.makeText(this, "Name and Field are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);

        Business updatedBusiness = new Business(userId, name, field, city, description, contact);
        
        mDatabase.child("businesses").child(userId).setValue(updatedBusiness)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        BusinessManager.setBusiness(updatedBusiness);
                        Toast.makeText(EditBusinessActivity.this, "Business updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditBusinessActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
