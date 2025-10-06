package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.MissionStatus;

import java.util.Date;

public class AllianceMission {

    private String id;
    private String allianceId;
    private int totalBossHp;
    private String bossId;
    private int currentBossHp;
    private Date startDate;
    private Date endDate;
    private MissionStatus status;

    public AllianceMission() {}

    public AllianceMission(String id, String allianceId, int totalBossHp, String bossId, int currentBossHp, Date startDate, Date endDate, MissionStatus status) {
        this.id = id;
        this.allianceId = allianceId;
        this.totalBossHp = totalBossHp;
        this.bossId = bossId;
        this.currentBossHp = currentBossHp;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public AllianceMission(String allianceId, int totalBossHp, String bossId, int currentBossHp, Date startDate, Date endDate, MissionStatus status) {
        this.allianceId = allianceId;
        this.totalBossHp = totalBossHp;
        this.bossId = bossId;
        this.currentBossHp = currentBossHp;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(String allianceId) {
        this.allianceId = allianceId;
    }

    public int getTotalBossHp() {
        return totalBossHp;
    }

    public void setTotalBossHp(int totalBossHp) {
        this.totalBossHp = totalBossHp;
    }

    public int getCurrentBossHp() {
        return currentBossHp;
    }

    public void setCurrentBossHp(int currentBossHp) {
        this.currentBossHp = currentBossHp;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public String getBossId() {
        return bossId;
    }

    public void setBossId(String bossId) {
        this.bossId = bossId;
    }
}
