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

                     if (alId == null) {
                         tcs.setException(new Exception("User has no alliance"));
                         return;
                     }

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

    public Task<Void> deleteAllianceAndClearUsers(String allianceId) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        userService.getUsersInAlliance(allianceId).addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {

                int totalUsers = querySnapshot.size();
                final int[] completed = {0};
                final boolean[] failed = {false};

                for (var document : querySnapshot.getDocuments()) {
                    User user = document.toObject(User.class);
                    if (user != null && user.getUid() != null) {
                        userService.updateAllianceId(user.getUid(), null)
                                .addOnCompleteListener(task -> {
                                    completed[0]++;
                                    if (!task.isSuccessful()) {
                                        failed[0] = true;
                                    }

                                    if (completed[0] == totalUsers) {
                                        if (failed[0]) {
                                            tcs.setException(new Exception("Failed to clear some users’ allianceId"));
                                        } else {

                                            allianceRepository.getAlliance(allianceId).addOnSuccessListener(snapshot -> {
                                                if (snapshot.exists()) {
                                                    snapshot.getReference().delete()
                                                            .addOnSuccessListener(aVoid -> tcs.setResult(null))
                                                            .addOnFailureListener(tcs::setException);
                                                } else {
                                                    tcs.setException(new Exception("Alliance not found"));
                                                }
                                            }).addOnFailureListener(tcs::setException);
                                        }
                                    }
                                });
                    } else {
                        completed[0]++;
                        if (completed[0] == totalUsers) {
                            tcs.setException(new Exception("Invalid user data"));
                        }
                    }
                }
            } else {

                allianceRepository.getAlliance(allianceId).addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        snapshot.getReference().delete()
                                .addOnSuccessListener(aVoid -> tcs.setResult(null))
                                .addOnFailureListener(tcs::setException);
                    } else {
                        tcs.setException(new Exception("Alliance not found"));
                    }
                }).addOnFailureListener(tcs::setException);
            }
        }).addOnFailureListener(tcs::setException);

        return tcs.getTask();
    }

}
