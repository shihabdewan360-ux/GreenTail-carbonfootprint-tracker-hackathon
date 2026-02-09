package com.example.greentail_hackathon;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TravelServey extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private RadioGroup distanceGroup, transportGroup, vehicleTypeGroup,
            flightsGroup, carpoolGroup, rideHailingGroup, routePlanningGroup;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travelservey);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        distanceGroup = findViewById(R.id.distanceGroup);
        transportGroup = findViewById(R.id.transportGroup);
        vehicleTypeGroup = findViewById(R.id.vehicleTypeGroup);
        flightsGroup = findViewById(R.id.flightsGroup);
        carpoolGroup = findViewById(R.id.carpoolGroup);
        rideHailingGroup = findViewById(R.id.rideHailingGroup);
        routePlanningGroup = findViewById(R.id.routePlanningGroup);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            if (validateSelections()) {
                saveSurveyData();
            } else {
                Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateSelections() {
        return distanceGroup.getCheckedRadioButtonId() != -1 &&
                transportGroup.getCheckedRadioButtonId() != -1 &&
                vehicleTypeGroup.getCheckedRadioButtonId() != -1 &&
                flightsGroup.getCheckedRadioButtonId() != -1 &&
                carpoolGroup.getCheckedRadioButtonId() != -1 &&
                rideHailingGroup.getCheckedRadioButtonId() != -1 &&
                routePlanningGroup.getCheckedRadioButtonId() != -1;
    }

    // --- EMISSION HELPERS (All values in kg CO2e/year based on 2025 DEFRA) ---

    private double getDistanceEmission() {
        int id = distanceGroup.getCheckedRadioButtonId();
        // Based on average 0.165 kg/km (Petrol 2025)
        if (id == R.id.noDriveOption) return 0.0;
        if (id == R.id.lowDistanceOption) return 86.0;    // ~10km/week
        if (id == R.id.mediumDistanceOption) return 300.0; // ~35km/week
        if (id == R.id.highDistanceOption) return 600.0;   // ~70km/week
        return 0;
    }

    private double getTransportEmission() {
        int id = transportGroup.getCheckedRadioButtonId();
        if (id == R.id.walkOption) return 0.0;
        if (id == R.id.bicycleOption) return 13.0;
        if (id == R.id.publicTransportOption) return 223.0;
        if (id == R.id.motorcycleOption) return 283.0;
        if (id == R.id.carOption) return 413.0;
        return 0;
    }

    private double getVehicleTypeEmission() {
        int id = vehicleTypeGroup.getCheckedRadioButtonId();
        // Return factor per KM (will be applied to distance)
        if (id == R.id.electricOption) return 0.040;
        if (id == R.id.hybridOption) return 0.108;
        if (id == R.id.petrolOption) return 0.165;
        if (id == R.id.dieselOption) return 0.170;
        return 0.165; // Default average
    }

    private double getFlightsEmission() {
        int id = flightsGroup.getCheckedRadioButtonId();
        if (id == R.id.noFlightsOption) return 0.0;
        if (id == R.id.fewFlightsOption) return 550.0;
        if (id == R.id.moderateFlightsOption) return 1450.0;
        if (id == R.id.manyFlightsOption) return 3100.0;
        return 0;
    }

    private double getCarpoolMultiplier() {
        int id = carpoolGroup.getCheckedRadioButtonId();
        if (id == R.id.alwaysCarpoolOption) return 0.33;
        if (id == R.id.sometimesCarpoolOption) return 0.50;
        if (id == R.id.rarelyCarpoolOption) return 1.0;
        if (id == R.id.neverCarpoolOption) return 1.0;
        return 1.0;
    }

    private double getRideHailingEmission() {
        int id = rideHailingGroup.getCheckedRadioButtonId();
        if (id == R.id.neverRideOption) return 0.0;
        if (id == R.id.occasionallyOption) return 25.0;
        if (id == R.id.weeklyOption) return 103.0;
        if (id == R.id.dailyOption) return 513.0;
        return 0;
    }

    private double getRoutePlanningMultiplier() {
        int id = routePlanningGroup.getCheckedRadioButtonId();
        if (id == R.id.yesPlanOption) return 0.90;
        if (id == R.id.sometimesPlanOption) return 0.95;
        if (id == R.id.noPlanOption) return 1.0;
        return 1.0;
    }

    private String getSelectedRadioText(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        return (radioButton != null) ? radioButton.getText().toString() : "";
    }

    private void saveSurveyData() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";

        // Logic processing
        double vehicleFactor = getVehicleTypeEmission();
        double baseDriving = getDistanceEmission();
        double carpoolMod = getCarpoolMultiplier();
        double routeMod = getRoutePlanningMultiplier();

        // Calculate adjusted car emissions
        double adjustedCarEmissions = (baseDriving * carpoolMod * routeMod);

        double transE = getTransportEmission();
        double flightsE = getFlightsEmission();
        double rideE = getRideHailingEmission();

        // Total Annual kg
        double annualKgTotal = adjustedCarEmissions + transE + flightsE + rideE;
        double annualTonnes = annualKgTotal / 1000.0;
        double weeklyKg = annualKgTotal / 52.0;

        SurveyData surveyData = new SurveyData(
                getSelectedRadioText(distanceGroup),
                getSelectedRadioText(transportGroup),
                getSelectedRadioText(vehicleTypeGroup),
                getSelectedRadioText(flightsGroup),
                getSelectedRadioText(carpoolGroup),
                getSelectedRadioText(rideHailingGroup),
                getSelectedRadioText(routePlanningGroup),
                adjustedCarEmissions, transE, vehicleFactor,
                flightsE, carpoolMod, rideE, routeMod,
                weeklyKg, annualTonnes
        );

        mDatabase.child("surveys").child("travel").child(userId).setValue(surveyData);

        Intent intent = new Intent(TravelServey.this, FoodSurveyActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public static class SurveyData {
        public String distance, transport, vehicleType, flights, carpool, rideHailing, routePlanning;
        public double distanceEmission, transportEmission, vehicleTypeEmission, flightsEmission, carpoolEmission, rideHailingEmission, routePlanningEmission;
        public double weeklyEmissions, annualEmissions;
        public long timestamp = System.currentTimeMillis();

        public SurveyData() {}

        public SurveyData(String d, String t, String v, String f, String c, String r, String rp,
                          double de, double te, double ve, double fe, double ce, double re, double rpe,
                          double weekly, double annual) {
            this.distance = d; this.transport = t; this.vehicleType = v;
            this.flights = f; this.carpool = c; this.rideHailing = r; this.routePlanning = rp;
            this.distanceEmission = de; this.transportEmission = te;
            this.vehicleTypeEmission = ve; this.flightsEmission = fe;
            this.carpoolEmission = ce; this.rideHailingEmission = re;
            this.routePlanningEmission = rpe;
            this.weeklyEmissions = weekly; this.annualEmissions = annual;
        }
    }
}