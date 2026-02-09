package com.example.greentail_hackathon;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Clean extends AppCompatActivity {

    private static final String TAG = "CleanActivity";

    Button btnIDidThis;
    ImageButton addActionButton; // Camera Trigger
    ImageView gifImageView1, gifImageView2;
    TextView quantityText, pointsText, habitTracker;
    SharedPreferences sharedPreferences;
    Gson gson = new Gson();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final String DB_URL = "https://greentail-hackathon-default-rtdb.asia-southeast1.firebasedatabase.app";

    private static final String PREF_NAME = "click_pref";
    private static final String CARD_LIST_KEY = "card_list";
    private static final String ACTION_TITLE = "Take part in a local clean-up";
    private static final String ACTION_QUANTITY = "500g";
    private static final String ACTION_POINTS = "20";

    private static final int VERIFY_TASK_REQUEST = 101;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private GeminiVerificationHelper verificationHelper;
    private String currentPhotoPath;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clean);

        setupViews();
        setupFooter();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        try {
            verificationHelper = new GeminiVerificationHelper();
        } catch (Exception e) {
            Log.e(TAG, "AI Init Failed", e);
        }

        // Disable button until AI verifies
        btnIDidThis.setEnabled(false);
        btnIDidThis.setAlpha(0.5f);

        loadGif(gifImageView1, R.drawable.gif5);
        loadGif(gifImageView2, R.drawable.gif3);

        addActionButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openHighResCamera();
            }
        });

        btnIDidThis.setOnClickListener(v -> {
            saveCardData();
            updateFirebaseData();
        });
    }

    private void setupViews() {
        btnIDidThis = findViewById(R.id.action_button);
        addActionButton = findViewById(R.id.addActionButton); // Ensure this ID is in XML
        gifImageView1 = findViewById(R.id.gifImageView1);
        gifImageView2 = findViewById(R.id.gifImageView2);
        quantityText = findViewById(R.id.quantity_text);
        pointsText = findViewById(R.id.points_text);
        habitTracker = findViewById(R.id.habit_tracker);

        quantityText.setText(ACTION_QUANTITY);
        pointsText.setText("+" + ACTION_POINTS);
    }

    private void openHighResCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createTempImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error creating file", ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.greentail_hackathon.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, VERIFY_TASK_REQUEST);
            }
        }
    }

    private File createTempImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File image = File.createTempFile("CLEAN_" + timeStamp, ".jpg", getCacheDir());
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VERIFY_TASK_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (bitmap != null) {
                verifyCleanupWithAI(bitmap);
            }
        }
    }

    private void verifyCleanupWithAI(Bitmap bitmap) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Environmental Auditor");
        progressDialog.setMessage("Verifying your clean-up effort...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String taskGoal = "ACT AS A STRICT FRAUD DETECTOR. Task: Local Community Clean-up.\n" +
                "CRITERIA:\n" +
                "1. ACCEPT: User holding a trash bag outdoors, wearing gloves, or picking up litter in a park/beach/street.\n" +
                "2. REJECT: Photos of just trash on the ground without a person or bag. Photos of indoor household bins.\n" +
                "3. REJECT: Blurry photos or photos of a computer screen.\n" +
                "4. MANDATORY: There must be clear evidence of active cleaning. A trash bag or gloves are strong indicators.";

        verificationHelper.verifyProof(bitmap, taskGoal, new GeminiVerificationHelper.VerificationCallback() {
            @Override
            public void onResult(boolean isApproved, String explanation) {
                runOnUiThread(() -> {
                    if (progressDialog != null) progressDialog.dismiss();

                    File file = new File(currentPhotoPath);
                    if (file.exists()) file.delete();

                    if (isApproved) {
                        handleApproval(explanation);
                    } else {
                        handleRejection(explanation);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    if (progressDialog != null) progressDialog.dismiss();
                    Toast.makeText(Clean.this, "AI Error: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void handleApproval(String explanation) {
        btnIDidThis.setEnabled(true);
        btnIDidThis.setAlpha(1.0f);
        new AlertDialog.Builder(this)
                .setTitle("✅ Action Verified")
                .setMessage(explanation)
                .setPositiveButton("Get 20 Points", null)
                .show();
    }

    private void handleRejection(String explanation) {
        new AlertDialog.Builder(this)
                .setTitle("❌ Verification Failed")
                .setMessage(explanation + "\n\nTip: Make sure to show your trash bag or gloves in the frame!")
                .setPositiveButton("Try Again", (dialog, which) -> openHighResCamera())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadGif(ImageView imageView, int resId) {
        Glide.with(this).asGif().load(resId).transition(DrawableTransitionOptions.withCrossFade()).into(imageView);
    }

    private void setupFooter() {
        findViewById(R.id.home).setOnClickListener(v -> startActivity(new Intent(this, Main1.class)));
        findViewById(R.id.search).setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        findViewById(R.id.progress).setOnClickListener(v -> startActivity(new Intent(this, Progress.class)));
        findViewById(R.id.connect).setOnClickListener(v -> startActivity(new Intent(this, connect.class)));
        findViewById(R.id.reward).setOnClickListener(v -> startActivity(new Intent(this, Reward.class)));
    }

    private void saveCardData() {
        String json = sharedPreferences.getString(CARD_LIST_KEY, null);
        Type type = new TypeToken<ArrayList<CardModel>>(){}.getType();
        List<CardModel> cardList = json == null ? new ArrayList<>() : gson.fromJson(json, type);
        boolean found = false;
        for (CardModel card : cardList) {
            if (card.getTitle().equals(ACTION_TITLE)) {
                if (card.getProgress() < 5) card.setProgress(card.getProgress() + 1);
                found = true;
                break;
            }
        }
        if (!found) cardList.add(new CardModel(ACTION_TITLE, ACTION_QUANTITY, ACTION_POINTS, 1));
        sharedPreferences.edit().putString(CARD_LIST_KEY, gson.toJson(cardList)).apply();
    }

    private void updateFirebaseData() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = mDatabase.child("users").child(userId);
        userRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user == null) {
                    user = new User();
                    user.setTotalCO2Saved(0.5);
                    user.setTaliPoints(20);
                } else {
                    user.setTotalCO2Saved(user.getTotalCO2Saved() + 0.5);
                    user.setTaliPoints(user.getTaliPoints() + 20);
                }
                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    startActivity(new Intent(Clean.this, CleanDone.class));
                    finish();
                }
            }
        });
    }
}