package com.example.greentail_hackathon;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiVerificationHelper {

    private static final String TAG = "GeminiVerify";

    // TODO: Ensure this key has no spaces and the "Generative Language API" is enabled in Cloud Console
    private static final String API_KEY = "Paste your own gemini key for testing";

    private final GenerativeModelFutures model;
    private final Executor executor;

    public interface VerificationCallback {
        void onResult(boolean isApproved, String explanation);
        void onError(String error);
    }

    public GeminiVerificationHelper() {
        Log.d(TAG, "=== Gemini AI Helper Initialized ===");

        /* * FIX: We are using "gemini-3-flash" which is the 2026 default.
         * If you want to use the stable 2.5 version, use "gemini-2.5-flash".
         * The "models/" prefix is often added automatically by the SDK,
         * but we'll use the clean ID here which is most compatible with the Java/Kotlin SDK.
         */
        GenerativeModel gm = new GenerativeModel("gemini-3-flash-preview", API_KEY);

        this.model = GenerativeModelFutures.from(gm);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void verifyProof(Bitmap bitmap, String taskDescription, VerificationCallback callback) {
        Log.d(TAG, "Starting AI Verification for: " + taskDescription);

        if (bitmap == null) {
            callback.onError("No image provided");
            return;
        }

        // Refined prompt for Gemini 3's better reasoning
        String prompt = "Review this image as a sustainability validator. " +
                "Task: " + taskDescription + ". " +
                "Does the image confirm this task was completed? " +
                "Start your response with 'VERIFIED' or 'REJECTED', followed by a short explanation.";

        Content content = new Content.Builder()
                .addText(prompt)
                .addImage(bitmap)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String responseText = result.getText();
                Log.d(TAG, "AI Response: " + responseText);

                if (responseText != null) {
                    boolean isApproved = responseText.trim().toUpperCase().startsWith("VERIFIED");
                    callback.onResult(isApproved, responseText);
                } else {
                    callback.onError("AI returned an empty response.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "AI Error Detail: ", t);
                // If it's STILL a 404, the SDK might be too old for Gemini 3
                if (t.getMessage() != null && t.getMessage().contains("404")) {
                    callback.onError("Model not found. Try updating your build.gradle dependency to the latest version.");
                } else {
                    callback.onError("AI Error: " + t.getMessage());
                }
            }
        }, executor);
    }
}
