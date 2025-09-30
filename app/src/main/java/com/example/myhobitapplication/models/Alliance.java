package com.example.myhobitapplication.models;

import java.util.Date;

public class Alliance {
    String id;
    String name;
    String leaderId;
    boolean hasActivatedMission;
    Date missionStartDate;
    Date missionEndDate;

    public Alliance(){}

    public Alliance(String name, String leaderId, boolean hasActivatedMission, Date missionStartDate, Date missionEndDate) {
        this.name = name;
        this.leaderId = leaderId;
        this.hasActivatedMission = hasActivatedMission;
        this.missionStartDate = missionStartDate;
        this.missionEndDate = missionEndDate;
    }
    public Alliance(String id,String name, String leaderId, boolean hasActivatedMission, Date missionStartDate, Date missionEndDate) {
        this.name = name;
        this.leaderId = leaderId;
        this.hasActivatedMission = hasActivatedMission;
        this.missionStartDate = missionStartDate;
        this.missionEndDate = missionEndDate;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public boolean getHasActivatedMission() {
        return hasActivatedMission;
    }

    public void setHasActivatedMission(boolean hasActivatedMission) {
        this.hasActivatedMission = hasActivatedMission;
    }

    public Date getMissionStartDate() {
        return missionStartDate;
    }

    public void setMissionStartDate(Date missionStartDate) {
        this.missionStartDate = missionStartDate;
    }

    public Date getMissionEndDate() {
        return missionEndDate;
    }

    public void setMissionEndDate(Date missionEndDate) {
        this.missionEndDate = missionEndDate;
    }
}
