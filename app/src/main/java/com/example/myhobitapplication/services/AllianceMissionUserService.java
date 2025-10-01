package com.example.myhobitapplication.services;

import android.util.Log;

import com.example.myhobitapplication.enums.MissionStatus;
import com.example.myhobitapplication.models.AllianceMission;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class AllianceMissionUserService {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final UserService userService;
    private final BattleService battleService;
    private final ProfileService profileService;

    public AllianceMissionUserService(UserService userService, BattleService battleService) {
        this.userService = userService;
        this.battleService = battleService;
        this.profileService = ProfileService.getInstance();
    }


    public Task<Void> processMissionCompletion(String missionId) {
        DocumentReference missionRef = db.collection("allianceMissions").document(missionId);

        return missionRef.get().onSuccessTask(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                return Tasks.forException(new Exception("Misija ne postoji."));
            }

            AllianceMission mission = documentSnapshot.toObject(AllianceMission.class);

            // Provjeri je li boss poražen
            if (mission.getCurrentBossHp() <= 0) {
                Log.d("MissionCompletion", "Misija uspješna! Pripremam nagrade.");
                mission.setStatus(MissionStatus.FINISHED_SUCCESS);

                // 1. Prvo dohvati listu User objekata (članova)
                return userService.getAllAllianceMember(mission.getAllianceId())
                        .onSuccessTask(members -> {

                            // 2. Kreiraj listu asinkronih Task-ova, gdje je svaki task
                            //    dohvaćanje JEDNOG Profile dokumenta.
                            List<Task<Profile>> profileTasks = new ArrayList<>();
                            for (User member : members) {
                                profileTasks.add(profileService.getProfileById(member.getUid()));
                            }

                            // 3. Izvrši SVE Task-ove za dohvaćanje profila paralelno
                            return Tasks.whenAllSuccess(profileTasks)
                                    .onSuccessTask(profileObjects -> {
                                        // 'profileObjects' je sada List<Object>, moramo ga castati.
                                        // Ovaj blok se izvršava tek kada su SVI profili dohvaćeni.

                                        WriteBatch batch = db.batch();

                                        // 4. Sada prolazimo kroz listu dohvaćenih profila
                                        for (Object obj : profileObjects) {
                                            Profile profile = (Profile) obj;
                                            if (profile == null) continue; // Preskoči ako profil nije pronađen

                                            // --- SADA IMAMO SVE PODATKE ZA JEDNOG KORISNIKA ---

                                            // a) Dodaj napitak (vaša logika)
                                            // b) Dodaj nasumični komad odjeće
                                            //    (Ovo ne može biti u batch-u ako uključuje logiku,
                                            //     mora se izvršiti odvojeno ili preko Cloud Function)
                                            //    userEquipmentService.gainRandomClothing(profile.getUserId());

                                            // c) Izračunaj i dodaj 50% novčića
                                            //    Koristimo SADA AŽURNI nivo iz Profile objekta
                                            int nextBossReward = battleService.calculateCoinsRewardForBoss(profile.getlevel() + 1, profile.getuserUid());
                                            int missionCoinReward = nextBossReward / 2;

                                            DocumentReference profileRef = db.collection("profiles").document(profile.getuserUid());
                                            batch.update(profileRef, "coins", FieldValue.increment(missionCoinReward));

                                            // d) Ažuriraj bedž (npr. inkrementiraj polje 'completedMissions')
                                            batch.update(profileRef, "completedMissions", FieldValue.increment(1));
                                        }

                                        // 5. Na kraju, ažuriraj i status same misije
                                        batch.update(missionRef, "status", mission.getStatus());

                                        // 6. Izvrši sve promjene odjednom
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
}
