package com.example.myhobitapplication.activities;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
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
import com.example.myhobitapplication.databases.ProfileRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.ActivityBossBinding;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.shakeDetector.ShakeDetector;
import com.example.myhobitapplication.viewModels.BattleViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class BossActivity extends AppCompatActivity {

    private ActivityBossBinding binding;
    private AnimationDrawable currentAnimation;

    private BattleViewModel battleViewModel;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private FrameLayout chestOverlayContainer;

    private String userUid;

    private ShakeDetector shakeDetector;

    private boolean isChestPhase = false;
    private boolean hasChestBeenOpened = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        TaskRepository taskRepository = new TaskRepository(getApplicationContext());
        BossRepository bossRepository = new BossRepository(getApplicationContext());
        ProfileService profileService = new ProfileService();
        Boss boss = new Boss(2,400,userUid,400,false,2,200);
        bossRepository.insertBoss(boss);

        battleViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new BattleViewModel(taskRepository, bossRepository, profileService);
            }
        }).get(BattleViewModel.class);



        EdgeToEdge.enable(this);

        binding = ActivityBossBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        chestOverlayContainer = binding.chestOverlayContainer;

        setupShakeDetector();
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
        battleViewModel.loadBattleState(userUid);

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


                new Handler(Looper.getMainLooper()).postDelayed(() -> {

                    Integer finalHp = battleViewModel.getBossCurrentHp().getValue();
                    if (finalHp != null && finalHp <= 0) {

                        Toast.makeText(this, "U WIN!", Toast.LENGTH_SHORT).show();
                        showChestOverlay();
                    } else {

                        Toast.makeText(this, "U LOST!", Toast.LENGTH_LONG).show();

                    }

                }, 1000);

                //Integer finalHp = battleViewModel.getBossCurrentHp().getValue();
//                if (finalHp != null && finalHp <= 0) {
//                    Toast.makeText(this, "WIN! BOSS IS DEFEATED!", Toast.LENGTH_LONG).show();
//                    //TODO:OTVARANJE KOVCEGA CU STAVITI ODVJE I DBOIJANJE PARA
//                } else {
//                    Toast.makeText(this, "U LOST!", Toast.LENGTH_LONG).show();
//                  //TODO: OVDJE IDE UTJESNA NAGRADA VALJDA
//                }
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

    private void showChestOverlay() {
        isChestPhase = true;
        chestOverlayContainer.setVisibility(View.VISIBLE);
        registerShakeDetector();
    }

    private void registerShakeDetector() {
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void unregisterShakeDetector() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(shakeDetector);
        }
    }
    private void setupShakeDetector() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            shakeDetector = new ShakeDetector();
            shakeDetector.setOnShakeListener(count -> {
                // SADA, na osnovu zastavice, odlučujemo koju logiku da pokrenemo
                if (isChestPhase) {
                    handleChestShake();
                } else {
                    handleAttackShake();
                }
            });
        }
    }

    // --- NOVE, ODVOJENE METODE ZA OBRADU SHAKE-a ---

    private void handleAttackShake() {
        // Logika za napad (tvoj postojeći kod)
        if (binding.attackButton.isEnabled()) {
            Toast.makeText(this, "Napad protresanjem!", Toast.LENGTH_SHORT).show();
            battleViewModel.performAttack();
        }
    }

    private void handleChestShake() {

        if (hasChestBeenOpened) return;
        hasChestBeenOpened = true;

        unregisterShakeDetector();
        binding.shakeToOpenText.setVisibility(View.GONE);
        binding.staticChestImage.setVisibility(View.GONE);
        binding.openingChestAnimation.setVisibility(View.VISIBLE);
        binding.openingChestAnimation.playAnimation();
        binding.openingChestAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override public void onAnimationEnd(Animator animation) {

                displayRewards();
            }
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
        });
    }

    private void displayRewards() {
        new AlertDialog.Builder(this)
                .setTitle("Nagrade!")
                .setMessage("Osvojio/la si: 200 novčića i Mač Fokusa!")
                .setPositiveButton("Sjajno!", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

}
