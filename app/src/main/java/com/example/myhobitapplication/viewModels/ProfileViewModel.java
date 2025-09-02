package com.example.myhobitapplication.viewModels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.UserInfoDTO;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.services.ProfileService;

public class ProfileViewModel extends ViewModel {


    private final ProfileService profileService;
    private final MutableLiveData<Profile> profile = new MutableLiveData<>(new Profile());
    private final MutableLiveData<UserInfoDTO> userInfo = new MutableLiveData<>(new UserInfoDTO());
    private final MutableLiveData<String> response = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> loadSuccess = new MutableLiveData<>(false);

    public ProfileViewModel() {
        this.profileService = new ProfileService();
    }

    public MutableLiveData<Profile> getProfile() {
        return profile;
    }
    public MutableLiveData<UserInfoDTO> getUserInfo() {
        return userInfo;
    }
    public MutableLiveData<String> getResponse() {
        return response;
    }

    public MutableLiveData<Boolean> getLoadSuccess() {
        return loadSuccess;
    }

    public void loadProfile(String userUid) {
        response.setValue("");
        loadSuccess.setValue(false);

        profileService.getByUid(userUid) .addOnSuccessListener(documentReference ->
                        documentReference.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Profile loadedProfile = documentSnapshot.toObject(Profile.class);
                                profile.setValue(loadedProfile);
                                Log.d("Firestore", " profile found with title: "+ loadedProfile.getTitle() );
                                response.setValue("Profile loaded successfully!");
                                loadSuccess.setValue(true);
                                profileService.getUserData(userUid).addOnSuccessListener(documentReference2 ->{
                                        documentReference2.get().addOnSuccessListener(documentSnapshot2 -> {
                                            if (documentSnapshot2.exists()) {
                                                userInfo.setValue(documentSnapshot2.toObject(UserInfoDTO.class));
                                            }else{
                                                response.setValue("Profile loading failed! Couldn retrieve usere data!");
                                                loadSuccess.setValue(false);
                                            }
                                        }); });
                            } else {
                                response.setValue("Profile not found");
                                loadSuccess.setValue(false);
                            }
                        }).addOnFailureListener(e -> {
                            response.setValue(e.getMessage());
                            loadSuccess.setValue(false);
                        })
                ).addOnFailureListener(e -> {
                    response.setValue(e.getMessage());
                    loadSuccess.setValue(false);
                });


    }
}
