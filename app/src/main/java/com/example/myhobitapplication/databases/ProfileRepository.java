package com.example.myhobitapplication.databases;

import android.util.Log;

import com.example.myhobitapplication.models.Profile;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        profile1.put("xpRequired", profile.getXpRequired());
        profile1.put("level", profile.getlevel());
        profile1.put("numberOgBadges", profile.getnumberOgbadges());
        profile1.put("badges", profile.getbadges());
        profile1.put("previousLevelDate", profile.getPreviousLevelDate());
        profile1.put("currentLevelDate", profile.getCurrentLevelDate());
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

    public Task<Void> incrementUserProfileField(String uid, String fieldName, long newValue) {

        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        profileCollection.whereEqualTo("userUid", uid).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        tcs.setException(new Exception("Profile not found for UID: ".concat(uid)));
                    } else {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        document.getReference().update(fieldName, FieldValue.increment(newValue))
                                .addOnSuccessListener(aVoid -> tcs.setResult(null))
                                .addOnFailureListener(tcs::setException);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error with searching for profile with  UID: " + uid, e);
                    tcs.setException(e);
                });

        return tcs.getTask();

    }

    public Task<Profile> getProfileById(String uid) {

        final TaskCompletionSource<Profile> tcs = new TaskCompletionSource<>();

        profileCollection.whereEqualTo("userUid", uid).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("Firestore", "No profile found for UID: " + uid);
                        tcs.setException(new Exception("Profile not found for user."));

                    } else {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Profile profile = document.toObject(Profile.class);

                        if (profile != null) {
                            tcs.setResult(profile);
                        } else {
                            tcs.setException(new Exception("Failed to parse profile data."));
                        }
                    }

                }).addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting profile for UID: " + uid, e);
                    tcs.setException(e);
                });
        return tcs.getTask();
    }
    public Task<Void> updateCoins(String uid, int newCoinsValue) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        profileCollection.whereEqualTo("userUid", uid).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        tcs.setException(new Exception("Profile not found for UID: " + uid));
                    } else {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        document.getReference()
                                .update("coins", newCoinsValue) // directly set new value
                                .addOnSuccessListener(aVoid -> tcs.setResult(null))
                                .addOnFailureListener(tcs::setException);
                    }
                })
                .addOnFailureListener(tcs::setException);

        return tcs.getTask();
    }

    public Task<Void> updatePp(String uid, int newppValue) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        profileCollection.whereEqualTo("userUid", uid).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        tcs.setException(new Exception("Profile not found for UID: " + uid));
                    } else {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        document.getReference()
                                .update("pp", newppValue) // directly set new value
                                .addOnSuccessListener(aVoid -> tcs.setResult(null))
                                .addOnFailureListener(tcs::setException);
                    }
                })
                .addOnFailureListener(tcs::setException);

        return tcs.getTask();
    }

            public Task<Void> updateLevel(String uid, int newLevel, int newXpRequired, int newPP, String newTitle) {
                TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        profileCollection.whereEqualTo("userUid", uid).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        tcs.setException(new Exception("Profile not found for UID: " + uid));
                    } else {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                                Map<String, Object> updates = new HashMap<>();
                                updates.put("level", newLevel);
                                updates.put("xpRequired", newXpRequired);
                                updates.put("pp", newPP);
                                updates.put("title", newTitle);

                        document.getReference()
                                .update(updates)
                                .addOnSuccessListener(aVoid -> tcs.setResult(null))
                                .addOnFailureListener(tcs::setException);
                    }
                })
                .addOnFailureListener(tcs::setException);

        return tcs.getTask();
    }
}

