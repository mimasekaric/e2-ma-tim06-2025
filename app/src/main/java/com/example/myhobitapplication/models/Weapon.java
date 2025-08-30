package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.WeaponTypes;

public class Weapon extends Equipment{

    private WeaponTypes type;

    //TODO:SLIKA

    public Weapon(Integer id, Boolean activated, WeaponTypes type) {
        super(id, activated);
        this.type = type;
    }

    public WeaponTypes getType() {
        return type;
    }

    public void setType(WeaponTypes type) {
        this.type = type;
    }
}
