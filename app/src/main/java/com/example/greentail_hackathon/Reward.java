package com.example.greentail_hackathon;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Reward extends AppCompatActivity {

    private TextView taliPointsTextView;
    // These must be LinearLayout to match your XML
    private LinearLayout btnWallet;
    private LinearLayout btnRedeemPts;

    private DatabaseReference databaseRef;
    private DatabaseReference suggestionsRef;
    private String userId;

    private EditText editSuggestion;
    private ImageView btnSendSuggestion;

    private final String DB_URL = "https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reward);

        // 1. Initialize UI Elements (Matching your exact XML IDs)
        taliPointsTextView = findViewById(R.id.taliPoints);
        editSuggestion = findViewById(R.id.editSuggestion);
        btnSendSuggestion = findViewById(R.id.btnSendSuggestion);

        // Matching IDs: btnWallet and btnRedeem from your LinearLayout tags
        btnWallet = findViewById(R.id.btnWallet);
        btnRedeemPts = findViewById(R.id.btnRedeem);

        // 2. WALLET BUTTON CLICK (Opens PayActivity)
        if (btnWallet != null) {
            btnWallet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Reward.this, PayActivity.class);
                    startActivity(intent);
                }
            });
        }

        // 3. REDEEM BUTTON CLICK (Opens RedeemPointsActivity)
        if (btnRedeemPts != null) {
            btnRedeemPts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Reward.this, RedeemPointsActivity.class);
                    startActivity(intent);
                }
            });
        }

        // --- ECOSHOP BANNER ---
        View cardBanner = findViewById(R.id.card_banner);
        if (cardBanner != null) {
            cardBanner.setOnClickListener(v -> {
                Intent intent = new Intent(Reward.this, EcoShopActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // --- CHARITY CARD ---
        View card1 = findViewById(R.id.card1);
        if (card1 != null) {
            card1.setOnClickListener(v -> {
                Intent intent = new Intent(Reward.this, Reforest.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // --- SUGGESTION LOGIC ---
        if (btnSendSuggestion != null) {
            btnSendSuggestion.setOnClickListener(v -> {
                String suggestionText = editSuggestion.getText().toString().trim();
                if (!suggestionText.isEmpty() && userId != null) {
                    String suggestionId = suggestionsRef.push().getKey();
                    Map<String, Object> suggestionData = new HashMap<>();
                    suggestionData.put("text", suggestionText);
                    suggestionData.put("timestamp", System.currentTimeMillis());

                    suggestionsRef.child(suggestionId).setValue(suggestionData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(Reward.this, "Suggestion sent!", Toast.LENGTH_SHORT).show();
                                editSuggestion.setText("");
                                hideKeyboard(v);
                            });
                }
            });
        }

        setupFooterNavigation();

        // Firebase Setup
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseRef = FirebaseDatabase.getInstance(DB_URL).getReference("users").child(userId);
            suggestionsRef = FirebaseDatabase.getInstance(DB_URL).getReference("suggestions").child(userId);
            loadUserData();
        }
    }

    private void loadUserData() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Update Points
                    Integer taliValue = dataSnapshot.child("taliPoints").getValue(Integer.class);
                    if (taliPointsTextView != null) {
                        taliPointsTextView.setText(taliValue != null ? String.valueOf(taliValue) : "0");
                    }

                    // Update Wallet Balance inside the btnWallet text child
                    Double walletValue = dataSnapshot.child("walletBalance").getValue(Double.class);
                    if (btnWallet != null) {
                        // Find the TextView inside the LinearLayout
                        TextView tvWallet = (TextView) btnWallet.getChildAt(1);
                        if (tvWallet != null) {
                            tvWallet.setText(walletValue != null ? String.format("RM %.2f", walletValue) : "RM 0.00");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void setupFooterNavigation() {
        ImageView home = findViewById(R.id.home);
        ImageView search = findViewById(R.id.search);
        ImageView progress = findViewById(R.id.progress);
        ImageView connect = findViewById(R.id.connect);
        ImageView reward = findViewById(R.id.reward);

        if (home != null) home.setOnClickListener(v -> startActivity(new Intent(this, Main1.class)));
        if (search != null) search.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        if (progress != null) progress.setOnClickListener(v -> startActivity(new Intent(this, Progress.class)));
        if (connect != null) connect.setOnClickListener(v -> startActivity(new Intent(this, connect.class)));
        // Current page is Reward, no action needed
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}