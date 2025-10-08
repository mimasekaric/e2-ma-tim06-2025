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
import com.example.myhobitapplication.events.GameEvent;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.models.Clothing;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.models.UserEquipment;
import com.example.myhobitapplication.models.Weapon;
import com.example.myhobitapplication.services.AllianceMissionService;
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


    public enum BattleScreenState {
        LOADING,
        BOSS_FOUND,
        NO_BOSS_FOUND
    }
    private final TaskService taskService;
    private final BossService bossService;
    private final BattleService battleService;
    private final ProfileService profileService;
    private final AllianceMissionService missionService;
    private final UserEquipmentService userEquipmentService;

    private double hitChance = 1.0;

    private String userUid;

    private Integer startAttackNumber;

    private final MutableLiveData<Integer> _bossCurrentHp = new MutableLiveData<>();
    private final MutableLiveData<Integer> _bossMaxHp = new MutableLiveData<>();
    private final MutableLiveData<Integer> _userPP = new MutableLiveData<>();
    private final MutableLiveData<Integer> _remainingAttacks = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _hitAnimationEvent = new MutableLiveData<>();
    MutableLiveData<List<UserEquipmentDTO>> activatedEquipmentList= new MutableLiveData<List<UserEquipmentDTO>>();
    private final MutableLiveData<Integer> _coins = new MutableLiveData<>(0);

    private final MutableLiveData<Boolean> _attackMissedEvent = new MutableLiveData<>();
    private final MutableLiveData<Profile> _userProfile = new MutableLiveData<>();
    private final MutableLiveData<Equipment> _equipment = new MutableLiveData<>();
    private final MutableLiveData<Integer> _rewardEquipmentImage = new MutableLiveData<>(0);
    public MutableLiveData<Integer> getImageResource() { return _rewardEquipmentImage;}
    public void setImageResource(Integer src) { _rewardEquipmentImage.setValue(src);}
    public MutableLiveData<List<UserEquipmentDTO>> getActivatedEquipment() { return activatedEquipmentList;}
    public void setActivatedEquipment(List<UserEquipmentDTO> activatedEquipmentsdata) { activatedEquipmentList.setValue(activatedEquipmentsdata);}
    public int getStartAttackNumber() {return startAttackNumber;}
    public void setStartAttackNumber(int number) {startAttackNumber = number;}
    public LiveData<Boolean> getAttackMissedEvent() { return _attackMissedEvent; }

    private final MutableLiveData<String> _equipmentName = new MutableLiveData<>();
    public MutableLiveData<String> getEquipmentName() { return _equipmentName;}
    public void setEquipmentName(String name) { _equipmentName.setValue(name);}
    public boolean attemptedThisLevel = false;
    public boolean getAttemptedThisLevel() {return attemptedThisLevel;}
    public void setAttemptedThisLevel(boolean flag) { attemptedThisLevel = flag;}

    private final MutableLiveData<BattleScreenState> _screenState = new MutableLiveData<>(BattleScreenState.LOADING);

    public LiveData<BattleScreenState> getScreenState() {return _screenState;}

    public MutableLiveData<Equipment> getEquipment() { return _equipment;}
    public void setEquipment(Equipment equipment) { _equipment.setValue(equipment);}
    public MutableLiveData<Integer> getCoins() { return _coins;}
    public void setCoins(Integer coins) { _coins.setValue(coins);}


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
    private final MutableLiveData<Integer> _potentialCoinReward = new MutableLiveData<>();
    public LiveData<Integer> getPotentialCoinReward() {
        return _potentialCoinReward;
    }
    private final MutableLiveData<Double> _hitChanceLiveData = new MutableLiveData<>(1.0);
    public LiveData<Double> getHitChanceLiveData() {
        return _hitChanceLiveData;
    }
    public double getHitChance() {
        return _hitChanceLiveData.getValue();
    }

    private Profile loadedProfile;
    private BossDTO currentBoss;

    public BattleViewModel(TaskRepository taskRepository, BossRepository bossRepository, ProfileService profileService, UserEquipmentService userEquipmentService) {
        this.bossService = new BossService(bossRepository);
        this.profileService = profileService;
        this.battleService = new BattleService(bossService, profileService);
        this.missionService = new AllianceMissionService(profileService);
        this.taskService = TaskService.getInstance(taskRepository, profileService, battleService, missionService);
        this.userEquipmentService = userEquipmentService;
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public void loadBattleState(String userId){


        currentBoss = bossService.getLowestLevelBossForUser(userId);
        if(currentBoss==null){
            _screenState.setValue(BattleScreenState.NO_BOSS_FOUND);
            return;
        }
        if(currentBoss.isAttemptedThisLevel()){
            currentBoss = null;
            _screenState.setValue(BattleScreenState.NO_BOSS_FOUND);
            return;
        }

        _screenState.setValue(BattleScreenState.BOSS_FOUND);

        //int userPower = battleService.calculateUserAttackPower(userId);

        //int userPower = 80;

        _bossCurrentHp.setValue(currentBoss.getHP());
        _bossMaxHp.setValue(currentBoss.getHP());
        _potentialCoinReward.setValue(currentBoss.getCoinsReward());

        //this.hitChance = 1.0;



        int remainingAttacks=5;
        _remainingAttacks.setValue(remainingAttacks);
        setStartAttackNumber(remainingAttacks);
        //todo: moracu koristiti od usera datume levela prethodnog i sadanjeg i moracu povezati taskove sa userom - URADILA
        profileService.getProfileById(userUid).addOnSuccessListener(profile -> {
            _userPP.setValue(profile.getPp());
            userEquipmentService.activatedEquipmentEffect(profile);
            _userProfile.setValue(profile);

            List<UserEquipmentDTO> ueList= userEquipmentService.getUserActivatedEquipment(profile.getuserUid());
            setActivatedEquipment(ueList);
            for(UserEquipmentDTO u :  ueList){
                UserEquipment userEquipment = userEquipmentService.getById(u.getUserEquipmentId());
                if (u.getEquipment().getequipmentType().equals(EquipmentTypes.CLOTHING)){
                    if(((Clothing)u.getEquipment()).getType().equals(ClothingTypes.SHIELD)){
                        if(userEquipment.getEffect()==0) {
                            double effect =hitChance * ((Clothing) u.getEquipment()).getpowerPercentage() / 100;
                            hitChance =hitChance + effect;
                            _hitChanceLiveData.setValue(this.hitChance);
                            userEquipment.setEffect(effect);
                            userEquipmentService.updateUserEquipment(userEquipment);
                        }else if(userEquipment.getFightsCounter() >1){
                            hitChance = hitChance - userEquipment.getEffect();
                            userEquipmentService.delete(userEquipment);
                        }
                    }
                    if(((Clothing)u.getEquipment()).getType().equals(ClothingTypes.BOOTS)){
                        if(userEquipmentService.getById(u.getUserEquipmentId()).getEffect()==0 || userEquipment.getFightsCounter() <2){
                            setStartAttackNumber(_remainingAttacks.getValue()+1);
                            _remainingAttacks.setValue(_remainingAttacks.getValue() + 1);
                            userEquipment.setEffect(1);
                            userEquipmentService.updateUserEquipment(userEquipment);
                        }else if(userEquipment.getFightsCounter() >1){
                            hitChance = hitChance - userEquipment.getEffect();
                            userEquipmentService.delete(userEquipment);
                        }else if(userEquipment.getFightsCounter() <2){
                            setStartAttackNumber(_remainingAttacks.getValue()+1);
                            _remainingAttacks.setValue(_remainingAttacks.getValue() + 1);
                            userEquipment.setEffect(userEquipment.getEffect()+1);
                            userEquipmentService.updateUserEquipment(userEquipment);
                        }
                    }
                }
            }

            _bossCurrentHp.postValue(_bossCurrentHp.getValue());
            _bossMaxHp.postValue(_bossMaxHp.getValue());
            _userPP.postValue(_userPP.getValue());
            _remainingAttacks.postValue(_remainingAttacks.getValue());
            _potentialCoinReward.postValue( _potentialCoinReward.getValue());
            //this.hitChance = 1;
            this.hitChance = taskService.calculateChanceForAttack(profile);
            _hitChanceLiveData.setValue(this.hitChance);

        }).addOnFailureListener(e -> {
        });
    }

    public void deleteEquipmentEffect(){
        List<UserEquipmentDTO> ueList= userEquipmentService.getUserActivatedEquipment(userUid);
        for(UserEquipmentDTO u :  ueList){
            UserEquipment userEquipment = userEquipmentService.getById(u.getUserEquipmentId());
            if (u.getEquipment().getequipmentType().equals(EquipmentTypes.CLOTHING)){
                if(((Clothing)u.getEquipment()).getType().equals(ClothingTypes.SHIELD) && userEquipmentService.getById(u.getUserEquipmentId()).getFightsCounter()>0){
                    hitChance = hitChance - userEquipment.getEffect();
                    _hitChanceLiveData.setValue(this.hitChance);
                    userEquipmentService.delete(userEquipment);
                }
                if(((Clothing)u.getEquipment()).getType().equals(ClothingTypes.BOOTS)  && userEquipmentService.getById(u.getUserEquipmentId()).getEffect()==0){
                    hitChance = hitChance - userEquipment.getEffect();
                    _hitChanceLiveData.setValue(this.hitChance);
                    userEquipmentService.delete(userEquipment);
                }
            }
        }
    }
    public void performAttack() {


        Integer attacksLeft = _remainingAttacks.getValue();
        Integer currentHp = _bossCurrentHp.getValue();
        Integer attackPower = _userPP.getValue();

        if (attacksLeft == null || attacksLeft <= 0 || currentHp == null || attackPower == null) {
            return;
        }

        _remainingAttacks.setValue(attacksLeft - 1);
        currentBoss.setAttemptedThisLevel(true);

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
                setAttemptedThisLevel(true);
                currentBoss.setAttemptedThisLevel(true);
                profileService.getProfileById(userUid).addOnSuccessListener(profile -> {
                    userEquipmentService.incrementFightsCounter(userUid,profile);
                });
                deleteEquipmentEffect();
                battleService.rewardUserWithCoins(currentBoss);
                Equipment equipment = rewardUserWithEquipment(1.0);
                setEquipmentDetails(equipment);
            }


            battleService.updateBoss(currentBoss);
            missionService.handleGameEvent(new GameEvent(AllianceMissionService.MissionEventType.SUCCESSFUL_BOSS_HIT,userUid));

            _hitAnimationEvent.setValue(true);
        } else {

            _attackMissedEvent.setValue(true);
            battleService.updateBoss(currentBoss);
        }


        if (_remainingAttacks.getValue() <= 0 && currentBoss.getCurrentHP() > 0) {
            _isBattleOver.setValue(true);
            setAttemptedThisLevel(true);
            currentBoss.setAttemptedThisLevel(true);
            battleService.updateBoss(currentBoss);


            double halfMaxHp = currentBoss.getHP() / 2.0;

            if (currentBoss.getCurrentHP() <= halfMaxHp) {

                int halfCoinsReward = currentBoss.getCoinsReward() / 2;
                setCoins(halfCoinsReward);
                battleService.rewardUserWithHalfCoins(currentBoss.getUserId(), halfCoinsReward, currentBoss.getBossLevel());

                Equipment equipment = rewardUserWithEquipment(0.5);
                setEquipmentDetails(equipment);

            } else {
                setCoins(0);
            }
            profileService.getProfileById(userUid).addOnSuccessListener(profile -> {
                userEquipmentService.incrementFightsCounter(userUid,profile);
            });
            deleteEquipmentEffect();
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

    public Equipment rewardUserWithEquipment(double modificator) {

        double randomChanceToGainEquipment = Math.random();
        double randomChanceEquipment = Math.random();
        double randomChanceEquipmentType = Math.random();
        double chanceToGainEquipment = 1; //0.2;
        double weaponChance = 0.05;
        ClothingTypes clothingTypes;
        WeaponTypes weaponTypes;
        //randomChanceToGainEquipment <= chanceToGainEquipment
        modificator = 1;
        if (randomChanceToGainEquipment <= chanceToGainEquipment*modificator) {
            if(randomChanceEquipment <= weaponChance) {
                if (randomChanceEquipmentType < 0.5) {
                    weaponTypes = WeaponTypes.BOW_AND_ARROW_OF_LEGOLAS;
                } else {
                    weaponTypes = WeaponTypes.ANDURIL_OF_ARAGORN;
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

            else {
                if (randomChanceEquipmentType < 0.333) {
                    clothingTypes = ClothingTypes.BOOTS;
                } else if (randomChanceEquipmentType < 0.666) {
                    clothingTypes = ClothingTypes.GLOVES;
                } else {
                    clothingTypes = ClothingTypes.SHIELD;
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
