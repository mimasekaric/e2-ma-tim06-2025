package com.example.myhobitapplication.dto;

import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.models.UserMission;
public class UserProgressDTO {

    private String userId;
    private String username;
    private String avatarName;

    private int totalDamage;
    private int purchaseCount;
    private int successfulAttackCount;
    private int easyTaskCompleteCount;
    private int hardTaskCompleteCount;
    private boolean messageSentToday;
    private int messageCount;

    public UserProgressDTO() {
    }

    public UserProgressDTO(User user, UserMission missionProgress) {
        if (user != null) {
            this.userId = user.getUid();
            this.username = user.getusername();
            this.avatarName = user.getavatarName();
        }

        if (missionProgress != null) {
            this.totalDamage = missionProgress.getTotalDamage();
            this.purchaseCount = missionProgress.getPurchaseCount();
            this.successfulAttackCount = missionProgress.getSuccessfulAttackCount();
            this.easyTaskCompleteCount = missionProgress.getEasyTaskCompleteCount();
            this.hardTaskCompleteCount = missionProgress.getHardTaskCompleteCount();
            this.messageSentToday = missionProgress.isMessageSent();
            this.messageCount = missionProgress.getMessageCount();
        } else {

            this.totalDamage = 0;
            this.purchaseCount = 0;
            this.successfulAttackCount = 0;
            this.easyTaskCompleteCount = 0;
            this.hardTaskCompleteCount = 0;
            this.messageSentToday = false;
            this.messageCount = 0;
        }
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public int getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(int totalDamage) {
        this.totalDamage = totalDamage;
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

    public int getEasyTaskCompleteCount() {
        return easyTaskCompleteCount;
    }

    public void setEasyTaskCompleteCount(int easyTaskCompleteCount) {
        this.easyTaskCompleteCount = easyTaskCompleteCount;
    }

    public int getHardTaskCompleteCount() {
        return hardTaskCompleteCount;
    }

    public void setHardTaskCompleteCount(int hardTaskCompleteCount) {
        this.hardTaskCompleteCount = hardTaskCompleteCount;
    }
    public boolean isMessageSentToday() {
        return messageSentToday;
    }

    public void setMessageSentToday(boolean messageSentToday) {
        this.messageSentToday = messageSentToday;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }
}