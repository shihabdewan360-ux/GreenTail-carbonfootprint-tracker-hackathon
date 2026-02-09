package com.example.greentail_hackathon;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseHelper {
    private static final String SURVEY_COMPLETED_KEY = "survey_completed";

    public static void markSurveyCompleted() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "anonymous";

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId);

        userRef.child(SURVEY_COMPLETED_KEY).setValue(true);
    }
}