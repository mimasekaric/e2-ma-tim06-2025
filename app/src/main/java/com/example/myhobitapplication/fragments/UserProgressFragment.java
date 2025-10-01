package com.example.myhobitapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myhobitapplication.adapters.UserProgressAdapter;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.FragmentCategoryBinding;
import com.example.myhobitapplication.databinding.FragmentUserProgressBinding;
import com.example.myhobitapplication.services.AllianceMissionService;
import com.example.myhobitapplication.services.AllianceService;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.UserService;
import com.example.myhobitapplication.viewModels.UserProgressViewModel;
import com.example.myhobitapplication.viewModels.categoryViewModels.CategoryViewModel;

public class UserProgressFragment extends Fragment {


    private UserProgressViewModel viewModel;
    private FragmentUserProgressBinding binding;
    private UserProgressAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserService userService = new UserService();
        ProfileService profileService = ProfileService.getInstance();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                // Kreiraj instance servisa i proslijedi ih u ViewMode
                UserService userService = new UserService();
                AllianceMissionService missionService = new AllianceMissionService(profileService);
                return (T) new UserProgressViewModel(userService, missionService);
            }
        }).get(UserProgressViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserProgressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setupRecyclerView();

        setupObservers();
        String allianceId = "SepFZFusR8JKNCTqhaNn";
        viewModel.loadAllianceMissionProgress(allianceId);

    }

    private void setupRecyclerView() {
        adapter = new UserProgressAdapter();
        binding.allianceProgressRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.allianceProgressRecycler.setAdapter(adapter);
    }

    private void setupObservers() {

        viewModel.getActiveMission().observe(getViewLifecycleOwner(), mission -> {
            if (mission != null) {
                binding.bossInfoContainer.setVisibility(View.VISIBLE);
                binding.allianceBossHpBar.setMax(mission.getTotalBossHp());
                binding.allianceBossHpBar.setProgress(mission.getCurrentBossHp());
                binding.allianceBossHpText.setText(mission.getCurrentBossHp() + " / " + mission.getTotalBossHp() + " HP");
                // Ovdje dodajte logiku za prikaz preostalog vremena...
            } else {
                // Nema aktivne misije, sakrij prikaz bosa i prikaži poruku
                binding.bossInfoContainer.setVisibility(View.GONE);
            }
        });

        // GLAVNI OBSERVER: Za listu napretka članova
        viewModel.getAllianceProgress().observe(getViewLifecycleOwner(), progressList -> {
            if (progressList != null) {
                // Kada stigne nova lista, samo je proslijedi adapteru
                adapter.updateProgressList(progressList);
            }
        });

        // Observer za poruke o statusu/greškama
        viewModel.getResponse().observe(getViewLifecycleOwner(), response -> {
            if (response != null && !response.isEmpty()) {
                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Važno za sprječavanje curenja memorije
    }
}
