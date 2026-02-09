package com.example.greentail_hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ReforestDone extends AppCompatActivity {

    private TextView tvSpentPoints, tvGiveMore;
    private Button btnReturnReward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reforestdone);

        // 1. Initialize Views with IDs from your XML
        tvSpentPoints = findViewById(R.id.tv_spent_points);
        btnReturnReward = findViewById(R.id.returnButton); // Matches your XML

        // Finding the "Give more" text (We need to find it by text or add an ID)
        // I will assume you add android:id="@+id/tv_give_more" to that TextView
        tvGiveMore = findViewById(R.id.tv_give_more);

        // 2. Display the points
        int spentPoints = getIntent().getIntExtra("spent_points", 0);
        tvSpentPoints.setText(String.valueOf(spentPoints));

        // 3. Button: Return to causes
        if (btnReturnReward != null) {
            btnReturnReward.setOnClickListener(v -> {
                startActivity(new Intent(ReforestDone.this, Reward.class));
                finish();
            });
        }

        // 4. TextView Click: Give more points
        if (tvGiveMore != null) {
            tvGiveMore.setOnClickListener(v -> {
                startActivity(new Intent(ReforestDone.this, Reforest.class));
                finish();
            });
        }

        // 5. Footer Navigation
        setupFooter();
    }

    private void setupFooter() {
        ImageView home = findViewById(R.id.home);
        ImageView search = findViewById(R.id.search);
        ImageView progress = findViewById(R.id.progress);
        ImageView connect = findViewById(R.id.connect);
        ImageView reward = findViewById(R.id.reward);

        if (home != null) home.setOnClickListener(v -> startActivity(new Intent(this, Main1.class)));
        if (search != null) search.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        if (progress != null) progress.setOnClickListener(v -> startActivity(new Intent(this, Progress.class)));
        if (connect != null) connect.setOnClickListener(v -> startActivity(new Intent(this, connect.class)));
        if (reward != null) reward.setOnClickListener(v -> startActivity(new Intent(this, Reward.class)));
    }
}