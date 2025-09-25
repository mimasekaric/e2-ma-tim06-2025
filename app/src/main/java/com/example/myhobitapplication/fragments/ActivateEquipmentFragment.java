package com.example.myhobitapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhobitapplication.adapters.ActivateEquipmentAdapter;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.databinding.ActivateEquipmentBinding;
import com.example.myhobitapplication.dto.UserEquipmentDTO;
import com.example.myhobitapplication.models.Boss;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.EquipmentService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.viewModels.ProfileViewModel;
import com.example.myhobitapplication.viewModels.UserEquipmentViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ActivateEquipmentFragment extends Fragment {

    private List<UserEquipmentDTO> equipmentList;
    private UserEquipmentViewModel viewModel;
    private ProfileViewModel profileViewModel;
    private Profile profile;
    private ActivateEquipmentAdapter activateEquipmentAdapter;

    private ActivateEquipmentBinding binding;
    public ActivateEquipmentFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = ActivateEquipmentBinding.inflate(inflater, container, false);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        BossRepository bossRepository = new BossRepository(requireContext());
        EquipmentRepository equipmentRepository = new EquipmentRepository(requireContext());
        ProfileService profileService = ProfileService.getInstance();
        BossService bossService = new BossService(bossRepository);
        EquipmentService equipmentService = new EquipmentService(equipmentRepository);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserEquipmentViewModel(requireContext(), bossService,equipmentService,profileService );
            }
        }).get(UserEquipmentViewModel.class);

        profileViewModel.getProfile().observe(getViewLifecycleOwner(), loadedProfile -> {
            if (loadedProfile != null) {
                profile = loadedProfile;
            }
        });

        equipmentList = viewModel.getNotActivatedEquipmentForUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        RecyclerView recyclerView = binding.shopRecycler;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        activateEquipmentAdapter = new ActivateEquipmentAdapter(requireContext(),equipmentList, new ActivateEquipmentAdapter.OnItemButtonClickListener() {
            @Override
            public void onButtonClick(UserEquipmentDTO item) {
                if(profile!= null) {
                    viewModel.activateEquipment(item, profile);
                    equipmentList = viewModel.getNotActivatedEquipmentForUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    activateEquipmentAdapter.updateList(equipmentList);
                    Toast.makeText(requireContext(),"Succesfully activated!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(requireContext(),"Connection error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(activateEquipmentAdapter);
        return binding.getRoot();
    }

}
