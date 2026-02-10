GreenTail: Gamified Carbon Tracking & Sustainable Living.
GreenTail is an Android-based application designed to transform environmental awareness from a chore into a rl rewarding, gamified experience. By combining rigorous footprint calculation with AI-assisted action verification, GreenTail helps users bridge the gap between "knowing" and "doing."
Features:
Multi-Category Footprint Survey.
A comprehensive estimation engine that calculates your impact across four pillars using internationally recognized standards
AI Action Verification (Powered by Google Gemini 3.0).
To ensure credibility, GreenTail utilizes the Google Gemini API to verify eco-friendly actions. Users take a photo of their activity (e.g., using a reusable bottle or recycling), and our AI-assisted workflow validates the action before awarding points.
Gamification & Habits.
Social Challenges.
Tech Stack:
Language: Java
Platform: Android Studio
AI Integration: Google Gemini 3.0 API
Version Control: Git & GitHub
Getting Started:
Prerequisites:
Android Studio Ladybug or newer.
An Android device or emulator running API 24+.

Installation
Clone this repository:
Open the project in Android Studio.
Important: To enable the AI Verification, navigate to app/src/main/java/com/example/greentail_hackathon/GeminiVerificationHelper.java and insert your Gemini API Key in the API_KEY variable:
Java
private static final String API_KEY = "YOUR_API_KEY_HERE";
Build and Run the application.
