package com.example.myhobitapplication.services;

import android.util.Log;

import com.example.myhobitapplication.enums.MissionStatus;
import com.example.myhobitapplication.models.AllianceMission;
import com.example.myhobitapplication.models.Badge;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.models.UserMission;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllianceMissionUserService {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final UserService userService;
    private final BattleService battleService;
    private final ProfileService profileService;
    private final TaskService taskService;

    private final UserEquipmentService userEquipmentService;

    public AllianceMissionUserService(UserService userService, BattleService battleService, TaskService taskService, UserEquipmentService userEquipmentService) {
        this.userService = userService;
        this.battleService = battleService;
        this.taskService = taskService;
        this.userEquipmentService = userEquipmentService;
        this.profileService = ProfileService.getInstance();
    }


    // U AllianceMissionService.java

    public Task<Void> processMissionCompletion(String missionId) {
        DocumentReference missionRef = db.collection("allianceMissions").document(missionId);

        return missionRef.get().onSuccessTask(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                return Tasks.forException(new Exception("Misija ne postoji."));
            }

            AllianceMission mission = documentSnapshot.toObject(AllianceMission.class);

            if (mission.getCurrentBossHp() <= 0) {
                Log.d("MissionCompletion", "Misija uspješna! Pripremam nagrade i bedževe.");
                mission.setStatus(MissionStatus.FINISHED_SUCCESS);


                return userService.getAllAllianceMember(mission.getAllianceId())
                        .onSuccessTask(members -> {


                            List<Task<?>> allTasks = new ArrayList<>();


                            List<Task<Profile>> profileTasks = new ArrayList<>();
                            for (User member : members) {
                                Task<Profile> profileTask = profileService.getProfileById(member.getUid());
                                profileTasks.add(profileTask);
                                allTasks.add(profileTask);
                            }


                            Task<QuerySnapshot> progressTask = missionRef.collection("userProgress").get();
                            allTasks.add(progressTask);


                            return Tasks.whenAllComplete(allTasks)
                                    .onSuccessTask(tasks -> {


                                        QuerySnapshot progressSnapshots = (QuerySnapshot) progressTask.getResult();
                                        Map<String, UserMission> progressMap = new HashMap<>();
                                        for (DocumentSnapshot doc : progressSnapshots) {
                                            progressMap.put(doc.getId(), doc.toObject(UserMission.class));
                                        }

                                        WriteBatch batch = db.batch();
                                        Date dateEarned = mission.getEndDate();


                                        for (Task<Profile> profileTask : profileTasks) {
                                            Profile profile = profileTask.getResult();
                                            if (profile == null) continue;

                                            UserMission progress = progressMap.get(profile.getuserUid());
                                            if (progress == null) continue;

                                            int totalActions = Math.abs(progress.getTotalDamage());

                                            String badgeType;
                                            if (totalActions >= 25) {
                                                badgeType = "GOLD";
                                            } else if (totalActions >= 10) {
                                                badgeType = "SILVER";
                                            } else if (totalActions > 0) {
                                                badgeType = "BRONZE";
                                            } else {
                                                badgeType = null;
                                            }


                                            if (badgeType != null) {
                                                Badge newBadge = new Badge(badgeType, totalActions, dateEarned, missionId);
                                                DocumentReference profileRefForBadge = db.collection("profiles").document(profile.getuserUid());
                                                batch.update(profileRefForBadge, "badges", FieldValue.arrayUnion(newBadge));
                                            }


                                            int nextBossReward = battleService.calculateCoinsRewardForBoss(profile.getlevel() + 1, profile.getuserUid());
                                            int missionCoinReward = nextBossReward / 2;

                                            DocumentReference profileRefForCoins = db.collection("profiles").document(profile.getuserUid());
                                            batch.update(profileRefForCoins, "coins", FieldValue.increment(missionCoinReward));


                                        }


                                        batch.update(missionRef, "status", mission.getStatus());
                                        return batch.commit();
                                    });
                        });

            } else {

                Log.d("MissionCompletion", "Misija neuspješna.");
                mission.setStatus(MissionStatus.FINISHED_FAILURE);
                return missionRef.update("status", mission.getStatus());
            }
        });
    }
