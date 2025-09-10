package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.PotionTypes;

public class Potion extends Equipment{

    private PotionTypes type;

    private boolean isPermanent;

    public Potion(String id, PotionTypes type) {
        super(id);
        this.type = type;
    }


    public void setpotionDescription(String potionDescription) {
        potionDescription = potionDescription;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public void setPermanent(boolean permanent) {
        isPermanent = permanent;
    }

    public Potion(String id) {
        super(id);
    }
    public Potion(){
    }

    public PotionTypes getType() {
        return type;
    }

    public void setType(PotionTypes type) {
        this.type = type;
    }

}
