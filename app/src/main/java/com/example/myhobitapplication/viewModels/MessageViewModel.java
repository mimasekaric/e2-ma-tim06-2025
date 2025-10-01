package com.example.myhobitapplication.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.databases.MessageRepository;
import com.example.myhobitapplication.databases.UserRepository;
import com.example.myhobitapplication.models.Message;
import com.example.myhobitapplication.models.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageViewModel extends ViewModel {

    private final MessageRepository messageRepo;
    private final UserRepository userRepo;

    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>(new ArrayList<>());
    private final Map<String, String> uidToUsername = new HashMap<>();

    public MessageViewModel() {
        messageRepo = new MessageRepository();
        userRepo = new UserRepository();
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

    public void sendMessage(String allianceId, String text) {
        String currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        String senderName = uidToUsername.getOrDefault(currentUid, currentUid);
        messageRepo.sendMessage(allianceId, currentUid, senderName, text);
    }
}
