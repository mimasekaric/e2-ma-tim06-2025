package com.example.myhobitapplication.models;

import android.graphics.Bitmap;

import com.example.myhobitapplication.enums.Title;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Profile {

    private String userUid;
    private String title;
    private Integer coins;
    private Integer pp;
    private Integer xp;
    private Integer level;

    // private Bitmap QRCode;
    private Integer numberOgBadges;
    private List<String> badges;

    private int xpRequired;

    private Date previousLevelDate;
    private Date currentLevelDate;

    public Profile(String userUid, Integer coins, Integer xp, Integer level, Integer numberOfbadges, List<String> badges, int xpRequired, String title, Integer pp, Date lastLevelDate, Date currentLevelDate) {
        this.userUid = userUid;
        this.coins = coins;
        this.xp = xp;
        this.level = level;
        this.numberOgBadges = numberOfbadges;
        this.badges = badges;
        this.xpRequired = xpRequired;
        this.title=title;
        this.pp=pp;
        this.currentLevelDate= currentLevelDate;
        this.previousLevelDate = lastLevelDate;
    }

    public Profile(String userUid) {
        this.userUid = userUid;
        this.coins = 0;
        this.xp = 0;
        this.title= Title.CURIOUS_WANDERER.toString();
        this.level = 0;
        this.numberOgBadges = 0;
        this.badges = new ArrayList<>();
        this.pp=0;
        this.xpRequired = 200;
        this.previousLevelDate = new Date();
        this.currentLevelDate = new Date();
    }

    public Profile(){
        badges = new ArrayList<>();
    }
    public String getuserUid() {
        return userUid;
    }

    public int getXpRequired() {
        return xpRequired;
    }

    public void setXpRequired(int xpRequired) {
        this.xpRequired = xpRequired;
    }

    public void setuserUid(String userUid) {
        this.userUid = userUid;
    }


    public Integer getcoins() {
        return coins;
    }

    public void setcoins(Integer coins) {
        this.coins = coins;
    }

    public Integer getxp() {
        return xp;
    }

    public void setxp(Integer xp) {
        this.xp = xp;
    }

    public Integer getlevel() {
        return level;
    }

    public void setlevel(Integer level) {
        this.level = level;
    }

    public Integer getnumberOgbadges() {
        return numberOgBadges;
    }

    public void setnumberOgbadges(Integer numberOfbadges) {
        this.numberOgBadges = numberOfbadges;
    }

    public List<String> getbadges() {
        return badges;
    }

    public void setbadges(List<String> badges) {
        this.badges = badges;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPp() {
        return pp;
    }

    public void setPp(Integer pp) {
        this.pp = pp;
    }

    public Date getPreviousLevelDate() {
        return previousLevelDate;
    }

    public void setPreviousLevelDate(Date previousLevelDate) {
        this.previousLevelDate = previousLevelDate;
    }

    public Date getCurrentLevelDate() {
        return currentLevelDate;
    }

    public void setCurrentLevelDate(Date currentLevelDate) {
        this.currentLevelDate = currentLevelDate;
    }
}