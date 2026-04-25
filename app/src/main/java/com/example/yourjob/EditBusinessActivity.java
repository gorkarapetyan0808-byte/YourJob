package com.example.yourjob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EditBusinessActivity extends AppCompatActivity {

    EditText nameInput, phoneInput, emailInput;
    Spinner citySpinner, fieldSpinner;
    Button saveBtn, selectLogoBtn;
    ImageView logoPreview;
    Uri selectedLogoUri;

    String[] cities;
    String[] fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business);

        nameInput = findViewById(R.id.editBusinessName);
        phoneInput = findViewById(R.id.editBusinessPhone);
        emailInput = findViewById(R.id.editBusinessEmail);
        citySpinner = findViewById(R.id.editBusinessCitySpinner);
        fieldSpinner = findViewById(R.id.editBusinessFieldSpinner);
        saveBtn = findViewById(R.id.saveEditBusinessButton);
        selectLogoBtn = findViewById(R.id.selectLogoButton);
        logoPreview = findViewById(R.id.editBusinessLogo);

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

        selectLogoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 102);
        });

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