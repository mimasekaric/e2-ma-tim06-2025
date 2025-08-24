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


    private EditText nametext;
    private EditText surnametext;
    private Button addB;
    private FirebaseFirestore db;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//
//
//    }

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

        // Inicijalizujte Firestore instancu jednom u onCreate
        db = FirebaseFirestore.getInstance();

        // Povežite UI elemente
        nametext = findViewById(R.id.editTextText);
        surnametext = findViewById(R.id.editTextText2);
        addB = findViewById(R.id.button);

        // Postavite OnClickListener
        addB.setOnClickListener((v) -> {
            Log.d("Kliknuo", "Dugme za dodavanje kliknuto");

            String name = nametext.getText().toString().trim();
            String surname = surnametext.getText().toString().trim();

            // Proverite da li su polja prazna
            if (name.isEmpty() || surname.isEmpty()) {
                Log.w("InputError", "Ime ili prezime je prazno.");
                return; // Prekini izvršavanje ako nema unosa
            }

            // Kreiranje HashMap-a sa podacima iz EditText polja
            Map<String, Object> user = new HashMap<>();
            user.put("first", name);
            user.put("last", surname);

            // Dodavanje novog dokumenta u Firestore
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("FirestoreSuccess", "Dokument uspešno dodat sa ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("FirestoreError", "Greška prilikom dodavanja dokumenta", e);
                        }
                    });
        });
    }

    // onStart() metoda sada može biti prazna ili se može obrisati ako nema drugu svrhu
    @Override
    protected void onStart() {
        super.onStart();
    }
}