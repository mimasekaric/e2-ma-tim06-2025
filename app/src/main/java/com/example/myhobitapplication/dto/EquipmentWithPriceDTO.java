package com.example.myhobitapplication.dto;

import com.example.myhobitapplication.models.Equipment;

public class EquipmentWithPriceDTO {
    private Equipment equipment;
    private double price;

    public EquipmentWithPriceDTO(Equipment equipment, double price) {
        this.equipment = equipment;
        this.price = price;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
