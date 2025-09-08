package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.WeaponTypes;

public class Weapon extends Equipment{

    private WeaponTypes type;

    public Weapon(String id, Boolean activated, WeaponTypes type) {
        super(id, activated);
        this.type = type;
    }
    public Weapon(String id, Boolean activated){
            super(id,activated);
    }

    public Weapon(){}

    public WeaponTypes getType() {
        return type;
    }

    public void setType(WeaponTypes type) {
        this.type = type;
    }
}
