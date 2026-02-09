package com.example.greentail_hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge screen use
        EdgeToEdge.enable(this);

        // Hide ActionBar for SplashActivity
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set the splash screen layout
        setContentView(R.layout.activity_splash);


        // Delay to show the splash screen for 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // After the delay, start MainActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // Close SplashActivity to prevent user from returning to it
            }
        }, 3000);  // 3-second delay
    }
}
