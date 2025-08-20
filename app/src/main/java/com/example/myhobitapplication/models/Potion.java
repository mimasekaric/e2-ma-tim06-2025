package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.PotionTypes;

public class Potion extends Equipment{

    private PotionTypes type;

    private Float price;

    //TODO: FALI SLIKA AL NE ZNAM KAKO DA JE INTERPRETIRAMO

    public Potion(Integer id, Boolean activated, PotionTypes type, Float price) {
        super(id, activated);
        this.type = type;
        this.price = price;
    }

    public PotionTypes getType() {
        return type;
    }

    public void setType(PotionTypes type) {
        this.type = type;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
