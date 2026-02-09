package com.example.greentail_hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main1 extends AppCompatActivity {

    private final String firebaseUrl = "https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app";
    private TextView introText; // To handle the dynamic greeting

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main1);

        // Initialize the TextView from your XML
        introText = findViewById(R.id.IntroText);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";
        DatabaseReference userRef = FirebaseDatabase.getInstance(firebaseUrl).getReference("users").child(userId);

        // --- DYNAMIC NAME LOGIC ---
        if (mAuth.getCurrentUser() != null) {
            userRef.child("firstName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        String firstName = snapshot.getValue(String.class);
                        // Using \n to match your original XML layout formatting
                        introText.setText("It’s your chance to \ntake action, " + firstName + ".");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Fallback is handled by the default text in XML
                }
            });
        }

        // --- SURVEY BUTTON LOGIC ---
        LinearLayout innerLayout = findViewById(R.id.innerLinearLayout);
        innerLayout.setOnClickListener(v -> {
            userRef.child("survey_completed").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isCompleted = false;
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        isCompleted = snapshot.getValue(Boolean.class);
                    }

                    Intent intent;
                    if (isCompleted) {
                        intent = new Intent(Main1.this, Overall.class);
                    } else {
                        intent = new Intent(Main1.this, TakeServey.class);
                    }
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    startActivity(new Intent(Main1.this, TakeServey.class));
                }
            });
        });

        // --- ALL OTHER BUTTONS ---
        findViewById(R.id.connect).setOnClickListener(v -> {
            startActivity(new Intent(this, connect.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        findViewById(R.id.card1).setOnClickListener(v -> {
            startActivity(new Intent(this, ReduceF_waste.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        findViewById(R.id.Card2).setOnClickListener(v -> {
            startActivity(new Intent(this, ShortWalkAction.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        ((CardView)findViewById(R.id.card3)).setOnClickListener(v -> {
            startActivity(new Intent(this, BottolWater.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        findViewById(R.id.card4).setOnClickListener(v -> {
            startActivity(new Intent(this, Clean.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        findViewById(R.id.userImage2).setOnClickListener(v -> {
            startActivity(new Intent(this, DashBoard.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        ((ImageView)findViewById(R.id.hehe)).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        ((ImageView)findViewById(R.id.progress)).setOnClickListener(v -> {
            startActivity(new Intent(this, Progress.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        ((ImageView)findViewById(R.id.Reward)).setOnClickListener(v -> {
            startActivity(new Intent(this, Reward.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}