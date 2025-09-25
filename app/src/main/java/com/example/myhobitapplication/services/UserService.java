package com.example.myhobitapplication.services;

import com.example.myhobitapplication.databases.ProfileRepository;
import com.example.myhobitapplication.databases.UserRepository;
import com.example.myhobitapplication.models.Profile;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class UserService {
    private final UserRepository repository;
    private final ProfileService profileService;

    private  String userIdd;
    public UserService(){
        this.repository = new UserRepository();
        this.profileService = ProfileService.getInstance();
        this.userIdd="";
    }
    public String getId(){
        return userIdd;
    }
    public Task<Void> updatePass(String pass){
        return repository.changePass(pass);
    }

    public void logout(){
        repository.logout();
    }
   public Task<AuthResult> Login(String email, String password) {
       TaskCompletionSource<AuthResult> taskCompletionSource = new TaskCompletionSource<>();
       repository.mailExistsCheck(email).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               Task<AuthResult> loginTask = repository.authLogin(email, password);
               loginTask.addOnSuccessListener(authResult -> {
                           FirebaseUser user = authResult.getUser();
                           if (user != null && user.isEmailVerified()) {
                               this.userIdd= user.getUid();
                               repository.verificatedCheck(user);
                               taskCompletionSource.setResult(authResult);
                           } else {
                               repository.authSignOut();
                               taskCompletionSource.setException(new Exception("Email not verified"));
                           }

                       })
                       .addOnFailureListener( e ->{
                               taskCompletionSource.setException(new Exception("Bad password, login failed!"));

                       });
           } else {
               taskCompletionSource.setException(task.getException());
           }
       });
       return taskCompletionSource.getTask();
   }

    public Task<DocumentReference> Register(String email, String username, String password, String avatarName, Date registrationDate) {
        final TaskCompletionSource<DocumentReference> taskCompletionSource = new TaskCompletionSource<>();
        repository.checkEmailUnique(email)
                .onSuccessTask(aVoid -> repository.usernameExistsCheck(username))
                .onSuccessTask(aVoid -> repository.authinsert(email, password)).addOnSuccessListener(authResult -> {
                                    FirebaseUser user = authResult.getUser();
                                    if (user != null) {
                                        String uid = user.getUid();
                                        profileService.insert(new Profile(uid));
                                        repository.insert(uid, email, username, avatarName, registrationDate, false)
                                                .addOnSuccessListener(documentReference -> {
                                                    repository.sendVerificationEmail()
                                                            .addOnSuccessListener(aVoid -> {
                                                                taskCompletionSource.setResult(documentReference);
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                taskCompletionSource.setException(new Exception("Failed to send verification email: " + e.getMessage()));
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    taskCompletionSource.setException(e);
                                                });
                                    } else {
                                        taskCompletionSource.setException(new Exception("Authentication succeeded but user is null."));
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    taskCompletionSource.setException(e);
                                });
        return taskCompletionSource.getTask();
    }
}
