# GreenTail: Gamified Carbon Tracking & Sustainable Living

GreenTail is an Android-based application designed to transform environmental awareness from a chore into a rewarding, gamified experience. By combining rigorous footprint calculation with AI-assisted action verification, GreenTail helps users bridge the gap between "knowing" and "doing."

## Key Features

* **Multi-Category Footprint Survey**: A comprehensive estimation engine that calculates your impact across four pillars using internationally recognized standards.
* **AI Action Verification**: (Powered by Google Gemini 3.0) To ensure credibility, GreenTail utilizes the Google Gemini API to verify eco-friendly actions. 
* **Gamification & Habits**: Earn rewards and visualize progress through dynamic charts.
* **Social Challenges**: Join teams and participate in collective goals.

## Tech Stack

* **Language**: Java
* **Platform**: Android Studio
* **AI Integration**: Google Gemini 3.0 API
* **Version Control**: Git & GitHub

## Getting Started

### Prerequisites
* Android Studio Ladybug or newer.
* An Android device or emulator running API 24+.

### Installation
1. Clone this repository.
2. Open the project in Android Studio.
3. **Important**: Navigate to `app/src/main/java/com/example/greentail_hackathon/GeminiVerificationHelper.java` and insert your API Key:
   `private static final String API_KEY = "YOUR_API_KEY_HERE";`
4. Build and Run the application.
