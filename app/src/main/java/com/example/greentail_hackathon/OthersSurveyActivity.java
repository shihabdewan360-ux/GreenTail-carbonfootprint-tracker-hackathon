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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OthersSurveyActivity extends AppCompatActivity {

    // 2025 Annual kg CO2e factors (applied per week/activity)
    // Screen time factors: kg CO2e per hour/day annually
    private static final double[] SCREEN_TIME_FACTORS = {0, 15, 31, 46, 62, 78, 93, 109, 125, 140, 156, 172, 200};

    // Clothing/Shopping: Annual impact of shopping habits
    private static final double[] SHOPPING_FREQ_FACTORS = {52.0, 145.0, 312.0, 780.0};

    // Recycling/Waste: Now correctly represented as REDUCTIONS (negative) or costs
    private static final double[] RECYCLE_FACTORS = {-154.0, -90.0, -45.0, 50.0};
    private static final double[] PLASTIC_FACTORS = {110.0, 55.0, 20.0, -15.0};
    private static final double[] ECO_BRAND_FACTORS = {-60.0, 80.0, 15.0};
    private static final double[] COMPOST_FACTORS = {-120.0, -40.0, 30.0};
    private static final double[] DISPOSAL_FACTORS = {-30.0, -20.0, 100.0, -25.0};

    private SeekBar screenHoursSeekBar;
    private TextView screenHoursValue;
    private RadioGroup ecoBrandsGroup, shoppingFrequencyGroup, recycleGroup, plasticGroup, compostGroup, disposalGroup;
    private Button submitButton;

    private double annualEmissions = 0; // Stored as Tonnes
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_othersservey);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";
        mDatabase = FirebaseDatabase.getInstance("https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        screenHoursValue = findViewById(R.id.screenHoursValue);
        screenHoursSeekBar = findViewById(R.id.screenHoursSeekBar);
        ecoBrandsGroup = findViewById(R.id.ecoBrandsGroup);
        shoppingFrequencyGroup = findViewById(R.id.shoppingFrequencyGroup);
        recycleGroup = findViewById(R.id.recycleGroup);
        plasticGroup = findViewById(R.id.plasticGroup);
        compostGroup = findViewById(R.id.compostGroup);
        disposalGroup = findViewById(R.id.disposalGroup);
        submitButton = findViewById(R.id.submitButton);

        screenHoursSeekBar.setMax(SCREEN_TIME_FACTORS.length - 1);
        screenHoursSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                screenHoursValue.setText(String.valueOf(progress));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        submitButton.setOnClickListener(v -> {
            if (validateSelections()) {
                calculateFootprint();
                saveSurveyData();
            } else {
                Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateSelections() {
        return ecoBrandsGroup.getCheckedRadioButtonId() != -1 &&
                shoppingFrequencyGroup.getCheckedRadioButtonId() != -1 &&
                recycleGroup.getCheckedRadioButtonId() != -1 &&
                plasticGroup.getCheckedRadioButtonId() != -1 &&
                compostGroup.getCheckedRadioButtonId() != -1 &&
                disposalGroup.getCheckedRadioButtonId() != -1;
    }

    private void calculateFootprint() {
        double annualKg = 0;

        // Digital (Annual kg)
        int screenHours = screenHoursSeekBar.getProgress();
        annualKg += (screenHours < SCREEN_TIME_FACTORS.length) ? SCREEN_TIME_FACTORS[screenHours] : SCREEN_TIME_FACTORS[SCREEN_TIME_FACTORS.length - 1];

        // Shopping Habits
        int shopId = shoppingFrequencyGroup.getCheckedRadioButtonId();
        if (shopId == R.id.rarelyOption) annualKg += SHOPPING_FREQ_FACTORS[0];
        else if (shopId == R.id.monthlyOption) annualKg += SHOPPING_FREQ_FACTORS[1];
        else if (shopId == R.id.weeklyOption) annualKg += SHOPPING_FREQ_FACTORS[2];
        else if (shopId == R.id.frequentlyOption) annualKg += SHOPPING_FREQ_FACTORS[3];

        // Eco Brands Modifier
        int ecoId = ecoBrandsGroup.getCheckedRadioButtonId();
        if (ecoId == R.id.yesEcoOption) annualKg += ECO_BRAND_FACTORS[0];
        else if (ecoId == R.id.noEcoOption) annualKg += ECO_BRAND_FACTORS[1];
        else if (ecoId == R.id.sometimesEcoOption) annualKg += ECO_BRAND_FACTORS[2];

        // Recycling Modifier
        int recId = recycleGroup.getCheckedRadioButtonId();
        if (recId == R.id.alwaysRecycleOption) annualKg += RECYCLE_FACTORS[0];
        else if (recId == R.id.oftenRecycleOption) annualKg += RECYCLE_FACTORS[1];
        else if (recId == R.id.sometimesRecycleOption) annualKg += RECYCLE_FACTORS[2];
        else if (recId == R.id.neverRecycleOption) annualKg += RECYCLE_FACTORS[3];

        // Plastic Usage
        int plasId = plasticGroup.getCheckedRadioButtonId();
        if (plasId == R.id.alwaysPlasticOption) annualKg += PLASTIC_FACTORS[0];
        else if (plasId == R.id.oftenPlasticOption) annualKg += PLASTIC_FACTORS[1];
        else if (plasId == R.id.sometimesPlasticOption) annualKg += PLASTIC_FACTORS[2];
        else if (plasId == R.id.neverPlasticOption) annualKg += PLASTIC_FACTORS[3];

        // Composting
        int compId = compostGroup.getCheckedRadioButtonId();
        if (compId == R.id.yesCompostOption) annualKg += COMPOST_FACTORS[0];
        else if (compId == R.id.planningCompostOption) annualKg += COMPOST_FACTORS[1];
        else if (compId == R.id.noCompostOption) annualKg += COMPOST_FACTORS[2];

        // Disposal Method
        int dispId = disposalGroup.getCheckedRadioButtonId();
        if (dispId == R.id.donateOption) annualKg += DISPOSAL_FACTORS[0];
        else if (dispId == R.id.recycleOption) annualKg += DISPOSAL_FACTORS[1];
        else if (dispId == R.id.throwOption) annualKg += DISPOSAL_FACTORS[2];
        else if (dispId == R.id.saleOption) annualKg += DISPOSAL_FACTORS[3];

        // Final Calculation: convert total annual kg to Tonnes
        if (annualKg < 0) annualKg = 0; // Safety floor
        annualEmissions = annualKg / 1000.0;
    }

    private void saveSurveyData() {
        OthersSurveyData surveyData = new OthersSurveyData(
                screenHoursSeekBar.getProgress(),
                getSelectedRadioText(ecoBrandsGroup),
                getSelectedRadioText(shoppingFrequencyGroup),
                getSelectedRadioText(recycleGroup),
                getSelectedRadioText(plasticGroup),
                getSelectedRadioText(compostGroup),
                getSelectedRadioText(disposalGroup),
                annualEmissions
        );

        mDatabase.child("surveys").child("others").child(userId).setValue(surveyData)
                .addOnSuccessListener(aVoid -> {
                    mDatabase.child("users").child(userId).child("survey_completed").setValue(true)
                            .addOnSuccessListener(aVoid2 -> updateTotalFootprint());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Save Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateTotalFootprint() {
        mDatabase.child("surveys").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double total = 0;
                if (snapshot.child("home").child(userId).exists()) total += getVal(snapshot.child("home").child(userId));
                if (snapshot.child("food").child(userId).exists()) total += getVal(snapshot.child("food").child(userId));
                if (snapshot.child("travel").child(userId).exists()) total += getVal(snapshot.child("travel").child(userId));
                if (snapshot.child("others").child(userId).exists()) total += getVal(snapshot.child("others").child(userId));

                mDatabase.child("users").child(userId).child("total_footprint").setValue(total)
                        .addOnCompleteListener(task -> {
                            Intent intent = new Intent(OthersSurveyActivity.this, Overall.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        });
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private double getVal(DataSnapshot snapshot) {
        // Ensuring we fetch the annualEmissions field correctly
        Double d = snapshot.child("annualEmissions").getValue(Double.class);
        return (d != null) ? d : 0;
    }

    private String getSelectedRadioText(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        return (selectedId != -1) ? ((RadioButton) findViewById(selectedId)).getText().toString() : "N/A";
    }

    public static class OthersSurveyData {
        public int screenHours;
        public String ecoBrands, shoppingFrequency, recycling, plasticUsage, composting, disposalMethod;
        public double annualEmissions;
        public long timestamp = System.currentTimeMillis();

        public OthersSurveyData() {}
        public OthersSurveyData(int screenHours, String ecoBrands, String shoppingFrequency, String recycling, String plasticUsage, String composting, String disposalMethod, double annualEmissions) {
            this.screenHours = screenHours;
            this.ecoBrands = ecoBrands;
            this.shoppingFrequency = shoppingFrequency;
            this.recycling = recycling;
            this.plasticUsage = plasticUsage;
            this.composting = composting;
            this.disposalMethod = disposalMethod;
            this.annualEmissions = annualEmissions;
        }
    }
}