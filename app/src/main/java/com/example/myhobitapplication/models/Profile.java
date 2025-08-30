package com.example.myhobitapplication.models;

import android.graphics.Bitmap;

import java.util.List;


public class Profile {

    private String UserId;

    private Integer Coins;
    private Integer XP;
    private Integer Level;

    private Bitmap QRCode;
    private Integer NumberOfBadges;
    private List<Badge> Badges;

    private List<Equipment> Equipment;

    public Profile(String userId, Integer coins, Integer XP, Integer level, Bitmap QRCode, Integer numberOfBadges, List<Badge> badges, List<Equipment> equipment) {
        UserId = userId;
        Coins = coins;
        this.XP = XP;
        Level = level;
        this.QRCode = QRCode;
        NumberOfBadges = numberOfBadges;
        Badges = badges;
        Equipment = equipment;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }


    public Integer getCoins() {
        return Coins;
    }

    public void setCoins(Integer coins) {
        Coins = coins;
    }

    public Integer getXP() {
        return XP;
    }

    public void setXP(Integer XP) {
        this.XP = XP;
    }

    public Integer getLevel() {
        return Level;
    }

    public void setLevel(Integer level) {
        Level = level;
    }

    public Integer getNumberOfBadges() {
        return NumberOfBadges;
    }

    public void setNumberOfBadges(Integer numberOfBadges) {
        NumberOfBadges = numberOfBadges;
    }

    public List<Badge> getBadges() {
        return Badges;
    }

    public void setBadges(List<Badge> badges) {
        this.Badges = badges;
    }

    public Bitmap getQRCode() {
        return QRCode;
    }

    public void setQRCode(Bitmap QRCode) {
        this.QRCode = QRCode;
    }

    public List<Equipment> getEquipment() {
        return Equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        Equipment = equipment;
    }
}
