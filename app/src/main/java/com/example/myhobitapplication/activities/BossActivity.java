package com.example.myhobitapplication.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databinding.ActivityBossBinding;

public class BossActivity extends AppCompatActivity {

    private ActivityBossBinding binding;
    private AnimationDrawable currentAnimation;
    ImageView attackAttemptsImage;
    private int remainingAttacks = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBossBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView bossImage = binding.myAnimatedImage;
        attackAttemptsImage = binding.attackAttemptsImage;

        currentAnimation = (AnimationDrawable) bossImage.getBackground();


        currentAnimation.start();



        binding.attackButton.setOnClickListener(v -> {
            if (remainingAttacks > 0) {

                playHitAndReturnToIdle();
            }

        });
    }

    private void playHitAndReturnToIdle() {
        AnimationDrawable hitAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.boss_hit_animation, getTheme());

        binding.myAnimatedImage.setBackground(hitAnimation);
        hitAnimation.start();

        remainingAttacks--;
        updateAttackAttemptsImage();

        int hitDuration = 350;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AnimationDrawable idleAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.boss_idle_animation, getTheme());
            binding.myAnimatedImage.setBackground(idleAnimation);

            idleAnimation.start();
        }, hitDuration);
    }

    private void updateAttackAttemptsImage() {
        int drawableResourceId;
        switch (remainingAttacks) {
            case 4:
                drawableResourceId = R.drawable.lifes_frame_2;
                break;
            case 3:
                drawableResourceId = R.drawable.lifes_frame_3;
                break;
            case 2:
                drawableResourceId = R.drawable.lifes_frame_4;
                break;
            case 1:
                drawableResourceId = R.drawable.lifes_frame_5;
                break;
            case 0:
            default:
                drawableResourceId = R.drawable.lifes_frame_6;
                break;

        }
        attackAttemptsImage.setImageResource(drawableResourceId);
    }
    

}
