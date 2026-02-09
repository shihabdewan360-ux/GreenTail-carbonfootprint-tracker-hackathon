package com.example.greentail_hackathon;

import androidx.activity.EdgeToEdge;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashBoard extends AppCompatActivity {

    private TextView userName, monthYear, co2Saved, taliPoints;
    private View homeBar, travelBar, foodBar, othersBar;
    private DatabaseReference databaseRef;
    private String userId;

    private int categoriesLoaded = 0;
    private static final int TOTAL_CATEGORIES = 4;
    private float homeEmission = 0, travelEmission = 0, foodEmission = 0, othersEmission = 0;

    // THE NECESSARY URL FIX
    private final String DB_URL = "https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app";

    // Fixed bar height values
    private static final float BAR_HEIGHT_FOR_0_5_TONS = 100f;
    private static final float BAR_HEIGHT_FOR_1_0_TONS = 200f;
    private static final float BAR_HEIGHT_FOR_1_5_TONS = 300f;
    private static final float BAR_HEIGHT_FOR_2_0_TONS = 400f;
    private static final float BAR_HEIGHT_FOR_2_5_TONS = 500f;
    private static final float BAR_HEIGHT_FOR_3_0_TONS = 600f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        userName = findViewById(R.id.userName);
        monthYear = findViewById(R.id.monthYear);
        co2Saved = findViewById(R.id.co2Saved);
        taliPoints = findViewById(R.id.taliPoints);
        homeBar = findViewById(R.id.homeBar);
        travelBar = findViewById(R.id.travelBar);
        foodBar = findViewById(R.id.foodBar);
        othersBar = findViewById(R.id.othersBar);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton settingsButton = findViewById(R.id.settingsButton);

        // Set current month/year
        setCurrentDate();

        // Firebase initialization
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance(DB_URL).getReference("users").child(userId);

        // Button listeners
        backButton.setOnClickListener(v -> finish());
        settingsButton.setOnClickListener(v -> openSettings());

        // Load user data (Name, Points, Total CO2)
        loadUserData();

        // Load emission data for the chart
        loadEmissionData();
    }

    private void setCurrentDate() {
        String currentDate = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
        monthYear.setText(currentDate);
    }

    private void loadUserData() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // --- DYNAMIC NAME FIX ---
                    String nameFromDb = dataSnapshot.child("firstName").getValue(String.class);
                    if (nameFromDb == null) {
                        nameFromDb = dataSnapshot.child("name").getValue(String.class); // Fallback if you used "name"
                    }

                    if (nameFromDb != null) {
                        userName.setText(nameFromDb);
                    } else {
                        userName.setText("User"); // Default if nothing is found
                    }

                    // --- CO2 & POINTS ---
                    Double co2Value = dataSnapshot.child("total_footprint").getValue(Double.class);
                    Integer taliValue = dataSnapshot.child("taliPoints").getValue(Integer.class);

                    if (co2Value == null) {
                        co2Value = dataSnapshot.child("totalCO2Saved").getValue(Double.class);
                    }

                    co2Saved.setText(String.format("%.2f Tons", co2Value != null ? co2Value : 0.0));
                    taliPoints.setText(String.valueOf(taliValue != null ? taliValue : 0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "User data error: " + databaseError.getMessage());
            }
        });
    }

    private void loadEmissionData() {
        DatabaseReference surveysRef = FirebaseDatabase.getInstance(DB_URL).getReference("surveys");
        categoriesLoaded = 0;

        // Home emissions
        surveysRef.child("home").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) homeEmission = getFloatValue(snapshot);
                checkAllCategoriesLoaded();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { checkAllCategoriesLoaded(); }
        });

        // Travel emissions
        surveysRef.child("travel").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) travelEmission = getFloatValue(snapshot);
                checkAllCategoriesLoaded();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { checkAllCategoriesLoaded(); }
        });

        // Food emissions
        surveysRef.child("food").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) foodEmission = getFloatValue(snapshot);
                checkAllCategoriesLoaded();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { checkAllCategoriesLoaded(); }
        });

        // Others emissions
        surveysRef.child("others").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) othersEmission = getFloatValue(snapshot);
                checkAllCategoriesLoaded();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { checkAllCategoriesLoaded(); }
        });
    }

    private float getFloatValue(DataSnapshot snapshot) {
        Object val = null;
        if (snapshot.hasChild("annualEmissions")) val = snapshot.child("annualEmissions").getValue();
        else if (snapshot.hasChild("footprint")) val = snapshot.child("footprint").getValue();
        else if (snapshot.hasChild("carbon_footprint")) val = snapshot.child("carbon_footprint").getValue();

        if (val instanceof Double) return ((Double) val).floatValue();
        if (val instanceof Long) return ((Long) val).floatValue();
        if (val instanceof Float) return (Float) val;
        return 0f;
    }

    private void checkAllCategoriesLoaded() {
        categoriesLoaded++;
        if (categoriesLoaded >= TOTAL_CATEGORIES) {
            updateChart();
        }
    }

    private void updateChart() {
        setBarHeight(homeBar, homeEmission);
        setBarHeight(travelBar, travelEmission);
        setBarHeight(foodBar, foodEmission);
        setBarHeight(othersBar, othersEmission);
    }

    private void setBarHeight(View bar, float tons) {
        float heightDp;
        if (tons <= 0.5f) heightDp = BAR_HEIGHT_FOR_0_5_TONS * (tons / 0.5f);
        else if (tons <= 1.0f) heightDp = BAR_HEIGHT_FOR_1_0_TONS * (tons / 1.0f);
        else if (tons <= 1.5f) heightDp = BAR_HEIGHT_FOR_1_5_TONS * (tons / 1.5f);
        else if (tons <= 2.0f) heightDp = BAR_HEIGHT_FOR_2_0_TONS * (tons / 2.0f);
        else if (tons <= 2.5f) heightDp = BAR_HEIGHT_FOR_2_5_TONS * (tons / 2.5f);
        else heightDp = BAR_HEIGHT_FOR_3_0_TONS * (tons / 3.0f);

        if (heightDp < 10) heightDp = 10;
        if (heightDp > BAR_HEIGHT_FOR_3_0_TONS) heightDp = BAR_HEIGHT_FOR_3_0_TONS;

        float scale = getResources().getDisplayMetrics().density;
        int heightPx = (int) (heightDp * scale + 0.5f);

        bar.getLayoutParams().height = heightPx;
        bar.requestLayout();
    }

    private void openSettings() {
        // Implement settings activity if needed
    }
}