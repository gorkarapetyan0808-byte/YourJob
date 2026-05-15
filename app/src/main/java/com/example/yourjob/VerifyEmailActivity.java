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
    TextView backToLoginBtn, subtitleText;
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
        subtitleText = findViewById(R.id.verifyEmailSubtitle);
        progressBar = findViewById(R.id.verifyProgressBar);

        checkVerificationBtn.setOnClickListener(v -> checkVerificationStatus());
        resendEmailBtn.setOnClickListener(v -> resendVerificationEmail());

        backToLoginBtn.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(VerifyEmailActivity.this, MainActivity.class));
            finish();
        });
        
        updateUIStrings();
    }

    private void updateUIStrings() {
        checkVerificationBtn.setText(R.string.i_verified);
        resendEmailBtn.setText(R.string.resend_email);
        backToLoginBtn.setText(R.string.back_to_login);
        subtitleText.setText(R.string.verify_email_msg);
    }

    private void checkVerificationStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            user.reload().addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (user.isEmailVerified()) {
                    Toast.makeText(VerifyEmailActivity.this, R.string.success_save, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VerifyEmailActivity.this, RoleSelectActivity.class));
                    finish();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, R.string.email_not_verified, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(VerifyEmailActivity.this, R.string.success_apply, Toast.LENGTH_SHORT).show();
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Error";
                    Toast.makeText(VerifyEmailActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
