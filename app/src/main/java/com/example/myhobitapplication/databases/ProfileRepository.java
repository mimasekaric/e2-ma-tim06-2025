package com.example.myhobitapplication.databases;

import android.util.Log;

import com.example.myhobitapplication.models.Profile;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileRepository {

    private final FirebaseFirestore db;
    private final CollectionReference profileCollection;

    public ProfileRepository() {
        db = FirebaseFirestore.getInstance();
        profileCollection = db.collection("profiles");
    }

    public Task<DocumentReference> insert(Profile profile) {
        Map<String, Object> profile1 = new HashMap<>();
        profile1.put("userUid", profile.getuserUid());
        profile1.put("title", profile.getTitle());
        profile1.put("coins", profile.getcoins());
        profile1.put("pp", profile.getPp());
        profile1.put("xp", profile.getxp());
        profile1.put("level", profile.getlevel());
        profile1.put("numberOgBadges", profile.getnumberOgbadges());
        profile1.put("badges", profile.getbadges());
        profile1.put("equipment", profile.getequipment());
        return profileCollection
                .add(profile1);
    }

    public Task<DocumentReference> getByUid(String uid){
        final TaskCompletionSource<DocumentReference> taskCompletionSource = new TaskCompletionSource<>();
         profileCollection.whereEqualTo("userUid" , uid).get().addOnSuccessListener(queryDocumentSnapshots ->{
            queryDocumentSnapshots.getDocuments().get(0).getReference();
             if(queryDocumentSnapshots.isEmpty()){
                 taskCompletionSource.setException(new Exception("Coudn't find profile!"));
                 Log.d("Firestore", "No profile found with UID: "+ uid );
             }else taskCompletionSource.setResult(queryDocumentSnapshots.getDocuments().get(0).getReference());

    }).addOnFailureListener(e -> { taskCompletionSource.setException(e);});
         return taskCompletionSource.getTask();
    }

    public void delete(String uid){
       profileCollection.whereEqualTo("userUid", uid).get().addOnSuccessListener(queryDocumentSnapshots -> {
           DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
           document.getReference().delete();
       }).addOnFailureListener(e->{new Exception("Failed to delete profile");});

       }


}
