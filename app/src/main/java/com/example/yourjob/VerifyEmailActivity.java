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
<<<<<<< HEAD
    TextView backToLoginBtn, subtitleText;
=======
    TextView backToLoginBtn;
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
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
<<<<<<< HEAD
        subtitleText = findViewById(R.id.verifyEmailSubtitle);
        progressBar = findViewById(R.id.verifyProgressBar);

        checkVerificationBtn.setOnClickListener(v -> checkVerificationStatus());
=======
        progressBar = findViewById(R.id.verifyProgressBar);

        checkVerificationBtn.setOnClickListener(v -> checkVerificationStatus());
        
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        resendEmailBtn.setOnClickListener(v -> resendVerificationEmail());

        backToLoginBtn.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(VerifyEmailActivity.this, MainActivity.class));
            finish();
        });
<<<<<<< HEAD
        
        updateUIStrings();
    }

    private void updateUIStrings() {
        checkVerificationBtn.setText(R.string.i_verified);
        resendEmailBtn.setText(R.string.resend_email);
        backToLoginBtn.setText(R.string.back_to_login);
        subtitleText.setText(R.string.verify_email_msg);
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
    }

    private void checkVerificationStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            user.reload().addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (user.isEmailVerified()) {
<<<<<<< HEAD
                    Toast.makeText(VerifyEmailActivity.this, R.string.success_save, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VerifyEmailActivity.this, RoleSelectActivity.class));
                    finish();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, R.string.email_not_verified, Toast.LENGTH_LONG).show();
=======
                    Toast.makeText(VerifyEmailActivity.this, "Email Verified!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VerifyEmailActivity.this, RoleSelectActivity.class));
                    finish();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Email not verified yet. Please check your inbox.", Toast.LENGTH_LONG).show();
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
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
<<<<<<< HEAD
                    Toast.makeText(VerifyEmailActivity.this, R.string.success_apply, Toast.LENGTH_SHORT).show();
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Error";
                    Toast.makeText(VerifyEmailActivity.this, error, Toast.LENGTH_SHORT).show();
=======
                    Toast.makeText(VerifyEmailActivity.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
                }
            });
        }
    }
}
