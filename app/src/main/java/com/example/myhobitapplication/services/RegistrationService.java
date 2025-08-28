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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationService {
    private final RegistrationRepository repository;
    public RegistrationService(RegistrationRepository repository){
        this.repository = repository;
    }


    public Task<DocumentReference> Register(String email, String username, String password, String avatarName){
     return repository.insert(email,username,password,avatarName);

    }
}
