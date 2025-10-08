package com.example.myhobitapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.activities.BossActivity;
import com.example.myhobitapplication.activities.CategoryViewActivity;
import com.example.myhobitapplication.activities.TaskActivity;
import com.example.myhobitapplication.databinding.FragmentHomeDashboardBinding;
import com.example.myhobitapplication.models.Avatar;
import com.example.myhobitapplication.staticData.AvatarList;
import com.example.myhobitapplication.viewModels.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class HomeDashboardFragment extends Fragment {
    private FragmentHomeDashboardBinding binding;
    private ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);


        profileViewModel.getUserInfo().observe(getViewLifecycleOwner(), userInfoDto -> {
            if (userInfoDto != null && userInfoDto.getavatarName() != null) {

                for (Avatar a : AvatarList.getAvatarList()) {
                    if (a.getName().equals(userInfoDto.getavatarName())) {
                        binding.avatarImage.setImageResource(a.getImage());
                        break;
                    }
                }
            }
        });

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        profileViewModel.loadProfile(currentUserId);


        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.buttonTasks.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TaskActivity.class));
        });

        binding.buttonBoss.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), BossActivity.class));
        });

        binding.buttonCategories.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CategoryViewActivity.class));
        });

        binding.buttonShop.setOnClickListener(v -> {

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(com.example.myhobitapplication.R.id.fragment_container, new ShopFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
