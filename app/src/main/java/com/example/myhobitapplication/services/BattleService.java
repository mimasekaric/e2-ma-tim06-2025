package com.example.myhobitapplication.services;

import com.example.myhobitapplication.dto.BossDTO;
import com.example.myhobitapplication.models.Boss;

import java.time.LocalDate;

public class BattleService {

    private TaskService taskService;
    private BossService bossService;

    public BattleService(TaskService taskService, BossService bossService){

        this.bossService = bossService;
        this.taskService = taskService;

    }

    public Boss getFirstUndefeatedBoss(int userId){

        return getFirstUndefeatedBoss(userId);
    }

    public long updateBoss(BossDTO boss){

        return bossService.updateBoss(boss);

    }

    public Double calculateChanceForAttack(LocalDate previousLevelDate, LocalDate currentLevelDate){

        Integer completedTasks = taskService.countFinishedTasksForDateRange(previousLevelDate,currentLevelDate);
        Integer createdTasks = taskService.countCreatedTasksForDateRange(previousLevelDate,currentLevelDate);

        if (createdTasks == null || createdTasks == 0) {
            return 0.0;
        }

        return (double)completedTasks /createdTasks;
    }
}
