package com.example.myhobitapplication.workers;

import android.content.Context;
import android.telephony.euicc.EuiccInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databases.UserEquipmentRepository;
import com.example.myhobitapplication.models.AllianceMission;
import com.example.myhobitapplication.services.AllianceMissionService;
import com.example.myhobitapplication.services.AllianceMissionUserService;
import com.example.myhobitapplication.services.AllianceService;
import com.example.myhobitapplication.services.BattleService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.EquipmentService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.services.UserEquipmentService;
import com.example.myhobitapplication.services.UserService;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;

public class MissionCompletionWorker extends Worker {

    public static final String KEY_MISSION_ID = "MISSION_ID";
    private UserService userService;
    private ProfileService profileService;
    private BossRepository bossRepository;
    private BossService bossService;
    private BattleService battleService;
    private TaskRepository taskRepository;
    private AllianceMissionService allianceMissionService;
    private TaskService taskService;
    private Context context;
    private UserEquipmentService userEquipmentService;

    public MissionCompletionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;

    }
    /*UserEquipmentRepository repository, ProfileService profileService, BossService bossService, EquipmentService equipmentService, AllianceMissionService allianceMissionService*/

    @NonNull
    @Override
    public Result doWork() {

        userService = new UserService();
        profileService = ProfileService.getInstance();
        bossRepository = new BossRepository(context);
        bossService = new BossService(bossRepository);
        battleService = new BattleService(bossService,profileService);
        taskRepository = new TaskRepository(context);
        allianceMissionService = new AllianceMissionService(profileService);
        UserEquipmentRepository userEquipmentRepository = new UserEquipmentRepository(context);
        EquipmentRepository equipmentRepository = new EquipmentRepository(context);
        EquipmentService equipmentService = new EquipmentService(equipmentRepository);
        userEquipmentService = new UserEquipmentService(userEquipmentRepository,profileService,bossService,equipmentService,allianceMissionService);
        taskService =  TaskService.getInstance(taskRepository,profileService,battleService,allianceMissionService);

        String missionId = getInputData().getString(KEY_MISSION_ID);

        if (missionId == null || missionId.isEmpty()) {
            Log.e("MissionWorker", "Mission ID is null.");
            return Result.failure();
        }

        Log.d("MissionWorker", "Starting check for mission: " + missionId);


        AllianceMissionUserService userMissionService = new AllianceMissionUserService(userService,battleService,userEquipmentService);

        try {

            Task<Void> completionTask = userMissionService.processMissionCompletionTest(missionId);
            Tasks.await(completionTask);

            Log.d("MissionWorker", "Mission " + missionId + " successfully obradjen.");
            return Result.success();

        } catch (ExecutionException | InterruptedException e) {
            Log.e("MissionWorker", "Error with obrada.", e);
            return Result.retry();
        }
    }
}