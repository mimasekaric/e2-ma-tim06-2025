package com.example.myhobitapplication.viewModels;

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
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.models.Clothing;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.models.UserEquipment;
import com.example.myhobitapplication.services.BattleService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.services.UserEquipmentService;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BattleViewModel extends ViewModel {
    private final TaskService taskService;
    private final BossService bossService;
    private final BattleService battleService;
    private final ProfileService profileService;

    private final UserEquipmentService userEquipmentService;

    private double hitChance = 1.0;

    private String userUid;

    private Integer startAttackNumber;

    private final MutableLiveData<Integer> _bossCurrentHp = new MutableLiveData<>();
    private final MutableLiveData<Integer> _bossMaxHp = new MutableLiveData<>();
    private final MutableLiveData<Integer> _userPP = new MutableLiveData<>();
    private final MutableLiveData<Integer> _remainingAttacks = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _hitAnimationEvent = new MutableLiveData<>();

    private final MutableLiveData<Boolean> _attackMissedEvent = new MutableLiveData<>();
    private final MutableLiveData<Profile> _userProfile = new MutableLiveData<>();

    public double getHitChance() {return hitChance;}
    public int getStartAttackNumber() {return startAttackNumber;}
    public void setStartAttackNumber(int number) {startAttackNumber = number;}
    public LiveData<Boolean> getAttackMissedEvent() { return _attackMissedEvent; }

    public LiveData<Integer> getBossCurrentHp() { return _bossCurrentHp; }
    public LiveData<Integer> getBossMaxHp() { return _bossMaxHp; }
    public LiveData<Integer> getUserPP() { return _userPP; }
    public LiveData<Integer> getRemainingAttacks() { return _remainingAttacks; }
    public LiveData<Boolean> getHitAnimationEvent() { return _hitAnimationEvent; }

    private final MutableLiveData<Boolean> _isBattleOver = new MutableLiveData<>(false);
    public LiveData<Boolean> isBattleOver() { return _isBattleOver; }


    private Profile loadedProfile;
    private BossDTO currentBoss;

    public BattleViewModel(TaskRepository taskRepository, BossRepository bossRepository, ProfileService profileService, UserEquipmentService userEquipmentService){
        this.bossService = new BossService(bossRepository);
        this.profileService = profileService;
        this.taskService = new TaskService(taskRepository, profileService);
        this.battleService = new BattleService(taskService, bossService, profileService);
        this.userEquipmentService = userEquipmentService;
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public void loadBattleState(String userId){

        currentBoss = bossService.getLowestLevelBossForUser(userId);

        //int userPower = battleService.calculateUserAttackPower(userId);

        //int userPower = 80;

        _bossCurrentHp.setValue(currentBoss.getCurrentHP());
        _bossMaxHp.setValue(currentBoss.getHP());

        this.hitChance = 1.0;
        //todo: moracu u bosu pamtiti koliko je ostalo pokusaja ubuduce?
        int remainingAttacks=5;
        _remainingAttacks.setValue(remainingAttacks);
        setStartAttackNumber(remainingAttacks);
        //todo: moracu koristiti od usera datume levela prethodnog i sadanjeg i moracu povezati taskove sa userom - URADILA
        profileService.getProfileById(userUid).addOnSuccessListener(profile -> {
            _userPP.setValue(profile.getPp());
            userEquipmentService.activatedEquipmentEffect(profile);
            _userProfile.setValue(profile);
            List<UserEquipmentDTO> ueList= userEquipmentService.getUserActivatedEquipment(profile.getuserUid());
            for(UserEquipmentDTO u :  ueList){
                UserEquipment userEquipment = userEquipmentService.getById(u.getUserEquipmentId());
                if (u.getEquipment().getequipmentType().equals(EquipmentTypes.CLOTHING)){
                    if(((Clothing)u.getEquipment()).getType().equals(ClothingTypes.SHIELD)){
                        if(userEquipment.getEffect()==0) {
                            double effect =hitChance * ((Clothing) u.getEquipment()).getpowerPercentage() / 100;
                            hitChance =hitChance + effect;
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

       // this.hitChance = battleService.calculateChanceForAttack(profile);

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
                    userEquipmentService.delete(userEquipment);
                }
                if(((Clothing)u.getEquipment()).getType().equals(ClothingTypes.BOOTS)  && userEquipmentService.getById(u.getUserEquipmentId()).getEffect()==0){
                    hitChance = hitChance - userEquipment.getEffect();
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

        boolean isHitSuccessful = Math.random() <= this.hitChance;

        if(isHitSuccessful) {

            int newHp = currentHp - attackPower;
            if (newHp < 0) {
                newHp = 0;
            }
            _bossCurrentHp.setValue(newHp);

            currentBoss.setCurrentHP(newHp);

            boolean isBossDefeated = (newHp <= 0);

            if (isBossDefeated) {
                currentBoss.setDefeated(true);
                _isBattleOver.setValue(true);
                profileService.getProfileById(userUid).addOnSuccessListener(profile -> {
                    userEquipmentService.incrementFightsCounter(userUid,profile);
                });
                deleteEquipmentEffect();
                battleService.rewardUserWithCoins(currentBoss);
            }


            battleService.updateBoss(currentBoss);

            _hitAnimationEvent.setValue(true);
        }
        else{
            _attackMissedEvent.setValue(true);
        }


        if (_remainingAttacks.getValue() <= 0 && currentBoss.getCurrentHP() > 0) {
            _isBattleOver.setValue(true);
            profileService.getProfileById(userUid).addOnSuccessListener(profile -> {
                userEquipmentService.incrementFightsCounter(userUid,profile);
                });
            deleteEquipmentEffect();

        }


    }

    public void onAttackMissedEventHandled() {
        _attackMissedEvent.setValue(false);
    }
    public void onHitAnimationFinished() {
        _hitAnimationEvent.setValue(false);
    }




}
