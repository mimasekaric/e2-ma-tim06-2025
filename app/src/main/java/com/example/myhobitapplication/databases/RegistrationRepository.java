package com.example.myhobitapplication.databases;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myhobitapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegistrationRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth firebaseAuth;
    private final CollectionReference usersCollection;

    public RegistrationRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
    }

    public Task<DocumentReference> insert(String uid, String email, String username, String avatarName, Date registrationDate, Boolean isRegistered) {
        Map<String, Object> user2 = new HashMap<>();
        user2.put("uid", uid);
        user2.put("email", email);
        user2.put("username", username);
        user2.put("avatarName", avatarName);
        user2.put("registrationDate", registrationDate);
        user2.put("isRegistered", isRegistered);
        return usersCollection
                .add(user2);
    }

    public Task<AuthResult> authinsert(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> sendVerificationEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            return user.sendEmailVerification();
        }
        return null;
    }

    public Task<AuthResult> authLogin(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public void verificatedCheck() {
        usersCollection
                .whereEqualTo("uid", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        User user = document.toObject(User.class);

                        Log.d("Firestore", "User found: " + user.getusername());


                       Date sentTime = user.getRegistrationDate();
                        LocalDateTime currentTime = LocalDateTime.now();
                        Date dateNow = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());

                        long twentyFourHoursInMillis = 24 * 60 * 60 * 1000L;
                        Date sentTimePlus24h = new Date(sentTime.getTime() + twentyFourHoursInMillis);


                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null && !firebaseUser.isEmailVerified()
                                && dateNow.after(sentTimePlus24h)) {
                            firebaseUser.delete()
                                    .addOnSuccessListener(aVoid -> {Log.d("Auth", "User deleted");  })
                                    .addOnFailureListener(e -> Log.e("Auth", "Failed to delete user", e));
                            document.getReference().delete();
                            firebaseAuth.signOut();
                        }else{
                            user.setRegistered(true);
                        }

                    } else {
                        Log.d("Firestore", "No user found with UID: " + firebaseAuth.getCurrentUser().getUid());
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error getting user", e));
    }

}

