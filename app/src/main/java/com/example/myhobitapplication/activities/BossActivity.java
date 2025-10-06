package com.example.myhobitapplication.activities;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.ActivityBossBinding;
import com.example.myhobitapplication.dto.UserEquipmentDTO;
import com.example.myhobitapplication.models.Avatar;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.services.AllianceMissionService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.EquipmentService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.UserEquipmentService;
import com.example.myhobitapplication.shakeDetector.ShakeDetector;
import com.example.myhobitapplication.staticData.AvatarList;
import com.example.myhobitapplication.viewModels.BattleViewModel;
import com.example.myhobitapplication.viewModels.ProfileViewModel;
import com.example.myhobitapplication.viewModels.ProfileViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class BossActivity extends AppCompatActivity {

    private ActivityBossBinding binding;
    private MotionLayout rewardsAnimationLayout;
    private ProfileViewModel profileViewModel;
    private AnimationDrawable currentAnimation;

    private BattleViewModel battleViewModel;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private FrameLayout chestContainer;
    private FrameLayout yellowStarsContainer;
    private FrameLayout greenStarsContainer;


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
        EquipmentRepository equipmentRepository = new EquipmentRepository(getApplicationContext());
        ProfileService profileService = ProfileService.getInstance();
        //Boss boss = new Boss(2,400,userUid,400,false,2,200, 0.2);
        //bossRepository.insertBoss(boss);
        BossService bossService = new BossService(bossRepository);
        EquipmentService equipmentService = new EquipmentService(equipmentRepository);
        AllianceMissionService allianceMissionService = new AllianceMissionService(profileService);
        battleViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new BattleViewModel(taskRepository, bossRepository, profileService, new UserEquipmentService(getApplicationContext(), profileService, bossService, equipmentService,allianceMissionService));
            }
        }).get(BattleViewModel.class);


        ProfileViewModelFactory profileFactory = new ProfileViewModelFactory(getApplicationContext(),bossService,equipmentService);

        profileViewModel = new ViewModelProvider(this, profileFactory).get(ProfileViewModel.class);

        EdgeToEdge.enable(this);

        binding = ActivityBossBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        chestContainer = binding.chestOverlayContainer;
        //rewardsAnimationLayout = binding.rewardsAnimationLayout;
        //rewardsAnimationLayout.transitionToEnd();



//        Integer userPP = battleViewModel.getUserPP().getValue();
//
//        if (userPP == null) return;

