package com.example.myhobitapplication.models;

import java.util.Date;

public class Badge {
    private String type;
    private int damage;
    private Date dateEarned;
    private String missionId;

    public Badge() {}

    public Badge(String type, int taskCount, Date dateEarned, String missionId) {
        this.type = type;
        this.damage = taskCount;
        this.dateEarned = dateEarned;
        this.missionId = missionId;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Date getDateEarned() {
        return dateEarned;
    }

    public void setDateEarned(Date dateEarned) {
        this.dateEarned = dateEarned;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }
}