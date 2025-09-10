package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.EquipmentTypes;

public  class Equipment {

    private String id;

    private EquipmentTypes equipmentType;
    private double powerPercentage;
    private int image;
    private double coef;
    public Equipment(){}
    public Equipment(String id) {
        this.id = id;
        this.equipmentType = equipmentType;
    }

    public Equipment(String id,EquipmentTypes equipmentType, double powerPercentage) {
        this.id = id;
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

    public double getCoef() {
        return coef;
    }

    public void setCoef(double coef) {
        this.coef = coef;
    }
}
