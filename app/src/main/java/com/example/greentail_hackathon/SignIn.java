package com.example.greentail_hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    // Declare the EditText fields and Button
    EditText emailEditText, passwordEditText;
    Button loginButton;

    private FirebaseAuth mAuth; // Firebase authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge screen use
        setContentView(R.layout.activity_signin); // Set the layout for SignIn activity

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize the views (EditText and Button)
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Set up the login button action
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered email and password
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate email and password (for simplicity, just check non-empty fields)
                if (email.isEmpty() || password.isEmpty()) {
                    // Show a Toast if either field is empty
                    Toast.makeText(SignIn.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    // Show a Toast if password is less than 8 characters
                    Toast.makeText(SignIn.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                } else {
                    // Attempt to sign in using Firebase Authentication
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignIn.this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign-in success, navigate to the main screen
                                    Intent intent = new Intent(SignIn.this, Main1.class); // Redirect to Main1 activity
                                    startActivity(intent);
                                    finish(); // Finish the SignIn activity so the user can't go back to it
                                } else {
                                    // Sign-in failed, show error message
                                    Toast.makeText(SignIn.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}
