package com.example.yourjob;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    EditText nameInput;
    Spinner citySpinner, ageSpinner, fieldSpinner;
    Button saveButton;
    ProgressBar progressBar;

    String[] cities;
    List<String> ages;
    String[] fields;

    DatabaseReference mDatabase;
    String userId;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameInput = view.findViewById(R.id.nameInput);
        citySpinner = view.findViewById(R.id.citySpinner);
        ageSpinner = view.findViewById(R.id.ageSpinner);
        fieldSpinner = view.findViewById(R.id.fieldSpinner);
        saveButton = view.findViewById(R.id.saveProfileButton);
        progressBar = view.findViewById(R.id.profileProgressBar);

        userId = FirebaseAuth.getInstance().getUid();
        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();

        setupSpinners();

        if (userId != null) {
            loadUserProfile();
        }

        saveButton.setOnClickListener(v -> saveUserProfile());

        return view;
    }

    private void setupSpinners() {
        cities = new String[]{
                "Ընտրել քաղաք", "Երևան", "Գյումրի", "Վանաձոր", "Աբովյան", "Հրազդան", "Կապան",
                "Արտաշատ", "Արմավիր", "Գորիս", "Մասիս", "Չարենցավան", "Իջևան", "Սևան", "Վեդի",
                "Եղեգնաձոր", "Ալավերդի", "Դիլիջան", "Սիսիան", "Սպիտակ", "Մարտունի", "Աշտարակ", "Թալին"
        };

        ages = new ArrayList<>();
        ages.add("Ընտրել տարիք");
        for (int i = 16; i <= 70; i++) {
            ages.add(String.valueOf(i));
        }

        fields = new String[]{
                "Ընտրել ոլորտ", "Ծրագրավորում / IT", "Frontend Developer", "Backend Developer",
                "Mobile Developer", "QA / Testing", "DevOps", "UI/UX Design", "Graphic Design",
                "Marketing", "SMM", "SEO", "Sales", "Customer Support", "Finance", "Accounting",
                "HR", "Education", "Medicine"
        };

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, ages);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdapter);

        ArrayAdapter<String> fieldAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, fields);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldSpinner.setAdapter(fieldAdapter);
    }

    private void loadUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isAdded()) {
                    progressBar.setVisibility(View.GONE);
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            nameInput.setText(user.name);
                            setSpinnerSelection(citySpinner, cities, user.city);
                            setSpinnerSelection(ageSpinner, ages.toArray(new String[0]), user.age);
                            setSpinnerSelection(fieldSpinner, fields, user.field);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserProfile() {
        String name = nameInput.getText().toString().trim();
        String city = citySpinner.getSelectedItem().toString();
        String age = ageSpinner.getSelectedItem().toString();
        String field = fieldSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Name is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);

        mDatabase.child("users").child(userId).child("name").setValue(name);
        mDatabase.child("users").child(userId).child("city").setValue(city);
        mDatabase.child("users").child(userId).child("age").setValue(age);
        mDatabase.child("users").child(userId).child("field").setValue(field)
                .addOnCompleteListener(task -> {
                    if (isAdded()) {
                        progressBar.setVisibility(View.GONE);
                        saveButton.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setSpinnerSelection(Spinner spinner, String[] array, String value) {
        if (value == null) return;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}
