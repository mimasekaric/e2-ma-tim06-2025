package com.example.myhobitapplication.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.databases.MessageRepository;
import com.example.myhobitapplication.databases.UserRepository;
import com.example.myhobitapplication.models.Message;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.services.AllianceMissionService;
import com.example.myhobitapplication.services.ProfileService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageViewModel extends ViewModel {

    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final AllianceMissionService missionService;

    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>(new ArrayList<>());
    private final Map<String, String> uidToUsername = new HashMap<>();

    public MessageViewModel() {
        messageRepo = new MessageRepository();
        userRepo = new UserRepository();
        missionService = new AllianceMissionService(ProfileService.getInstance());
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public void listenForMessages(String allianceId) {
        messageRepo.getMessages(allianceId).addSnapshotListener((snapshots, e) -> {
            if (e != null || snapshots == null) return;

            List<Message> list = new ArrayList<>();
            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                Message msg = doc.toObject(Message.class);
                if (msg != null) {
                    String senderUid = msg.getSenderId();

                    if (!uidToUsername.containsKey(senderUid)) {
                        userRepo.getUserInfo(senderUid).addOnSuccessListener(userDocRef ->
                                userDocRef.get().addOnSuccessListener(userDoc -> {
                                    if (userDoc.exists()) {
                                        User user = userDoc.toObject(User.class);
                                        if (user != null) {
                                            uidToUsername.put(senderUid, user.getusername());
                                            msg.setSenderName(user.getusername());
                                            updateMessagesLiveData(list, msg);
                                        }
                                    }
                                })
                        );
                    } else {
                        msg.setSenderName(uidToUsername.get(senderUid));
                        list.add(msg);
                    }
                }
            }


            messages.setValue(list);
        });
    }

    private void updateMessagesLiveData(List<Message> list, Message msg) {
        list.add(msg);
        messages.setValue(new ArrayList<>(list));
    }
    private void sendOneSignalNotification(String senderName, String messageText, String allianceId) {
        new Thread(() -> {
            try {
                URL url = new URL("https://onesignal.com/api/v1/notifications");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Authorization", "Basic os_v2_app_2y2zge5mdjewxpqsa7grrxh2i3ye6vue5whebmn7s7kgc3mti37hshnpce54cqw3zwnkebf3scikm4f2sjsgdgu6i5vyrn4vloqpslq");

                JSONObject body = new JSONObject();
                body.put("app_id", "d6359313-ac1a-496b-be12-07cd18dcfa46");

                JSONArray filters = new JSONArray();
                filters.put(new JSONObject().put("field", "tag").put("key", "alliance_id").put("relation", "=").put("value", allianceId));
                /// Ako ne radi, skloni narednih 7 linija koda ali ce onda slati notifikaciju i senderu
               filters.put(new JSONObject().put("operator", "AND"));
                filters.put(new JSONObject()
                        .put("field", "tag")
                        .put("key", "user_id")
                        .put("relation", "!=")
                        .put("value", FirebaseAuth.getInstance().getCurrentUser().getUid())
                );
                body.put("filters", filters);

                body.put("headings", new JSONObject().put("en", "New Message"));
                body.put("contents", new JSONObject().put("en", senderName + " sent a new message!"));

                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(body.toString());
                writer.flush();
                writer.close();

                int responseCode = con.getResponseCode();
                Log.d("OneSignal", "Response: " + responseCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(String allianceId, String text) {
        String currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        missionService.trackMessageSent(currentUid, allianceId)
                .addOnSuccessListener(aVoid -> Log.d("MissionTracking", "Message tracking."))
                .addOnFailureListener(e -> Log.e("MissionTracking", "Error message tracking", e));
        String senderName = uidToUsername.getOrDefault(currentUid, currentUid);
        messageRepo.sendMessage(allianceId, currentUid, senderName, text);
        sendOneSignalNotification(senderName, text, allianceId);

    }
}
