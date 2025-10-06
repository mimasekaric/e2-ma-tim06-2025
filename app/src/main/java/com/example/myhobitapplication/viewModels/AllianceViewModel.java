package com.example.myhobitapplication.viewModels;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.models.Alliance;
import com.example.myhobitapplication.models.AllianceMission;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.services.AllianceMissionService;
import com.example.myhobitapplication.services.AllianceService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import okhttp3.*;
public class AllianceViewModel extends ViewModel {

    private final AllianceService allianceService;
    private final UserService userService;

    private final MutableLiveData<Alliance> createdAlliance = new MutableLiveData<>(null);

    private final MutableLiveData<List<User>> members = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Alliance> userAlliance = new MutableLiveData<>(null);
    private final MutableLiveData<User> owner = new MutableLiveData<>(new User());
    private final MutableLiveData<String> createdResponse = new MutableLiveData<>("");
    private final AllianceMissionService missionService;
    private final MutableLiveData<String> missionActivationResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> missionActivationSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasUserActiveMission = new MutableLiveData<>();


    public MutableLiveData<String> getMissionActivationResponse() {
        return missionActivationResponse;
    }

    public MutableLiveData<Boolean> getMissionActivationSuccess() {
        return missionActivationSuccess;
    }
    public LiveData<Boolean> getHasUserActiveMission() {
        return hasUserActiveMission;
    }

    public AllianceViewModel() {
        this.allianceService = new AllianceService();
        this.userService = new UserService();
        ProfileService profileService = ProfileService.getInstance();
        this.missionService = new AllianceMissionService(profileService);
    }

    public MutableLiveData<Alliance> getUserAlliance() {
        return userAlliance;
    }

    public MutableLiveData<User> getOwner() {
        return owner;
    }

    public MutableLiveData<List<User>> getMembers() {
        return members;
    }

    public MutableLiveData<Alliance> getCreatedAlliance() { return createdAlliance; }
    public MutableLiveData<String> getCreatedREsponse() { return createdResponse; }
    public void getAlliance(String userId){
        userAlliance.setValue(null);
        allianceService.getAllianceByUser(userId)
                .addOnSuccessListener(documentRef -> {
                    documentRef.get().addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            Alliance found = snapshot.toObject(Alliance.class);
                            found.setId(snapshot.getId());
                           userAlliance.setValue(found);
                        }
                    }).addOnFailureListener(e -> createdResponse.setValue(e.getMessage()));
                })
                .addOnFailureListener(e -> createdResponse.setValue(e.getMessage()));

    }

    public void getUsersInAlliance(){
        userService.getUsersInAlliance(userAlliance.getValue().getId()).addOnSuccessListener(queryDocumentSnapshots -> {
            List<User> list = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                User user = doc.toObject(User.class);
                if (user != null ) {
                    list.add(user);
                    }
                if (user.getUid().equals(userAlliance.getValue().getLeaderId())){
                    owner.setValue(user);
                }
                }
            members.setValue(list);
        });
    }
    public void createAlliance(Alliance alliance) {

        createdAlliance.setValue(null);
        allianceService.insert(alliance)
                .addOnSuccessListener(documentRef -> {
                    documentRef.get().addOnSuccessListener(snapshot -> {
                                if (snapshot.exists()) {
                                    Alliance inserted = snapshot.toObject(Alliance.class);
                                    inserted.setId(snapshot.getId());
                                    createdAlliance.setValue(inserted);
                                    userAlliance.setValue(inserted);
                                    createdResponse.setValue("Alliance successfully created!");
                                    userService.updateAllianceId(FirebaseAuth.getInstance().getCurrentUser().getUid(), snapshot.getId());
                                } else {
                                    createdResponse.setValue("Document does not exist after insert!");
                                }
                            }).addOnFailureListener(e -> createdResponse.setValue(e.getMessage()));
                })
            .addOnFailureListener(e -> createdResponse.setValue(e.getMessage()));


    }



    public void sendInvite(String invitedUserUid, String inviterName, String allianceName) {
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String jsonBody = "{"
                + "\"invitedUserUid\":\"" + invitedUserUid + "\","
                + "\"inviterName\":\"" + inviterName + "\","
                + "\"allianceName\":\"" + allianceName + "\","
                + "\"inviterUid\":\"" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "\""
                + "}";

        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                /// TO DO: ne zaboravi pormijeniti i[ ovdje a i u res/xml/network_security_config]
                .url("http://192.168.1.130:3001/api/notifications/invite")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String result = responseBody.string();
                        Log.d("HTTP", result);
                        createdResponse.postValue("Successfully sent");
                    } else {
                        Log.e("HTTP", "Request failed: " + response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }
    public void addUserToAlliance(String ownerId, String memberId) {

        allianceService.getAllianceByUser(ownerId).addOnSuccessListener(allianceDocRef -> {
            if (allianceDocRef != null) {
                String allianceId = allianceDocRef.getId();

                userService.updateAllianceId(memberId, allianceId)
                        .addOnSuccessListener(aVoid -> {
                            System.out.println("User " + memberId + " successfully added to alliance " + allianceId);
                        })
                        .addOnFailureListener(e -> {
                            System.err.println("Failed to add user to alliance: " + e.getMessage());
                        });
            } else {
                System.err.println("Alliance not found for owner: " + ownerId);
            }
        }).addOnFailureListener(e -> {
            System.err.println("Error fetching alliance for owner: " + e.getMessage());
        });
    }

    public void respondToInvite(String invitedUserUid, String inviterUid, String action) {
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String jsonBody = "{"
                + "\"invitedUserUid\":\"" + invitedUserUid + "\","
                + "\"inviterUid\":\"" + inviterUid + "\","
                + "\"action\":\"" + action + "\""
                + "}";

        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url("http://192.168.1.130:3001/api/notifications/respond")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                /**try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String result = responseBody.string();
                        Log.d("HTTP", "Response: " + result);
                        createdResponse.postValue("Response sent: " + action);
                    } else {
                        Log.e("HTTP", "Request failed: " + response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });
    }

    public void activateMission(String allianceId, Context context) {

        missionActivationResponse.setValue("Activating mission...");
        missionActivationSuccess.setValue(false);

        userService.getAllianceMember(allianceId)
                .addOnSuccessListener(memberIds -> {

                    if (memberIds == null || memberIds.isEmpty()) {
                        missionActivationResponse.setValue("Cannot start mission: No members in the alliance.");
                        missionActivationSuccess.setValue(false);
                        return;
                    }

                    missionService.startMissionForAlliance(allianceId, memberIds, context)
                            .addOnSuccessListener(aVoid -> {
                                missionActivationResponse.setValue("Special mission has been successfully activated!");
                                missionActivationSuccess.setValue(true);
                            })
                            .addOnFailureListener(e -> {
                                missionActivationResponse.setValue("Failed to activate mission: " + e.getMessage());
                                missionActivationSuccess.setValue(false);
                            });
                })
                .addOnFailureListener(e -> {
                    missionActivationResponse.setValue("Failed to get alliance members: " + e.getMessage());
                    missionActivationSuccess.setValue(false);
                });
    }
    public void checkUserActiveMissionStatus(String userId) {

        hasUserActiveMission.setValue(null);

        missionService.checkIfUserHasActiveAlliance(userId)
                .addOnSuccessListener(isActive -> {
                    hasUserActiveMission.setValue(isActive);
                })
                .addOnFailureListener(e -> {
                    hasUserActiveMission.setValue(false);
                    Log.e("AllianceVM", "Error.", e);
                });
    }


}
