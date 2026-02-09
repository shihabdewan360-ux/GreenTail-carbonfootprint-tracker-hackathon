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

public class FoodSurveyActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private RadioGroup meatFrequencyGroup, vegetarianDaysGroup, foodPurchaseGroup,
            organicProduceGroup, eatOutFrequencyGroup, foodWasteGroup, reusableGroup;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodservey);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        meatFrequencyGroup = findViewById(R.id.meatFrequencyGroup);
        vegetarianDaysGroup = findViewById(R.id.vegetarianDaysGroup);
        foodPurchaseGroup = findViewById(R.id.foodPurchaseGroup);
        organicProduceGroup = findViewById(R.id.organicProduceGroup);
        eatOutFrequencyGroup = findViewById(R.id.eatOutFrequencyGroup);
        foodWasteGroup = findViewById(R.id.foodWasteGroup);
        reusableGroup = findViewById(R.id.reusableGroup);
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
        return meatFrequencyGroup.getCheckedRadioButtonId() != -1 &&
                vegetarianDaysGroup.getCheckedRadioButtonId() != -1 &&
                foodPurchaseGroup.getCheckedRadioButtonId() != -1 &&
                organicProduceGroup.getCheckedRadioButtonId() != -1 &&
                eatOutFrequencyGroup.getCheckedRadioButtonId() != -1 &&
                foodWasteGroup.getCheckedRadioButtonId() != -1 &&
                reusableGroup.getCheckedRadioButtonId() != -1;
    }

    // --- EMISSION HELPERS (All values in kg CO2e/year) ---

    private double getMeatFrequencyEmission() {
        int id = meatFrequencyGroup.getCheckedRadioButtonId();
        if (id == R.id.neverMeatOption) return 0.0;
        if (id == R.id.onceTwiceOption) return 267.0;
        if (id == R.id.threeFourOption) return 623.0;
        if (id == R.id.fivePlusOption) return 1069.0;
        return 0;
    }

    private double getVegetarianDaysEmission() {
        int id = vegetarianDaysGroup.getCheckedRadioButtonId();
        if (id == R.id.zeroDaysOption) return 0.0;
        if (id == R.id.oneTwoDaysOption) return -536.0;
        if (id == R.id.threeFiveDaysOption) return -1430.0;
        if (id == R.id.sixSevenDaysOption) return -2323.0;
        return 0;
    }

    private double getFoodPurchaseEmission() {
        int id = foodPurchaseGroup.getCheckedRadioButtonId();
        if (id == R.id.localMarketsOption) return 150.0;
        if (id == R.id.supermarketsOption) return 320.0;
        if (id == R.id.importedStoresOption) return 650.0;
        if (id == R.id.onlineGroceryOption) return 410.0;
        return 0;
    }

    private double getOrganicProduceEmission() {
        int id = organicProduceGroup.getCheckedRadioButtonId();
        if (id == R.id.alwaysOrganicOption) return -58.0;
        if (id == R.id.sometimesOrganicOption) return -29.0;
        if (id == R.id.rarelyOrganicOption) return 0.0;
        if (id == R.id.neverOrganicOption) return 29.0;
        return 0;
    }

    private double getEatOutFrequencyEmission() {
        int id = eatOutFrequencyGroup.getCheckedRadioButtonId();
        if (id == R.id.neverEatOutOption) return 0.0;
        if (id == R.id.onceTwiceEatOutOption) return 211.0;
        if (id == R.id.threeFiveEatOutOption) return 562.0;
        if (id == R.id.moreThanFiveOption) return 982.0;
        return 0;
    }

    private double getFoodWasteEmission() {
        int id = foodWasteGroup.getCheckedRadioButtonId();
        if (id == R.id.noWasteOption) return 0.0;
        if (id == R.id.littleWasteOption) return 293.0;
        if (id == R.id.someWasteOption) return 1560.0;
        if (id == R.id.lotWasteOption) return 3900.0;
        return 0;
    }

    private double getReusableEmission() {
        int id = reusableGroup.getCheckedRadioButtonId();
        if (id == R.id.alwaysReusableOption) return -102.0;
        if (id == R.id.sometimesReusableOption) return -51.0;
        if (id == R.id.rarelyReusableOption) return -20.0;
        if (id == R.id.neverReusableOption) return 102.0;
        return 0;
    }

    private String getSelectedRadioText(RadioGroup group) {
        int selectedId = group.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        return (radioButton != null) ? radioButton.getText().toString() : "";
    }

    private void saveSurveyData() {
        String userId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : "anonymous";

        // Get logic results
        double meatE = getMeatFrequencyEmission();
        double vegE = getVegetarianDaysEmission();
        double purchaseE = getFoodPurchaseEmission();
        double organicE = getOrganicProduceEmission();
        double eatOutE = getEatOutFrequencyEmission();
        double wasteE = getFoodWasteEmission();
        double reusableE = getReusableEmission();

        double annualKgTotal = meatE + vegE + purchaseE + organicE + eatOutE + wasteE + reusableE;
        if (annualKgTotal < 0) annualKgTotal = 0;

        double annualTonnes = annualKgTotal / 1000.0;
        double weeklyKg = annualKgTotal / 52.0;

        SurveyData surveyData = new SurveyData(
                getSelectedRadioText(meatFrequencyGroup),
                getSelectedRadioText(vegetarianDaysGroup),
                getSelectedRadioText(foodPurchaseGroup),
                getSelectedRadioText(organicProduceGroup),
                getSelectedRadioText(eatOutFrequencyGroup),
                getSelectedRadioText(foodWasteGroup),
                getSelectedRadioText(reusableGroup),
                meatE, vegE, purchaseE, organicE, eatOutE, wasteE, reusableE,
                weeklyKg, annualTonnes
        );

        mDatabase.child("surveys").child("food").child(userId).setValue(surveyData);

        Intent intent = new Intent(FoodSurveyActivity.this, OthersSurveyActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public static class SurveyData {
        public String meatFrequency, vegetarianDays, foodPurchase, organicProduce, eatOutFrequency, foodWaste, reusableContainers;
        public double meatEmission, vegetarianEmission, purchaseEmission, organicEmission, eatOutEmission, wasteEmission, reusableEmission;
        public double weeklyEmissions, annualEmissions;
        public long timestamp = System.currentTimeMillis();

        public SurveyData() {}

        public SurveyData(String m, String v, String p, String o, String e, String w, String r,
                          double me, double ve, double pe, double oe, double ee, double we, double re,
                          double weekly, double annual) {
            this.meatFrequency = m; this.vegetarianDays = v; this.foodPurchase = p;
            this.organicProduce = o; this.eatOutFrequency = e; this.foodWaste = w;
            this.reusableContainers = r;
            this.meatEmission = me; this.vegetarianEmission = ve; this.purchaseEmission = pe;
            this.organicEmission = oe; this.eatOutEmission = ee; this.wasteEmission = we;
            this.reusableEmission = re;
            this.weeklyEmissions = weekly; this.annualEmissions = annual;
        }
    }
}