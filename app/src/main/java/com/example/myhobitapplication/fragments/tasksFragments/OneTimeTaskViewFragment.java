package com.example.myhobitapplication.fragments.tasksFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.activities.TaskDetailActivity;
import com.example.myhobitapplication.adapters.OneTimeTaskListAdapter;
import com.example.myhobitapplication.adapters.RecurringTaskListAdapter;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.FragmentOnetimeTaskListBinding;
import com.example.myhobitapplication.dto.OneTimeTaskDTO;
import com.example.myhobitapplication.services.BattleService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.taskViewModels.OneTimeTaskListViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class OneTimeTaskViewFragment extends Fragment {


    private FragmentOnetimeTaskListBinding binding;
    private List<OneTimeTaskDTO> oneTimeTaskDTOS;

    private OneTimeTaskListViewModel viewModel;

    private OneTimeTaskListAdapter taskItemsAdapter;

    private ActivityResultLauncher<Intent> taskDetailsLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskDetailsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getContext(), "Refreshing One-Time tasks...", Toast.LENGTH_SHORT).show();
                        viewModel.loadRecurringTasks();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentOnetimeTaskListBinding.inflate(inflater, container, false);

        TaskRepository taskRepository = new TaskRepository(getContext());
        ProfileService profileService = new ProfileService();
        BossRepository bossRepository = new BossRepository(getContext());
        BossService bossService = new BossService(bossRepository);
        BattleService battleService = new BattleService(bossService, profileService);
        TaskService taskService = new TaskService(taskRepository, profileService, battleService);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new OneTimeTaskListViewModel(taskService);
            }
        }).get(OneTimeTaskListViewModel.class);

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
        RecyclerView recyclerView = binding.otaskRecycler;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        taskItemsAdapter = new OneTimeTaskListAdapter(new ArrayList<>(), viewModel);
        recyclerView.setAdapter(taskItemsAdapter);
    }

    private void setupListenersAndObservers() {

        viewModel.getOneTimeTasks().observe(getViewLifecycleOwner(), newTasks -> {
            if (newTasks != null) {
                taskItemsAdapter.updateData(newTasks);
            }
        });

        viewModel.getNavigateToTaskDetails().observe(getViewLifecycleOwner(), task -> {
            if (task != null) {
                Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                intent.putExtra("TASK_ID_EXTRA", task.getId());
                intent.putExtra("TASK_TYPE_EXTRA", "ONETIME");
                taskDetailsLauncher.launch(intent);
                viewModel.onTaskDetailsNavigated();
            }
        });

        getParentFragmentManager().setFragmentResultListener("for_list_signal", getViewLifecycleOwner(), (requestKey, bundle) -> {
            Toast.makeText(getContext(), "Lista se osvježava...", Toast.LENGTH_SHORT).show();
            viewModel.loadRecurringTasks();
        });

        binding.btnAddNewTask.setOnClickListener(v -> {
            OneTimeTaskFragment createNewTaskFragment = new OneTimeTaskFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, createNewTaskFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}
