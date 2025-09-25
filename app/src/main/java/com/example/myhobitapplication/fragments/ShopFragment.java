package com.example.myhobitapplication.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.activities.RegistrationActivity;
import com.example.myhobitapplication.adapters.AvatarSpinnerAdapter;
import com.example.myhobitapplication.adapters.ShopItemsAdapter;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.databinding.ActivityProfileBinding;
import com.example.myhobitapplication.databinding.FragmentShopBinding;
import com.example.myhobitapplication.dto.EquipmentWithPriceDTO;
import com.example.myhobitapplication.enums.EquipmentTypes;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.Profile;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.EquipmentService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.staticData.AvatarList;
import com.example.myhobitapplication.viewModels.ProfileViewModel;
import com.example.myhobitapplication.viewModels.UserEquipmentViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ShopFragment extends Fragment {

    private FragmentShopBinding binding;
    private List<EquipmentWithPriceDTO> equipmentList;
    private UserEquipmentViewModel viewModel;
    private EquipmentTypes type;
    private ProfileViewModel profileViewModel;
    private Profile profile;
    private ShopItemsAdapter shopItemsAdapter;
    public ShopFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentShopBinding.inflate(inflater, container, false);

        type = EquipmentTypes.CLOTHING;
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
                return (T) new UserEquipmentViewModel(requireContext(),bossService,equipmentService,profileService);
            }
        }).get(UserEquipmentViewModel.class);
        observeViewModels();

        binding.buttonPotions.setOnClickListener(v->{
            type= EquipmentTypes.POTION;
            populateView();
        });

        binding.buttonClothing.setOnClickListener(v->{
            type= EquipmentTypes.CLOTHING;
            populateView();
        });


        return binding.getRoot();
    }

    private void observeViewModels() {
        profileViewModel.getProfile().observe(getViewLifecycleOwner(), loadedProfile -> {
            if (loadedProfile != null) {
                 profile = loadedProfile;

                binding.textViewCoins.setText(profile.getcoins().toString());

                populateView();
            }
        });
    }



    private void populateView(){
        equipmentList = viewModel.getEquipmentByTypeWithPrice(type, profile);

        RecyclerView recyclerView = binding.shopRecycler;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        shopItemsAdapter = new ShopItemsAdapter(requireContext(), equipmentList, new ShopItemsAdapter.OnItemButtonClickListener() {
            @Override
            public void onButtonClick(EquipmentWithPriceDTO item) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogWhiteText);
                dialog.setMessage("Confirm purchase")
                        .setCancelable(false)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean response=viewModel.buyEquipment(profile,item.getEquipment());
                                if(response){
                                    profileViewModel.loadProfile(profile.getuserUid());
                                    Toast.makeText(requireContext(),"Bought Succesfully", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(requireContext(),"Couldn't buy item", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }). setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog alert = dialog.create();
                alert.getWindow().setBackgroundDrawable( ContextCompat.getDrawable(requireContext(), R.drawable.logincard));

                alert.show();
            }
        });
        recyclerView.setAdapter(shopItemsAdapter);
    }
}