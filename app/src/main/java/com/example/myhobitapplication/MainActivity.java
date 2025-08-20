package com.example.myhobitapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



    }

    @Override
    protected void onStart(){
        super.onStart();


        // Dobijanje instance Firebase Firestore baze
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("Instanca","uzeta instanca");
        // Kreiranje HashMap-a sa fiksnim vrednostima
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        // Dodavanje novog dokumenta sa generisanim ID-em
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Upisano", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Upisano", "Error adding document", e);
                    }
                });





        EditText nametext = findViewById(R.id.editTextText);
        EditText surnametext = findViewById(R.id.editTextText2);
        Button addB = (Button) findViewById(R.id.button);


        addB.setOnClickListener((v)->{
            Log.w("Kliknuo", "Kliknuto");
            String name = nametext.getText().toString().trim();
            String surname = surnametext.getText().toString().trim();
            SQliteConnection connection = new SQliteConnection(MainActivity.this);
            connection.addUser(name,surname);
        });


    }
}