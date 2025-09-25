package com.example.myhobitapplication.fragments.tasksFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myhobitapplication.R;
import com.example.myhobitapplication.activities.TaskDetailActivity;
import com.example.myhobitapplication.adapters.RecurringTaskListAdapter;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.FragmentRecurringTaskListBinding;

import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.EquipmentService;
import com.example.myhobitapplication.services.BattleService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.ProfileViewModel;
import com.example.myhobitapplication.viewModels.taskViewModels.RecurringTaskListViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RecurringTaskViewFragment extends Fragment {



    private FragmentRecurringTaskListBinding binding;
    private List<RecurringTaskDTO> recurringTaskDTOS;

    private RecurringTaskListViewModel viewModel;

    private RecurringTaskListAdapter taskItemsAdapter;

    private RecyclerView recyclerView;
    private ProfileViewModel profileViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRecurringTaskListBinding.inflate(inflater, container, false);

        recyclerView = binding.rtaskRecycler;
        this.profileViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ProfileViewModel(requireContext(),new BossService(new BossRepository(requireContext())),new EquipmentService(new EquipmentRepository(requireContext())));
            }
        }).get(ProfileViewModel.class);

        TaskRepository taskRepository = new TaskRepository(getContext());
        ProfileService profileService =  ProfileService.getInstance();
        BossRepository bossRepository = new BossRepository(getContext());
        BossService bossService = new BossService(bossRepository);
        BattleService battleService = new BattleService(bossService, profileService);
        TaskService taskService = new TaskService(taskRepository, profileService,battleService);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new RecurringTaskListViewModel(taskService);
            }
        }).get(RecurringTaskListViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupListenersAndObservers();
        viewModel.loadRecurringTasks();
    }


    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.rtaskRecycler;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        taskItemsAdapter = new RecurringTaskListAdapter(new ArrayList<>(), viewModel);
        recyclerView.setAdapter(taskItemsAdapter);
    }

    private void setupListenersAndObservers() {

        viewModel.getRecurringTasks().observe(getViewLifecycleOwner(), newTasks -> {
            if (newTasks != null) {
                taskItemsAdapter.updateData(newTasks);
            }
        });
        profileViewModel.levelUpEvent.observe(getViewLifecycleOwner(), newLevel -> {
            if (newLevel != null) {
                showLevelUpDialog(newLevel);
                profileViewModel.onLevelUpEventHandled();
            }
        });
        viewModel.getNavigateToTaskDetails().observe(getViewLifecycleOwner(), task -> {
            if (task != null) {
                Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                intent.putExtra("TASK_ID_EXTRA", task.getId());
                intent.putExtra("TASK_TYPE_EXTRA", "RECURRING");
                startActivity(intent);
                viewModel.onTaskDetailsNavigated();
            }
        });

        requireActivity().getSupportFragmentManager().setFragmentResultListener("taskUpdated_ForList", getViewLifecycleOwner(), (requestKey, bundle) -> {
            Toast.makeText(getContext(), "Lista zadataka je primila signal!", Toast.LENGTH_SHORT).show();
            viewModel.loadRecurringTasks();
        });



        binding.btnAddNewTask.setOnClickListener(v -> {
            RecurringTaskFragment createNewTaskFragment = new RecurringTaskFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, createNewTaskFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadRecurringTasks();
    }



//    private void populateView(){
//
//        RecyclerView recyclerView = binding.rtaskRecycler;
//
//        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
//
//
//        taskItemsAdapter = new RecurringTaskListAdapter(new ArrayList<>(), viewModel);
//
//
//        recyclerView.setAdapter(taskItemsAdapter);
//
//        viewModel.getNavigateToTaskDetails().observe(getViewLifecycleOwner(), task -> {
//            if (task != null) {
//
//
//                Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
//                intent.putExtra("TASK_ID_EXTRA", task.getId());
//                intent.putExtra("TASK_TYPE_EXTRA", "RECURRING");
//
//                startActivity(intent);
//
//                viewModel.onTaskDetailsNavigated();
//            }
//        });
//
//        viewModel.getRecurringTasks().observe(getViewLifecycleOwner(), newTasks -> {
//            if (newTasks != null) {
//                taskItemsAdapter.updateData(newTasks);
//            }
//        });
//
//        binding.btnAddNewTask.setOnClickListener(v -> {
//
//            Toast.makeText(getContext(), "Otvaram ekran za novi zadatak...", Toast.LENGTH_SHORT).show();
//            RecurringTaskFragment createNewTaskFragment = new RecurringTaskFragment();
//
//            getParentFragmentManager().beginTransaction()
//
//                    .replace(R.id.fragment_container, createNewTaskFragment)
//
//                    .addToBackStack(null)
//                    .commit();
//        });
//    }
private void showLevelUpDialog(int level) {
    final Dialog dialog = new Dialog(requireContext());


    dialog.setContentView(R.layout.level_up_dialog);


    if (dialog.getWindow() != null) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    LottieAnimationView firework1 = dialog.findViewById(R.id.firework1);


    firework1.setAnimation(R.raw.firework);


    firework1.setVisibility(View.VISIBLE);



    firework1.playAnimation();

    TextView messageTextView = dialog.findViewById(R.id.textViewLevelMessage);
    messageTextView.setText("Congratulations, you reached level " + level + "!");


    Button okButton = dialog.findViewById(R.id.buttonOK);
    okButton.setOnClickListener(v -> {
        dialog.dismiss();

        profileViewModel.onLevelUpEventHandled();
    });

    if (dialog.getWindow() != null) {

    }

    dialog.show();
}





}
