package com.example.myhobitapplication.staticData;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.enums.EquipmentTypes;
import com.example.myhobitapplication.enums.PotionTypes;
import com.example.myhobitapplication.models.Potion;

import java.util.ArrayList;
import java.util.List;

public class PotionList {

    public static List<Potion> getPotionList() {
        List<Potion> potionList = new ArrayList<>();

        Potion potion5 = new Potion("0");
        potion5.setequipmentType(EquipmentTypes.POTION);
        potion5.setType(PotionTypes.FAIRY_DUST_KISS);
        potion5.setImage(R.mipmap.ic_launcher_potion5);
        potion5.setCoef(2);
        potion5.setpowerPercentage(5);
        potion5.setPermanent(true);
        potionList.add(potion5);

        Potion potion10 = new Potion("1");
        potion10.setequipmentType(EquipmentTypes.POTION);
        potion10.setType(PotionTypes.SEED_OF_STRENGTH);
        potion10.setImage(R.mipmap.ic_launcher_potion10);
        potion10.setCoef(10);
        potion10.setpowerPercentage(10);
        potion10.setPermanent(true);
        potionList.add(potion10);

        Potion potion20 = new Potion("2");
        potion20.setequipmentType(EquipmentTypes.POTION);
        potion20.setType(PotionTypes.DEEP_FOREST_ESSENCE);
        potion20.setImage(R.mipmap.ic_launcher_potion20);
        potion20.setCoef(0.5);
        potion20.setpowerPercentage(20);
        potion20.setPermanent(false);
        potionList.add(potion20);

        Potion potion40 = new Potion("3");
        potion40.setequipmentType(EquipmentTypes.POTION);
        potion40.setType(PotionTypes.ETERNAL_POWER_ELIXIR);
        potion40.setImage(R.mipmap.ic_launcher_potion40);
        potion40.setCoef(0.7);
        potion40.setpowerPercentage(40);
        potion40.setPermanent(false);
        potionList.add(potion40);

        return potionList;
    }


}
