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

    public BossDTO getLowestLevelBossForUser(int userId) {
        Boss boss = bossRepository.getAllUndefeatedBossesForUser(userId)
                .stream()
                .min(Comparator.comparingInt(Boss::getBossLevel))
                .orElse(null);

        BossDTO bossDTO = new BossDTO(boss);
        return  bossDTO;
    }

    public long updateBoss(BossDTO bossDTO){

        Boss boss = new Boss(

                bossDTO.getId(),
                bossDTO.getHP(),
                bossDTO.getUserId(),
                bossDTO.getCurrentHP(),
                bossDTO.getDefeated(),
                bossDTO.getBossLevel(),
                bossDTO.getCoinsReward()
        );


        return bossRepository.updateBoss(boss);

    }


}
