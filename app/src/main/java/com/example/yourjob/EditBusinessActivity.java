package com.example.yourjob;

import android.content.Intent;
<<<<<<< HEAD
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
=======
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

<<<<<<< HEAD
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
=======
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82

public class EditBusinessActivity extends AppCompatActivity {

    EditText nameInput, phoneInput, emailInput;
    Spinner citySpinner, fieldSpinner;
    Button saveBtn, selectLogoBtn;
    ImageView logoPreview;
    Uri selectedLogoUri;
<<<<<<< HEAD
    String originalPhone = "";
    DatabaseReference mDatabase;
=======

    String[] cities;
    String[] fields;
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business);

<<<<<<< HEAD
        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();

=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        nameInput = findViewById(R.id.editBusinessName);
        phoneInput = findViewById(R.id.editBusinessPhone);
        emailInput = findViewById(R.id.editBusinessEmail);
        citySpinner = findViewById(R.id.editBusinessCitySpinner);
        fieldSpinner = findViewById(R.id.editBusinessFieldSpinner);
        saveBtn = findViewById(R.id.saveEditBusinessButton);
        selectLogoBtn = findViewById(R.id.selectLogoButton);
        logoPreview = findViewById(R.id.editBusinessLogo);

<<<<<<< HEAD
        setupSpinners();

        nameInput.setText(BusinessManager.getName(this));
        originalPhone = BusinessManager.getPhone(this);
        phoneInput.setText(originalPhone);
        emailInput.setText(BusinessManager.getEmail(this));

        String logoUriStr = BusinessManager.getLogo(this);
        if (!TextUtils.isEmpty(logoUriStr)) {
            selectedLogoUri = Uri.parse(logoUriStr);
            if (logoUriStr.startsWith("data:image")) {
                try {
                    String base64Image = logoUriStr.split(",")[1];
                    byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    logoPreview.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    logoPreview.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                try {
                    logoPreview.setImageURI(selectedLogoUri);
                } catch (Exception e) {
                    logoPreview.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }
        }

        setSpinnerSelection(citySpinner, getResources().getStringArray(R.array.cities_array), BusinessManager.getCity(this));
        setSpinnerSelection(fieldSpinner, getResources().getStringArray(R.array.fields_array), BusinessManager.getField(this));
=======
        // --- DATA FROM PROFILE ---

        cities = new String[]{
                "Ընտրել քաղաք",
                "Երևան","Գյումրի","Վանաձոր","Աբովյան","Հրազդան","Կապան",
                "Արտաշատ","Արմավիր","Գորիս","Մասիս","Չարենցավան",
                "Իջևան","Սևան","Վեդի","Եղեգնաձոր","Ալավերդի","Դիլիջան",
                "Սիսիան","Սպիտակ","Մարտունի","Աշտարակ","Թալին"
        };

        fields = new String[]{
                "Ընտրել ոլորտ",
                "Ծրագրավորում / IT","Frontend Developer","Backend Developer","Mobile Developer",
                "QA / Testing","DevOps","UI/UX Design","Graphic Design",
                "Marketing","SMM","SEO","Sales","Customer Support",
                "Finance","Accounting","HR","Education","Medicine"
        };

        // --- ADAPTERS (Using spinner_item for white text) ---

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        ArrayAdapter<String> fieldAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, fields);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldSpinner.setAdapter(fieldAdapter);

        // Load existing data
        nameInput.setText(BusinessManager.getName(this));
        phoneInput.setText(BusinessManager.getPhone(this));
        emailInput.setText(BusinessManager.getEmail(this));
        
        String logoUriStr = BusinessManager.getLogo(this);
        if (!TextUtils.isEmpty(logoUriStr)) {
            selectedLogoUri = Uri.parse(logoUriStr);
            logoPreview.setImageURI(selectedLogoUri);
        }

        setSpinnerValue(citySpinner, cities, BusinessManager.getCity(this));
        setSpinnerValue(fieldSpinner, fields, BusinessManager.getField(this));
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82

        selectLogoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 102);
        });

<<<<<<< HEAD
        saveBtn.setOnClickListener(v -> checkPhoneAndSave());
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
                    @Override public void onCancelled(@NonNull DatabaseError error) { saveBusinessData(name, phone, email, city, field); }
                });
    }

    private void saveBusinessData(String name, String phone, String email, String city, String field) {
        BusinessManager.save(EditBusinessActivity.this, name, phone, email, selectedLogoUri, city, field, success -> {
            if (success) {
                Toast.makeText(EditBusinessActivity.this, getString(R.string.success_save), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditBusinessActivity.this, "Failed to save business profile", Toast.LENGTH_SHORT).show();
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
=======
        saveBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String phone = phoneInput.getText().toString();
            String email = emailInput.getText().toString();
            String city = citySpinner.getSelectedItem().toString();
            String field = fieldSpinner.getSelectedItem().toString();
            String logoUri = (selectedLogoUri != null) ? selectedLogoUri.toString() : "";

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please enter company name", Toast.LENGTH_SHORT).show();
                return;
            }

            BusinessManager.save(this, name, phone, email, logoUri, city, field);
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setSpinnerValue(Spinner spinner, String[] array, String value) {
        if (value == null) return;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
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
<<<<<<< HEAD
            try {
                getContentResolver().takePersistableUriPermission(selectedLogoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception ignored) {}
=======
            getContentResolver().takePersistableUriPermission(selectedLogoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
            logoPreview.setImageURI(selectedLogoUri);
        }
    }
}