package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.ClothingTypes;

public class Clothing extends Equipment{


    private ClothingTypes type;


    public Clothing(String id) {
        super(id);
    }
    public Clothing(){}

    public ClothingTypes getType() {
        return type;
    }


    public void setType(ClothingTypes type) {
        this.type = type;
    }



}
