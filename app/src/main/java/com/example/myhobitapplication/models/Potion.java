package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.PotionTypes;

public class Potion extends Equipment{



    private PotionTypes type;

    private boolean isPermanent;
    private double coef;



    public Potion(String id, Boolean activated, PotionTypes type, double coef) {
        super(id,activated);
        this.type = type;
        this.coef = coef;

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

    public Potion(String id, Boolean activated) {
        super(id, activated);
    }
    public Potion(){
    }

    public double getCoef() {
        return coef;
    }

    public void setCoef(double coef) {
        this.coef = coef;
    }


    public PotionTypes getType() {
        return type;
    }

    public void setType(PotionTypes type) {
        this.type = type;
    }

}
