package com.example.greentail_hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class EcoShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecoshop);

        // Back button to return to Reward page
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // --- Initialize All 6 Brand Cards ---
        CardView hygrCard = findViewById(R.id.shopCard1);
        CardView unplugCard = findViewById(R.id.shopCard2);
        CardView fernCard = findViewById(R.id.shopCard3);
        CardView dapoCard = findViewById(R.id.shopCard4);
        CardView bennsCard = findViewById(R.id.shopCard5);
        CardView asliCard = findViewById(R.id.shopCard6);

        // --- Set Click Listeners with YOUR Full Descriptions ---

        hygrCard.setOnClickListener(v -> openBrandDetail(
                "HYGR",
                "Hygr is a Malaysian startup that focuses on eco-friendly personal care products such as deodorants, lip balms, and sunscreens. What sets Hygr apart is its commitment to reducing plastic waste—most of its products come in compostable, biodegradable, or refillable packaging instead of single-use plastics. This aligns with a zero-waste lifestyle, encouraging consumers to make small but consistent eco-conscious choices.",
                "https://hygr.com/?srsltid=AfmBOool-zFxmGahc_7-jDpbGzOj3oL_PI8pZXTMPIXzH2FoZtBgk5Av",
                R.drawable.hygr_banner,
                R.drawable.hygr_logo
        ));

        unplugCard.setOnClickListener(v -> openBrandDetail(
                "U N P L U G",
                "UNPLUG is a curated multi-brand platform and conscious label that champions \"slow living.\" While they carry various items, their clothing line (often associated with the NN label) focuses on timeless, gender-neutral silhouettes designed to outlast fleeting trends. They prioritize natural fibers like linen and organic cotton, and their business model is built around ethical sourcing and radical transparency. By encouraging consumers to \"unplug\" from the cycle of fast fashion, they promote a wardrobe built on longevity rather than disposability.",
                "https://shopunplug.com/?srsltid=AfmBOop1mtkRrcvG0fJgNDaJY2j9tFXHX4xnLcOTra-1dmhvmEP9mJRf",
                R.drawable.nn_banner,
                R.drawable.nn_logo
        ));

        fernCard.setOnClickListener(v -> openBrandDetail(
                "FERN",
                "Founded by Fern Chua, this design house is credited with reviving the traditional art of Batik for the modern woman. FERN is a \"slow fashion\" brand that celebrates artisanal craftsmanship. Each piece is handcrafted using traditional block-printing or hand-painting techniques, ensuring that the heritage of Malaysian textile art is preserved. By focusing on small-batch production and high-quality natural fabrics, FERN reduces the environmental footprint typically associated with mass-produced garments.",
                "https://fern.gallery/",
                R.drawable.fern_banner,
                R.drawable.fern_logo
        ));

        dapoCard.setOnClickListener(v -> openBrandDetail(
                "DAPO",
                "DAPO (Malay for \"kitchen\") focuses on sustainable homeware, specifically kitchenware crafted from PEFC-certified Malaysian wood. They are known for their sleek, minimalist wooden boards and utensils that are both functional and aesthetic. What makes DAPO stand out is their dedication to circularity; they ensure their wood is ethically harvested, and they often use off-cuts that would otherwise go to waste, turning \"scraps\" into premium, food-safe household items.",
                "https://greenfactory.shop/pages/dapo",
                R.drawable.dapo_banner,
                R.drawable.dapo_logo
        ));

        bennsCard.setOnClickListener(v -> openBrandDetail(
                "BENNS",
                "Benns is a bean-to-bar chocolate maker that revolutionized the Malaysian cocoa industry with its Ethicoa (Ethical Cocoa) platform. Unlike mass-market chocolate, Benns works directly with local farmers in Asia (including estates in Pahang and Melaka) to ensure fair pricing and transparent trade. Their sustainability focus is two-fold: supporting the livelihoods of smallholder farmers and promoting biodiversity by encouraging sustainable farming practices that move away from industrial monoculture.",
                "https://bennschocolate.com/",
                R.drawable.benns_banner,
                R.drawable.benns_logo
        ));

        asliCard.setOnClickListener(v -> openBrandDetail(
                "The ASLI Co.",
                "The ASLI Co. is a social enterprise that empowers Orang Asli (indigenous) mothers by providing them with sustainable livelihoods. They produce a variety of eco-friendly lifestyle products, such as hand-sewn fabric masks, lavender eye pillows, and artisanal soaps. By training mothers to craft these items from their homes in the village, the brand helps them earn a living wage while staying with their families. Their products often utilize reusable materials, helping to bridge the gap between social impact and environmental consciousness.",
                "https://www.theasli.co/",
                R.drawable.asli_banner,
                R.drawable.asli_logo
        ));
    }

    private void openBrandDetail(String name, String desc, String url, int banner, int logo) {
        Intent intent = new Intent(EcoShopActivity.this, BrandDetailActivity.class);
        intent.putExtra("brandName", name);
        intent.putExtra("brandDesc", desc);
        intent.putExtra("brandUrl", url);
        intent.putExtra("brandImage", banner);
        intent.putExtra("brandLogo", logo);
        startActivity(intent);
        // Soft transition effect
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}