package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.ClothingTypes;

public class Clothing extends Equipment{


    private ClothingTypes type;
    private double coef;

    private int fightsCounter;
    public Clothing(String id, Boolean activated) {
        super(id, activated);
        this.fightsCounter=0;
    }
    public Clothing(){}

    public int getFightsCounter() {
        return fightsCounter;
    }

    public void setFightsCounter(int fightsCounter) {
        this.fightsCounter = fightsCounter;
    }



    public ClothingTypes getType() {
        return type;
    }

    public void setType(ClothingTypes type) {
        this.type = type;
    }

    public double getCoef() {
        return coef;
    }

    public void setCoef(double coef) {
        this.coef = coef;
    }


}
