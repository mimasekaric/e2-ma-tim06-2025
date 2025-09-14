package com.example.myhobitapplication.dto;

import com.example.myhobitapplication.models.Equipment;

public class UserEquipmentDTO {
    private Integer userEquipmentId;
    private Equipment equipment;

    public UserEquipmentDTO ( Integer userEquipmentId, Equipment equipment){
        this.userEquipmentId = userEquipmentId;
        this.equipment = equipment;
    }
    public Integer getUserEquipmentId() {
        return userEquipmentId;
    }

    public void setUserEquipmentId(Integer userEquipmentId) {
        this.userEquipmentId = userEquipmentId;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }
}
