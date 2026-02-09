package com.example.greentail_hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PayActivity extends AppCompatActivity {

    private Button btnBackToReward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        btnBackToReward = findViewById(R.id.btnBackToReward);

        if (btnBackToReward != null) {
            btnBackToReward.setOnClickListener(v -> {
                // Return to the Reward page
                Intent intent = new Intent(PayActivity.this, Reward.class);
                // Clear the activity stack so they don't "go back" into the pay screen
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }
}