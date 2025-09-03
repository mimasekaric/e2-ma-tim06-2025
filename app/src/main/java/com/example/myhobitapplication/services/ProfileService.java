package com.example.myhobitapplication.services;

import com.example.myhobitapplication.databases.ProfileRepository;
import com.example.myhobitapplication.databases.UserRepository;
import com.google.android.gms.tasks.Task;
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
}
