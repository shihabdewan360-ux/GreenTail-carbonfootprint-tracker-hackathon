package com.example.greentail_hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final String DB_URL = "https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app";

    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private boolean isOrgSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Capture the mode from MainActivity
        isOrgSignUp = getIntent().getBooleanExtra("IS_ORG", false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        TextView loginText = findViewById(R.id.loginText);
        String text = "Have an account? Log in";
        SpannableString spannableString = new SpannableString(text);

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xFF1B4332);
        spannableString.setSpan(colorSpan, text.indexOf("Log in"), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, text.indexOf("Log in"), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(SignUpActivity.this, SignIn.class));
            }
        };

        spannableString.setSpan(clickableSpan, text.indexOf("Log in"), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginText.setText(spannableString);
        loginText.setMovementMethod(LinkMovementMethod.getInstance());
        loginText.setTextColor(0xFF000000);

        findViewById(R.id.createAccountButton).setOnClickListener(v -> signUpUser());
    }

    private void signUpUser() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();

                        // Logic for generating Organization Group Code
                        String groupCode = "";
                        if (isOrgSignUp) {
                            groupCode = "GT-" + (100000 + new Random().nextInt(900000));
                        }

                        // Create User with organization data
                        User newUser = new User(firstName, lastName, email, isOrgSignUp, groupCode);

                        final String finalGroupCode = groupCode;
                        mDatabase.child("users").child(userId).setValue(newUser)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        if (isOrgSignUp) {
                                            showOrgDialog(finalGroupCode);
                                        } else {
                                            navigateToSignIn();
                                        }
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Database storage failed.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(SignUpActivity.this, "Auth failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showOrgDialog(String code) {
        new AlertDialog.Builder(this)
                .setTitle("Organization Created!")
                .setMessage("Your Group Code is: " + code + "\n\nShare this code with your members so they can join your team in the Connect page.")
                .setCancelable(false)
                .setPositiveButton("Got it", (dialog, which) -> navigateToSignIn())
                .show();
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(SignUpActivity.this, SignIn.class);
        startActivity(intent);
        finish();
    }

    public static class User {
        public String firstName, lastName, email, groupCode, linkedOrgId;
        public boolean isOrganization;
        public double totalCO2Saved;
        public int taliPoints;

        public User() {}

        public User(String firstName, String lastName, String email, boolean isOrganization, String groupCode) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.isOrganization = isOrganization;
            this.groupCode = groupCode;
            this.linkedOrgId = ""; // Default empty
            this.totalCO2Saved = 0.0;
            this.taliPoints = 0;
        }
    }
}