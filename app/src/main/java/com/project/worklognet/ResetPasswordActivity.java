package com.project.worklognet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton, btnBack;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Full screen and hide action bar
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        // [End] Full screen and hide action bar

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Link views from XML
        emailEditText = findViewById(R.id.editTextEmail);
        resetPasswordButton = findViewById(R.id.buttonResetPassword);
        progressBar = findViewById(R.id.progressBar);

        // Back button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Set onClick listener for the reset password button
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ResetPasswordActivity.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show progress bar and disable button while sending the reset email
                progressBar.setVisibility(View.VISIBLE);
                resetPasswordButton.setEnabled(false);

                // Send password reset email
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                resetPasswordButton.setEnabled(true);

                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "Reset email sent successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
