package com.example.myhobitapplication.staticData;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.enums.EquipmentTypes;
import com.example.myhobitapplication.enums.WeaponTypes;
import com.example.myhobitapplication.models.Weapon;

import java.util.ArrayList;
import java.util.List;

public class WeaponList {

    public static List<Weapon> getWeaponList() {
        List<Weapon> weaponList = new ArrayList<>();

        Weapon sword = new Weapon("7", false);
        sword.setequipmentType(EquipmentTypes.WEAPON);
        sword.setImage(R.mipmap.ic_launcher_sword);
        sword.setpowerPercentage(0.05);
        sword.setType(WeaponTypes.ANDURIL_OF_ARAGORN);
        weaponList.add(sword);

        Weapon bowAndArrow = new Weapon("8", false);
        bowAndArrow.setequipmentType(EquipmentTypes.WEAPON);
        bowAndArrow.setImage(R.mipmap.ic_launcher_bow_and_arrow);
        bowAndArrow.setpowerPercentage(0.05);
        bowAndArrow.setType(WeaponTypes.BOW_AND_ARROW_OF_LEGOLAS);
        weaponList.add(bowAndArrow);

        return weaponList;
    }
}