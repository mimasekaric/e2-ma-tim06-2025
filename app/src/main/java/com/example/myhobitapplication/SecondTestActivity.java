package com.example.myhobitapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SecondTestActivity extends AppCompatActivity {

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second_test);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected  void onStart(){
        super.onStart();
        EditText inputField = findViewById(R.id.editTextText);
        TextView animatedTextView = findViewById(R.id.animatedTextView);
        Button b = (Button) findViewById(R.id.buttonn);
        Animation name_anim = AnimationUtils.loadAnimation(this, R.anim.name_anim);
        b.setOnClickListener((v) -> {
            name = inputField.getText().toString();
            animatedTextView.setVisibility(View.VISIBLE);
            animatedTextView.setText(name);
            animatedTextView.setBackgroundColor(Color.parseColor("#7c8784"));
            animatedTextView.startAnimation(name_anim);
        });

    }

    @Override
    protected  void onStop(){
        super.onStop();
    }
}