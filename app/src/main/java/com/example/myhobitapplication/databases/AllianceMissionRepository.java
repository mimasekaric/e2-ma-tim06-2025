package com.example.myhobitapplication.databases;

import com.example.myhobitapplication.enums.MissionStatus;
import com.example.myhobitapplication.models.AllianceMission;
import com.example.myhobitapplication.models.UserMission;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.List;

public class AllianceMissionRepository {

    private final FirebaseFirestore db ;
    private final CollectionReference allianceMissionsRef;

    public AllianceMissionRepository() {
        db = FirebaseFirestore.getInstance();
        allianceMissionsRef = db.collection("allianceMissions");
    }
    public Task<QuerySnapshot> getActiveMissionForAlliance(String allianceId) {
        return allianceMissionsRef
                .whereEqualTo("allianceId", allianceId)
                .whereEqualTo("status", MissionStatus.ACTIVE)
                .limit(1)
                .get();
    }
    public Task<Void> createMission(AllianceMission mission, List<UserMission> initialProgressList) {
        WriteBatch batch = db.batch();

        DocumentReference missionRef = allianceMissionsRef.document();
        mission.setId(missionRef.getId());
        batch.set(missionRef, mission);

        CollectionReference progressRef = missionRef.collection("userProgress");
        for (UserMission userProgress : initialProgressList) {
            DocumentReference userProgressDoc = progressRef.document(userProgress.getUserId());
            userProgress.setId(userProgressDoc.getId());
            batch.set(userProgressDoc, userProgress);
        }

        return batch.commit();
    }

    public Task<QuerySnapshot> getAllUserProgressForMission(String missionId) {
        return allianceMissionsRef.document(missionId).collection("userProgress").get();
    }

    public ListenerRegistration listenForActiveMission(String allianceId, EventListener<QuerySnapshot> listener) {
        return allianceMissionsRef
                .whereEqualTo("allianceId", allianceId)
                .whereEqualTo("status", "ACTIVE")
                .limit(1)
                .addSnapshotListener(listener);
    }

    public ListenerRegistration listenForAllUserProgress(String missionId, EventListener<QuerySnapshot> listener) {
        return allianceMissionsRef.document(missionId).collection("userProgress")
                .orderBy("totalDamage", Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }

}
