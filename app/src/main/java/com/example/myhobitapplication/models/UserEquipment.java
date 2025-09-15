package com.example.myhobitapplication.models;

public class UserEquipment{

    private Integer id;
    private String equipmentId;
    private String userId;
    private Boolean activated;
    private int fightsCounter;
    private int effect;
    private double coef;


    public UserEquipment(){}
    public UserEquipment(Integer id, String equipmentId,String userId, Boolean activated, int fightsCounter, double coef) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.activated = activated;
        this.fightsCounter = fightsCounter;
        this.userId=userId;
        this.coef = coef;

    }

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public double getCoef() {
        return coef;
    }

    public void setCoef(double coef) {
        this.coef = coef;
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
