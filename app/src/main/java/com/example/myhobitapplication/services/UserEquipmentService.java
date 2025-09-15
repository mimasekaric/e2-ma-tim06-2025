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
/// /AKo je vec jednom aktiviran clothes, napravi da se ne primjenjuje efekat tokom druge borbe i koristi servise ovde a ne repo
public class UserEquipmentService {

   // private final BossService BossService;
    private final BossService bossService;
    private final UserEquipmentRepository repository;

    //private final EquipmentService EquipmentService;
    private final EquipmentService equipmentService;
    //private final ProfileService ProfileService;
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


    public List<Equipment> getUserEquipment(String userId){
        List<UserEquipment> all = getAllbyUserId(userId);
        List<Equipment> equipment = new ArrayList<>();
        for(UserEquipment ue : all){
            equipment.add(equipmentService.getEquipmentById(ue.getEquipmentId()));
        }
        return equipment;
    }

    public void incrementFightsCounter(String userid, Profile profile){
        Map<ClothingTypes, Double> clothingEffects = new HashMap<>();
            getUserActivatedEquipment(userid).forEach(ueDTO->{
                UserEquipment userEquipment = getById(ueDTO.getUserEquipmentId());
                userEquipment.setFightsCounter(userEquipment.getFightsCounter() + 1);
                if(ueDTO.getEquipment().getequipmentType().equals(EquipmentTypes.POTION)){
                    if(!((Potion)ueDTO.getEquipment()).isPermanent()){
                        double powerdelta = profile.getPp() * (((Potion)ueDTO.getEquipment()).getpowerPercentage()/ 100.0);
                        int newpp = profile.getPp() - (int) Math.round(powerdelta);
                        profileService.updatePp(profile.getuserUid(), newpp);
                        repository.delete(userEquipment);
                    }
                }else if(ueDTO.getEquipment().getequipmentType().equals(EquipmentTypes.CLOTHING) && userEquipment.getFightsCounter() > 1){
                    Clothing c = (Clothing) ueDTO.getEquipment();
                    clothingEffects.merge(c.getType(), ueDTO.getEquipment().getpowerPercentage(), Double::sum);
                    repository.delete(userEquipment);
                }else{
                    repository.updateUserEquipment(userEquipment);
                }

        });
        clothingEffects.entrySet().forEach(clothingTypesDoubleEntry -> {
            ClothingTypes type = clothingTypesDoubleEntry.getKey();
            if (type.equals(ClothingTypes.GLOVES)) {
                double powerdelta = profile.getPp() * (clothingTypesDoubleEntry.getValue()/ 100.0);
                int newpp = profile.getPp() - (int) Math.round(powerdelta);
                profileService.updatePp(profile.getuserUid(), newpp);
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
        return repository.insertUserEquipment(ue);
    }
    public List<UserEquipment> getAllbyUserId(String id){ return repository.getAllByUserId(id);}

    public UserEquipment getById(Integer id){ return repository.getById(id);}


    //za sad obradjeno samo za aktivaciju napitaka i rukavica!!
    //efekat cizama i stita je hendlovan u battleviewmodel
   public boolean activateEquipment(UserEquipmentDTO userEquipDTO,Profile profile )
   {
       Equipment equipment  = userEquipDTO.getEquipment();
       UserEquipment userEquipment =  repository.getById(userEquipDTO.getUserEquipmentId());
       userEquipment.setActivated(true);
       repository.updateUserEquipment(userEquipment);
        return true;
   }

    public void activatedEquipmentEffect(Profile profile){
        String userId = profile.getuserUid();
       Map<ClothingTypes, Double> clothingEffects = new HashMap<>();
        getUserActivatedEquipment(userId).forEach(userEquipmentDTO -> {
            Equipment equipment  = userEquipmentDTO.getEquipment();
            UserEquipment userEquipment =  repository.getById(userEquipmentDTO.getUserEquipmentId());
            Equipment e = equipmentService.getEquipmentById(userEquipment.getEquipmentId());
            if(e.getequipmentType().equals(EquipmentTypes.POTION) ) {
                double powerdelta = profile.getPp() * (e.getpowerPercentage() / 100.0);
                int newpp = profile.getPp() + (int) Math.round(powerdelta);
                profileService.updatePp(profile.getuserUid(), newpp);
            }else if (e.getequipmentType().equals(EquipmentTypes.CLOTHING) ) {
                Clothing c = (Clothing) e;
                if(c.getType().equals(ClothingTypes.GLOVES)) {
                    clothingEffects.merge(c.getType(), e.getpowerPercentage(), Double::sum);
                }
            }else if(e.getequipmentType().equals(EquipmentTypes.WEAPON)){
                Weapon w = (Weapon) e;
                if(w.getType().equals(WeaponTypes.ANDURIL_OF_ARAGORN)){
                    double powerdelta = profile.getPp() * (e.getpowerPercentage() / 100.0);
                    int newpp = profile.getPp() + (int) Math.round(powerdelta);
                    profileService.updatePp(profile.getuserUid(), newpp);
                }else{
                    BossDTO currentBossForUser = bossService.getCurrentBossForUser(userId, profile.getlevel());
                    double delta= currentBossForUser.getCoinRewardPercent()* (e.getpowerPercentage() / 100.0);
                    currentBossForUser.setCoinRewardPercent(currentBossForUser.getCoinRewardPercent()+ delta);
                    bossService.updateBoss(currentBossForUser);
                }
            }
        });
        clothingEffects.entrySet().forEach(clothingTypesDoubleEntry -> {
            ClothingTypes type = clothingTypesDoubleEntry.getKey();
            if (type.equals(ClothingTypes.GLOVES)) {
                double powerdelta = profile.getPp() * (clothingTypesDoubleEntry.getValue()  / 100.0);
                int newpp = profile.getPp() + (int) Math.round(powerdelta);
                profileService.updatePp(profile.getuserUid(), newpp);
            }
        });
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

   public List<UserEquipment> getAllSameTypeActivatedClothing(Profile profile, ClothingTypes type){
       List<UserEquipment> eqlist=  repository.getAllByUserId(profile.getuserUid());
       List<UserEquipment> eqlistActivated = new ArrayList<>();
       for (UserEquipment eq : eqlist){
            EquipmentTypes etype = equipmentService.getEquipmentById(eq.getEquipmentId()).getequipmentType();
            if(etype.equals(EquipmentTypes.CLOTHING)) {
                if (eq.getActivated() && ((Clothing) (equipmentService.getEquipmentById(eq.getEquipmentId()))).getType().equals(type)) {
                    eqlistActivated.add(eq);
                }
            }
       }
        return  eqlistActivated;
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
