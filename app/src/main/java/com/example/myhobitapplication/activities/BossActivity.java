package com.example.myhobitapplication.activities;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databinding.ActivityBossBinding;

public class BossActivity extends AppCompatActivity {

    private ActivityBossBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBossBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView myImage = binding.myAnimatedImage;

        Animation moveUpDown = AnimationUtils.loadAnimation(this, R.anim.dragon_up_down);

        myImage.startAnimation(moveUpDown);
    }
    

}
