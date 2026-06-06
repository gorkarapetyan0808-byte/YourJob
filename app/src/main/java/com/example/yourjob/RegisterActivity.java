package com.example.yourjob;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();

        emailInput = findViewById(R.id.regEmailInput);
        passwordInput = findViewById(R.id.regPasswordInput);
        confirmPasswordInput = findViewById(R.id.regConfirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        goToLogin = findViewById(R.id.goToLoginText);
        progressBar = findViewById(R.id.regProgressBar);

        registerButton.setOnClickListener(v -> registerUser());
        goToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.GONE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            User newUser = new User(userId, email);
                            
                            mDatabase.child("users").child(userId).setValue(newUser)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            firebaseUser.sendEmailVerification()
                                                .addOnCompleteListener(vTask -> {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(RegisterActivity.this, "Account created! Verify your email.", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(RegisterActivity.this, VerifyEmailActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                });
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            registerButton.setVisibility(View.VISIBLE);
                                            Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        registerButton.setVisibility(View.VISIBLE);
                        String error = task.getException() != null ? task.getException().getMessage() : "Auth failed";
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
