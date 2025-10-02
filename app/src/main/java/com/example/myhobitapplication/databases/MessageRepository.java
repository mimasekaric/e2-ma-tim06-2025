package com.example.myhobitapplication.databases;

import com.example.myhobitapplication.models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.type.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
public class MessageRepository {

    private final FirebaseFirestore db;

    public MessageRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> sendMessage(String allianceId, String senderId, String senderName, String text) {
        CollectionReference messagesRef = db.collection("alliances")
                .document(allianceId)
                .collection("messages");

        Message msg = new Message(senderId, senderName, text,Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        return messagesRef.add(msg).continueWith(task -> null);
    }

    public Query getMessages(String allianceId) {
        return db.collection("alliances")
                .document(allianceId)
                .collection("messages")
                .orderBy("messageSent", Query.Direction.ASCENDING);
    }
}
