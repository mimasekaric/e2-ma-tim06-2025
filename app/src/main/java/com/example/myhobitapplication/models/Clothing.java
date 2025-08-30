package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.ClothingTypes;

public class Clothing extends Equipment{

    private ClothingTypes type;

    //TODO:SLIKA

    private Float price;

    public Clothing(Integer id, Boolean activated, ClothingTypes type, Float price) {
        super(id, activated);
        this.type = type;
        this.price = price;
    }

    public ClothingTypes getType() {
        return type;
    }

    public Float getPrice() {
        return price;
    }

    public void setType(ClothingTypes type) {
        this.type = type;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
