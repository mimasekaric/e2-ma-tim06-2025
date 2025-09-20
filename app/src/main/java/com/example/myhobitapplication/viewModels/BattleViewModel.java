package com.example.myhobitapplication.viewModels;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.ProfileRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.dto.BossDTO;
import com.example.myhobitapplication.dto.UserEquipmentDTO;
import com.example.myhobitapplication.enums.ClothingTypes;
import com.example.myhobitapplication.enums.EquipmentTypes;
import com.example.myhobitapplication.enums.WeaponTypes;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.models.Clothing;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.models.UserEquipment;
import com.example.myhobitapplication.models.Weapon;
import com.example.myhobitapplication.services.BattleService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.services.UserEquipmentService;
import com.example.myhobitapplication.staticData.ClothingList;
import com.example.myhobitapplication.staticData.WeaponList;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class BattleViewModel extends ViewModel {
    private final TaskService taskService;
    private final BossService bossService;
    private final BattleService battleService;
    private final ProfileService profileService;

    private final UserEquipmentService userEquipmentService;

    private double hitChance = 1.0;

    private String userUid;

    private final MutableLiveData<Integer> _bossCurrentHp = new MutableLiveData<>();
    private final MutableLiveData<Integer> _bossMaxHp = new MutableLiveData<>();
    private final MutableLiveData<Integer> _userPP = new MutableLiveData<>();
    private final MutableLiveData<Integer> _remainingAttacks = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _hitAnimationEvent = new MutableLiveData<>();
    private final MutableLiveData<Integer> _coins = new MutableLiveData<>(0);

    private final MutableLiveData<Boolean> _attackMissedEvent = new MutableLiveData<>();
    private final MutableLiveData<Profile> _userProfile = new MutableLiveData<>();
    private final MutableLiveData<Equipment> _equipment = new MutableLiveData<>();
    private final MutableLiveData<Integer> _rewardEquipmentImage = new MutableLiveData<>(0);
    public MutableLiveData<Integer> getImageResource() { return _rewardEquipmentImage;}
    public void setImageResource(Integer src) { _rewardEquipmentImage.setValue(src);}


    private final MutableLiveData<String> _equipmentName = new MutableLiveData<>();
    public MutableLiveData<String> getEquipmentName() { return _equipmentName;}
    public void setEquipmentName(String name) { _equipmentName.setValue(name);}



    public MutableLiveData<Equipment> getEquipment() { return _equipment;}
    public void setEquipment(Equipment equipment) { _equipment.setValue(equipment);}
    public double getHitChance() {
        return hitChance;
    }
    public MutableLiveData<Integer> getCoins() { return _coins;}
    public void setCoins(Integer coins) { _coins.setValue(coins);}

    public LiveData<Boolean> getAttackMissedEvent() {
        return _attackMissedEvent;
    }

    public LiveData<Integer> getBossCurrentHp() {
        return _bossCurrentHp;
    }

    public LiveData<Integer> getBossMaxHp() {
        return _bossMaxHp;
    }

    public LiveData<Integer> getUserPP() {
        return _userPP;
    }

    public LiveData<Integer> getRemainingAttacks() {
        return _remainingAttacks;
    }

    public LiveData<Boolean> getHitAnimationEvent() {
        return _hitAnimationEvent;
    }

    private final MutableLiveData<Boolean> _isBattleOver = new MutableLiveData<>(false);

    public LiveData<Boolean> isBattleOver() {
        return _isBattleOver;
    }


    private BossDTO currentBoss;

    public BattleViewModel(TaskRepository taskRepository, BossRepository bossRepository, ProfileService profileService, UserEquipmentService userEquipmentService) {
        this.bossService = new BossService(bossRepository);
        this.profileService = profileService;
        this.taskService = new TaskService(taskRepository, profileService);
        this.battleService = new BattleService(taskService, bossService, profileService);
        this.userEquipmentService = userEquipmentService;
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void loadBattleState(String userId) {

        currentBoss = bossService.getLowestLevelBossForUser(userId);


        //int userPower = battleService.calculateUserAttackPower(userId);

        int userPower = 80;

        _bossCurrentHp.setValue(currentBoss.getCurrentHP());
        _bossMaxHp.setValue(currentBoss.getHP());
        _userPP.setValue(userPower);
        this.hitChance = 1.0;
        // this.hitChance = battleService.calculateChanceForAttack(profile);
        //todo: moracu u bosu pamtiti koliko je ostalo pokusaja ubuduce?
        _remainingAttacks.setValue(5);
        //todo: moracu koristiti od usera datume levela prethodnog i sadanjeg i moracu povezati taskove sa userom - URADILA
        profileService.getProfileById(userUid).addOnSuccessListener(profile -> {
            userEquipmentService.activatedEquipmentEffect(profile);
            _userProfile.setValue(profile);
            List<UserEquipmentDTO> ueList = userEquipmentService.getUserActivatedEquipment(profile.getuserUid());
            for (UserEquipmentDTO u : ueList) {
                if (u.getEquipment().getequipmentType().equals(EquipmentTypes.CLOTHING)) {
                    if (((Clothing) u.getEquipment()).getType().equals(ClothingTypes.SHIELD)) {
                        hitChance = hitChance + (hitChance * ((Clothing) u.getEquipment()).getpowerPercentage() / 100);
                    }
                    if (((Clothing) u.getEquipment()).getType().equals(ClothingTypes.BOOTS)) {
                        _remainingAttacks.setValue(_remainingAttacks.getValue() + 1);
                    }
                }
            }


        }).addOnFailureListener(e -> {
        });
    }

    public void performAttack() {


        Integer attacksLeft = _remainingAttacks.getValue();
        Integer currentHp = _bossCurrentHp.getValue();
        Integer attackPower = _userPP.getValue();

        if (attacksLeft == null || attacksLeft <= 0 || currentHp == null || attackPower == null) {
            return;
        }

        _remainingAttacks.setValue(attacksLeft - 1);

        boolean isHitSuccessful = Math.random() <= this.hitChance;

        if (isHitSuccessful) {

            int newHp = currentHp - attackPower;
            if (newHp < 0) {
                newHp = 0;
            }
            _bossCurrentHp.setValue(newHp);

            currentBoss.setCurrentHP(newHp);

            boolean isBossDefeated = (newHp <= 0);

            if (isBossDefeated) {
                currentBoss.setDefeated(true);
                setCoins(currentBoss.getCoinsReward());
                _isBattleOver.setValue(true);
                userEquipmentService.incrementFightsCounter(userUid, _userProfile.getValue());
                battleService.rewardUserWithCoins(currentBoss);
                Equipment equipment = rewardUserWithEquipment();
                setEquipmentDetails(equipment);
            }


            battleService.updateBoss(currentBoss);

            _hitAnimationEvent.setValue(true);
        } else {
            _attackMissedEvent.setValue(true);
        }


        if (_remainingAttacks.getValue() <= 0 && currentBoss.getCurrentHP() > 0) {
            _isBattleOver.setValue(true);
            //userEquipmentService.incrementFightsCounter();
        }


    }
    public void setEquipmentDetails(Equipment equipment){

        if(equipment instanceof Weapon){
            Weapon weapon = (Weapon) equipment;
            WeaponTypes weaponTypes = weapon.getType();
            List<Weapon> weaponList = WeaponList.getWeaponList();

            for(Weapon w:weaponList){

                if (w.getType() == weaponTypes) {

                    setImageResource(w.getImage());
                    setEquipmentName(String.valueOf(w.getType()));
                    break;
                }

            }
        }

        if(equipment instanceof Clothing){
            Clothing clothing = (Clothing) equipment;
            ClothingTypes clothingTypes = clothing.getType();
            List<Clothing> clothingList = ClothingList.getClothingList();

            for(Clothing c:clothingList){

                if (c.getType() == clothingTypes) {

                    setImageResource(c.getImage());
                    setEquipmentName(String.valueOf(c.getType()));
                    break;
                }

            }
        }
    }

    public Equipment rewardUserWithEquipment() {

        double randomChanceToGainEquipment = Math.random();
        double randomChanceEquipment = Math.random();
        double randomChanceEquipmentType = Math.random();
        double chanceToGainEquipment = 0.2;
        double weaponChance = 0.05;
        ClothingTypes clothingTypes;
        WeaponTypes weaponTypes;
        //randomChanceToGainEquipment <= chanceToGainEquipment

        if (true) {
            if (randomChanceEquipment <= weaponChance) {
                if (randomChanceEquipmentType < 0.333) {
                    clothingTypes = ClothingTypes.GLOVES;
                } else if (randomChanceEquipmentType < 0.666) {
                    clothingTypes = ClothingTypes.SHIELD;
                } else {
                    clothingTypes = ClothingTypes.BOOTS;
                }

                List<Equipment> clothes = userEquipmentService.getByType(EquipmentTypes.CLOTHING);

                Optional<Clothing> foundBoots = clothes.stream()
                        .filter(item -> item instanceof Clothing)
                        .map(item -> (Clothing) item)
                        .filter(clothing -> clothing.getType() == clothingTypes)
                        .findFirst();

                if (foundBoots.isPresent()) {

                    Clothing clothing = foundBoots.get();
                    userEquipmentService.gainEquipment(userUid,clothing);
                    return clothing;

                } else {
                   return null;
                }

            }
            else {
                if (randomChanceEquipmentType < 0.5) {
                    weaponTypes = WeaponTypes.ANDURIL_OF_ARAGORN;
                } else {
                    weaponTypes = WeaponTypes.BOW_AND_ARROW_OF_LEGOLAS;
                }
                List<Equipment> weapons = userEquipmentService.getByType(EquipmentTypes.WEAPON);

                Optional<Weapon> foundWeapon = weapons.stream()
                        .filter(item -> item instanceof Weapon)
                        .map(item -> (Weapon) item)
                        .filter(clothing -> clothing.getType() == weaponTypes)
                        .findFirst();

                if (foundWeapon.isPresent()) {

                    Weapon weapon = foundWeapon.get();
                    userEquipmentService.gainEquipment(userUid,weapon);
                    return weapon;

                } else {
                    return null;
                }

            }

        }

        return null;
    }



    public void onAttackMissedEventHandled() {
        _attackMissedEvent.setValue(false);
    }
    public void onHitAnimationFinished() {
        _hitAnimationEvent.setValue(false);
    }




}
