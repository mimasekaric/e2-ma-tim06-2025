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
    ImageView attackAttemptsImage;

    private ProgressBar hpProgressBar;
    private TextView hpTextView;

    private BattleViewModel battleViewModel;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdateTime = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 800;

    private FrameLayout chestOverlayContainer;

    private String userUid;

    private ShakeDetector shakeDetector;

    // Zastavica koja nam govori da li je borba gotova i da li čekamo shake za kovčeg
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
                        // POBEDA!
                        Toast.makeText(this, "U WIN!", Toast.LENGTH_SHORT).show();
                        showChestOverlay(); // Prikaži sloj sa kovčegom
                    } else {
                        // PORAZ!
                        Toast.makeText(this, "U LOST!", Toast.LENGTH_LONG).show();
                        // Ovde možeš prikazati dijalog za poraz i opciju za povratak
                        // ili jednostavno zatvoriti aktivnost nakon par sekundi
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
        // 1. Postavi zastavicu na 'true'. Sada će svaki shake pokrenuti 'handleChestShake'.
        isChestPhase = true;

        // 2. Učini ceo sloj vidljivim
        chestOverlayContainer.setVisibility(View.VISIBLE);

        // 3. PONOVO REGISTRUJ listener za senzor da počne da sluša za shake kovčega
        registerShakeDetector();
    }

    private void registerShakeDetector() {
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * Pomoćna metoda za odjavljivanje (prestanak slušanja) senzora.
     * Ovo je VAŽNO da bi se sačuvala baterija.
     */
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
        // Logika za otvaranje kovčega (kod koji si tražio/la)
        if (hasChestBeenOpened) return; // Spreči višestruko otvaranje
        hasChestBeenOpened = true;

        // Odjavi listener da ne bi mogao ponovo da se aktivira
        unregisterShakeDetector();

        // Sakrij tekst "Protresi..."
        binding.shakeToOpenText.setVisibility(View.GONE);

        // Sakrij statičnu sliku i prikaži/pokreni Lottie animaciju
        binding.staticChestImage.setVisibility(View.GONE);
        binding.openingChestAnimation.setVisibility(View.VISIBLE);
        binding.openingChestAnimation.playAnimation();

        // Listener za kraj Lottie animacije
        binding.openingChestAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override public void onAnimationEnd(Animator animation) {
                // Kada se otvaranje završi, prikaži nagrade
                displayRewards();
            }
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
        });
    }

    // Pomoćna metoda za prikaz nagrada
    private void displayRewards() {
        new AlertDialog.Builder(this)
                .setTitle("Nagrade!")
                .setMessage("Osvojio/la si: 200 novčića i Mač Fokusa!")
                .setPositiveButton("Sjajno!", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }


//    @Override
//    public void onSensorChanged(SensorEvent event) {
//
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            long currentTime = System.currentTimeMillis();
//
//            if ((currentTime - lastUpdateTime) > 100) {
//                long diffTime = (currentTime - lastUpdateTime);
//                lastUpdateTime = currentTime;
//
//                float x = event.values[0];
//                float y = event.values[1];
//                float z = event.values[2];
//
//
//                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
//
//
//                if (speed > SHAKE_THRESHOLD) {
//
//
//                    if (binding.attackButton.isEnabled()) {
//                        Toast.makeText(this, "SHAKE ACTIVATED!", Toast.LENGTH_SHORT).show();
//                        battleViewModel.performAttack();
//                    }
//                }
//
//                last_x = x;
//                last_y = y;
//                last_z = z;
//            }
//        }
//
//    }
}