//    public Task<Void> processMissionCompletionTest(String missionId) {
//        DocumentReference missionRef = db.collection("allianceMissions").document(missionId);
//
//        return missionRef.get().onSuccessTask(documentSnapshot -> {
//            if (!documentSnapshot.exists()) {
//                return Tasks.forException(new Exception("Misija ne postoji."));
//            }
//
//            AllianceMission mission = documentSnapshot.toObject(AllianceMission.class);
//
//
//            // ne zaboravi promijeniti na if (mission != null && mission.getCurrentBossHp() <= 0)
//            if (true) {
//                Log.d("MissionCompletion", "Misija uspješna! Pripremam nagrade i bedževe.");
//                mission.setStatus(MissionStatus.FINISHED_SUCCESS);
//
//
//                return userService.getAllAllianceMember(mission.getAllianceId())
//                        .onSuccessTask(members -> {
//                            if (members == null || members.isEmpty()) {
//                                Log.w("MissionCompletion", "Nema članova za nagrađivanje.");
//                                return missionRef.update("status", mission.getStatus());
//                            }
//
//
//                            List<Task<?>> allTasks = new ArrayList<>();
//                            List<Task<QuerySnapshot>> profileQueryTasks = new ArrayList<>();
//
//                            for (User member : members) {
//                                Task<QuerySnapshot> queryTask = db.collection("profiles")
//                                        .whereEqualTo("userUid", member.getUid())
//                                        .limit(1)
//                                        .get();
//                                profileQueryTasks.add(queryTask);
//                                allTasks.add(queryTask);
//                            }
//
//                            Task<QuerySnapshot> progressTask = missionRef.collection("userProgress").get();
//                            allTasks.add(progressTask);
//
//                            return Tasks.whenAllComplete(allTasks)
//                                    .onSuccessTask(tasks -> {
//
//
//                                        Map<String, UserMission> progressMap = new HashMap<>();
//                                        QuerySnapshot progressSnapshots = progressTask.getResult();
//                                        if (progressSnapshots != null) {
//                                            for (DocumentSnapshot doc : progressSnapshots) {
//                                                progressMap.put(doc.getId(), doc.toObject(UserMission.class));
//                                            }
//                                        }
//
//                                        WriteBatch batch = db.batch();
//                                        Date dateEarned = mission.getEndDate();
//
//                                        for (Task<QuerySnapshot> profileQueryTask : profileQueryTasks) {
//                                            if (!profileQueryTask.isSuccessful() || profileQueryTask.getResult() == null || profileQueryTask.getResult().isEmpty()) {
//                                                Log.w("MissionCompletion", "Profil za jednog člana nije pronađen.");
//                                                continue;
//                                            }
//
//                                            DocumentSnapshot profileDoc = profileQueryTask.getResult().getDocuments().get(0);
//                                            Profile profile = profileDoc.toObject(Profile.class);
//
//                                            UserMission progress = progressMap.get(profile.getuserUid());
//                                            if (progress == null) continue;
//
//
//
//                                            int completedTaskCategories = calculateCompletedSpecialTasks(progress);
//
//                                            String badgeType = null;
//                                            if (completedTaskCategories >= 5) {
//                                                badgeType = "GOLD";
//                                            } else if (completedTaskCategories >= 3) {
//                                                badgeType = "SILVER";
//                                            } else if (completedTaskCategories > 0) {
//                                                badgeType = "BRONZE";
//                                            }
//
//                                            if (badgeType != null) {
//                                                Badge newBadge = new Badge(badgeType, completedTaskCategories, dateEarned, missionId);
//                                                batch.update(profileDoc.getReference(), "badges", FieldValue.arrayUnion(newBadge));
//                                            }
//
//                                            int nextBossReward = battleService.calculateCoinsRewardForBoss(profile.getlevel() + 1, profile.getuserUid());
//                                            int missionCoinReward = nextBossReward / 2;
//                                            batch.update(profileDoc.getReference(), "coins", FieldValue.increment(missionCoinReward));
//
//                                           userEquipmentService.grantRandomClothingToUser(profile.getuserUid());
//                                        }
//
//
//                                        batch.update(missionRef, "status", mission.getStatus());
//                                        return batch.commit();
//                                    });
//                        });
//
//            } else {
//
//                Log.d("MissionCompletion", "Misija neuspješna.");
//                mission.setStatus(MissionStatus.FINISHED_FAILURE);
//                return missionRef.update("status", mission.getStatus());
//            }
//        });
//    }

    public Task<Void> processMissionCompletionTest(String missionId) {
        DocumentReference missionRef = db.collection("allianceMissions").document(missionId);


        return missionRef.get().onSuccessTask(missionSnapshot -> {
            if (!missionSnapshot.exists()) {
                return Tasks.forException(new Exception("Misija s ID-jem " + missionId + " ne postoji."));
            }
            AllianceMission mission = missionSnapshot.toObject(AllianceMission.class);
            String allianceId = mission.getAllianceId();

            return userService.getAllAllianceMember(allianceId)
                    .onSuccessTask(members -> {
                        if (members == null || members.isEmpty()) {
                            Log.w("MissionCompletion", "Nema članova, misija se završava kao neuspješna.");
                            return missionRef.update("status", MissionStatus.FINISHED_FAILURE);
                        }


                        WriteBatch initialBatch = db.batch();
                        int totalBonusDamage = 0;

                        for (User member : members) {

                            int overdueTasksCount = taskService.countAllOverdueTasks(member.getUid());


                            DocumentReference userProgressRef = missionRef.collection("userProgress").document(member.getUid());
                            initialBatch.update(userProgressRef, "uncompletedTasksCount", overdueTasksCount);

                            if (overdueTasksCount == 0) {
                                totalBonusDamage += 10;
                            }
                        }


                        if (totalBonusDamage > 0) {
                            initialBatch.update(missionRef, "currentBossHp", FieldValue.increment(-totalBonusDamage));
                        }


                        return initialBatch.commit().onSuccessTask(aVoid -> {


                            return missionRef.get().onSuccessTask(updatedMissionSnapshot -> {
                                AllianceMission finalMission = updatedMissionSnapshot.toObject(AllianceMission.class);

                                if (finalMission.getCurrentBossHp() <= 0) {
                                    Log.d("MissionCompletion", "Finalni HP bosa je <= 0. Misija uspješna.");
                                    return grantRewards(finalMission, members);
                                } else {
                                    Log.d("MissionCompletion", "Finalni HP bosa (" + finalMission.getCurrentBossHp() + ") je > 0. Misija neuspješna.");
                                    return missionRef.update("status", MissionStatus.FINISHED_FAILURE);
                                }
                            });
                        });
                    });
        });
    }


    private Task<Void> grantRewards(AllianceMission mission, List<User> members) {
        DocumentReference missionRef = db.collection("allianceMissions").document(mission.getId());

        List<Task<?>> allTasks = new ArrayList<>();
        List<Task<QuerySnapshot>> profileQueryTasks = new ArrayList<>();
        for (User member : members) {
            Task<QuerySnapshot> queryTask = db.collection("profiles")
                    .whereEqualTo("userUid", member.getUid()).limit(1).get();
            profileQueryTasks.add(queryTask);
            allTasks.add(queryTask);
        }
        Task<QuerySnapshot> progressTask = missionRef.collection("userProgress").get();
        allTasks.add(progressTask);

        return Tasks.whenAllComplete(allTasks)
                .onSuccessTask(tasks -> {
                    Map<String, UserMission> progressMap = new HashMap<>();
                    QuerySnapshot progressSnapshots = progressTask.getResult();
                    if (progressSnapshots != null) {
                        for (DocumentSnapshot doc : progressSnapshots) {
                            progressMap.put(doc.getId(), doc.toObject(UserMission.class));
                        }
                    }

                    WriteBatch batch = db.batch();
                    Date dateEarned = mission.getEndDate();

                    for (Task<QuerySnapshot> profileQueryTask : profileQueryTasks) {
                        if (!profileQueryTask.isSuccessful() || profileQueryTask.getResult() == null || profileQueryTask.getResult().isEmpty()) {
                            continue;
                        }

                        DocumentSnapshot profileDoc = profileQueryTask.getResult().getDocuments().get(0);
                        Profile profile = profileDoc.toObject(Profile.class);
                        UserMission progress = progressMap.get(profile.getuserUid());
                        if (progress == null) continue;


                        int totalActions = calculateCompletedSpecialTasks(progress);
                        String badgeType = null;
                        if (totalActions >= 5) badgeType = "GOLD";
                        else if (totalActions >= 3) badgeType = "SILVER";
                        else if (totalActions > 0) badgeType = "BRONZE";

                        if (badgeType != null) {
                            Badge newBadge = new Badge(badgeType, totalActions, dateEarned, mission.getId());
                            batch.update(profileDoc.getReference(), "badges", FieldValue.arrayUnion(newBadge));
                        }


                        int nextBossReward = battleService.calculateCoinsRewardForBoss(profile.getlevel(), profile.getuserUid());
                        int missionCoinReward = nextBossReward / 2;
                        batch.update(profileDoc.getReference(), "coins", FieldValue.increment(missionCoinReward));

                         userEquipmentService.grantRandomClothingToUser(profile.getuserUid());
                    }

                    batch.update(missionRef, "status", MissionStatus.FINISHED_SUCCESS);
                    return batch.commit();
                });
    }


    private int calculateCompletedSpecialTasks(UserMission progress) {
        if (progress == null) {
            return 0;
        }

        int completedCategories = 0;


        final int SHOP_LIMIT = 5;
        final int BOSS_HIT_LIMIT = 10;
        final int EASY_TASK_LIMIT = 10;
        final int HARD_TASK_LIMIT = 6;
        final int MESSAGE_LIMIT = 14;

        if (progress.getPurchaseCount() >= SHOP_LIMIT) {
            completedCategories++;
        }


        if (progress.getSuccessfulAttackCount() >= BOSS_HIT_LIMIT) {
            completedCategories++;
        }

        if (progress.getEasyTaskCompleteCount() >= EASY_TASK_LIMIT) {
            completedCategories++;
        }


        if (progress.getHardTaskCompleteCount() >= HARD_TASK_LIMIT) {
            completedCategories++;
        }
        if (progress.getMessageCount() >= MESSAGE_LIMIT) {
            completedCategories++;
        }

        if (progress.getUncompletedTasksCount() == 0) {
            completedCategories++;
        }

        return completedCategories;
    }
}
