package com.example.greentail_hackathon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Habit extends AppCompatActivity {
    LinearLayout cardContainer;
    SharedPreferences sharedPreferences;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_habit);

        cardContainer = findViewById(R.id.cardContainer);
        // Uses the constants from Progress
        sharedPreferences = getSharedPreferences(Progress.PREF_NAME, MODE_PRIVATE);

        View btnMyActions = findViewById(R.id.btnMyActions);
        btnMyActions.setOnClickListener(v ->
                startActivity(new Intent(Habit.this, Progress.class))
        );

        ImageView homeIcon = findViewById(R.id.home);
        ImageView searchIcon = findViewById(R.id.search);
        ImageView progressIcon = findViewById(R.id.progress);
        ImageView connectIcon = findViewById(R.id.connect);
        ImageView rewardIcon = findViewById(R.id.reward);

        homeIcon.setOnClickListener(v -> startActivity(new Intent(Habit.this, Main1.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(Habit.this, SearchActivity.class)));
        progressIcon.setOnClickListener(v -> startActivity(new Intent(Habit.this, Progress.class)));
        connectIcon.setOnClickListener(v -> startActivity(new Intent(Habit.this, connect.class)));
        rewardIcon.setOnClickListener(v -> startActivity(new Intent(Habit.this, Reward.class)));

        loadHabitCards();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHabitCards();
    }

    private void loadHabitCards() {
        cardContainer.removeAllViews();
        // Fixed: Use Progress.HABIT_LIST_KEY
        String json = sharedPreferences.getString(Progress.HABIT_LIST_KEY, null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<CardModel>>(){}.getType();
            List<CardModel> habitList = gson.fromJson(json, type);

            for (int i = 0; i < habitList.size(); i++) {
                CardModel card = habitList.get(i);
                View cardView = getLayoutInflater().inflate(R.layout.card_design, null);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                if (i < habitList.size() - 1) {
                    params.bottomMargin = dpToPx(20);
                }

                cardView.setLayoutParams(params);

                TextView title = cardView.findViewById(R.id.cardTitle);
                TextView quantity = cardView.findViewById(R.id.cardQuantity);
                TextView count = cardView.findViewById(R.id.cardCount);
                SeekBar slider = cardView.findViewById(R.id.cardSlider);

                title.setText(card.getTitle());
                quantity.setText(card.getQuantity());
                count.setText("+" + card.getCount());

                slider.setMax(4);
                slider.setProgress(card.getProgress());
                slider.setEnabled(false);
                slider.setOnTouchListener((v, event) -> true);

                cardContainer.addView(cardView);
            }
        }
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}