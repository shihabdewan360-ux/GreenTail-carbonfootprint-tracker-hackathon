package com.example.greentail_hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RedeemPointsActivity extends AppCompatActivity {

    private SeekBar pointSeekBar;
    private TextView tvBalanceRM;
    private android.widget.Button btnRedeem;

    private DatabaseReference userRef;
    private String userId;
    private int userCurrentPoints = 0;
    private double userCurrentWallet = 0.0;

    // Replace with your actual Realtime Database URL
    private final String DB_URL = "https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_points);

        // 1. Initialize UI Elements from your XML
        tvBalanceRM = findViewById(R.id.tvBalanceRM);
        pointSeekBar = findViewById(R.id.pointSeekBar);
        btnRedeem = findViewById(R.id.btnRedeem);

        // 2. Firebase Setup
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance(DB_URL).getReference("users").child(userId);
            fetchUserData();
        }

        // 3. SeekBar Logic
        pointSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Calculation: 1 point = RM 0.01
                double convertedRM = progress * 0.01;
                tvBalanceRM.setText(String.format("%.2f", convertedRM));
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // 4. Redeem Button Logic
        btnRedeem.setOnClickListener(v -> {
            int pointsToRedeem = pointSeekBar.getProgress();
            double rmToAdd = pointsToRedeem * 0.01;

            if (pointsToRedeem > 0) {
                processRedemption(pointsToRedeem, rmToAdd);
            } else {
                Toast.makeText(this, "Please select points to redeem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get current points
                    Integer pts = snapshot.child("taliPoints").getValue(Integer.class);
                    userCurrentPoints = (pts != null) ? pts : 0;

                    // Get current wallet balance
                    Double wallet = snapshot.child("walletBalance").getValue(Double.class);
                    userCurrentWallet = (wallet != null) ? wallet : 0.0;

                    // Set SeekBar Max based on user points (limited to 30,000 per month as per your UI)
                    int maxPossible = Math.min(userCurrentPoints, 30000);
                    pointSeekBar.setMax(maxPossible);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void processRedemption(int points, double amount) {
        int newPoints = userCurrentPoints - points;
        double newWalletBalance = userCurrentWallet + amount;

        // Update Firebase
        userRef.child("taliPoints").setValue(newPoints);
        userRef.child("walletBalance").setValue(newWalletBalance)
                .addOnSuccessListener(aVoid -> {
                    // Navigate to success screen
                    Intent intent = new Intent(RedeemPointsActivity.this, RedemptionDoneActivity.class);
                    intent.putExtra("POINTS_REDEEMED", points);
                    intent.putExtra("MONEY_GAINED", amount);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Redemption failed. Try again.", Toast.LENGTH_SHORT).show();
                });
    }
}