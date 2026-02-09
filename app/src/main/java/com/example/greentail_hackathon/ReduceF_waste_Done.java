package com.example.greentail_hackathon;

import androidx.activity.EdgeToEdge;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ReduceF_waste_Done extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reduce_food_waste_actioncard_done);

        // Initialize Action Buttons (Check Progress / Keep Going)
        // Note: Make sure these IDs exist in your XML
        Button actionButton = findViewById(R.id.action_button);
        Button actionButton2 = findViewById(R.id.action_button2);

        if (actionButton != null) {
            actionButton.setOnClickListener(v -> {
                startActivity(new Intent(this, Progress.class));
                finish();
            });
        }

        if (actionButton2 != null) {
            actionButton2.setOnClickListener(v -> {
                startActivity(new Intent(this, SearchActivity.class));
                finish();
            });
        }

        // Footer Navigation
        ImageView home = findViewById(R.id.home);
        ImageView search = findViewById(R.id.search);
        ImageView progress = findViewById(R.id.progress);
        ImageView connect = findViewById(R.id.connect);
        ImageView reward = findViewById(R.id.reward);

        home.setOnClickListener(v -> {
            startActivity(new Intent(ReduceF_waste_Done.this, Main1.class));
            finish();
        });

        search.setOnClickListener(v -> {
            startActivity(new Intent(ReduceF_waste_Done.this, SearchActivity.class));
            finish();
        });

        progress.setOnClickListener(v -> {
            startActivity(new Intent(ReduceF_waste_Done.this, Progress.class));
            finish();
        });

        connect.setOnClickListener(v -> {
            startActivity(new Intent(ReduceF_waste_Done.this, connect.class));
            finish();
        });

        reward.setOnClickListener(v -> {
            startActivity(new Intent(ReduceF_waste_Done.this, Reward.class));
            finish();
        });
    }
}