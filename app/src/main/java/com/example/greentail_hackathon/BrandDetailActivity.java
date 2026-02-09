package com.example.greentail_hackathon;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BrandDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_detail);

        // 1. Link XML IDs to Java Variables
        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView brandImg = findViewById(R.id.brandImage);
        ImageView brandLogo = findViewById(R.id.brandLogoSmall);
        TextView titleTv = findViewById(R.id.brandTitleText);
        TextView descTv = findViewById(R.id.brandDescription);
        Button btnGo = findViewById(R.id.btnGoToShop);

        // 2. "Catch" the data being sent from EcoShopActivity
        String name = getIntent().getStringExtra("brandName");
        String desc = getIntent().getStringExtra("brandDesc");
        String url = getIntent().getStringExtra("brandUrl");
        int imageRes = getIntent().getIntExtra("brandImage", 0);
        int logoRes = getIntent().getIntExtra("brandLogo", 0);

        // 3. Update the Screen with the "Caught" data
        if (name != null) titleTv.setText(name);
        if (desc != null) descTv.setText(desc);
        if (imageRes != 0) brandImg.setImageResource(imageRes);
        if (logoRes != 0) brandLogo.setImageResource(logoRes);

        // 4. Back Button Logic
        btnBack.setOnClickListener(v -> finish());

        // 5. External Website Logic
        btnGo.setOnClickListener(v -> {
            if (url != null && !url.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }
}