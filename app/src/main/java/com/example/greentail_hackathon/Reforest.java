package com.example.greentail_hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class Reforest extends AppCompatActivity {

    private TextView taliPointsTextView, tvPoints;
    private ImageButton btnPlus, btnMinus;
    private Button btnSpend;

    private DatabaseReference databaseRef;
    private String userId;

    private int availablePoints = 0; // Current balance from Firebase
    private int selectedPoints = 10;  // Default selection set to 10

    // THE CRITICAL URL FIX
    private final String DB_URL = "https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reforest);

        // Initialize views
        taliPointsTextView = findViewById(R.id.taliPoints);
        tvPoints = findViewById(R.id.tv_points);
        btnPlus = findViewById(R.id.imageButton);
        btnMinus = findViewById(R.id.imageButton2);
        btnSpend = findViewById(R.id.btn_spend);

        // Firebase setup
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            // FIXED: Using Singapore URL and lowercase "users"
            databaseRef = FirebaseDatabase.getInstance(DB_URL).getReference("users").child(userId);

            // Load Tailpoints from Firebase
            loadTailpointsData();
        } else {
            Log.e("Reforest", "User not logged in!");
        }

        // Plus button click
        btnPlus.setOnClickListener(v -> {
            if (selectedPoints < availablePoints) {
                selectedPoints++;
                tvPoints.setText(String.valueOf(selectedPoints));
            } else {
                Toast.makeText(this, "Not enough points!", Toast.LENGTH_SHORT).show();
            }
        });

        // Minus button click
        btnMinus.setOnClickListener(v -> {
            if (selectedPoints > 0) {
                selectedPoints--;
                tvPoints.setText(String.valueOf(selectedPoints));
            }
        });

        // Spend button click
        btnSpend.setOnClickListener(v -> {
            if (selectedPoints > 0 && selectedPoints <= availablePoints) {
                int newBalance = availablePoints - selectedPoints;

                // Update in Firebase
                databaseRef.child("taliPoints").setValue(newBalance)
                        .addOnSuccessListener(aVoid -> {
                            int spentPoints = selectedPoints;

                            // Redirect to ReforestDone activity
                            Intent intent = new Intent(Reforest.this, ReforestDone.class);
                            intent.putExtra("spent_points", spentPoints);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> Log.e("Firebase", "Update failed: " + e.getMessage()));
            } else if (selectedPoints == 0) {
                Toast.makeText(this, "Please select points to donate", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTailpointsData() {
        databaseRef.child("taliPoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer taliValue = snapshot.getValue(Integer.class);
                if (taliValue != null) {
                    availablePoints = taliValue;
                    taliPointsTextView.setText(String.valueOf(availablePoints));

                    // Default logic: set to 10 if they have it, otherwise set to whatever they have
                    if (availablePoints >= 10) {
                        selectedPoints = 10;
                    } else {
                        selectedPoints = availablePoints;
                    }
                    tvPoints.setText(String.valueOf(selectedPoints));

                } else {
                    availablePoints = 0;
                    selectedPoints = 0;
                    taliPointsTextView.setText("0");
                    tvPoints.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
    }
}