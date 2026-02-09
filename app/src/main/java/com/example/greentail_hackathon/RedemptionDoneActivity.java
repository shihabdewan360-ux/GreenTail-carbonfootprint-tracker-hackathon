package com.example.greentail_hackathon;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RedemptionDoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redemption_done);

        TextView tvPtsRedeemed = findViewById(R.id.tvPtsRedeemed);
        TextView tvMoneyRedeemed = findViewById(R.id.tvMoneyRedeemed);
        Button btnDone = findViewById(R.id.btnDone);

        // FIXED: Changed keys to match the ones sent from RedeemPointsActivity
        int pts = getIntent().getIntExtra("POINTS_REDEEMED", 0);
        double money = getIntent().getDoubleExtra("MONEY_GAINED", 0.0);

        if (tvPtsRedeemed != null) {
            tvPtsRedeemed.setText("-" + pts + " pts");
        }

        if (tvMoneyRedeemed != null) {
            tvMoneyRedeemed.setText(String.format("RM %.2f", money));
        }

        if (btnDone != null) {
            btnDone.setOnClickListener(v -> finish()); // Returns to Rewards screen
        }
    }
}