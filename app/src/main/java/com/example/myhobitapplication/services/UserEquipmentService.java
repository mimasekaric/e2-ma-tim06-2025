package com.example.myhobitapplication.services;

import android.content.Context;

import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.databases.ProfileRepository;
import com.example.myhobitapplication.databases.UserEquipmentRepository;
import com.example.myhobitapplication.dto.EquipmentWithPriceDTO;
import com.example.myhobitapplication.enums.ClothingTypes;
import com.example.myhobitapplication.enums.EquipmentTypes;
import com.example.myhobitapplication.models.Clothing;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.models.UserEquipment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

public class UserEquipmentService {

    private final BossRepository bossRepository;
    private final UserEquipmentRepository repository;

    private final EquipmentRepository equipmentRepository;
    private final ProfileRepository profileRepository;
    public UserEquipmentService(UserEquipmentRepository repository, ProfileRepository profilerepo, BossRepository bossRepository, EquipmentRepository equipmentRepository){
        this.repository = repository;
        this.profileRepository = profilerepo;
        this.bossRepository = bossRepository;
        this.equipmentRepository = equipmentRepository;
        this.repository.open();
    }

    public UserEquipmentService(Context context){
        this.repository = new UserEquipmentRepository(context);
        this.profileRepository = new ProfileRepository();
        this.bossRepository = new BossRepository(context);
        this.equipmentRepository = new EquipmentRepository(context);
        this.repository.open();
    }


    public List<Equipment> getUserEquipment(String userId){
        List<UserEquipment> all = getAllbyUserId(userId);
        List<Equipment> equipment = new ArrayList<>();
        for(UserEquipment ue : all){
            equipment.add(equipmentRepository.getEquipmentById(ue.getEquipmentId()));
        }
        return equipment;
    }

    public long insert(UserEquipment ue){
        return repository.insertUserEquipment(ue);
    }
    public long save(String userId, String equpmentId ){
        UserEquipment ue = new UserEquipment();
        ue.setUserId(userId);
        ue.setEquipmentId(equpmentId);
        ue.setFightsCounter(0);
        ue.setActivated(false);
        return repository.insertUserEquipment(ue);
    }
    public List<UserEquipment> getAllbyUserId(String id){ return repository.getAllByUserId(id);}

    public UserEquipment getById(Integer id){ return repository.getById(id);}


    // napitak ima polje isPermanent pa na osnovu njega brisemo/ili ne nakon borbe sa bosom
    //za uklanjanje odjece nakon borbi sa bosom ima fightsCounter polje
    //za sad obradjeno samo za aktivaciju napitaka i rukavica!!
   public boolean activateEquipment(UserEquipment userEquipment,Profile profile ){
       userEquipment.setActivated(true);
       Equipment e = equipmentRepository.getEquipmentById(userEquipment.getEquipmentId());
       if(e.getequipmentType().equals(EquipmentTypes.POTION) ) {
           int newpp=((int) Math.round(profile.getxp() + (e.getpowerPercentage() / 100 * profile.getxp())));
           profileRepository.updatePp(profile.getuserUid(), newpp);
       }else if (e.getequipmentType().equals(EquipmentTypes.CLOTHING) ) {
           Clothing c = (Clothing) e;
           int size = getAllSameTypeActivatedClothing(profile, c.getType()).size();
           double pp= e.getpowerPercentage() + (e.getpowerPercentage()*size);
           if(((Clothing) e).getType().equals(ClothingTypes.GLOVES) ) {
               int newpp=((int) Math.round(profile.getxp() + (pp / 100 * profile.getxp())));
               profileRepository.updatePp(profile.getuserUid(), newpp);
           }
           else if(((Clothing) e).getType().equals(ClothingTypes.BOOTS) ) {
           }
       }
       repository.updateUserEquipment(userEquipment);
        return true;
   }

   public void deactivateEquipment(UserEquipment userEquipment,Profile profile){
        userEquipment.setActivated(false);
       repository.updateUserEquipment(userEquipment);

   }

   public List<UserEquipment> getAllSameTypeActivatedClothing(Profile profile, ClothingTypes type){
       List<UserEquipment> eqlist=  repository.getAllByUserId(profile.getuserUid());
       List<UserEquipment> eqlistActivated = new ArrayList<>();
       for (UserEquipment eq : eqlist){
           if (eq.getActivated() &&( (Clothing)(equipmentRepository.getEquipmentById(eq.getEquipmentId()))).getType().equals(type) ){
               eqlistActivated.add(eq);
           }
       }
        return  eqlistActivated;
   }
    public boolean buyEquipment(Profile profile, Equipment equipment){
        double price = countPrice(profile, equipment);
        if(profile.getcoins() >= price) {
            save(profile.getuserUid(), equipment.getId());
            int newCoinsValue = (int) Math.round(profile.getcoins() - price);
            profileRepository.updateCoins(profile.getuserUid(), newCoinsValue);
            return true;
        }
        return false;
    }

    public List<Equipment> getByType(EquipmentTypes type) {
        return equipmentRepository.getEquipmentByType(type);
    }


    public double countPrice(Profile profile, Equipment equipment) {
        int bossReward = 200;

        if (profile != null && profile.getlevel() > 1) {
            var boss = bossRepository.getPreviousBossForUser(
                    profile.getuserUid(),
                    profile.getlevel()
            );

            if (boss != null) {
                Integer reward = boss.getCoinsReward();
                bossReward = (reward != null) ? reward : 200;
            }
        }

        return equipment.getCoef() * bossReward;

    }

}
