package com.example.myhobitapplication.services;

import android.util.Log;

import com.example.myhobitapplication.databases.AllianceMissionRepository;
import com.example.myhobitapplication.enums.MissionStatus;
import com.example.myhobitapplication.events.GameEvent;
import com.example.myhobitapplication.models.AllianceMission;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.models.UserMission;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllianceMissionService {
    private final AllianceMissionRepository missionRepository;
    private final ProfileService profileService;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // Potreban za transakcije

    public AllianceMissionService(ProfileService profileService) {
        this.profileService = profileService;
        this.missionRepository = new AllianceMissionRepository();
    }

    public enum MissionEventType {
        SUCCESSFUL_BOSS_HIT,
        VERY_EASY_TASK_COMPLETED,
        EASY_TASK_COMPLETED,
        BUY_FROM_SHOP,
        SUCCESSFUL_HIT,
        HARD_TASK_COMPLETED

    }

    public Task<Void> startMissionForAlliance(String allianceId, List<String> memberIds) {

        int memberCount = memberIds.size();
        int totalHp = 100 * memberCount;

        Instant now = Instant.now();
        Instant endDateInstant = now.plus(14, ChronoUnit.DAYS);

        Date startDate = Date.from(now);
        Date endDate = Date.from(endDateInstant);

        AllianceMission newMission = new AllianceMission();
        newMission.setAllianceId(allianceId);
        newMission.setTotalBossHp(totalHp);
        newMission.setCurrentBossHp(totalHp);
        newMission.setStatus(MissionStatus.ACTIVE);
        newMission.setStartDate(startDate);
        newMission.setEndDate(endDate);

        List<UserMission> initialProgressList = new ArrayList<>();
        for (String memberId : memberIds) {
            UserMission progress = new UserMission(memberId,memberId, allianceId, 0, 0, 0, 0, 0, 0, null, false);
            initialProgressList.add(progress);
        }

        return missionRepository.createMission(newMission, initialProgressList);
    }
    private static class MissionEventRule {
        final int damage;
        final String fieldToIncrement;
        final int limit;

        MissionEventRule(int damage, String fieldToIncrement, int limit) {
            this.damage = damage;
            this.fieldToIncrement = fieldToIncrement;
            this.limit = limit;
        }
    }
    private MissionEventRule getRuleForEventType(MissionEventType eventType) {
        switch (eventType) {
            case SUCCESSFUL_BOSS_HIT:
                return new MissionEventRule(2, "successfulAttackCount", 10);
            case VERY_EASY_TASK_COMPLETED:
                return new MissionEventRule(1, "easyTaskCompleteCount", 10);
            case EASY_TASK_COMPLETED:
                return new MissionEventRule(1, "easyTaskCompleteCount", 10);
            case HARD_TASK_COMPLETED:
                return new MissionEventRule(4, "hardTaskCompleteCount", 6);
            case BUY_FROM_SHOP:
                return new MissionEventRule(2, "purchaseCount", 5);
            case SUCCESSFUL_HIT:
                return new MissionEventRule(2, "successfulAttackCount", 5);
            default:
                return new MissionEventRule(0, "", 0);
        }
    }
    public Task<Void> trackProgress(String userId, String allianceId, MissionEventType eventType) {

        final MissionEventRule rule = getRuleForEventType(eventType);

        if (rule.damage == 0) {
            return Tasks.forResult(null);
        }

        return missionRepository.getActiveMissionForAlliance(allianceId)
                .onSuccessTask(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        return Tasks.forResult(null);
                    }

                    DocumentReference missionRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                    DocumentReference userProgressRef = missionRef.collection("userProgress").document(userId);

                    return db.runTransaction(transaction -> {

                        DocumentSnapshot snapshot = transaction.get(userProgressRef);

                        if (snapshot.exists()) {
                            // Ako dokument postoji, pokušaj pročitati jedno polje
                            Long purchaseCount = snapshot.getLong("purchaseCount");
                            Log.d("FirestoreDebug", "Dokument postoji. Vrijednost purchaseCount: " + purchaseCount);

                            // Sada pokušaj pretvoriti u objekt
                            UserMission userProgress = snapshot.toObject(UserMission.class);
                            if (userProgress == null) {
                                Log.e("FirestoreDebug", "toObject() je vratio NULL unatoč postojanju dokumenta!");
                            }

                        } else {
                            Log.e("FirestoreDebug", "DOKUMENT userProgress NE POSTOJI na putanji: " + userProgressRef.getPath());
                        }



                        UserMission userProgress = transaction.get(userProgressRef).toObject(UserMission.class);
                        if (userProgress == null) return null;

                        int currentCount = 0;

                        switch (eventType) {
                            case SUCCESSFUL_BOSS_HIT:
                                currentCount = userProgress.getSuccessfulAttackCount();
                                break;
                            case EASY_TASK_COMPLETED:
                                currentCount = userProgress.getEasyTaskCompleteCount();
                                break;
                            case VERY_EASY_TASK_COMPLETED:
                                currentCount = userProgress.getEasyTaskCompleteCount();
                                break;
                            case HARD_TASK_COMPLETED:
                                currentCount = userProgress.getHardTaskCompleteCount();
                            case BUY_FROM_SHOP:
                                currentCount = userProgress.getPurchaseCount();
                            case SUCCESSFUL_HIT:
                                currentCount = userProgress.getSuccessfulAttackCount();
                                break;
                        }


                        if (currentCount >= rule.limit) {
                            return null;
                        }

                        if(eventType.equals(MissionEventType.VERY_EASY_TASK_COMPLETED)){
                            transaction.update(missionRef, "currentBossHp", FieldValue.increment(-rule.damage));
                            transaction.update(userProgressRef, rule.fieldToIncrement, FieldValue.increment(2));
                            transaction.update(userProgressRef, "totalDamage", FieldValue.increment(-rule.damage));
                        }

                        transaction.update(missionRef, "currentBossHp", FieldValue.increment(-rule.damage));
                        transaction.update(userProgressRef, rule.fieldToIncrement, FieldValue.increment(1));
                        transaction.update(userProgressRef, "totalDamage", FieldValue.increment(-rule.damage));

                        return null;
                    });
                });
    }

    public Task<Boolean> checkIfUserHasActiveAlliance(String userId) {

        return profileService.getUserData(userId)
                .onSuccessTask(documentReference -> {
                    if (documentReference == null) {
                        return Tasks.forException(new Exception("User document reference not found for user: " + userId));
                    }

                    return documentReference.get();
                })
                .onSuccessTask(documentSnapshot -> {

                    if (documentSnapshot == null || !documentSnapshot.exists()) {
                        return Tasks.forResult(false);
                    }

                    User user = documentSnapshot.toObject(User.class);

                    if (user == null || user.getAllianceId() == null || user.getAllianceId().isEmpty()) {
                        return Tasks.forResult(false);
                    }


                    String allianceId = user.getAllianceId();
                    return missionRepository.getActiveMissionForAlliance(allianceId)
                            .onSuccessTask(queryDocumentSnapshots -> {
                                return Tasks.forResult(!queryDocumentSnapshots.isEmpty());
                            });
                });
    }

    public void handleGameEvent(GameEvent event) {
        if (event == null) return;

        Log.d("MissionListener", "Event registered: " + event.getEventType());

        String userId = event.getUserId();

        checkIfUserHasActiveAlliance(userId)
                .addOnSuccessListener(hasActiveMission -> {
                    if (hasActiveMission != null && hasActiveMission) {

                        profileService.getUserData(userId).onSuccessTask(documentReference -> {
                            return documentReference.get();
                        }).addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                if (user != null && user.getAllianceId() != null) {
                                    trackProgress(userId, user.getAllianceId(), event.getEventType());

                                }
                            }
                        });
                    }
                });
    }

    public Task<QuerySnapshot> getAllUserProgressForMission(String missionId) {
        return missionRepository.getAllUserProgressForMission(missionId);
    }

    public Task<AllianceMission> getActiveMission(String allianceId) {

        return missionRepository.getActiveMissionForAlliance(allianceId)
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        return querySnapshot.getDocuments().get(0).toObject(AllianceMission.class);
                    } else {
                        return null;
                    }
                });
    }

}
