package com.example.myhobitapplication.services;

import android.util.Log;

import com.example.myhobitapplication.databases.ProfileRepository;
import com.example.myhobitapplication.databases.UserRepository;
import com.example.myhobitapplication.enums.Title;
import com.example.myhobitapplication.interfaces.LevelUpListener;
import com.example.myhobitapplication.models.Profile;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;


public class ProfileService {

    private static ProfileService instance;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private  String userIdd;
    private ProfileService(){
        this.profileRepository = new ProfileRepository();
        userRepository = new UserRepository();
        this.userIdd="";
    }
    public static synchronized ProfileService getInstance() {
        if (instance == null) {
            instance = new ProfileService();
        }
        return instance;
    }
    public Task<DocumentReference>  getByUid(String uid){
        return profileRepository.getByUid(uid);
    }
    public Task<DocumentReference> getUserData(String uid){
        return userRepository.getUserInfo(uid);
    }

    public Task<Void> incrementProfileFieldValue(String userUid, String fieldName, Integer xp){
        return profileRepository.incrementUserProfileField(userUid, fieldName, xp);
    }
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

    public Task<Void> updateLevel( String uid,int newLevel, int newxp, int newPP, String newTitle){
        return  profileRepository.updateLevel(uid, newLevel, newxp, newPP, newTitle);
    }

    public Task<Integer> checkForLevelUpdates(String uid) {
        TaskCompletionSource<Integer> tcs = new TaskCompletionSource<>();
        getProfileById(uid).addOnSuccessListener(p->{
            Profile profile = p;
            if (profile.getxp() >= profile.getXpRequired()) {
                int newXpRequired = (profile.getXpRequired() * 2) + (profile.getXpRequired() / 2);
                int newLevel = profile.getlevel() + 1;
                int newPP;
                String newTitle ;
                if(newLevel ==1){
                    newPP= profile.getPp()+40;
                    newTitle=Title.BRAVE_ADVENTURER.toString();
                }else if (newLevel==2){
                    newPP =Math.round( profile.getPp() +  ((float) 3 / 4) * profile.getPp());
                    newTitle =Title.MASTER_OF_SECRETS.toString();
                }else{
                    newPP =Math.round( profile.getPp() +  ((float) 3 / 4) * profile.getPp());
                    newTitle="Specialist " + newLevel;
                }
                updateLevel(profile.getuserUid(), newLevel, newXpRequired,newPP, newTitle)
                        .addOnSuccessListener(v ->{
                        /*    if (levelUpListener != null) {
                            levelUpListener.onLevelUp(profile.getuserUid(), newLevel);
                        }*/
                            for (LevelUpListener listener : levelUpListeners) {
                                listener.onLevelUp(profile.getuserUid(), newLevel);
                            }
                        tcs.setResult(newLevel);
                        })
                        .addOnFailureListener(tcs::setException);
            } else {
                tcs.setResult(null);
            }
        }
        ) .addOnFailureListener(tcs::setException);
        return tcs.getTask();

    }

    /*private LevelUpListener levelUpListener;
    public void setLevelUpListener(LevelUpListener listener) {
        Log.d("ProfileService", "Listener set: " + listener);
        this.levelUpListener = listener;
    }*/
    private final List<LevelUpListener> levelUpListeners = new ArrayList<>();

    public void addLevelUpListener(LevelUpListener listener) {
        Log.d("ProfileService", "Adding listener: " + listener);
        this.levelUpListeners.add(listener);
    }

    public void removeLevelUpListener(LevelUpListener listener) {
        this.levelUpListeners.remove(listener);
    }




}
