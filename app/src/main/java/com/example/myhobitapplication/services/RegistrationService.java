package com.example.myhobitapplication.services;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.RegistrationRepository;
import com.example.myhobitapplication.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.time.LocalDateTime;
import java.util.Date;

public class RegistrationService {
    private final RegistrationRepository repository;
    public RegistrationService(RegistrationRepository repository){
        this.repository = repository;
    }


    public Task<AuthResult> Login(String email, String password) {
        Task<AuthResult> loginTask = repository.authLogin(email, password);
        loginTask.addOnSuccessListener(authResult -> repository.verificatedCheck());
        return loginTask;
    }
    public Task<DocumentReference> Register(String email, String username, String password, String avatarName, Date registrationDate) {
        final TaskCompletionSource<DocumentReference> taskCompletionSource = new TaskCompletionSource<>();
        repository.authinsert(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        String uid = user.getUid();
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
                        //FirebaseAuth.getInstance().signOut();
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
