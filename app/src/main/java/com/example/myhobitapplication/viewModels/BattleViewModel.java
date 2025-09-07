package com.example.myhobitapplication.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.dto.BossDTO;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.services.BattleService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.util.Optional;

public class BattleViewModel extends ViewModel {


    // Servisi za pristup podacima i logici
    private final TaskService taskService;
    private final BossService bossService;
    private final BattleService battleService;

    private double hitChance = 1.0;

    private String userUid;

    private final MutableLiveData<Integer> _bossCurrentHp = new MutableLiveData<>();
    private final MutableLiveData<Integer> _bossMaxHp = new MutableLiveData<>();
    private final MutableLiveData<Integer> _userPP = new MutableLiveData<>();
    private final MutableLiveData<Integer> _remainingAttacks = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _hitAnimationEvent = new MutableLiveData<>();

    private final MutableLiveData<Boolean> _attackMissedEvent = new MutableLiveData<>();

    public double getHitChance() {return hitChance;}
    public LiveData<Boolean> getAttackMissedEvent() { return _attackMissedEvent; }

    public LiveData<Integer> getBossCurrentHp() { return _bossCurrentHp; }
    public LiveData<Integer> getBossMaxHp() { return _bossMaxHp; }
    public LiveData<Integer> getUserPP() { return _userPP; }
    public LiveData<Integer> getRemainingAttacks() { return _remainingAttacks; }
    public LiveData<Boolean> getHitAnimationEvent() { return _hitAnimationEvent; }

    private final MutableLiveData<Boolean> _isBattleOver = new MutableLiveData<>(false);
    public LiveData<Boolean> isBattleOver() { return _isBattleOver; }

    private BossDTO currentBoss;

    public BattleViewModel(TaskRepository taskRepository, BossRepository bossRepository, ProfileService profileService){
        this.bossService = new BossService(bossRepository);
        this.taskService = new TaskService(taskRepository, profileService);
        this.battleService = new BattleService(taskService, bossService);
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void loadBattleState(int userId){

        currentBoss = bossService.getLowestLevelBossForUser(userId);

        //int userPower = battleService.calculateUserAttackPower(userId);

        int userPower = 80;

        _bossCurrentHp.setValue(currentBoss.getCurrentHP());
        _bossMaxHp.setValue(currentBoss.getHP());
        _userPP.setValue(userPower);

        //todo: moracu u bosu pamtiti koliko je ostalo pokusaja ubuduce
        _remainingAttacks.setValue(5);
        //todo: moracu koristiti od usera datume levela prethodnog i sadanjeg i moracu povezati taskove sa userom
        this.hitChance = battleService.calculateChanceForAttack(LocalDate.of(2025, 8, 1), LocalDate.of(2025, 9, 1),userUid);
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
            }


            battleService.updateBoss(currentBoss);

            _hitAnimationEvent.setValue(true);
        }
        else{
            _attackMissedEvent.setValue(true);
        }


        if (_remainingAttacks.getValue() <= 0 && currentBoss.getCurrentHP() > 0) {
            _isBattleOver.setValue(true);
        }


    }

    public void onAttackMissedEventHandled() {
        _attackMissedEvent.setValue(false);
    }
    public void onHitAnimationFinished() {
        _hitAnimationEvent.setValue(false);
    }




}
