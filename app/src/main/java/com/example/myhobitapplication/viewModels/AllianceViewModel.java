package com.example.myhobitapplication.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.models.Alliance;
import com.example.myhobitapplication.services.AllianceService;
import com.example.myhobitapplication.services.UserService;
import com.google.firebase.auth.FirebaseAuth;

public class AllianceViewModel extends ViewModel {

    private final AllianceService allianceService;
    private final UserService userService;

    private final MutableLiveData<Alliance> createdAlliance = new MutableLiveData<>(null);
    private final MutableLiveData<String> createdResponse = new MutableLiveData<>("");

    public AllianceViewModel() {
        this.allianceService = new AllianceService();
        this.userService = new UserService();
    }

    public MutableLiveData<Alliance> getCreatedAlliance() { return createdAlliance; }
    public MutableLiveData<String> getCreatedREsponse() { return createdResponse; }

    public void createAlliance(Alliance alliance) {

        createdAlliance.setValue(null);
        allianceService.insert(alliance)
                .addOnSuccessListener(documentRef -> {
                    documentRef.get().addOnSuccessListener(snapshot -> {
                                if (snapshot.exists()) {
                                    Alliance inserted = snapshot.toObject(Alliance.class);
                                    createdAlliance.setValue(inserted);
                                    createdResponse.setValue("Alliance successfully created!");
                                    userService.updateAllianceId(FirebaseAuth.getInstance().getCurrentUser().getUid(), snapshot.getId());
                                } else {
                                    createdResponse.setValue("Document does not exist after insert!");
                                }
                            }).addOnFailureListener(e -> createdResponse.setValue(e.getMessage()));
                })
            .addOnFailureListener(e -> createdResponse.setValue(e.getMessage()));


    }
}
