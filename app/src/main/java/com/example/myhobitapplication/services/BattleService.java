package com.example.myhobitapplication.services;

import com.example.myhobitapplication.dto.BossDTO;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.models.Profile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class BattleService {


    private BossService bossService;

    private final ProfileService profileService;

    public BattleService(BossService bossService, ProfileService profileService){

        this.bossService = bossService;
        this.profileService = profileService;
    }

    public Boss getFirstUndefeatedBoss(int userId){

        return getFirstUndefeatedBoss(userId);
    }

    public long updateBoss(BossDTO boss){

        return bossService.updateBoss(boss);

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

    public boolean rewardUserWithHalfCoins(String userId, int halfCoins, int bossLevel){

        if(userId!=null) {

            if(bossLevel==0){
                profileService.incrementProfileFieldValue(userId,"coins",100);
                return true;
            }
            else{
                profileService.incrementProfileFieldValue(userId,"coins", halfCoins);
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
                int previousBossLevel = bossLevel-1;
                BossDTO previousBossDTO = bossService.getPrevioussBossForUser(userId,previousBossLevel);
                if (previousBossDTO != null) {
                    Integer previousCoinsReward = previousBossDTO.getCoinsReward();
                    double newCoinsReward = previousCoinsReward + previousCoinsReward * calculatePercentage(bossLevel, userId);
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
            int previousBossLevel = bossLevel-1;
            BossDTO previousBossDTO = bossService.getPrevioussBossForUser(userId,previousBossLevel);
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
            int previousBossLevel = bossLevel-1;
            BossDTO previousBossDTO = bossService.getPrevioussBossForUser(userId,previousBossLevel);
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

        double percentage = calculatePercentage(newLevel-1, userId);

        int coinsReward = calculateCoinsRewardForBoss(newLevel-1, userId);
        //todo: potencijalno promijeni na dobule coins i hp bossa :(
        int HP = calculateHPForBoss(newLevel-1,userId);

        bossDTO.setCoinRewardPercent(percentage);
        bossDTO.setCoinsReward(coinsReward);
        bossDTO.setHP(HP);
        bossDTO.setCurrentHP(HP);
       // bossDTO.setCoinRewardPercent(calculateCoinsRewardForBoss(newLevel-1,userId));
        return bossService.createBoss(bossDTO);
    }

    public int resetAttemptForUndefeatedBosses(String userId){
        return bossService.resetAttemptForUndefeatedBosses(userId);
    }

}
