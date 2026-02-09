package com.example.greentail_hackathon;

import androidx.activity.EdgeToEdge;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        // Find all card views
        View Card1View = findViewById(R.id.Card1);
        View Card2View = findViewById(R.id.Card2);
        View Card3View = findViewById(R.id.Card3);
        View Card4View = findViewById(R.id.Card4);
        View main1View = findViewById(R.id.main1);

        // Footer icons
        ImageView progressIcon = findViewById(R.id.progress);
        ImageView connectIcon = findViewById(R.id.connect);
        ImageView rewardIcon = findViewById(R.id.reward);

        // Card1: Reduce Food Waste
        Card1View.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, ReduceF_waste.class))
        );

        // Card2: Short Walk Action
        Card2View.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, ShortWalkAction.class))
        );

        // Card3: Bottled Water
        Card3View.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, BottolWater.class))
        );

        // Card4: Clean Action
        Card4View.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, Clean.class))
        );

        // Main1 redirect
        main1View.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, Main1.class))
        );

        // Progress Icon: Redirect to Progress activity
        progressIcon.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, Progress.class))
        );

        // Connect Icon: Redirect to Connect activity
        connectIcon.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, connect.class))
        );

        // Reward Icon: Redirect to Reward activity
        rewardIcon.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, Reward.class))
        );
    }
}
