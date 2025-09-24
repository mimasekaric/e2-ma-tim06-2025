package com.example.myhobitapplication.services;

import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.dto.BossDTO;
import com.example.myhobitapplication.models.Boss;
import java.util.Comparator;

public class BossService {


    public BossRepository bossRepository;

    public BossService(BossRepository bossRepository){

        this.bossRepository = bossRepository;
    }

    public BossDTO getLowestLevelBossForUser(String userId) {
        Boss boss = bossRepository.getAllUndefeatedBossesForUser(userId)
                .stream()
                .min(Comparator.comparingInt(Boss::getBossLevel))
                .orElse(null);

        BossDTO bossDTO = new BossDTO(boss);
        return  bossDTO;
    }

    public BossDTO getLastDefeatedBossForUser(String userId) {
        Boss boss = bossRepository.getAllDefeatedBossesForUser(userId)
                .stream()
                .max(Comparator.comparingInt(Boss::getBossLevel))
                .orElse(null);

        BossDTO bossDTO = new BossDTO(boss);
        return  bossDTO;
    }

    public Boss getPreviousBossForUser(String userId, int userLevel){
        return bossRepository.getPreviousBossForUser(userId,userLevel-2);
    }
    public long updateBoss(BossDTO bossDTO){

        Boss boss = new Boss(

                bossDTO.getId(),
                bossDTO.getHP(),
                bossDTO.getUserId(),
                bossDTO.getCurrentHP(),
                bossDTO.getDefeated(),
                bossDTO.getBossLevel(),
                bossDTO.getCoinsReward(),
                bossDTO.getCoinRewardPercent(),
                bossDTO.isAttemptedThisLevel()
        );


        return bossRepository.updateBoss(boss);

    }

    public long createBoss(BossDTO bossDTO){
        Boss boss = new Boss(
                bossDTO.getId(),
                bossDTO.getHP(),
                bossDTO.getUserId(),
                bossDTO.getCurrentHP(),
                bossDTO.getDefeated(),
                bossDTO.getBossLevel(),
                bossDTO.getCoinsReward(),
                bossDTO.getCoinRewardPercent(),
                bossDTO.isAttemptedThisLevel()
        );
        return bossRepository.insertBoss(boss);
    }


    public BossDTO getPrevioussBossForUser(String userId, int previousBossLevel){
        Boss boss = bossRepository.getPreviousBossForUser(userId, previousBossLevel);
        return new BossDTO(boss);
    }

    public BossDTO getCurrentBossForUser(String userId, int currentBossLevel){
        Boss boss = bossRepository.getPreviousBossForUser(userId, currentBossLevel);
        return new BossDTO(boss);
    }

    public int resetAttemptForUndefeatedBosses(String userId){
       return bossRepository.resetAttemptForUndefeatedBosses(userId);
    }

}
