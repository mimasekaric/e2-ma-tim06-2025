package com.example.myhobitapplication.services;

import android.content.Context;

import com.example.myhobitapplication.dto.BossDTO;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.EquipmentService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.databases.UserEquipmentRepository;
import com.example.myhobitapplication.dto.EquipmentWithPriceDTO;
import com.example.myhobitapplication.dto.UserEquipmentDTO;
import com.example.myhobitapplication.enums.ClothingTypes;
import com.example.myhobitapplication.enums.EquipmentTypes;
import com.example.myhobitapplication.enums.WeaponTypes;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.models.Clothing;
import com.example.myhobitapplication.models.Potion;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.models.UserEquipment;
import com.example.myhobitapplication.models.Weapon;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/// /vidi mozda ako vec ima aktiviran clothes istog tipa al da nije counterfight 0 i effect 0 ... p=mozda i tu treba sabirati ucinak?
/// PROVJERI RADI LI ZA CIZME I STIT
///kad gainuje isto oruyje povecava s evjerovatnocaa
/// ///NAMJESTI KAD POBIJEDI DA SE LOADUJE PROFIL OPET i vidi dal na dobrom ide u won

public class UserEquipmentService {

    private final BossService bossService;
    private final UserEquipmentRepository repository;

    private final EquipmentService equipmentService;
    private final ProfileService profileService;
    public UserEquipmentService(UserEquipmentRepository repository, ProfileService profileService, BossService bossService, EquipmentService equipmentService){
        this.repository = repository;
        this.profileService = profileService;
        this.bossService = bossService;
        this.equipmentService = equipmentService;
        this.repository.open();
    }

    public UserEquipmentService(Context context,  ProfileService profileService, BossService bossService, EquipmentService equipmentService){
        this.repository = new UserEquipmentRepository(context);
        this.profileService = profileService;
        this.bossService = bossService;
        this.equipmentService = equipmentService;
        this.repository.open();
    }

    public void delete(UserEquipment ue){
        repository.delete(ue);
    }
    public List<Equipment> getUserEquipment(String userId){
        List<UserEquipment> all = getAllbyUserId(userId);
        List<Equipment> equipment = new ArrayList<>();
        for(UserEquipment ue : all){
            equipment.add(equipmentService.getEquipmentById(ue.getEquipmentId()));
        }
        return equipment;
    }

    public void incrementFightsCounter(String userid, Profile profile){
            getUserActivatedEquipment(userid).forEach(ueDTO->{
                UserEquipment userEquipment = getById(ueDTO.getUserEquipmentId());
                userEquipment.setFightsCounter(userEquipment.getFightsCounter() + 1);
                if(ueDTO.getEquipment().getequipmentType().equals(EquipmentTypes.POTION)){
                    repository.delete(userEquipment);
                    if(!((Potion)ueDTO.getEquipment()).isPermanent()){
                        double newpp = profile.getPp() - userEquipment.getEffect();
                        profileService.updatePp(profile.getuserUid(), (int)newpp);
                        repository.delete(userEquipment);
                    }
                }else if(ueDTO.getEquipment().getequipmentType().equals(EquipmentTypes.CLOTHING) && userEquipment.getFightsCounter() > 1){
                    Clothing c = (Clothing) ueDTO.getEquipment();
                    double newpp = profile.getPp() - userEquipment.getEffect();
                    profileService.updatePp(profile.getuserUid(), (int)newpp);
                    repository.delete(userEquipment);
                }else{
                    repository.updateUserEquipment(userEquipment);
                }

        });
    }
    public List<UserEquipmentDTO> getUserNotActivatedEquipment(String userId){
        List<UserEquipmentDTO> equipment = new ArrayList<>();
            getAllbyUserId(userId).forEach(ue->{
                if(!ue.getActivated()) {
                    equipment.add(new UserEquipmentDTO(ue.getId(),equipmentService.getEquipmentById(ue.getEquipmentId())));
                }
        });
        return equipment;
    }

    public List<UserEquipmentDTO> getUserActivatedEquipment(String userId){
        List<UserEquipmentDTO> equipment = new ArrayList<>();
        getAllbyUserId(userId).forEach(ue->{
            if(ue.getActivated()) {
                equipment.add(new UserEquipmentDTO(ue.getId(),equipmentService.getEquipmentById(ue.getEquipmentId())));
            }
        });
        return equipment;
    }

    public long insert(UserEquipment ue){
        return repository.insertUserEquipment(ue);
    }
    public long save(String userId, Equipment e ){
        UserEquipment ue = new UserEquipment();
        ue.setUserId(userId);
        ue.setEquipmentId(e.getId());
        ue.setFightsCounter(0);
        ue.setActivated(false);
        ue.setCoef(e.getCoef());
        ue.setEffect(e.getpowerPercentage());
        return repository.insertUserEquipment(ue);
    }
    public List<UserEquipment> getAllbyUserId(String id){ return repository.getAllByUserId(id);}

    public UserEquipment getById(Integer id){ return repository.getById(id);}


    //za sad obradjeno samo za aktivaciju napitaka i rukavica!!
    //efekat cizama i stita je hendlovan u battleviewmodel
   public boolean activateEquipment(UserEquipmentDTO userEquipDTO,Profile profile )
   {
       UserEquipment userEquipment =  repository.getById(userEquipDTO.getUserEquipmentId());
       userEquipment.setActivated(true);
       repository.updateUserEquipment(userEquipment);
        return true;
   }

   public void updateUserEquipment(UserEquipment userEquipment){
        repository.updateUserEquipment(userEquipment);
   }

