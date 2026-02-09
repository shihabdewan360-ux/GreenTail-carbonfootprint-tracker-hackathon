package com.example.greentail_hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class connect extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final String DB_URL = "https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connect);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();

        // Initialize UI for Group Code (Matching your XML screenshot)
        EditText groupCodeInput = findViewById(R.id.groupCodeInput);
        ImageView btnJoinGroup = findViewById(R.id.btnJoinGroup); // Make sure the arrow image has this ID in XML

        if (btnJoinGroup != null) {
            btnJoinGroup.setOnClickListener(v -> {
                String code = groupCodeInput.getText().toString().trim();
                if (!code.isEmpty()) {
                    joinOrganization(code);
                } else {
                    Toast.makeText(this, "Please enter a group code", Toast.LENGTH_SHORT).show();
                }
            });
        }

        setupFooter();
    }

    private void joinOrganization(String enteredCode) {
        // Search all users to find the one who owns this groupCode
        mDatabase.child("users").orderByChild("groupCode").equalTo(enteredCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // We found the organization!
                            // The key of the snapshot is the Organization Admin's UID
                            String orgAdminId = "";
                            for (DataSnapshot child : snapshot.getChildren()) {
                                orgAdminId = child.getKey();
                            }

                            // Link the current user to this Org Admin
                            String currentUserId = mAuth.getCurrentUser().getUid();
                            mDatabase.child("users").child(currentUserId).child("linkedOrgId").setValue(orgAdminId)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(connect.this, "Successfully connected to Organization!", Toast.LENGTH_LONG).show();
                                        // Optional: Redirect to home or refresh
                                        startActivity(new Intent(connect.this, Main1.class));
                                        finish();
                                    });
                        } else {
                            Toast.makeText(connect.this, "Invalid Code. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(connect.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupFooter() {
        ImageView home = findViewById(R.id.home);
        ImageView search = findViewById(R.id.search);
        ImageView progress = findViewById(R.id.progress);
        ImageView reward = findViewById(R.id.reward);

        home.setOnClickListener(v -> startActivity(new Intent(this, Main1.class)));
        search.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        progress.setOnClickListener(v -> startActivity(new Intent(this, Progress.class)));
        reward.setOnClickListener(v -> startActivity(new Intent(this, Reward.class)));
    }
}