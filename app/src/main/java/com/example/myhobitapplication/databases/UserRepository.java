package com.example.myhobitapplication.databases;

import android.util.Log;

import com.example.myhobitapplication.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth firebaseAuth;
    private final CollectionReference usersCollection;

    private final ProfileRepository profileRepo;

    public UserRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        profileRepo = new ProfileRepository();
        usersCollection = db.collection("users");
    }
    public Task<DocumentReference> getUserInfo(String uid){
        final TaskCompletionSource<DocumentReference> taskCompletionSource = new TaskCompletionSource<>();
        usersCollection.whereEqualTo("uid", uid).get().addOnSuccessListener(queryDocumentSnapshots ->{
            if(!queryDocumentSnapshots.isEmpty())
            taskCompletionSource.setResult(queryDocumentSnapshots.getDocuments().get(0).getReference());
        }) .addOnFailureListener(e -> taskCompletionSource.setException(new Exception("Coudnt retrieve user data")));
        return taskCompletionSource.getTask();
    }

    public Task<DocumentReference> mailExistsCheck(String email){
        final TaskCompletionSource<DocumentReference> taskCompletionSource = new TaskCompletionSource<>();
        usersCollection.whereEqualTo("email", email).get().addOnSuccessListener(queryDocumentSnapshots ->{
            if(queryDocumentSnapshots.isEmpty()){
                taskCompletionSource.setException(new Exception("Wrong email"));
            }else taskCompletionSource.setResult(queryDocumentSnapshots.getDocuments().get(0).getReference());
        }) .addOnFailureListener(e -> taskCompletionSource.setException(new Exception("Wrong email")));
        return taskCompletionSource.getTask();
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

    public void authSignOut(){
         FirebaseAuth.getInstance().signOut();
    }
    public Task<AuthResult> authLogin(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }
    public Task<Void> changePass(String pass) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String newPassword = pass;
        return user.updatePassword(newPassword);
    }
    public void logout(){
        firebaseAuth.signOut();
    }
    public void verificatedCheck(FirebaseUser firebaseUser) {
        usersCollection
                .whereEqualTo("uid", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        User user = document.toObject(User.class);
                        if(!user.getRegistered()) {
                            Log.d("Firestore", "User found: " + user.getusername());

                            Date sentTime = user.getRegistrationDate();
                            LocalDateTime currentTime = LocalDateTime.now();
                            Date dateNow = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());

                            long twentyFourHoursInMillis = 24 * 60 * 60 * 1000L;
                            Date sentTimePlus24h = new Date(sentTime.getTime() + twentyFourHoursInMillis);
                            //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (firebaseUser != null && !firebaseUser.isEmailVerified()
                                    && dateNow.after(sentTimePlus24h)) {
                                firebaseUser.delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Auth", "User deleted");
                                        })
                                        .addOnFailureListener(e -> Log.e("Auth", "Failed to delete user", e));
                                document.getReference().delete();
                                profileRepo.delete(firebaseUser.getUid());
                                firebaseAuth.signOut();
                            } else {
                                user.setRegistered(true);
                                usersCollection.document(document.getId())
                                        .update("isRegistered", true);
                            }
                        }
                    } else {
                        Log.d("Firestore", "No user found with UID: " + firebaseAuth.getCurrentUser().getUid());
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error getting user", e));
    }



    public Task<DocumentReference> usernameExistsCheck(String username){
        final TaskCompletionSource<DocumentReference> taskCompletionSource = new TaskCompletionSource<>();
        usersCollection.whereEqualTo("username", username).get().addOnSuccessListener(queryDocumentSnapshots ->{
            if(!queryDocumentSnapshots.isEmpty()){
                taskCompletionSource.setException(new Exception("Account with this username exists!"));
            }else taskCompletionSource.setResult(null);
        }) .addOnFailureListener(e -> taskCompletionSource.setResult(null));
        return taskCompletionSource.getTask();
    }

    public Task<Void> checkEmailUnique(String email){
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        usersCollection.whereEqualTo("email", email).get()
                .addOnSuccessListener(qs -> {
                    if(qs.isEmpty()){
                        tcs.setResult(null); // email is unique
                    } else {
                        tcs.setException(new Exception("Account with this email already exists!"));
                    }
                })
                .addOnFailureListener(e -> tcs.setException(e));
        return tcs.getTask();
    }


}