    public void activatedEquipmentEffect(Profile profile){
        String userId = profile.getuserUid();
        Map<ClothingTypes, Double> clothingEffects = new HashMap<>();
        getUserActivatedEquipment(userId).forEach(userEquipmentDTO -> {
            UserEquipment userEquipment =  repository.getById(userEquipmentDTO.getUserEquipmentId());
            Equipment e = equipmentService.getEquipmentById(userEquipment.getEquipmentId());
            if(e.getequipmentType().equals(EquipmentTypes.POTION) ) {
               activatePotionEffect(profile, userEquipment);
            }else if (e.getequipmentType().equals(EquipmentTypes.CLOTHING) && userEquipment.getEffect()==0  ) {
                Clothing c = (Clothing) e;
                if(c.getType().equals(ClothingTypes.GLOVES)) {
                    clothingEffects.merge(c.getType(), e.getpowerPercentage(), Double::sum);
                }
            }else if(e.getequipmentType().equals(EquipmentTypes.WEAPON)){
               activateWeaponEffect(profile,userEquipment);
            }
        });
        clothingEffects.entrySet().forEach(clothingTypesDoubleEntry -> {
            ClothingTypes type = clothingTypesDoubleEntry.getKey();
            if (type.equals(ClothingTypes.GLOVES)) {
                double powerdelta = profile.getPp() * (clothingTypesDoubleEntry.getValue()  / 100.0);
                int delta = (int) Math.round(powerdelta);
                int newpp = profile.getPp() + delta ;
                profileService.updatePp(profile.getuserUid(), newpp);
                calculateGlovesEffect(profile, delta);
            }
        });
   }
    public void activatePotionEffect(Profile profile,  UserEquipment userEquipment){
        Equipment e = equipmentService.getEquipmentById(userEquipment.getEquipmentId());
        double powerdelta = profile.getPp() * (e.getpowerPercentage() / 100.0);
        int delta = (int) Math.round(powerdelta);
        int newpp = profile.getPp() + delta ;
        profileService.updatePp(profile.getuserUid(), newpp);
        userEquipment.setEffect(delta);
        repository.updateUserEquipment(userEquipment);
    }

    public void activateWeaponEffect(Profile profile,  UserEquipment userEquipment){
        Equipment e = equipmentService.getEquipmentById(userEquipment.getEquipmentId());
        String userId = profile.getuserUid();
        Weapon w = (Weapon) e;
        if(w.getType().equals(WeaponTypes.ANDURIL_OF_ARAGORN)){
            double powerdelta = profile.getPp() * (e.getpowerPercentage() / 100.0);
            int newpp = profile.getPp() + (int) Math.round(powerdelta);
            profileService.updatePp(profile.getuserUid(), newpp);
        }else{
            BossDTO currentBossForUser = bossService.getCurrentBossForUser(userId, profile.getlevel());
            double delta = currentBossForUser.getCoinRewardPercent() * (userEquipment.getEffect() / 100.0);
            currentBossForUser.setCoinRewardPercent(currentBossForUser.getCoinRewardPercent()+ delta);
            bossService.updateBoss(currentBossForUser);
        }
    }
   public void calculateGlovesEffect (Profile profile, int effect){
       String userId = profile.getuserUid();
       for (var userEquipmentDTO : getUserActivatedEquipment(userId)) {
           UserEquipment userEquipment = repository.getById(userEquipmentDTO.getUserEquipmentId());
           Equipment e = equipmentService.getEquipmentById(userEquipment.getEquipmentId());

           if (e.getequipmentType().equals(EquipmentTypes.CLOTHING)) {
               Clothing c = (Clothing) e;
               if (c.getType().equals(ClothingTypes.GLOVES)) {
                   userEquipment.setEffect(effect);
                   repository.updateUserEquipment(userEquipment);
               }
           }
       }
   }
   public void gainWeapon(Equipment equipment, String userid ){
       final boolean[] isEmptylist = {true};
        getAllbyUserId(userid).forEach(userEquipment -> {
            Equipment e = equipmentService.getEquipmentById(userEquipment.getEquipmentId());
            if(e.getequipmentType().equals(EquipmentTypes.WEAPON) && !userEquipment.getActivated()){
                userEquipment.setCoef(userEquipment.getCoef() + 0.02);
                repository.updateUserEquipment(userEquipment);
                isEmptylist[0] = false;
            }
        });
        if(isEmptylist[0]){
            save(userid, equipment);
        }
   }

    public boolean buyEquipment(Profile profile, Equipment equipment){
        double price = countPrice(profile, equipment);
        if(profile.getcoins() >= price) {
            save(profile.getuserUid(), equipment);
            int newCoinsValue = (int) Math.round(profile.getcoins() - price);
            profileService.updateCoins(profile.getuserUid(), newCoinsValue);
            return true;
        }
        return false;
    }

    public void gainEquipment(String userId, Equipment equipment){
        if(equipment!=null){
            save(userId, equipment);
        }
    }

    public List<Equipment> getByType(EquipmentTypes type) {
        return equipmentService.getEquipmentByType(type);
    }


    public double countPrice(Profile profile, Equipment equipment) {
        int bossReward = 200;

        if (profile != null && profile.getlevel() > 1) {
            var boss = bossService.getPreviousBossForUser(
                    profile.getuserUid(),
                    profile.getlevel() - 2
            );

            if (boss != null) {
                Integer reward = boss.getCoinsReward();
                bossReward = (reward != null) ? reward : 200;
            }
        }

        return equipment.getCoef() * bossReward;

    }

}
