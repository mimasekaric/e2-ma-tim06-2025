package com.example.myhobitapplication.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.ActivityProfileBinding;
import com.example.myhobitapplication.dto.UserInfoDTO;
import com.example.myhobitapplication.enums.Title;
import com.example.myhobitapplication.models.Avatar;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.services.AllianceMissionService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.EquipmentService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.staticData.AvatarList;
import com.example.myhobitapplication.viewModels.LoginViewModel;
import com.example.myhobitapplication.viewModels.ProfileViewModel;
import com.example.myhobitapplication.viewModels.UserEquipmentViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.List;

public class ProfileActivity extends Fragment {

    private LoginViewModel loginViewModel;
    private ProfileViewModel viewModel;

    private UserEquipmentViewModel userEquipmentViewModel;
    private ActivityProfileBinding binding;

    private String profileUrl = "";
    private UserInfoDTO userInfo;
    private Profile profile;

    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }
        BossRepository bossRepository = new BossRepository(requireContext());
        EquipmentRepository equipmentRepository = new EquipmentRepository(requireContext());
        ProfileService profileService = ProfileService.getInstance();
        BossService bossService = new BossService(bossRepository);
        EquipmentService equipmentService = new EquipmentService(equipmentRepository);
        AllianceMissionService allianceMissionService = new AllianceMissionService(profileService);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        userEquipmentViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserEquipmentViewModel(requireContext(), bossService, equipmentService, profileService, allianceMissionService);
            }
        }).get(UserEquipmentViewModel.class);

        loginViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new LoginViewModel();
            }
        }).get(LoginViewModel.class);


        viewModel.loadProfile(userId);
        observeViewModels();

        profileUrl = "https://myhobbitapplication/profil/" + userId;

        binding.imageButton.setOnClickListener(v -> showDialog());

        try {
            Bitmap qrCodeBitmap = generateQRCode(profileUrl);
            binding.qrimage.setImageBitmap(qrCodeBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        addDummyImages();
    }

    private void observeViewModels() {
        viewModel.getProfile().observe(getViewLifecycleOwner(), loadedProfile -> {
            if (loadedProfile != null) {
                this.profile = loadedProfile;
                if(!userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    binding.putpp.setVisibility(View.INVISIBLE);
                    binding.putcoins.setVisibility(View.INVISIBLE);
                    binding.imgcoin.setVisibility(View.INVISIBLE);
                    binding.imgpp.setVisibility(View.INVISIBLE);
                    binding.imageButton.setVisibility(View.INVISIBLE);
                    binding.chng.setVisibility(View.INVISIBLE);
                }

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
                    switch (profile.getTitle()) {
                        case "CURIOUS_WANDERER":
                            binding.titleput.setText("Curious Wanderer");
                            break;
                        case "BRAVE_ADVENTURER":
                            binding.titleput.setText("Brave Adventurer");
                            break;
                        case "DEFENDER_OF_THE_REALM":
                            binding.titleput.setText("Realm Defender");
                            break;
                        case "MASTER_OF_SECRETS":
                            binding.titleput.setText("Master Of Secrets");
                            break;
                        default:
                            binding.titleput.setText(profile.getTitle());
                            break;

                    }
                }
            }

            List<Equipment> equipmentList = userEquipmentViewModel.getEquipmentForUser(profile.getuserUid());
            binding.imgLayout2.removeAllViews();
            for (Equipment res : equipmentList) {
                ImageView imageView2 = new ImageView(getContext());
                imageView2.setImageResource(res.getImage());
                int widthInDp = 100;
                float scale = getResources().getDisplayMetrics().density;
                int widthInPx = (int) (widthInDp * scale + 0.5f);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        widthInPx,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                imageView2.setLayoutParams(params);
                binding.imgLayout2.addView(imageView2);
            }
        });



        viewModel.getUserInfo().observe(getViewLifecycleOwner(), userInfo -> {
            if (userInfo != null) {
                this.userInfo = userInfo;
                binding.putusername.setText(userInfo.getusername());

                for( Avatar a :AvatarList.getAvatarList()){
                    if (a.getName().equals(viewModel.getUserInfo().getValue().getavatarName())){
                        binding.imageProf.setImageResource(a.getImage());
                    }
                }
            }
        });

        loginViewModel.getPassSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Succesfully changed password!", Toast.LENGTH_SHORT).show();
            } else if (!success && !loginViewModel.getPassesponse().getValue().equals("")) {
                Toast.makeText(getContext(), "Failed to change password!", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void showDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.password_change);

        final EditText editPassword1 = dialog.findViewById(R.id.changepass1);
        final EditText editPassword2 = dialog.findViewById(R.id.changepass2);
        ImageButton submit = dialog.findViewById(R.id.imgbuttconf);

        submit.setOnClickListener(v -> {
            String pass1 = editPassword1.getText().toString();
            String pass2 = editPassword2.getText().toString();
            if (pass1.equals(pass2)) {
                loginViewModel.changePassword(pass1);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Passwords must match!", Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();
    }

    private void addDummyImages() {
        int[] images = {
                R.mipmap.ic_launcher_bulb,
                R.mipmap.ic_launcher_bulb,
                R.mipmap.ic_launcher_bulb,
                R.mipmap.ic_launcher_bulb,
                R.mipmap.ic_launcher_bulb
        };

        for (int res : images) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(res);
            int widthInDp = 100;
            float scale = getResources().getDisplayMetrics().density;
            int widthInPx = (int) (widthInDp * scale + 0.5f);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    widthInPx,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            imageView.setLayoutParams(params);
            binding.imgLayout1.addView(imageView);
        }

    }
}
