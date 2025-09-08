package com.example.myhobitapplication.services;

import com.example.myhobitapplication.databases.ProfileRepository;
import com.example.myhobitapplication.dto.BossDTO;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.models.Profile;
import com.google.firebase.auth.FirebaseAuth;

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
}
