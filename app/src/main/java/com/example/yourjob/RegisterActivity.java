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

<<<<<<< HEAD
import androidx.annotation.NonNull;
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

<<<<<<< HEAD
    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
=======
    EditText emailInput, passwordInput, confirmPasswordInput;
    Button registerButton;
    TextView goToLogin;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82

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
<<<<<<< HEAD
        progressBar = findViewById(R.id.regProgressBar);
        TextView goToLogin = findViewById(R.id.goToLoginText);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
=======
        goToLogin = findViewById(R.id.goToLoginText);
        progressBar = findViewById(R.id.regProgressBar);

        registerButton.setOnClickListener(v -> registerUser());
        goToLogin.setOnClickListener(v -> finish());
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
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

<<<<<<< HEAD
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }

<<<<<<< HEAD
        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);
=======
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.GONE);
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
<<<<<<< HEAD
                            User user = new User(userId, email);
                            
                            mDatabase.child("users").child(userId).setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        progressBar.setVisibility(View.GONE);
                                        if (dbTask.isSuccessful()) {
                                            firebaseUser.sendEmailVerification();
                                            Toast.makeText(RegisterActivity.this, "Account created! Verify your email.", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(RegisterActivity.this, VerifyEmailActivity.class));
                                            finish();
                                        } else {
                                            registerButton.setEnabled(true);
                                            Toast.makeText(RegisterActivity.this, "Database error: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
=======
                            User newUser = new User(userId, email);
                            
                            // Save user to DB
                            mDatabase.child("users").child(userId).setValue(newUser)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            firebaseUser.sendEmailVerification()
                                                .addOnCompleteListener(vTask -> {
                                                    progressBar.setVisibility(View.GONE);
                                                    Intent intent = new Intent(RegisterActivity.this, VerifyEmailActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                });
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            registerButton.setVisibility(View.VISIBLE);
                                            Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
                                        }
                                    });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
<<<<<<< HEAD
                        registerButton.setEnabled(true);
                        String error = task.getException() != null ? task.getException().getMessage() : "Auth failed";
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + error, Toast.LENGTH_LONG).show();
=======
                        registerButton.setVisibility(View.VISIBLE);
                        Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
                    }
                });
    }
}
