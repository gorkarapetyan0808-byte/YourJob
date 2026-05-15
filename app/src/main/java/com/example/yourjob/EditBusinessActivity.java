package com.example.yourjob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class EditBusinessActivity extends AppCompatActivity {

    EditText nameInput, phoneInput, emailInput;
    Spinner citySpinner, fieldSpinner;
    Button saveBtn, selectLogoBtn;
    ImageView logoPreview;
    Uri selectedLogoUri;
    String originalPhone = "";
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business);

        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();

        nameInput = findViewById(R.id.editBusinessName);
        phoneInput = findViewById(R.id.editBusinessPhone);
        emailInput = findViewById(R.id.editBusinessEmail);
        citySpinner = findViewById(R.id.editBusinessCitySpinner);
        fieldSpinner = findViewById(R.id.editBusinessFieldSpinner);
        saveBtn = findViewById(R.id.saveEditBusinessButton);
        selectLogoBtn = findViewById(R.id.selectLogoButton);
        logoPreview = findViewById(R.id.editBusinessLogo);

        setupSpinners();

        nameInput.setText(BusinessManager.getName(this));
        originalPhone = BusinessManager.getPhone(this);
        phoneInput.setText(originalPhone);
        emailInput.setText(BusinessManager.getEmail(this));

        String logoUriStr = BusinessManager.getLogo(this);
        if (!TextUtils.isEmpty(logoUriStr)) {
            selectedLogoUri = Uri.parse(logoUriStr);
            logoPreview.setImageURI(selectedLogoUri);
        }

        setSpinnerSelection(citySpinner, getResources().getStringArray(R.array.cities_array), BusinessManager.getCity(this));
        setSpinnerSelection(fieldSpinner, getResources().getStringArray(R.array.fields_array), BusinessManager.getField(this));

        selectLogoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 102);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPhoneAndSave();
            }
        });
    }

    private void checkPhoneAndSave() {
        final String name = nameInput.getText().toString().trim();
        final String phone = phoneInput.getText().toString().trim();
        final String email = emailInput.getText().toString().trim();
        final String city = citySpinner.getSelectedItem().toString();
        final String field = fieldSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter company name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.equals(originalPhone)) {
            saveBusinessData(name, phone, email, city, field);
            return;
        }

        // Check if phone number is already used by another business/user
        mDatabase.child("users").orderByChild("phone").equalTo(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            boolean alreadyUsed = false;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if (!ds.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                                    alreadyUsed = true;
                                    break;
                                }
                            }
                            if (alreadyUsed) {
                                phoneInput.setError("This phone number is already in use");
                                Toast.makeText(EditBusinessActivity.this, "Phone number already in use!", Toast.LENGTH_SHORT).show();
                            } else {
                                saveBusinessData(name, phone, email, city, field);
                            }
                        } else {
                            saveBusinessData(name, phone, email, city, field);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        saveBusinessData(name, phone, email, city, field);
                    }
                });
    }

    private void saveBusinessData(String name, String phone, String email, String city, String field) {
        BusinessManager.save(EditBusinessActivity.this, name, phone, email, selectedLogoUri, city, field, new BusinessManager.SaveCompleteListener() {
            @Override
            public void onSaveComplete(boolean success) {
                if (success) {
                    Toast.makeText(EditBusinessActivity.this, getString(R.string.success_save), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditBusinessActivity.this, "Failed to save business profile", Toast.LENGTH_SHORT).show();
                }
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
    }

    private void setSpinnerSelection(Spinner spinner, String[] array, String value) {
        if (value == null) return;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == RESULT_OK && data != null) {
            selectedLogoUri = data.getData();
            getContentResolver().takePersistableUriPermission(selectedLogoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            logoPreview.setImageURI(selectedLogoUri);
        }
    }
}
