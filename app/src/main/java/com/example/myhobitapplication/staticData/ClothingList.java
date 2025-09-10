package com.example.myhobitapplication.staticData;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.enums.ClothingTypes;
import com.example.myhobitapplication.enums.EquipmentTypes;
import com.example.myhobitapplication.models.Clothing;

import java.util.ArrayList;
import java.util.List;

public class ClothingList {

    public static List<Clothing> getClothingList() {
        List<Clothing> clothingList = new ArrayList<>();

        Clothing gloves = new Clothing("4");
        gloves.setequipmentType(EquipmentTypes.CLOTHING);
        gloves.setCoef(0.6);
        gloves.setpowerPercentage(10);
        gloves.setImage(R.mipmap.ic_launcher_gloves);
        gloves.setType(ClothingTypes.GLOVES);
        clothingList.add(gloves);

        Clothing boots = new Clothing("5");
        boots.setequipmentType(EquipmentTypes.CLOTHING);
        boots.setCoef(0.8);
        boots.setpowerPercentage(40);
        boots.setImage(R.mipmap.ic_launcher_boot);
        boots.setType(ClothingTypes.BOOTS);
        clothingList.add(boots);

        Clothing shield = new Clothing("6");
        shield.setequipmentType(EquipmentTypes.CLOTHING);
        shield.setCoef(0.6);
        shield.setpowerPercentage(10);
        shield.setImage(R.mipmap.ic_launcher_shield);
        shield.setType(ClothingTypes.SHIELD);
        clothingList.add(shield);


        return clothingList;




    }
}