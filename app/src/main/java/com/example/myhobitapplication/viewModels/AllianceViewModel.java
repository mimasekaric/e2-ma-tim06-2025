package com.example.myhobitapplication.viewModels;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.models.Alliance;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.services.AllianceService;
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

    public AllianceViewModel() {
        this.allianceService = new AllianceService();
        this.userService = new UserService();
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


}
