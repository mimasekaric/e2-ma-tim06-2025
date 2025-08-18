package com.example.myhobitapplication;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected  void onStart() {
        super.onStart();
        TextView textView = findViewById(R.id.textView);

        TypeWriterEffect typewriter = findViewById(R.id.typeWriter);
        typewriter.setText("");
        typewriter.setCharacterDelay(150);
        typewriter.animateText(getString(R.string.typew_text));
        textView.setText(R.string.home_text);
        TextView button = findViewById(R.id.buttonV);

        Animation buttonAnim = AnimationUtils.loadAnimation(this, R.anim.enterbutton_anim);
        buttonAnim.setFillAfter(true);
        buttonAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                button.setBackgroundResource(R.drawable.button2);
                button.setAlpha(1);
                buttonAnim.setFillAfter(true);
                typewriter.animateText("");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                button.setTextColor(Color.parseColor("#123d30"));
                Intent intent = new Intent(TestActivity.this, SecondTestActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        button.setOnClickListener((v) -> {
            button.startAnimation(buttonAnim);
        });
    }


    @Override
    protected void onStop(){
        super.onStop();
        TextView button = findViewById(R.id.buttonV);
        button.setBackgroundResource(R.drawable.button);
        button.clearAnimation();
    }
}