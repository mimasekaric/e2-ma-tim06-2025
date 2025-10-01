package com.example.myhobitapplication.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.UserInfoDTO;
import com.example.myhobitapplication.dto.UserProgressDTO;
import com.example.myhobitapplication.models.AllianceMission;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.models.UserMission;
import com.example.myhobitapplication.services.AllianceMissionService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.UserService;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserProgressViewModel extends ViewModel {

    private final UserService userService;
    private String userId;
    private final AllianceMissionService missionService;
    private final MutableLiveData<List<UserProgressDTO>> allianceProgress = new MutableLiveData<>();
    private final MutableLiveData<String> response = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> loadSuccess = new MutableLiveData<>(false);

    private final MutableLiveData<AllianceMission> activeMission = new MutableLiveData<>();
    public UserProgressViewModel(UserService userService, AllianceMissionService missionService) {
        this.userService = userService;
        this.missionService = missionService;
    }

    public MutableLiveData<List<UserProgressDTO>> getAllianceProgress() { return allianceProgress; }
    public MutableLiveData<String> getResponse() { return response; }
    public MutableLiveData<Boolean> getLoadSuccess() { return loadSuccess; }
    public LiveData<AllianceMission> getActiveMission() { return activeMission; }


    public void loadAllianceMissionProgress(String allianceId) {
        response.setValue("Loading mission data...");
        loadSuccess.setValue(false);
        activeMission.setValue(null);
        allianceProgress.setValue(new ArrayList<>());

        missionService.getActiveMission(allianceId)
                .addOnFailureListener(e -> {
                    response.setValue("Error finding active mission: " + e.getMessage());
                    loadSuccess.setValue(false);
                })
                .addOnSuccessListener(mission -> {


                    if (mission == null) {

                        response.setValue("No active mission found.");
                        loadSuccess.setValue(true);
                        activeMission.setValue(null);
                        allianceProgress.setValue(new ArrayList<>());
                        return;
                    }


                    activeMission.setValue(mission);
                    String missionId = mission.getId();

                    Task<List<User>> membersTask = userService.getAllAllianceMember(allianceId);


                    Task<List<UserMission>> progressTask = missionService.getAllUserProgressForMission(missionId)
                            .continueWith(task -> task.getResult().toObjects(UserMission.class));


                    Tasks.whenAllSuccess(membersTask, progressTask).addOnSuccessListener(results -> {
                        List<User> members = (List<User>) results.get(0);
                        List<UserMission> progressList = (List<UserMission>) results.get(1);


                        Map<String, UserMission> progressMap = progressList.stream()
                                .collect(Collectors.toMap(UserMission::getUserId, Function.identity()));


                        List<UserProgressDTO> dtoList = new ArrayList<>();
                        for (User member : members) {
                            UserMission memberProgress = progressMap.get(member.getUid());

                            dtoList.add(new UserProgressDTO(member, memberProgress));
                        }


                        dtoList.sort((o1, o2) -> Integer.compare(o2.getTotalDamage(), o1.getTotalDamage()));

                        allianceProgress.setValue(dtoList);
                        response.setValue("Alliance progress loaded.");
                        loadSuccess.setValue(true);
                    }).addOnFailureListener(e -> {

                        response.setValue("Error loading members' progress: " + e.getMessage());
                        loadSuccess.setValue(false);
                    });
                });
    }



}
