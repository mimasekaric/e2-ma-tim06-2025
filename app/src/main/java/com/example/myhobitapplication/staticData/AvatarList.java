package com.example.myhobitapplication.staticData;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.models.Avatar;

import java.util.ArrayList;
import java.util.List;

public class AvatarList {

    public static List<Avatar> getAvatarList(){
        List<Avatar> avatarList = new ArrayList<>();

        Avatar Witch = new Avatar();
        Witch.setName("Witch");
        Witch.setImage(R.mipmap.ic_launcher_witch);
        avatarList.add(Witch);

        Avatar Wizard = new Avatar();
        Wizard.setName("Wizard");
        Wizard.setImage(R.mipmap.ic_launcher_wizard);
        avatarList.add(Wizard);

        Avatar Elf = new Avatar();
        Elf.setName("Elf");
        Elf.setImage(R.mipmap.ic_launcher_elf);
        avatarList.add(Elf);

        Avatar Elffemme = new Avatar();
        Elffemme.setName("Elffemme");
        Elffemme.setImage(R.mipmap.ic_launcher_elffem);
        avatarList.add(Elffemme);

        Avatar Satyr = new Avatar();
        Satyr.setName("Satyr");
        Satyr.setImage(R.mipmap.ic_launcher_satyr);
        avatarList.add(Satyr);


        Avatar Sprite = new Avatar();
        Sprite.setName("Sprite");
        Sprite.setImage(R.mipmap.ic_launcher_sprite);
        avatarList.add(Sprite);





        return avatarList;
    }
}
