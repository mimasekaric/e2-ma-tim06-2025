package com.example.myhobitapplication.services;

import android.content.Context;

import com.example.myhobitapplication.databases.AllianceRepository;
import com.example.myhobitapplication.models.Alliance;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;

public class AllianceService {

    private AllianceRepository allianceRepository;
   private UserService userService;
    public AllianceService(){
        this.allianceRepository = new AllianceRepository();
        this.userService = new UserService();
    }

    public Task<DocumentReference> insert(Alliance alliance){
        return allianceRepository.insert(alliance);
    }
    public Task<DocumentReference> getAllianceByUser(String userId){
        TaskCompletionSource<DocumentReference> tcs = new TaskCompletionSource<>();
         userService.getUser(userId).addOnSuccessListener(documentReference -> {
             documentReference.get().addOnSuccessListener(documentSnapshot -> {
                 if (documentSnapshot.exists()){
                     User user = documentSnapshot.toObject(User.class);
                     String alId = user.getAllianceId();
                     allianceRepository.getAlliance(alId).addOnSuccessListener(documentSnapshot1 -> {
                            if(documentSnapshot1.exists()){
                                tcs.setResult(documentSnapshot1.getReference());
                            }
                     });
                 }
             });
         });
         return tcs.getTask();
    }
}
