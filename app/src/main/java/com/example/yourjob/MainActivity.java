package com.example.yourjob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginButton;
    TextView goToRegister;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        goToRegister = findViewById(R.id.goToRegisterText);
        progressBar = findViewById(R.id.loginProgressBar);


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserVerificationAndRedirect(currentUser);
        }

        loginButton.setOnClickListener(v -> loginUser());
        goToRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserVerificationAndRedirect(user);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserVerificationAndRedirect(FirebaseUser user) {
        if (user.isEmailVerified()) {
            loadAllDataAndProceed(user.getUid());
        } else {
            mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Boolean isVerifiedDB = snapshot.child("isVerified").getValue(Boolean.class);
                        if (isVerifiedDB != null && isVerifiedDB) {
                            loadAllDataAndProceed(user.getUid());
                        } else {
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(MainActivity.this, VerifyEmailActivity.class));
                            finish();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(MainActivity.this, VerifyEmailActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void loadAllDataAndProceed(String userId) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        

        BusinessManager.loadFromFirebase(this, () -> {

            JobStorage.loadApplicationsFromFirebase(this, () -> {

                proceedToRoleOrMenu(userId);
            });
        });
    }

    private void proceedToRoleOrMenu(String userId) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    if (role == null || role.equals("pending")) {
                        startActivity(new Intent(MainActivity.this, RoleSelectActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
                    }
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this, RoleSelectActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });
    }
}
