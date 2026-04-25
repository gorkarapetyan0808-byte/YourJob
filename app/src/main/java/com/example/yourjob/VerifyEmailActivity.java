package com.example.yourjob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends AppCompatActivity {

    Button checkVerificationBtn, resendEmailBtn;
    TextView backToLoginBtn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        mAuth = FirebaseAuth.getInstance();

        checkVerificationBtn = findViewById(R.id.checkVerificationBtn);
        resendEmailBtn = findViewById(R.id.resendEmailBtn);
        backToLoginBtn = findViewById(R.id.backToLoginBtn);
        progressBar = findViewById(R.id.verifyProgressBar);

        checkVerificationBtn.setOnClickListener(v -> checkVerificationStatus());
        
        resendEmailBtn.setOnClickListener(v -> resendVerificationEmail());

        backToLoginBtn.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(VerifyEmailActivity.this, MainActivity.class));
            finish();
        });
    }

    private void checkVerificationStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            user.reload().addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (user.isEmailVerified()) {
                    Toast.makeText(VerifyEmailActivity.this, "Email Verified!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VerifyEmailActivity.this, RoleSelectActivity.class));
                    finish();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Email not verified yet. Please check your inbox.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void resendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            user.sendEmailVerification().addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(VerifyEmailActivity.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
