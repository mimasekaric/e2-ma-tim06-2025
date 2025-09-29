package com.example.myhobitapplication.models;

import java.util.Date;

public class UserMission {

    private String id;
    private String userId;
    private String allianceId;
    private int purchaseCount;
    private int successfulAttackCount;
    private int easyTaskCompleteCount;
    private int totalDamage;
    private int hardTaskCompleteCount;
    private int uncompletedTasksCount;
    private Date todayDate;
    private boolean isMessageSent;

    public UserMission() {
    }

    public UserMission(String id, String userId, String allianceId, int purchaseCount, int successfulAttackCount, int easyTaskCompleteCount, int totalDamage, int hardTaskCompleteCount, int uncompletedTasksCount, Date todayDate, boolean isMessageSent) {
        this.id = id;
        this.userId = userId;
        this.allianceId = allianceId;
        this.purchaseCount = purchaseCount;
        this.successfulAttackCount = successfulAttackCount;
        this.easyTaskCompleteCount = easyTaskCompleteCount;
        this.totalDamage = totalDamage;
        this.hardTaskCompleteCount = hardTaskCompleteCount;
        this.uncompletedTasksCount = uncompletedTasksCount;
        this.todayDate = todayDate;
        this.isMessageSent = isMessageSent;
    }


    public UserMission(String userId, String allianceId, int purchaseCount, int successfulAttackCount, int easyTaskCompleteCount, int totalDamage, int hardTaskCompleteCount, int uncompletedTasksCount, Date todayDate, boolean isMessageSent) {
        this.userId = userId;
        this.allianceId = allianceId;
        this.purchaseCount = purchaseCount;
        this.successfulAttackCount = successfulAttackCount;
        this.easyTaskCompleteCount = easyTaskCompleteCount;
        this.totalDamage = totalDamage;
        this.hardTaskCompleteCount = hardTaskCompleteCount;
        this.uncompletedTasksCount = uncompletedTasksCount;
        this.todayDate = todayDate;
        this.isMessageSent = isMessageSent;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(String allianceId) {
        this.allianceId = allianceId;
    }

    public int getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(int purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    public int getSuccessfulAttackCount() {
        return successfulAttackCount;
    }

    public void setSuccessfulAttackCount(int successfulAttackCount) {
        this.successfulAttackCount = successfulAttackCount;
    }

    public int getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(int totalDamage) {
        this.totalDamage = totalDamage;
    }

    public int getHardTaskCompleteCount() {
        return hardTaskCompleteCount;
    }

    public void setHardTaskCompleteCount(int hardTaskCompleteCount) {
        this.hardTaskCompleteCount = hardTaskCompleteCount;
    }

    public int getEasyTaskCompleteCount() {
        return easyTaskCompleteCount;
    }

    public void setEasyTaskCompleteCount(int easyTaskCompleteCount) {
        this.easyTaskCompleteCount = easyTaskCompleteCount;
    }

    public Date getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(Date todayDate) {
        this.todayDate = todayDate;
    }

    public boolean isMessageSent() {
        return isMessageSent;
    }

    public void setMessageSent(boolean messageSent) {
        isMessageSent = messageSent;
    }

    public int getUncompletedTasksCount() {
        return uncompletedTasksCount;
    }

    public void setUncompletedTasksCount(int uncompletedTasksCount) {
        this.uncompletedTasksCount = uncompletedTasksCount;
    }
}
