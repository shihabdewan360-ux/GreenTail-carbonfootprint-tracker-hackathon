package com.example.greentail_hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        Button joinButtonOrg = findViewById(R.id.joinButtonOrg);
        Button joinButtonOwn = findViewById(R.id.joinButtonOwn);

        // Set navigation for Organization button
        joinButtonOrg.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            // Pass true to indicate this is an Organization registration
            intent.putExtra("IS_ORG", true);
            startActivity(intent);
        });

        // Set navigation for Own button
        joinButtonOwn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            // Pass false for a standard individual registration
            intent.putExtra("IS_ORG", false);
            startActivity(intent);
        });
    }
}