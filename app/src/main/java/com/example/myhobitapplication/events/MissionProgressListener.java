package com.example.myhobitapplication.events;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.services.AllianceMissionService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.UserService;

public class MissionProgressListener {

    private final AllianceMissionService missionService;
    private final ProfileService profileService;
    private final LifecycleOwner lifecycleOwner;

    public MissionProgressListener(LifecycleOwner lifecycleOwner) {
        this.profileService = ProfileService.getInstance();
        this.missionService = new AllianceMissionService(profileService);
        this.lifecycleOwner = lifecycleOwner;
    }

    public void startListening() {
        GameEventBus.getInstance().getEvents().observe(lifecycleOwner, this::handleGameEvent);
    }

    private void handleGameEvent(GameEvent event) {
        if (event == null) return;

        Log.d("MissionListener", "Event registered: " + event.getEventType());

        String userId = event.getUserId();

        missionService.checkIfUserHasActiveAlliance(userId)
                .addOnSuccessListener(hasActiveMission -> {
                    if (hasActiveMission != null && hasActiveMission) {

                        profileService.getUserData(userId).onSuccessTask(documentReference -> {
                            return documentReference.get();
                        }).addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                if (user != null && user.getAllianceId() != null) {
                                    missionService.trackProgress(userId, user.getAllianceId(), event.getEventType());

                                }
                            }
                        });
                    }
                });
    }
}
