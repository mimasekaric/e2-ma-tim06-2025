package com.example.myhobitapplication.databases;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myhobitapplication.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationRepository {

    private final FirebaseFirestore db;
    private final CollectionReference usersCollection;
    public RegistrationRepository() {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users2");
    }
    public Task<DocumentReference> insert(String email, String username, String password, String avatarName) {
        Map<String, Object> user2 = new HashMap<>();
        user2.put("email", email);
        user2.put("username", username);
        user2.put("password", password);
        user2.put("avatarName", avatarName);
        return usersCollection
                .add(user2);

                /*.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });}*/
    }

}
