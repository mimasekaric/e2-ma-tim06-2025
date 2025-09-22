package com.example.myhobitapplication.services;

import com.example.myhobitapplication.dto.BossDTO;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.models.Profile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class BattleService {

    private TaskService taskService;
    private BossService bossService;

    private final ProfileService profileService;

    public BattleService(TaskService taskService, BossService bossService, ProfileService profileService){

        this.bossService = bossService;
        this.taskService = taskService;
        this.profileService = profileService;
    }

    public Boss getFirstUndefeatedBoss(int userId){

        return getFirstUndefeatedBoss(userId);
    }

    public long updateBoss(BossDTO boss){

        return bossService.updateBoss(boss);

    }

    public Double calculateChanceForAttack(Profile profile){

        if (profile == null || profile.getCurrentLevelDate() == null) {
            return 0.0;
        }

        Date startDate = profile.getPreviousLevelDate();
        Date endDate = profile.getCurrentLevelDate();

        LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localEndaDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Integer completedTasks = taskService.countFinishedTasksForDateRange(localStartDate,localEndaDate, profile.getuserUid());
        Integer createdTasks = taskService.countCreatedTasksForDateRange(localStartDate,localEndaDate, profile.getuserUid());

        if (createdTasks == null || createdTasks == 0) {
            return 0.0;
        }

        return (double)completedTasks /createdTasks;
    }

    public boolean rewardUserWithCoins(BossDTO bossDTO){

        if(bossDTO!=null) {

            if(bossDTO.getBossLevel()==0){
                profileService.incrementProfileFieldValue(bossDTO.getUserId(),"coins",200);
                return true;
            }
            else{
                    profileService.incrementProfileFieldValue(bossDTO.getUserId(),"coins",bossDTO.getCoinsReward());
                    return true;
            }

        }
        return false;

    }

    public int calculateCoinsRewardForBoss(int bossLevel, String userId){

            if(bossLevel == 0){
                return 200;
            }
            else {
                BossDTO previousBossDTO = bossService.getPrevioussBossForUser(userId,bossLevel-1);
                if (previousBossDTO != null) {
                    Integer previousCoinsReward = previousBossDTO.getCoinsReward();
                    double newCoinsReward = previousCoinsReward * calculatePercentage(bossLevel, userId);
                    return (int) newCoinsReward;
                } else {
                    return 0;
                }
            }
    }

    public int calculateHPForBoss(int bossLevel, String userId){

        if(bossLevel==0){
            return 200;
        }
        else {
            BossDTO previousBossDTO = bossService.getPrevioussBossForUser(userId,bossLevel-1);
            if (previousBossDTO != null) {
                Integer previousHP = previousBossDTO.getHP();
                double newHP = previousHP * 2 + previousHP/2;
                return (int) newHP;
            } else {
                return 0;
            }
        }
    }

    public double calculatePercentage(int bossLevel, String userId){

        if(bossLevel==0){
            return 0.2;
        }
        else {
            BossDTO previousBossDTO = bossService.getPrevioussBossForUser(userId,bossLevel-1);
            if (previousBossDTO != null) {
                double previousPercentage = previousBossDTO.getCoinRewardPercent();
                return previousPercentage;
            } else {
                return 0.2;
            }
        }
    }
    public long generateBossForUser(String userId, int newLevel){

        BossDTO bossDTO = new BossDTO();
        bossDTO.setUserId(userId);
        bossDTO.setBossLevel(newLevel-1);
        bossDTO.setDefeated(false);

        double percentage = calculatePercentage(newLevel, userId);

        int coinsReward = calculateCoinsRewardForBoss(newLevel-1, userId);
        int HP = calculateHPForBoss(newLevel-1,userId);

        bossDTO.setCoinRewardPercent(percentage);
        bossDTO.setCoinsReward(coinsReward);
        bossDTO.setHP(HP);
        bossDTO.setCurrentHP(HP);
        bossDTO.setCoinRewardPercent(calculateCoinsRewardForBoss(newLevel-1,userId));
        return bossService.createBoss(bossDTO);
    }
}
