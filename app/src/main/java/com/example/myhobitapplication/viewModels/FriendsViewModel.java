package com.example.myhobitapplication.viewModels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.UserInfoDTO;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.services.AllianceMissionService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendsViewModel extends ViewModel {

    private final ProfileService profileService;
    private final UserService userService;


    private final MutableLiveData<List<User>> allUsers = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<UserInfoDTO>> friends = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<User>> usersFiltered = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Profile> friendProfile = new MutableLiveData<>(new Profile());
    private final MutableLiveData<UserInfoDTO> friendUserInfo = new MutableLiveData<>(new UserInfoDTO());
    private final MutableLiveData<String> response = new MutableLiveData<>("");

    private final MutableLiveData<User> owner = new MutableLiveData<>(new User());
    private final MutableLiveData<Boolean> loadSuccess = new MutableLiveData<>(false);


    public FriendsViewModel() {
        this.profileService = ProfileService.getInstance();
        this.userService = new UserService();
    }

    public MutableLiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public MutableLiveData<User> getOwner() {
        return owner;
    }

    public MutableLiveData<List<UserInfoDTO>> getFriends() {
        return friends;
    }

    public MutableLiveData<List<User>> getUsersFiltered() {
        return usersFiltered;
    }

    public MutableLiveData<Profile> getFriendProfile() {
        return friendProfile;
    }

    public MutableLiveData<UserInfoDTO> getFriendUserInfo() {
        return friendUserInfo;
    }

    public MutableLiveData<String> getResponse() {
        return response;
    }

    public MutableLiveData<Boolean> getLoadSuccess() {
        return loadSuccess;
    }



    public void addFriend(String userId, String friendId){
        userService.addFriend(userId, friendId);
    }
    public void loadAllUsers() {
            response.setValue("");
            loadSuccess.setValue(false);

            userService.getAllUsers()
                    .addOnSuccessListener(querySnapshot -> {
                        List<User> list = new ArrayList<>();
                        List<UserInfoDTO> friendList = friends.getValue();
                        for (DocumentSnapshot doc : querySnapshot) {
                            User user = doc.toObject(User.class);
                            if (user != null ) {
                                if(user.getemail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                    owner.setValue(user);
                                    continue;
                                }

                                list.add(user);
                            }
                        }
                        allUsers.setValue(list);
                        usersFiltered.setValue(list);
                        response.setValue("All users loaded");
                        loadSuccess.setValue(true);
                    })
                    .addOnFailureListener(e -> {
                        response.setValue(e.getMessage());
                        loadSuccess.setValue(false);
                    });
        }

        public void fiterFriends(String username){
        List<User> filtered = new ArrayList<>();
                allUsers.getValue().forEach(f->{
                    if(f.getusername().toLowerCase().contains(username.toLowerCase())){
                        filtered.add(f);
                    }
                });
            usersFiltered.setValue(filtered);
        }

        public void loadFriends(String userUid) {
        response.setValue("");
        loadSuccess.setValue(false);

        userService.getFriends(userUid)
                .addOnSuccessListener(friendUids -> {
                    List<UserInfoDTO> friendInfos = new ArrayList<>();
                    AtomicInteger counter = new AtomicInteger(friendUids.size());
                    if (friendUids.isEmpty()) {
                        friends.setValue(friendInfos);
                        response.setValue("Friends loaded");
                        loadSuccess.setValue(true);
                        return;
                    }

                    for (String friendUid : friendUids) {
                        profileService.getUserData(friendUid).addOnSuccessListener(documentReference ->{
                                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            friendInfos.add(documentSnapshot.toObject(UserInfoDTO.class));
                                            if (counter.decrementAndGet() == 0) {
                                                friends.setValue(friendInfos);
                                                response.setValue("Friends loaded");
                                                loadSuccess.setValue(true);
                                            }
                                        }else{
                                            response.setValue("Freinds loading failed! Couldn retrieve user data!");
                                            loadSuccess.setValue(false);
                                        }
                                    });

                                })
                                .addOnFailureListener(e -> {
                                    if (counter.decrementAndGet() == 0) {
                                        friends.setValue(friendInfos);
                                        response.setValue("Friends loaded with some errors");
                                        loadSuccess.setValue(true);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    response.setValue(e.getMessage());
                    loadSuccess.setValue(false);
                });
    }


    public void loadFriendProfile(String friendUid) {
        response.setValue("");
        loadSuccess.setValue(false);

        profileService.getByUid(friendUid)
                .addOnSuccessListener(documentReference ->
                        documentReference.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Profile loadedProfile = documentSnapshot.toObject(Profile.class);
                                friendProfile.setValue(loadedProfile);
                                response.setValue("Friend profile loaded");

                                profileService.getUserData(friendUid).addOnSuccessListener(documentReference2 ->
                                        documentReference2.get().addOnSuccessListener(documentSnapshot2 -> {
                                            if (documentSnapshot2.exists()) {
                                                friendUserInfo.setValue(documentSnapshot2.toObject(UserInfoDTO.class));
                                            } else {
                                                response.setValue("Could not retrieve friend user data");
                                            }
                                        }));
                                loadSuccess.setValue(true);
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
