package com.example.greentail_hackathon;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeServey extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private SeekBar householdSeekBar;
    private TextView householdValue;
    private RadioGroup bedroomsGroup, heatingGroup, renewableGroup, appliancesGroup, laundryGroup;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeservey);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        householdValue = findViewById(R.id.householdValue);
        householdSeekBar = findViewById(R.id.householdSeekBar);
        bedroomsGroup = findViewById(R.id.bedroomsGroup);
        heatingGroup = findViewById(R.id.heatingGroup);
        renewableGroup = findViewById(R.id.renewableGroup);
        appliancesGroup = findViewById(R.id.appliancesGroup);
        laundryGroup = findViewById(R.id.laundryGroup);
        submitButton = findViewById(R.id.submitButton);

        householdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                householdValue.setText(String.valueOf(progress + 1));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        submitButton.setOnClickListener(v -> {
            if (validateSelections()) {
                saveSurveyData();
            } else {
                Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateSelections() {
        return bedroomsGroup.getCheckedRadioButtonId() != -1 &&
                heatingGroup.getCheckedRadioButtonId() != -1 &&
                renewableGroup.getCheckedRadioButtonId() != -1 &&
                appliancesGroup.getCheckedRadioButtonId() != -1 &&
                laundryGroup.getCheckedRadioButtonId() != -1;
    }

    // --- 2025 ANNUAL FACTORS (kg CO2e / year) ---

    private double getHouseholdEmission(int count) {
        // Base overhead for a home divided by residents (2025 shared utility model)
        switch (count) {
            case 1: return 850.0;
            case 2: return 520.0;
            case 3: return 380.0;
            case 4: return 310.0;
            case 5: return 270.0;
            default: return 230.0;
        }
    }

    private double getBedroomsEmission(String selection) {
        // Annual floor space heating/lighting requirement
        switch (selection) {
            case "Studio": return 420.0;
            case "1": return 650.0;
            case "2": return 980.0;
            case "3+": return 1450.0;
            default: return 0;
        }
    }

    private double getHeatingEmission(String selection) {
        // 2025 DEFRA Factors (Direct + Upstream)
        switch (selection) {
            case "Gas boiler": return 2150.0;
            case "Gas condenser": return 1850.0;
            case "Oil": return 3100.0;
            case "Electricity": return 1200.0; // Improved due to grid renewables
            case "Ground-source heat pump": return 450.0;
            default: return 0;
        }
    }

    private double getRenewableEmission(String selection) {
        switch (selection) {
            case "Yes": return -450.0; // Reduction bonus
            case "No": return 300.0;
            case "Not sure": return 150.0;
            default: return 0;
        }
    }

    private double getAppliancesEmission(String selection) {
        switch (selection) {
            case "Always": return -100.0; // Bonus for EnergyStar efficiency
            case "Most of the time": return 50.0;
            case "Rarely": return 180.0;
            case "Never": return 350.0;
            default: return 0;
        }
    }

    private double getLaundryEmission(String selection) {
        switch (selection) {
            case "<2 times / week": return 65.0;
            case "3–4 times / week": return 145.0;
            case "5+ times / week": return 290.0;
            default: return 0;
        }
    }

    private void saveSurveyData() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";

        int householdCount = Integer.parseInt(householdValue.getText().toString());
        String bedrooms = getSelectedRadioText(bedroomsGroup);
        String heating = getSelectedRadioText(heatingGroup);
        String renewable = getSelectedRadioText(renewableGroup);
        String appliances = getSelectedRadioText(appliancesGroup);
        String laundry = getSelectedRadioText(laundryGroup);

        double h_E = getHouseholdEmission(householdCount);
        double b_E = getBedroomsEmission(bedrooms);
        double ht_E = getHeatingEmission(heating);
        double r_E = getRenewableEmission(renewable);
        double a_E = getAppliancesEmission(appliances);
        double l_E = getLaundryEmission(laundry);

        double annualKgTotal = h_E + b_E + ht_E + r_E + a_E + l_E;
        if (annualKgTotal < 0) annualKgTotal = 0;

        double annualTonnes = annualKgTotal / 1000.0;
        double weeklyKg = annualKgTotal / 52.0;

        SurveyData surveyData = new SurveyData(
                householdCount, bedrooms, heating, renewable, appliances, laundry,
                h_E, b_E, ht_E, r_E, a_E, l_E,
                weeklyKg, annualTonnes
        );

        mDatabase.child("surveys").child("home").child(userId).setValue(surveyData);

        Intent intent = new Intent(HomeServey.this, TravelServey.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private String getSelectedRadioText(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        return (radioButton != null) ? radioButton.getText().toString() : "N/A";
    }

    public static class SurveyData {
        public int householdCount;
        public String bedrooms, heatingSystem, renewableElectricity, appliancesUsage, laundryFrequency;
        public double householdEmission, bedroomsEmission, heatingEmission, renewableEmission, appliancesEmission, laundryEmission;
        public double weeklyEmissions, annualEmissions;
        public long timestamp = System.currentTimeMillis();

        public SurveyData() {}

        public SurveyData(int householdCount, String bedrooms, String heatingSystem,
                          String renewableElectricity, String appliancesUsage, String laundryFrequency,
                          double hE, double bE, double htE, double rE, double aE, double lE,
                          double weeklyEmissions, double annualEmissions) {
            this.householdCount = householdCount;
            this.bedrooms = bedrooms;
            this.heatingSystem = heatingSystem;
            this.renewableElectricity = renewableElectricity;
            this.appliancesUsage = appliancesUsage;
            this.laundryFrequency = laundryFrequency;
            this.householdEmission = hE;
            this.bedroomsEmission = bE;
            this.heatingEmission = htE;
            this.renewableEmission = rE;
            this.appliancesEmission = aE;
            this.laundryEmission = lE;
            this.weeklyEmissions = weeklyEmissions;
            this.annualEmissions = annualEmissions;
        }
    }
}