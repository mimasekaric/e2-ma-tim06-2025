package com.example.myhobitapplication.models;

import com.example.myhobitapplication.enums.WeaponTypes;

public class Weapon extends Equipment{

    private WeaponTypes type;

    public Weapon(String id, WeaponTypes type) {
        super(id);
        this.type = type;
    }
    public Weapon(String id){
            super(id);
    }

    public Weapon(){}

    public WeaponTypes getType() {
        return type;
    }

    public void setType(WeaponTypes type) {
        this.type = type;
    }
}
