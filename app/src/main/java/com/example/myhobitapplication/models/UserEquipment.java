package com.example.myhobitapplication.models;

public class UserEquipment{

    private Integer id;
    private String equipmentId;
    private String userId;
    private Boolean activated;
    private int fightsCounter;


    public UserEquipment(){}
    public UserEquipment(Integer id, String equipmentId,String userId, Boolean activated, int fightsCounter) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.activated = activated;
        this.fightsCounter = fightsCounter;
        this.userId=userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public int getFightsCounter() {
        return fightsCounter;
    }

    public void setFightsCounter(int fightsCounter) {
        this.fightsCounter = fightsCounter;
    }
}
