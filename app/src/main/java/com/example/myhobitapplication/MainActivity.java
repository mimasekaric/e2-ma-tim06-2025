package com.example.myhobitapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        EditText nametext = findViewById(R.id.editTextText);
        EditText surnametext = findViewById(R.id.editTextText2);
        Button addB = (Button) findViewById(R.id.button);


        addB.setOnClickListener((v)->{
            String name = nametext.getText().toString().trim();
            String surname = surnametext.getText().toString().trim();
            SQliteConnection connection = new SQliteConnection(MainActivity.this);
            connection.addUser(name,surname);
        });


    }
}