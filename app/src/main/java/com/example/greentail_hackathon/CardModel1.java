package com.example.greentail_hackathon;

public class CardModel1 {
    private String title;
    private String quantity;
    private String count;
    private int progress;

    public CardModel1() {
        // Default constructor required for Firebase
    }

    public CardModel1(String title, String quantity, String count, int progress) {
        this.title = title;
        this.quantity = quantity;
        this.count = count;
        this.progress = progress;
    }

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