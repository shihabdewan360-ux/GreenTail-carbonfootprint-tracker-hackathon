package com.example.greentail_hackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;

public class Overall extends AppCompatActivity {

    // INDUSTRY STANDARDS 2026
    private static final double MAX_BAR_VALUE = 8.0; // Scaled higher to show impact depth
    private static final double SAFE_BAR_TARGET = 2.5; // Paris Agreement 2030 goal per person

    private TextView homeValue, travelValue, foodValue, othersValue, overallText, overallValue, updateButton;
    private View homeProgress, travelProgress, foodProgress, othersProgress;
    private double homeFootprint = 0.0;
    private double travelFootprint = 0.0;
    private double foodFootprint = 0.0;
    private double othersFootprint = 0.0;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private FirebaseAuth mAuth;
    private int categoriesLoaded = 0;
    private final int TOTAL_CATEGORIES = 4;

    private final String DB_URL = "https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_overall);

        mAuth = FirebaseAuth.getInstance();

        homeValue = findViewById(R.id.homeValue);
        travelValue = findViewById(R.id.travelValue);
        foodValue = findViewById(R.id.foodValue);
        othersValue = findViewById(R.id.othersValue);
        overallText = findViewById(R.id.overallText);
        overallValue = findViewById(R.id.overallValue);
        updateButton = findViewById(R.id.updateButton);

        // --- FIXED ALIGNMENT PROGRAMMATICALLY ---
        overallText.setGravity(Gravity.CENTER);
        overallValue.setGravity(Gravity.CENTER);

        homeProgress = findViewById(R.id.homeProgressFill);
        travelProgress = findViewById(R.id.travelProgressFill);
        foodProgress = findViewById(R.id.foodProgressFill);
        othersProgress = findViewById(R.id.othersProgressFill);

        updateButton.setOnClickListener(v -> {
            String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";
            FirebaseDatabase.getInstance(DB_URL).getReference("users")
                    .child(userId)
                    .child("survey_completed")
                    .setValue(false)
                    .addOnSuccessListener(aVoid -> {
                        Intent intent = new Intent(Overall.this, TakeServey.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error resetting status", Toast.LENGTH_SHORT).show());
        });

        updateUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchFootprintData();
    }

    private void fetchFootprintData() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";
        categoriesLoaded = 0;
        homeFootprint = 0.0; travelFootprint = 0.0; foodFootprint = 0.0; othersFootprint = 0.0;

        DatabaseReference dbRef = FirebaseDatabase.getInstance(DB_URL).getReference("surveys");
        fetchCategoryData(dbRef.child("home"), userId, "home");
        fetchCategoryData(dbRef.child("travel"), userId, "travel");
        fetchCategoryData(dbRef.child("food"), userId, "food");
        fetchCategoryData(dbRef.child("others"), userId, "others");
    }

    private void fetchCategoryData(DatabaseReference categoryRef, String userId, String categoryName) {
        categoryRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double footprint = 0.0;
                    if (snapshot.child("annualEmissions").exists()) {
                        footprint = getDoubleValue(snapshot.child("annualEmissions"));
                    } else if (snapshot.child("annualTonnes").exists()) {
                        footprint = getDoubleValue(snapshot.child("annualTonnes"));
                    } else if (snapshot.child("carbon_footprint").exists()) {
                        footprint = getDoubleValue(snapshot.child("carbon_footprint"));
                    }

                    switch (categoryName) {
                        case "home": homeFootprint = footprint; break;
                        case "travel": travelFootprint = footprint; break;
                        case "food": foodFootprint = footprint; break;
                        case "others": othersFootprint = footprint; break;
                    }
                }
                categoriesLoaded++;
                if (categoriesLoaded >= TOTAL_CATEGORIES) {
                    updateUI();
                    updateTotalInDatabase();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { categoriesLoaded++; }
        });
    }

    private double getDoubleValue(DataSnapshot snapshot) {
        Object value = snapshot.getValue();
        if (value instanceof Double) return (Double) value;
        if (value instanceof Long) return ((Long) value).doubleValue();
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        return 0.0;
    }

    private void updateUI() {
        homeValue.setText(formatValue(homeFootprint));
        travelValue.setText(formatValue(travelFootprint));
        foodValue.setText(formatValue(foodFootprint));
        othersValue.setText(formatValue(othersFootprint));

        double totalTons = homeFootprint + travelFootprint + foodFootprint + othersFootprint;

        // DYNAMIC COLOR FEEDBACK
        if (totalTons <= SAFE_BAR_TARGET) {
            overallText.setTextColor(Color.parseColor("#4CAF50")); // Green (Safe)
        } else if (totalTons <= 5.0) {
            overallText.setTextColor(Color.parseColor("#FBC02D")); // Yellow (Average)
        } else {
            overallText.setTextColor(Color.parseColor("#D32F2F")); // Red (High)
        }

        overallText.setText("Total: " + formatValue(totalTons) + " Tonnes/Year");
        overallValue.setText(getImpactDescription(totalTons));
        updateProgressBarWithDelay();
    }

    private void updateTotalInDatabase() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";
        if (!userId.equals("anonymous")) {
            double total = homeFootprint + travelFootprint + foodFootprint + othersFootprint;
            FirebaseDatabase.getInstance(DB_URL).getReference("users").child(userId).child("total_footprint").setValue(total);
        }
    }

    private String formatValue(double value) {
        return value == 0 ? "0.0" : decimalFormat.format(value);
    }

    private String getImpactDescription(double totalTons) {
        if (totalTons <= 0) return "Complete surveys to see your impact!";

        double iceMelted = totalTons * 3.0; // 1 Tonne = 3m² melt
        String iceText;

        // Visual Object Comparison
        if (iceMelted < 15) {
            int beds = (int) Math.max(1, Math.round(iceMelted / 4.0));
            iceText = String.format("This melts enough Arctic ice to cover %d King Size %s.",
                    beds, beds == 1 ? "Bed" : "Beds");
        } else {
            int billboards = (int) Math.max(1, Math.round(iceMelted / 45.0));
            iceText = String.format("This melts enough Arctic ice to cover %d highway %s.",
                    billboards, billboards == 1 ? "billboard" : "billboards");
        }

        // Safe Bar Context (Dynamic Message)
        String safeBarText;
        if (totalTons <= SAFE_BAR_TARGET) {
            safeBarText = "\n\nGreat job! You are within the safe limit of 2.5t.";
        } else {
            double diff = totalTons / SAFE_BAR_TARGET;
            safeBarText = String.format("\n\nYou are %.1fx above the sustainable limit (2.5t).", diff);
        }

        return iceText + safeBarText;
    }

    private void updateProgressBarWithDelay() {
        homeProgress.post(() -> {
            updateProgressBar(homeProgress, homeFootprint);
            updateProgressBar(travelProgress, travelFootprint);
            updateProgressBar(foodProgress, foodFootprint);
            updateProgressBar(othersProgress, othersFootprint);
        });
    }

    private void updateProgressBar(View progressBar, double tonsValue) {
        float percentage = (tonsValue <= 0) ? 0.01f : (float) Math.min(tonsValue / MAX_BAR_VALUE, 1.0f);
        ViewGroup parent = (ViewGroup) progressBar.getParent();
        int parentWidth = parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight();
        ViewGroup.LayoutParams params = progressBar.getLayoutParams();
        params.width = (int) (parentWidth * percentage);
        progressBar.setLayoutParams(params);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Main1.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}