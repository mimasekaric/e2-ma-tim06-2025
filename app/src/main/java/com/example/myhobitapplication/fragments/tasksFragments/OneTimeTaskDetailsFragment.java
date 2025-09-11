package com.example.myhobitapplication.fragments.tasksFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
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

import com.example.myhobitapplication.activities.TaskEditActivity;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.FragmentOneTimeTaskDetailsBinding;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.taskViewModels.OneTimeTaskDetailsViewModel;

public class OneTimeTaskDetailsFragment extends Fragment {

    String categoryColour;
    String taskColour;
    TaskService taskService;
    TaskRepository taskRepository;

    CategoryService categoryService;
    CategoryRepository categoryRepository;

    OneTimeTaskDetailsViewModel taskDetailsViewModel;

    FragmentOneTimeTaskDetailsBinding binding;


    private ActivityResultLauncher<Intent> editTaskLauncher;


    private static final String ARG_TASK_ID = "taskId";

    private int taskId;


    public static OneTimeTaskDetailsFragment newInstance(int taskId) {
        OneTimeTaskDetailsFragment fragment = new OneTimeTaskDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TASK_ID, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getInt(ARG_TASK_ID);

        }

        taskRepository = new TaskRepository(getContext());
        categoryRepository = new CategoryRepository(getContext());
        ProfileService profileService = new ProfileService();
        taskService = new TaskService(taskRepository, profileService);

        taskDetailsViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new OneTimeTaskDetailsViewModel(taskService,categoryRepository);
            }
        }).get(OneTimeTaskDetailsViewModel.class);

        if (taskId != -1) {
            taskDetailsViewModel.loadTaskDetails(taskId);
        }


        editTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Podaci su izmenjeni, osveži ovaj ekran
                        taskDetailsViewModel.loadTaskDetails(taskId);
                        // Javi prethodnoj aktivnosti da postavi svoj rezultat na OK
                        if (getActivity() != null) {
                            getActivity().setResult(Activity.RESULT_OK);
                        }
                    }
                }
        );


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOneTimeTaskDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskDetailsViewModel.getTaskDetails().observe(getViewLifecycleOwner(), task -> {
            if (task != null) {


                binding.taskTitleTextView.setText(task.getName());
                binding.taskDescriptionTextView.setText(task.getDescription());
                binding.difficultyTextView.setText(String.valueOf(task.getDifficulty()));
                binding.importanceTextView.setText("");
                binding.startDateTextView.setText(task.getStartDate().toString());
                binding.timeTextView.setText(task.getExecutionTime().toString());
                binding.otStatus.setText(String.valueOf(task.getStatus()));

                try {
                    int color = Color.parseColor(task.getCategoryColour());

                    binding.categoryColorView.setBackgroundColor(color);

                } catch (IllegalArgumentException e) {
                    binding.categoryColorView.setBackgroundColor(Color.BLACK);
                }
            }
        });


        binding.editTaskButton.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), TaskEditActivity.class);
            intent.putExtra("TASK_ID_TO_EDIT", taskId);
            intent.putExtra("TASK_TYPE_EXTRA", "ONETIME");
            editTaskLauncher.launch(intent);

        });

        binding.btnOtaskDone.setOnClickListener(v -> {

            taskDetailsViewModel.markTaskAsDone();
        });

        binding.btnOtaskCancel.setOnClickListener(v -> {

            taskDetailsViewModel.markTaskAsCanceled();
        });

        binding.btnOtaskPause.setOnClickListener(v -> {

            taskDetailsViewModel.markTaskAsPaused();
        });

        binding.deleteTaskButton.setOnClickListener(v -> {

            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete?")
                    .setMessage("Are you shure you want to delete this recurring task?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        taskDetailsViewModel.deleteRecurringTask();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        taskDetailsViewModel.getTaskDeletedEvent().observe(getViewLifecycleOwner(), isDeleted -> {
            if (isDeleted != null && isDeleted) {
                Toast.makeText(getContext(), "Zadatak uspešno obrisan.", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
                taskDetailsViewModel.onTaskDeletedEventHandled();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
