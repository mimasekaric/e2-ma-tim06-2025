package com.example.myhobitapplication.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databinding.ActivityProfileBinding;
import com.example.myhobitapplication.databinding.ActivityRegistrationBinding;
import com.example.myhobitapplication.dto.UserInfoDTO;
import com.example.myhobitapplication.enums.Title;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.viewModels.ProfileViewModel;
import com.example.myhobitapplication.viewModels.RegistrationViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class ProfileActivity extends AppCompatActivity {


    String profileUrl ="";
    ActivityProfileBinding binding ;
    UserInfoDTO userInfo;
    Profile profile;
    ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ProfileViewModel();
            }
        }).get(ProfileViewModel.class);

        String userId = intent.getStringExtra("USER_ID");

        viewModel.loadProfile(userId);
        viewModel.getProfile().observe(this, loadedProfile -> {
            if (loadedProfile != null) {
                this.profile = loadedProfile;

                if (profile.getnumberOgbadges() != null) {
                    binding.putbadges.setText(profile.getnumberOgbadges().toString());
                }
                if (profile.getcoins() != null) {
                    binding.putcoins.setText(profile.getcoins().toString());
                }
                if (profile.getlevel() != null) {
                    binding.putlevel.setText(profile.getlevel().toString());
                }
                if (profile.getPp() != null) {
                    binding.putpp.setText(profile.getPp().toString());
                }
                if (profile.getxp() != null) {
                    binding.putxp.setText(profile.getxp().toString());
                }

                if (profile.getTitle() != null) {
                    if(profile.getTitle()==Title.CURIOUS_WANDERER) {
                        binding.titleput.setText("Curious Wanderer");
                    }else if(profile.getTitle()==Title.BRAVE_ADVENTURER) {
                        binding.titleput.setText("Brave Adventurer");
                    } else if(profile.getTitle()==Title.DEFENDER_OF_THE_REALM) {
                        binding.titleput.setText("Realm Defender");
                    } else if(profile.getTitle()==Title.MASTER_OF_SECRETS) {
                        binding.titleput.setText("Master Of Secrets");
                    }
                }

            }
        });
        viewModel.getUserInfo().observe(this, userInfo -> {
            if (userInfo != null) {
                this.userInfo = userInfo;
                binding.putusername.setText(userInfo.getusername());
            }
        });



        profileUrl =  "https://myhobbitapplication/profil/"+userId;
        Toast.makeText(this,profileUrl,Toast.LENGTH_SHORT).show();
        EdgeToEdge.enable(this);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected  void onResume(){
        super.onResume();


        try {
            Bitmap qrCodeBitmap = generateQRCode(profileUrl);
            binding.qrimage.setImageBitmap(qrCodeBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int[] images = {
                R.mipmap.ic_launcher_bulb,
                R.mipmap.ic_launcher_bulb,
                R.mipmap.ic_launcher_bulb,
                R.mipmap.ic_launcher_bulb,
                R.mipmap.ic_launcher_bulb
        };
        int[] equip = {
                R.mipmap.ic_launcher_equ,
                R.mipmap.ic_launcher_equ,
                R.mipmap.ic_launcher_equ,
                R.mipmap.ic_launcher_equ,
                R.mipmap.ic_launcher_equ
        };


        for (int res : images) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(res);
            int widthInDp = 100;
            float scale = getResources().getDisplayMetrics().density;
            int widthInPx = (int) (widthInDp * scale + 0.5f);

            // Set layout params: fixed width, match parent height
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    widthInPx,                            // width in px
                    LinearLayout.LayoutParams.MATCH_PARENT // height
            );
            imageView.setLayoutParams(params);
            binding.imgLayout1.addView(imageView);
        }

        for (int res : equip) {
            ImageView imageView2 = new ImageView(this);
            imageView2.setImageResource(res);
            int widthInDp = 100;
            float scale = getResources().getDisplayMetrics().density;
            int widthInPx = (int) (widthInDp * scale + 0.5f);

            // Set layout params: fixed width, match parent height
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    widthInPx,                            // width in px
                    LinearLayout.LayoutParams.MATCH_PARENT // height
            );
            imageView2.setLayoutParams(params);
            binding.imgLayout2.addView(imageView2);
        }
    }


   private Bitmap generateQRCode(String content) throws WriterException {
        int width = 350;
        int height = 350;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.parseColor("#367360") : Color.WHITE);
            }
        }
        return bmp;
    }

}