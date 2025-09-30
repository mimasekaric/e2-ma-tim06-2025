package com.example.myhobitapplication.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.UserInfoDTO;
import com.example.myhobitapplication.dto.UserProgressDTO;
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

    public UserProgressViewModel(ProfileService profileService) {
        this.userService = new UserService();
        this.missionService = new AllianceMissionService(profileService);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public MutableLiveData<List<UserProgressDTO>> getAllianceProgress() { return allianceProgress; }
    public MutableLiveData<String> getResponse() { return response; }
    public MutableLiveData<Boolean> getLoadSuccess() { return loadSuccess; }


    public void loadAllianceMissionProgress(String allianceId) {
        response.setValue("Loading progress...");
        loadSuccess.setValue(false);


//                    String missionId = mission.getId();
//
//                    Task<List<User>> membersTask = userService.getAllianceMembers(allianceId);
//
//                    Task<List<UserMission>> progressTask = missionService.getAllUserProgressForMission(missionId)
//                            .continueWith(task -> task.getResult().toObjects(UserMission.class));
//
//                    Tasks.whenAllSuccess(membersTask, progressTask).addOnSuccessListener(results -> {
//                        List<User> members = (List<User>) results.get(0);
//                        List<UserMission> progressList = (List<UserMission>) results.get(1);
//
//                        Map<String, UserMission> progressMap = progressList.stream()
//                                .collect(Collectors.toMap(UserMission::getUserId, Function.identity()));
//
//                        List<UserProgressDTO> dtoList = new ArrayList<>();
//                        for (User member : members) {
//                            UserMission memberProgress = progressMap.get(member.getUserId());
//                            dtoList.add(new UserProgressDTO(member.getUserId(), member.getUsername(), member.getAvatarName(), memberProgress));
//                        }
//
//                        dtoList.sort((o1, o2) -> Integer.compare(o2.getTotalDamage(), o1.getTotalDamage()));
//
//                        allianceProgress.setValue(dtoList);
//                        response.setValue("Alliance progress loaded.");
//                        loadSuccess.setValue(true);
//                    });

    }




}
