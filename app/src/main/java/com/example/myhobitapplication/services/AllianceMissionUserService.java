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

    private final UserEquipmentService userEquipmentService;

    public AllianceMissionUserService(UserService userService, BattleService battleService, UserEquipmentService userEquipmentService) {
        this.userService = userService;
        this.battleService = battleService;
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
                mission.setStatus(MissionStatus.FINISHED_SUCCESS); // Koristimo String

                // 1. Dohvati listu User objekata (članova)
                return userService.getAllAllianceMember(mission.getAllianceId())
                        .onSuccessTask(members -> {

                            // 1. Kreiraj zajedničku listu za SVE Task-ove
                            List<Task<?>> allTasks = new ArrayList<>();

                            // 2. Kreiraj i dodaj sve Task-ove za dohvaćanje profila
                            List<Task<Profile>> profileTasks = new ArrayList<>();
                            for (User member : members) {
                                Task<Profile> profileTask = profileService.getProfileById(member.getUid());
                                profileTasks.add(profileTask);
                                allTasks.add(profileTask); // Dodaj u zajedničku listu
                            }

                            // 3. Kreiraj i dodaj Task za dohvaćanje SVIH UserMission progressa
                            Task<QuerySnapshot> progressTask = missionRef.collection("userProgress").get();
                            allTasks.add(progressTask); // Dodaj u zajedničku listu

                            // 4. Sada pozovi whenAllComplete s tom JEDNOM, zajedničkom listom
                            return Tasks.whenAllComplete(allTasks)
                                    .onSuccessTask(tasks -> {

                                        // 5. Obradi rezultate: Kreiraj mapu napretka
                                        QuerySnapshot progressSnapshots = (QuerySnapshot) progressTask.getResult();
                                        Map<String, UserMission> progressMap = new HashMap<>();
                                        for (DocumentSnapshot doc : progressSnapshots) {
                                            progressMap.put(doc.getId(), doc.toObject(UserMission.class));
                                        }

                                        WriteBatch batch = db.batch();
                                        Date dateEarned = mission.getEndDate(); // Datum kada je misija završila

                                        // 6. Sada prolazimo kroz dohvaćene profile
                                        for (Task<Profile> profileTask : profileTasks) {
                                            Profile profile = profileTask.getResult();
                                            if (profile == null) continue;

                                            UserMission progress = progressMap.get(profile.getuserUid());
                                            if (progress == null) continue;

                                            // --- POČETAK LOGIKE ZA BEDŽEVE ---

                                            // a) Izračunaj ukupan broj akcija iz UserMission objekta
                                            int totalActions = Math.abs(progress.getTotalDamage());
                                            // ... Ovdje dodajte i ostale brojače ako ih imate ...

                                            // b) Odredi tip bedža
                                            String badgeType;
                                            if (totalActions >= 25) {
                                                badgeType = "GOLD";
                                            } else if (totalActions >= 10) {
                                                badgeType = "SILVER";
                                            } else if (totalActions > 0) {
                                                badgeType = "BRONZE";
                                            } else {
                                                badgeType = null; // Korisnik nije ništa uradio, ne dobiva bedž
                                            }

                                            // c) Ako je korisnik zaslužio bedž, dodaj ga u batch
                                            if (badgeType != null) {
                                                Badge newBadge = new Badge(badgeType, totalActions, dateEarned, missionId);
                                                DocumentReference profileRefForBadge = db.collection("profiles").document(profile.getuserUid());
                                                batch.update(profileRefForBadge, "badges", FieldValue.arrayUnion(newBadge));
                                            }

                                            // --- KRAJ LOGIKE ZA BEDŽEVE ---

                                            // --- LOGIKA ZA OSTALE NAGRADE (ostaje ista) ---
                                            int nextBossReward = battleService.calculateCoinsRewardForBoss(profile.getlevel() + 1, profile.getuserUid());
                                            int missionCoinReward = nextBossReward / 2;

                                            DocumentReference profileRefForCoins = db.collection("profiles").document(profile.getuserUid());
                                            batch.update(profileRefForCoins, "coins", FieldValue.increment(missionCoinReward));

                                            // Ovdje ide logika za napitak i odjeću...
                                        }

                                        // 7. Ažuriraj status misije i izvrši sve operacije
                                        batch.update(missionRef, "status", mission.getStatus());
                                        return batch.commit();
                                    });
                        });

            } else {
                // NEUSPJEH
                Log.d("MissionCompletion", "Misija neuspješna.");
                mission.setStatus(MissionStatus.FINISHED_FAILURE);
                return missionRef.update("status", mission.getStatus());
            }
        });
    }
    public Task<Void> processMissionCompletionTest(String missionId) {
        DocumentReference missionRef = db.collection("allianceMissions").document(missionId);

        return missionRef.get().onSuccessTask(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                return Tasks.forException(new Exception("Misija ne postoji."));
            }

            AllianceMission mission = documentSnapshot.toObject(AllianceMission.class);

            // Koristite `if(true)` samo za testiranje.
            // U produkciji, koristite: if (mission != null && mission.getCurrentBossHp() <= 0)
            if (true) {
                Log.d("MissionCompletion", "Misija uspješna! Pripremam nagrade i bedževe.");
                mission.setStatus(MissionStatus.FINISHED_SUCCESS); // Koristi se vaš ENUM

                // 1. Dohvati listu User objekata (članova)
                return userService.getAllAllianceMember(mission.getAllianceId())
                        .onSuccessTask(members -> {
                            if (members == null || members.isEmpty()) {
                                Log.w("MissionCompletion", "Nema članova za nagrađivanje.");
                                return missionRef.update("status", mission.getStatus());
                            }

                            // 2. Pripremi liste Task-ova za paralelno dohvaćanje
                            List<Task<?>> allTasks = new ArrayList<>();
                            List<Task<QuerySnapshot>> profileQueryTasks = new ArrayList<>();

                            for (User member : members) {
                                Task<QuerySnapshot> queryTask = db.collection("profiles")
                                        .whereEqualTo("userUid", member.getUid()) // Pretraga po polju
                                        .limit(1)
                                        .get();
                                profileQueryTasks.add(queryTask);
                                allTasks.add(queryTask);
                            }

                            Task<QuerySnapshot> progressTask = missionRef.collection("userProgress").get();
                            allTasks.add(progressTask);

                            // 3. Pričekaj da se završe SVI upiti
                            return Tasks.whenAllComplete(allTasks)
                                    .onSuccessTask(tasks -> {

                                        // 4. Obradi rezultate napretka (progress)
                                        Map<String, UserMission> progressMap = new HashMap<>();
                                        QuerySnapshot progressSnapshots = progressTask.getResult();
                                        if (progressSnapshots != null) {
                                            for (DocumentSnapshot doc : progressSnapshots) {
                                                progressMap.put(doc.getId(), doc.toObject(UserMission.class));
                                            }
                                        }

                                        WriteBatch batch = db.batch();
                                        Date dateEarned = mission.getEndDate();

                                        // 5. Obradi rezultate profila i pripremi batch
                                        for (Task<QuerySnapshot> profileQueryTask : profileQueryTasks) {
                                            if (!profileQueryTask.isSuccessful() || profileQueryTask.getResult() == null || profileQueryTask.getResult().isEmpty()) {
                                                Log.w("MissionCompletion", "Profil za jednog člana nije pronađen.");
                                                continue;
                                            }

                                            DocumentSnapshot profileDoc = profileQueryTask.getResult().getDocuments().get(0);
                                            Profile profile = profileDoc.toObject(Profile.class);

                                            UserMission progress = progressMap.get(profile.getuserUid());
                                            if (progress == null) continue;

                                            // --- Logika za bedževe ---
                                            int totalActions = progress.getPurchaseCount() + progress.getSuccessfulAttackCount() + progress.getEasyTaskCompleteCount() + progress.getHardTaskCompleteCount();

                                            String badgeType = null;
                                            if (totalActions >= 25) {
                                                badgeType = "GOLD";
                                            } else if (totalActions >= 10) {
                                                badgeType = "SILVER";
                                            } else if (totalActions >= 0) {
                                                badgeType = "BRONZE";
                                            }

                                            if (badgeType != null) {
                                                Badge newBadge = new Badge(badgeType, totalActions, dateEarned, missionId);
                                                batch.update(profileDoc.getReference(), "badges", FieldValue.arrayUnion(newBadge));
                                            }

                                            // --- Logika za novčiće ---
                                            int nextBossReward = battleService.calculateCoinsRewardForBoss(profile.getlevel() + 1, profile.getuserUid());
                                            int missionCoinReward = nextBossReward / 2;
                                            batch.update(profileDoc.getReference(), "coins", FieldValue.increment(missionCoinReward));

                                           userEquipmentService.grantRandomClothingToUser(profile.getuserUid());
                                        }

                                        // 6. Ažuriraj status misije i izvrši sve
                                        batch.update(missionRef, "status", mission.getStatus());
                                        return batch.commit();
                                    });
                        });

            } else {
                // NEUSPJEH
                Log.d("MissionCompletion", "Misija neuspješna.");
                mission.setStatus(MissionStatus.FINISHED_FAILURE); // Koristi se vaš ENUM
                return missionRef.update("status", mission.getStatus());
            }
        });
    }
}
