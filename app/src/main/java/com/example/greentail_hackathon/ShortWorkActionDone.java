package com.example.greentail_hackathon;

import androidx.activity.EdgeToEdge;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ShortWorkActionDone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shortwalk_done);

        // Button 1: Redirect to Progress page
        Button btnProgress = findViewById(R.id.action_button);
        btnProgress.setOnClickListener(v -> {
            startActivity(new Intent(ShortWorkActionDone.this, Progress.class));
            finish();
        });

        // Button 2: Redirect to SearchPage (Task List)
        Button btnSearchPage = findViewById(R.id.action_button2);
        btnSearchPage.setOnClickListener(v -> {
            startActivity(new Intent(ShortWorkActionDone.this, SearchActivity.class));
            finish();
        });

        // Footer Image Clicks
        ImageView home = findViewById(R.id.home);
        ImageView search = findViewById(R.id.search);
        ImageView progress = findViewById(R.id.progress);
        ImageView connect = findViewById(R.id.connect);
        ImageView reward = findViewById(R.id.reward);

        // Note: Changed MainActivity.class to Main1.class to match your other activities
        home.setOnClickListener(v -> {
            startActivity(new Intent(ShortWorkActionDone.this, Main1.class));
            finish();
        });

        search.setOnClickListener(v -> {
            startActivity(new Intent(ShortWorkActionDone.this, SearchActivity.class));
            finish();
        });

        progress.setOnClickListener(v -> {
            startActivity(new Intent(ShortWorkActionDone.this, Progress.class));
            finish();
        });

        connect.setOnClickListener(v -> {
            startActivity(new Intent(ShortWorkActionDone.this, connect.class));
            finish();
        });

        reward.setOnClickListener(v -> {
            startActivity(new Intent(ShortWorkActionDone.this, Reward.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}