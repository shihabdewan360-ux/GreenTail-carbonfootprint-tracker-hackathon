package com.example.greentail_hackathon;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TakeServey extends AppCompatActivity {

    private final String firebaseUrl = "https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_takeservey);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";
        DatabaseReference userRef = FirebaseDatabase.getInstance(firebaseUrl).getReference("users").child(userId);

        // 1. Check if survey is already completed to handle App Restart
        userRef.child("survey_completed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    boolean isCompleted = snapshot.getValue(Boolean.class);
                    if (isCompleted) {
                        // User already did this! Skip to results.
                        startActivity(new Intent(TakeServey.this, Overall.class));
                        finish();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        // 2. Navigation to Home Survey
        CardView homeCard = findViewById(R.id.homeCard);
        homeCard.setOnClickListener(v -> {
            startActivity(new Intent(TakeServey.this, HomeServey.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Add listeners for Travel, Food, and Others here if you have them in your XML
        // Example:
        // findViewById(R.id.travelCard).setOnClickListener(v -> startActivity(new Intent(this, TravelServey.class)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Go back to the main dashboard
        startActivity(new Intent(this, Main1.class));
        finish();
    }
}