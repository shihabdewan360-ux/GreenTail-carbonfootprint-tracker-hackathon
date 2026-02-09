package com.example.greentail_hackathon;

public class User {
    public String firstName;
    public String lastName; // Added to match your SignUp logic
    public String email;    // Added to match your SignUp logic
    public double totalCO2Saved;
    public int taliPoints;

    // Organization features
    public boolean isOrganization;
    public String groupCode;       // Code generated for Admin
    public String linkedOrgId;     // The Admin's UID for team members

    public User() {
        // Default constructor required for Firebase
    }

    // Updated constructor to handle all fields
    public User(String firstName, String lastName, String email, boolean isOrganization, String groupCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isOrganization = isOrganization;
        this.groupCode = groupCode;
        this.totalCO2Saved = 0.0;
        this.taliPoints = 0;
        this.linkedOrgId = ""; // Empty by default until a member joins
    }

    // Getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getTotalCO2Saved() {
        return totalCO2Saved;
    }

    public void setTotalCO2Saved(double totalCO2Saved) {
        this.totalCO2Saved = totalCO2Saved;
    }

    public int getTaliPoints() {
        return taliPoints;
    }

    public void setTaliPoints(int taliPoints) {
        this.taliPoints = taliPoints;
    }

    public boolean isOrganization() {
        return isOrganization;
    }

    public void setOrganization(boolean organization) {
        isOrganization = organization;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getLinkedOrgId() {
        return linkedOrgId;
    }

    public void setLinkedOrgId(String linkedOrgId) {
        this.linkedOrgId = linkedOrgId;
    }
}