//        binding.ppProgressBar.setMax(userPP);
//        binding.ppProgressBar.setProgress(userPP, true);
//        binding.ppTextView.setText("PP: " + userPP);


        ImageView bossImage = binding.myAnimatedImage;
        currentAnimation = (AnimationDrawable) bossImage.getBackground();
        currentAnimation.start();



        battleViewModel.loadBattleState(userUid);
        profileViewModel.loadProfile(userUid);


        setupObservers();
        setupShakeDetector();
        
        binding.attackButton.setOnClickListener(v -> {
            battleViewModel.performAttack();
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    private void setupObservers() {

        profileViewModel.getUserInfo().observe(this, userInfoDto -> {
                    if (userInfoDto != null && userInfoDto.getavatarName() != null) {
                        for (Avatar a : AvatarList.getAvatarList()) {
                            if (a.getName().equals(userInfoDto.getavatarName())) {
                                binding.avatarImage.setImageResource(a.getImage());
                                break;
                            }
                        }
                    }
        });


            battleViewModel.getBossCurrentHp().observe(this, currentHp -> {
            if (currentHp != null) {
                updateHpBar();
            }
        });


        battleViewModel.getRemainingAttacks().observe(this, remaining -> {
            if (remaining != null) {
                int startNumber = battleViewModel.getStartAttackNumber();
                updateAttackAttemptsImage(remaining, startNumber);

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
                        profileViewModel.loadProfile(userUid);//dodala ovdje da bi mi se azurirao profil nakon zavrsene borbe
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

        battleViewModel.getActivatedEquipment().observe(this, equipmentList -> {
            if (equipmentList != null) {
                loadActivatedEquipment(equipmentList);
            }
        });

        battleViewModel.getScreenState().observe(this, state -> {
            if (state == null) return;

            switch (state) {
                case BOSS_FOUND:
                    binding.bossBattleGroup.setVisibility(View.VISIBLE);
                    binding.noBoss.setVisibility(View.GONE);
                    binding.sleepingBoss.clearAnimation();
                    startRewardsFloatingAnimation();
                    registerShakeDetector();
                    break;
                case NO_BOSS_FOUND:
                    binding.bossBattleGroup.setVisibility(View.GONE);
                    binding.noBoss.setVisibility(View.VISIBLE);
                    binding.rewardsP.clearAnimation();
                    startFloatingAnimation();
                    break;
                case LOADING:
                    binding.bossBattleGroup.setVisibility(View.GONE);
                    binding.noBoss.setVisibility(View.GONE);
                    break;
            }
        });
        battleViewModel.getPotentialCoinReward().observe(this, coinReward -> {
            if (coinReward != null) {

                binding.potentialCoinsText.setText(String.valueOf(coinReward));
                binding.potentialCoinsLayout.setVisibility(View.VISIBLE);
            } else {
                binding.potentialCoinsLayout.setVisibility(View.GONE);
            }
        });
        battleViewModel.getHitChanceLiveData().observe(this, hitChance -> {
            if (hitChance != null) {
                int percentage = (int) (hitChance * 100);
                String hitChanceText = percentage + "%";

                binding.hitChanceText.setText(hitChanceText);
            }
        });

    }

    private void startFloatingAnimation() {

        Animation floatingAnimation = AnimationUtils.loadAnimation(this, R.anim.dragon_up_down);
        ImageView sleepingBossImage = binding.sleepingBoss;
        sleepingBossImage.startAnimation(floatingAnimation);
    }
    private void startRewardsFloatingAnimation(){
        Animation floatingAnimation = AnimationUtils.loadAnimation(this, R.anim.rewards_up_down);
        ImageView sleepingBossImage = binding.rewardsP;
        sleepingBossImage.startAnimation(floatingAnimation);
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

    private void updateAttackAttemptsImage(int remaining, int startNumber) {

        if (remaining == startNumber) {
            binding.attackAttemptsImage.setImageResource(R.drawable.life_1);
        }else if(remaining==startNumber-1){
            binding.attackAttemptsImage.setImageResource(R.drawable.life_2);
        }
        else if(remaining==startNumber-2){
            binding.attackAttemptsImage.setImageResource(R.drawable.life_3);
        }
        else if(remaining==startNumber-3){
            binding.attackAttemptsImage.setImageResource(R.drawable.life_4);
        }
        else if(remaining==startNumber-4){
            binding.attackAttemptsImage.setImageResource(R.drawable.life_5);
        }else if(remaining ==0){
            binding.attackAttemptsImage.setImageResource(R.drawable.life_6);
            binding.textShowRemaining.setVisibility(View.INVISIBLE);
        }else{
            binding.attackAttemptsImage.setVisibility(View.INVISIBLE);
            binding.textShowRemaining.setVisibility(View.VISIBLE);
            binding.textShowRemaining.setText("You have " + remaining + " additional attacks!");
        }

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
        chestContainer.setVisibility(View.VISIBLE);
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


    private void handleAttackShake() {

        Integer attacksLeft = battleViewModel.getRemainingAttacks().getValue();
        Boolean isBattleOver = battleViewModel.isBattleOver().getValue();

        if (attacksLeft == null || isBattleOver == null) {
            return;
        }

        if (attacksLeft > 0 && !isBattleOver) {

            if (!binding.attackButton.isEnabled()) {
                return;
            }

            Toast.makeText(this, "Shake attack!", Toast.LENGTH_SHORT).show();
            battleViewModel.performAttack();
        }
    }


    private void handleChestShake() {
        profileViewModel.loadProfile(userUid);//dodala ovdje da bi mi se azurirao profil nakon zavrsene borbe
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
        Integer coins = battleViewModel.getCoins().getValue();
        Integer imageResource = battleViewModel.getImageResource().getValue();
        String name = battleViewModel.getEquipmentName().getValue();

        View dialogView = LayoutInflater.from(this).inflate(R.layout.battle_rewards, null);

        TextView coinsTextView = dialogView.findViewById(R.id.coins);
        TextView equipmentNameTextView = dialogView.findViewById(R.id.equipment);
        ImageView equipmentImageView = dialogView.findViewById(R.id.equipmentImage);

        if (coins != null) {
            coinsTextView.setText(String.valueOf(coins));
        }

        if (imageResource != null && imageResource != 0) {
            equipmentImageView.setImageResource(imageResource);
            equipmentNameTextView.setText(name);
        } else {
            equipmentNameTextView.setText("No Equipment");
            equipmentImageView.setVisibility(View.INVISIBLE);
        }

        LottieAnimationView yellowStarsAnimation = dialogView.findViewById(R.id.sparksYellowContainer);
        LottieAnimationView greenStarsAnimation = dialogView.findViewById(R.id.spraksGreenContainer);
        yellowStarsAnimation.setVisibility(View.VISIBLE);
        greenStarsAnimation.setVisibility(View.VISIBLE);
        yellowStarsAnimation.playAnimation();
        greenStarsAnimation.playAnimation();




        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);

        dialog.setContentView(dialogView);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();
        final int DIALOG_DISPLAY_TIME_MS = 5000;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dialog.isShowing() && !isFinishing()) {
                dialog.dismiss();
                finish();
            }
        }, DIALOG_DISPLAY_TIME_MS);
    }



    private void loadActivatedEquipment(List<UserEquipmentDTO> userEquipmentDTOList) {


            if(userEquipmentDTOList.isEmpty()){
                TextView textView = new TextView(this);
                textView.setText("NO ACTIVATED EQUIPMENT");
                textView.setTextColor(Color.parseColor("#DA9100"));
                int widthInDp = 100;
                float scale = getResources().getDisplayMetrics().density;
                int widthInPx = (int) (widthInDp * scale + 0.5f);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        widthInPx,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                textView.setLayoutParams(params);
                binding.rewardsLinearLayout.addView(textView);
                return;
            }

            binding.rewardsLinearLayout.removeAllViews();
            for (UserEquipmentDTO res : userEquipmentDTOList) {
                ImageView imageView2 = new ImageView(this);
                imageView2.setImageResource(res.getEquipment().getImage());
                int widthInDp = 100;
                float scale = getResources().getDisplayMetrics().density;
                int widthInPx = (int) (widthInDp * scale + 0.5f);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        widthInPx,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                imageView2.setLayoutParams(params);
                binding.rewardsLinearLayout.addView(imageView2);
            }
    }

    //generated by chat-gpt
    @Override
    protected void onResume() {
        super.onResume();

//        // Pokrećemo animaciju kada se aktivnost prikaže
//        startRewardsAnimation();
    }

//    private void startRewardsAnimation() {
//        // Dohvatimo MotionLayout preko binding objekta
//        MotionLayout rewardsAnimationLayout = binding.rewardsAnimationLayout;
//
//        // Postavljamo listener koji će restartati animaciju kada završi, stvarajući beskonačnu petlju
//        rewardsAnimationLayout.setTransitionListener(new MotionLayout.TransitionListener() {
//            @Override
//            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {}
//
//            @Override
//            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {}
//
//            @Override
//            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
//                // Kada animacija dođe do kraja, odmah je pokreni ponovo
//                if (currentId == R.id.end) {
//                    motionLayout.transitionToStart(); // Vrati na početak bez animacije
//                    motionLayout.transitionToEnd();   // Pokreni animaciju ponovo
//                }
//            }
//
//            @Override
//            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {}
//        });
//
//        // Pokreni animaciju prvi put
//        rewardsAnimationLayout.transitionToEnd();
//
//    }
}
