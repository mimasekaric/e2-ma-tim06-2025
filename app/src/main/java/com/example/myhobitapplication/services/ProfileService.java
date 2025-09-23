package com.example.myhobitapplication.services;

import com.example.myhobitapplication.databases.ProfileRepository;
import com.example.myhobitapplication.databases.UserRepository;
import com.example.myhobitapplication.models.Profile;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;


public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private  String userIdd;
    public ProfileService(){
        this.profileRepository = new ProfileRepository();
        userRepository = new UserRepository();
        this.userIdd="";
    }
    public Task<DocumentReference>  getByUid(String uid){
        return profileRepository.getByUid(uid);
    }
    public Task<DocumentReference> getUserData(String uid){
        return userRepository.getUserInfo(uid);
    }

    public Task<Void> incrementProfileFieldValue(String userUid, String fieldName, Integer xp){
        return profileRepository.incrementUserProfileField(userUid, fieldName, xp);
    } // inccrementira xp ili novcice uzavnisnosti od proslijedjenih parametara lol
    public Task<Profile> getProfileById(String userUid){
        return profileRepository.getProfileById(userUid);
    }

    public Task<Void> updatePp( String uid,int newValue){
        return  profileRepository.updatePp(uid, newValue);
    }

    public Task<Void> updateCoins( String uid,int newValue){
        return  profileRepository.updateCoins(uid, newValue);
    }

    public Task<DocumentReference> insert(Profile profile){
        return profileRepository.insert(profile);
    }

    public Task<Void> updateLevel( String uid,int newLevel, int newxp){
        return  profileRepository.updateLevel(uid, newLevel, newxp);
    }

    public Task<Void> checkForLevelUpdates(String uid) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        getProfileById(uid).addOnSuccessListener(p->{
            Profile profile = p;
            if (profile.getxp() >= profile.getXpRequired()) {
                int newXpRequired = (profile.getXpRequired() * 2) + (profile.getXpRequired() / 2);
                int newLevel = profile.getlevel() + 1;

                updateLevel(profile.getuserUid(), newLevel, newXpRequired)
                        .addOnSuccessListener(v -> tcs.setResult(null))
                        .addOnFailureListener(tcs::setException);
            } else {
                tcs.setResult(null);
            }
        }
        ) .addOnFailureListener(tcs::setException);
        return tcs.getTask();

    }

}
