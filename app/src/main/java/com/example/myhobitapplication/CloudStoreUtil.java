package com.example.myhobitapplication;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import com.example.myhobitapplication.models.User;

public class CloudStoreUtil {
    static String usersId_Milica;
    static String usersId_Ivana;

    public static void initDB(){
        User user1 = new User("mima@gmail.com" , "mima", "MIMA123", 1);
        Map<String, Object> user2 = new HashMap<>();
        user2.put("email", "jelen.ica@gmail.com");
        user2.put("username", "jelena");
        user2.put("password", "jelena123");
        user2.put("avatarId", 1);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .add(user1)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        usersId_Milica = documentReference.getId();
                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
        db.collection("userss")
                .add(user2)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        usersId_Ivana = documentReference.getId();

                        Log.d("REZ_DB", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("REZ_DB", "Error adding document", e);
                    }
                });
    }

    public static void insert(){
        User user1 = new User("mita1@gmaqil.com", "mitar2001", "Kovacevic1", 2);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("userss")
                .add(user1)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
                });
    }
/*
    public static void select(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("REZ_DB", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("REZ_DB", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    public static void update(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // izmena dokumenta s ID-em "1wUqKBOWBI5O1Iq6rOAA" iz kolekcije "users"
        DocumentReference docRef = db.collection("users").document(usersId_Milica);
        docRef.update("firstName", "Dragan")
                .addOnSuccessListener(aVoid -> Log.d("REZ_DB", "User successfully changed"))
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error getting documents.", e));

    }

    public static void delete(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // izbrisati user-a s ID-om "1wUqKBOWBI5O1Iq6rOAA" iz kolekcije "users"
        db.collection("users")
                .document(usersId_Ivana)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("REZ_DB", "The user has been deleted." + usersId_Ivana))
                .addOnFailureListener(e -> Log.w("REZ_DB", "Error deleting document.", e));

    }
    public static void selectById(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef =  db.collection("users").document(usersId_Ivana);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                Log.d("REZ_DB", documentSnapshot.getId() + " => " + documentSnapshot.getData());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("REZ_DB", "Error getting documents.", e);
            }
        });

    }*/

}

