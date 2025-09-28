package com.example.myhobitapplication.services;

import android.content.Context;

import com.example.myhobitapplication.databases.AllianceRepository;
import com.example.myhobitapplication.models.Alliance;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

public class AllianceService {

    private AllianceRepository allianceRepository;
    public AllianceService(){
        this.allianceRepository = new AllianceRepository();
    }

    public Task<DocumentReference> insert(Alliance alliance){
        return allianceRepository.insert(alliance);
    }
}
