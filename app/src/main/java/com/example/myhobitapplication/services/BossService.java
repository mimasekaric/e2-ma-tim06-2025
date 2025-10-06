package com.example.myhobitapplication.services;

import android.util.Log;

import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.dto.BossDTO;
import com.example.myhobitapplication.models.Boss;
import java.util.Comparator;

public class BossService {


    public BossRepository bossRepository;

    public BossService(BossRepository bossRepository){

        this.bossRepository = bossRepository;
    }

    /// TODO: Mima baci oko
    public BossDTO getLowestLevelBossForUser(String userId) {
        Boss boss = bossRepository.getAllUndefeatedBossesForUser(userId)
                .stream()
                .min(Comparator.comparingInt(Boss::getBossLevel))
                .orElse(null);
//        if(boss==null){
//            BossDTO bossDTO = new BossDTO(new Boss(13,10,userId,10,false,1,200,0.2,false));
//            return bossDTO;
//        }

//        BossDTO bossDTO = new BossDTO();
//        if(boss!=null){
//            bossDTO = new BossDTO(boss);
//        }

       if(boss == null){
           return null;
       }
       return new BossDTO(boss);
    }

    public BossDTO getLastDefeatedBossForUser(String userId) {
        Boss boss = bossRepository.getAllDefeatedBossesForUser(userId)
                .stream()
                .max(Comparator.comparingInt(Boss::getBossLevel))
                .orElse(null);

        BossDTO bossDTO = new BossDTO(boss);
        return  bossDTO;
    }
    /// TODO: Mima baci oko drugo
    public Boss getPreviousBossForUser(String userId, int userLevel){
        Boss boss =  bossRepository.getPreviousBossForUser(userId,userLevel-2);
        if (boss == null) {
            Log.e("BossService", "Nema prethodnog bossa za usera: " + userId);
            Boss b= new Boss(userLevel+2,10,userId,10,false,userLevel-2,200,0.2,false);
            b.setId(userLevel+2);
            return b;
        }
        return boss;
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

    /// TODO: Mima baci oko opet ono prvo jer nemas trece
    public BossDTO getPrevioussBossForUser(String userId, int previousBossLevel){
        Boss boss = bossRepository.getPreviousBossForUser(userId, previousBossLevel);
        if (boss == null) {
            Log.e("BossService", "Nema prethodnog bossa za usera: " + userId);
            boss= new Boss(previousBossLevel+2,10,userId,10,false,previousBossLevel,200,0.2,false);
            return new BossDTO(boss);
        }
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
