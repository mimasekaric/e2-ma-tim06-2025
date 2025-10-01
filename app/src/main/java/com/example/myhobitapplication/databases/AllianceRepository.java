package com.example.myhobitapplication.databases;

import com.example.myhobitapplication.models.Alliance;
import com.example.myhobitapplication.models.Profile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AllianceRepository {

    private final FirebaseFirestore db;
    private final CollectionReference allianceCollection;

    public AllianceRepository() {
        db = FirebaseFirestore.getInstance();
        allianceCollection = db.collection("alliances");
    }

    public Task<DocumentReference> insert(Alliance alliance) {
        Map<String, Object> alliance1 = new HashMap<>();
        alliance1.put("leaderId", alliance.getLeaderId());
        alliance1.put("name", alliance.getName());
        alliance1.put("missionEndDate", alliance.getMissionEndDate());
        alliance1.put("missionStartDate", alliance.getMissionStartDate());
        alliance1.put("hasActivatedMission", alliance.getHasActivatedMission());
        return allianceCollection
                .add(alliance1);
    }

    public Task<DocumentSnapshot> getAlliance(String id){
        return allianceCollection.document(id).get();
    }


}
