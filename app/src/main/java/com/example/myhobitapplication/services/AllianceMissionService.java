package com.example.myhobitapplication.services;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.myhobitapplication.databases.AllianceMissionRepository;
import com.example.myhobitapplication.enums.MissionStatus;
import com.example.myhobitapplication.events.GameEvent;
import com.example.myhobitapplication.models.AllianceMission;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.models.UserMission;
import com.example.myhobitapplication.workers.MissionCompletionWorker;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public Task<Void> startMissionForAlliance(String allianceId, List<String> memberIds, Context context) {

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

        return missionRepository.createMission(newMission, initialProgressList)
                .onSuccessTask(aVoid -> {

                    long nowMillis = System.currentTimeMillis();
                    long endDateMillis = newMission.getEndDate().getTime();
                    long delayInMillis = endDateMillis - nowMillis;

                    if (delayInMillis < 0) {
                        delayInMillis = 0;
                    }


                    Data inputData = new Data.Builder()
                            .putString(MissionCompletionWorker.KEY_MISSION_ID, newMission.getId())
                            .build();

                    OneTimeWorkRequest completionWorkRequest =
                            new OneTimeWorkRequest.Builder(MissionCompletionWorker.class)
                                    .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                                    .setInputData(inputData)
                                    .build();

                    WorkManager.getInstance(context)
                            .enqueue(completionWorkRequest);

                    Log.d("MissionStart", "Worker for mission finish is scheduled for " + newMission.getEndDate());

                    return Tasks.forResult(null);
                });
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
                                break;
                            case BUY_FROM_SHOP:
                                currentCount = userProgress.getPurchaseCount();
                                break;
                        }


                        if (currentCount >= rule.limit) {
                            return null;
                        }

                        int incrementValue = 1;

                        if (eventType == MissionEventType.VERY_EASY_TASK_COMPLETED) {
                            incrementValue = 2;

                            if (currentCount + incrementValue > rule.limit) {

                                incrementValue = 1;
                            }
                        }

                        transaction.update(missionRef, "currentBossHp", FieldValue.increment(-rule.damage));
                        transaction.update(userProgressRef, rule.fieldToIncrement, FieldValue.increment(incrementValue));
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

    public ListenerRegistration listenForActiveMission(String allianceId, EventListener<QuerySnapshot> listener) {
        return missionRepository.listenForActiveMission(allianceId, listener);
    }

    public ListenerRegistration listenForAllUserProgress(String missionId, EventListener<QuerySnapshot> listener) {
        return missionRepository.listenForAllUserProgress(missionId, listener);
    }


}
