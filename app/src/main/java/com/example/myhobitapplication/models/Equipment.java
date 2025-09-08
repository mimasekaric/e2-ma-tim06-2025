package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.EquipmentTypes;

public  class Equipment {

    private String id;

    private Boolean activated;
    private EquipmentTypes equipmentType;
    private double powerPercentage;
    private int image;
    public Equipment(){}
    public Equipment(String id, Boolean activated) {
        this.id = id;
        this.activated = activated;
        this.equipmentType = equipmentType;
    }

    public Equipment(String id, Boolean activated, EquipmentTypes equipmentType, double powerPercentage) {
        this.id = id;
        this.activated = activated;
        this.equipmentType = equipmentType;
        this.powerPercentage = powerPercentage;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EquipmentTypes getequipmentType() {
        return equipmentType;
    }

    public void setequipmentType(EquipmentTypes equipmentType) {
        this.equipmentType = equipmentType;
    }

    public double getpowerPercentage() {
        return powerPercentage;
    }

    public void setpowerPercentage(double powerPercentage) {
        this.powerPercentage = powerPercentage;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }
}
