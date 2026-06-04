package com.example.yourjob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {

    private String phoneNumber;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    
    private EditText codeInput;
    private Button verifyBtn;
    private TextView phoneDisplay, resendBtn, systemStatus;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        mAuth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra("phone");

        codeInput = findViewById(R.id.verificationCodeInput);
        verifyBtn = findViewById(R.id.verifyCodeBtn);
        phoneDisplay = findViewById(R.id.phoneDisplay);
        resendBtn = findViewById(R.id.resendCodeBtn);
        progressBar = findViewById(R.id.verifyProgressBar);
        
        phoneDisplay.setText(phoneNumber);

        checkPhoneNumberUniqueness();

        verifyBtn.setOnClickListener(v -> {
            String code = codeInput.getText().toString().trim();
            if (code.length() < 6) {
                codeInput.setError("Enter 6 digit code");
                return;
            }
            verifyCode(code);
        });

        resendBtn.setOnClickListener(v -> resendVerificationCode(phoneNumber));
    }

    private void checkPhoneNumberUniqueness() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/")
                .getReference("users")
                .orderByChild("phone").equalTo(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            boolean alreadyUsed = false;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String existingUid = ds.getKey();
                                if (existingUid != null && !existingUid.equals(mAuth.getUid())) {
                                    alreadyUsed = true;
                                    break;
                                }
                            }

                            if (alreadyUsed) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(PhoneVerificationActivity.this, "This phone number is already linked to another account!", Toast.LENGTH_LONG).show();
                                verifyBtn.setEnabled(false);
                                resendBtn.setEnabled(false);
                            } else {
                                sendVerificationCode(phoneNumber);
                            }
                        } else {
                            sendVerificationCode(phoneNumber);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        sendVerificationCode(phoneNumber);
                    }
                });
    }

    private void sendVerificationCode(String phone) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phone) {
        if (resendToken != null) {
            progressBar.setVisibility(View.VISIBLE);
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(mCallbacks)
                    .setForceResendingToken(resendToken)
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            progressBar.setVisibility(View.GONE);
            String code = credential.getSmsCode();
            if (code != null) {
                codeInput.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            progressBar.setVisibility(View.GONE);
            String message = e.getMessage();
            if (message != null && message.contains("BILLING_NOT_ENABLED")) {
                Toast.makeText(PhoneVerificationActivity.this, "System Check: Real SMS is blocked. Use Test Number or Enable Billing.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PhoneVerificationActivity.this, "Verification failed: " + message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            progressBar.setVisibility(View.GONE);
            verificationId = s;
            resendToken = token;
            Toast.makeText(PhoneVerificationActivity.this, "Code sent successfully!", Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/")
                                    .getReference("users").child(uid).child("isPhoneVerified").setValue(true);
                            FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/")
                                    .getReference("users").child(uid).child("phone").setValue(phoneNumber);
                            
                            Toast.makeText(PhoneVerificationActivity.this, R.string.phone_verified, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(PhoneVerificationActivity.this, "Linking failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
