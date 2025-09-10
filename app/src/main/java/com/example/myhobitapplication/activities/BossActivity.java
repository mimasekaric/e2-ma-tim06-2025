package com.example.myhobitapplication.activities;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.ActivityBossBinding;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.viewModels.BattleViewModel;

public class BossActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityBossBinding binding;
    private AnimationDrawable currentAnimation;
    ImageView attackAttemptsImage;

    private ProgressBar hpProgressBar;
    private TextView hpTextView;

    private BattleViewModel battleViewModel;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdateTime = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 800;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TaskRepository taskRepository = new TaskRepository(getApplicationContext());
        BossRepository bossRepository = new BossRepository(getApplicationContext());
        Boss boss = new Boss(2,400,"6",400,false,4,200);
        bossRepository.insertBoss(boss);

        battleViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new BattleViewModel(taskRepository, bossRepository);
            }
        }).get(BattleViewModel.class);



        EdgeToEdge.enable(this);

        binding = ActivityBossBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        Integer userPP = battleViewModel.getUserPP().getValue();
//
//        if (userPP == null) return;

//        binding.ppProgressBar.setMax(userPP);
//        binding.ppProgressBar.setProgress(userPP, true);
//        binding.ppTextView.setText("PP: " + userPP);


        ImageView bossImage = binding.myAnimatedImage;
        currentAnimation = (AnimationDrawable) bossImage.getBackground();
        currentAnimation.start();

        setupObservers();
        //todo: moracu uzeti logovanog usera ubuduce
        battleViewModel.loadBattleState(6);

        binding.attackButton.setOnClickListener(v -> {
            battleViewModel.performAttack();
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    private void setupObservers() {

        battleViewModel.getBossCurrentHp().observe(this, currentHp -> {
            if (currentHp != null) {
                updateHpBar();
            }
        });


        battleViewModel.getRemainingAttacks().observe(this, remaining -> {
            if (remaining != null) {
                updateAttackAttemptsImage(remaining);

                binding.attackButton.setEnabled(remaining > 0);
            }
        });

        battleViewModel.getHitAnimationEvent().observe(this, shouldAnimate -> {
            if (shouldAnimate != null && shouldAnimate) {
                playHitAndReturnToIdle();
                battleViewModel.onHitAnimationFinished();
            }
        });

        battleViewModel.isBattleOver().observe(this, isOver -> {
            if (isOver != null && isOver) {

                binding.attackButton.setEnabled(false);

                Integer finalHp = battleViewModel.getBossCurrentHp().getValue();
                if (finalHp != null && finalHp <= 0) {
                    Toast.makeText(this, "WIN! BOSS IS DEFEATED!", Toast.LENGTH_LONG).show();
                    //TODO:OTVARANJE KOVCEGA CU STAVITI ODVJE I DBOIJANJE PARA
                } else {
                    Toast.makeText(this, "U LOST!", Toast.LENGTH_LONG).show();
                  //TODO: OVDJE IDE UTJESNA NAGRADA VALJDA
                }
            }
        });

        battleViewModel.getAttackMissedEvent().observe(this, hasMissed -> {
            if (hasMissed != null && hasMissed) {
                //TODO:DODACU ANIMACIJU KASNIJE
                String info = "FAILED: CHANCES FOR SUCCESS:"+battleViewModel.getHitChance();
                Toast.makeText(this, info, Toast.LENGTH_SHORT).show();

                playMissAndReturnToIdle();

                battleViewModel.onAttackMissedEventHandled();
            }
        });
    }


    private void updateHpBar() {

        Integer userPP = battleViewModel.getUserPP().getValue();

        if (userPP == null) return;

        binding.ppProgressBar.setMax(userPP);
        binding.ppProgressBar.setProgress(userPP, true);
        binding.ppTextView.setText("PP: " + userPP);

        Integer currentHp = battleViewModel.getBossCurrentHp().getValue();
        Integer maxHp = battleViewModel.getBossMaxHp().getValue();

        if (currentHp == null || maxHp == null) return;

        binding.hpProgressBar.setMax(maxHp);
        binding.hpProgressBar.setProgress(currentHp, true);
        binding.hpTextView.setText("HP: " + currentHp + " / " + maxHp);
    }

    private void updateAttackAttemptsImage(int remaining) {
        int drawableResourceId;
        switch (remaining) {
            case 5: drawableResourceId = R.drawable.lifes_frame_1; break;
            case 4: drawableResourceId = R.drawable.lifes_frame_2; break;
            case 3: drawableResourceId = R.drawable.lifes_frame_3; break;
            case 2: drawableResourceId = R.drawable.lifes_frame_4; break;
            case 1: drawableResourceId = R.drawable.lifes_frame_5; break;
            default: drawableResourceId = R.drawable.lifes_frame_6; break;
        }
        binding.attackAttemptsImage.setImageResource(drawableResourceId);
    }

    private void playMissAndReturnToIdle() {

        binding.attackButton.setEnabled(false);

        AnimationDrawable missAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.boss_miss_animation, getTheme());
        binding.myAnimatedImage.setBackground(missAnimation);
        missAnimation.start();

        int missDuration = 500;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AnimationDrawable idleAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.boss_idle_animation, getTheme());
            binding.myAnimatedImage.setBackground(idleAnimation);
            idleAnimation.start();

            Integer attacksLeft = battleViewModel.getRemainingAttacks().getValue();
            Integer currentHp = battleViewModel.getBossCurrentHp().getValue();
            if (attacksLeft != null && attacksLeft > 0 && attacksLeft != null && currentHp>0) {
                binding.attackButton.setEnabled(true);
            }
        }, 500);
    }

    private void playHitAndReturnToIdle() {

        binding.attackButton.setEnabled(false);

        AnimationDrawable hitAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.boss_hit_animation, getTheme());
        binding.myAnimatedImage.setBackground(hitAnimation);
        hitAnimation.start();

        int hitDuration = 350;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AnimationDrawable idleAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.boss_idle_animation, getTheme());
            binding.myAnimatedImage.setBackground(idleAnimation);
            idleAnimation.start();

            Integer attacksLeft = battleViewModel.getRemainingAttacks().getValue();
            Integer currentHp = battleViewModel.getBossCurrentHp().getValue();
            if (attacksLeft != null && attacksLeft > 0 && attacksLeft != null && currentHp>0) {
                binding.attackButton.setEnabled(true);
            }
        }, hitDuration);
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastUpdateTime) > 100) {
                long diffTime = (currentTime - lastUpdateTime);
                lastUpdateTime = currentTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];


                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;


                if (speed > SHAKE_THRESHOLD) {


                    if (binding.attackButton.isEnabled()) {
                        Toast.makeText(this, "SHAKE ACTIVATED!", Toast.LENGTH_SHORT).show();
                        battleViewModel.performAttack();
                    }
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }

    }
}
