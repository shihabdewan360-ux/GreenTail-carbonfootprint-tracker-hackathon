package com.example.greentail_hackathon;

import java.util.UUID;

public class CardModel {
    private String id;
    private String title;
    private String quantity;
    private String count;
    private int progress;

    public CardModel(String title, String quantity, String count, int progress) {
        this.id = UUID.randomUUID().toString(); // Generate unique ID
        this.title = title;
        this.quantity = quantity;
        this.count = count;
        this.progress = progress;
    }

    // Add this constructor for Gson deserialization
    public CardModel() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Other getters and setters remain the same
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}