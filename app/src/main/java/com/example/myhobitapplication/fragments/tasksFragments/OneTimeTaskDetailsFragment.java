package com.example.myhobitapplication.fragments.tasksFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.activities.TaskEditActivity;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.FragmentOneTimeTaskDetailsBinding;
import com.example.myhobitapplication.enums.OneTimeTaskStatus;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.services.BattleService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.taskViewModels.OneTimeTaskDetailsViewModel;

import java.time.LocalDate;

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
        BossRepository bossRepository = new BossRepository(getContext());
        BossService bossService = new BossService(bossRepository);
        BattleService battleService = new BattleService(bossService, profileService);
        taskService = new TaskService(taskRepository, profileService, battleService);

        taskDetailsViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new OneTimeTaskDetailsViewModel(taskService,categoryRepository, taskRepository);
            }
        }).get(OneTimeTaskDetailsViewModel.class);

        if (taskId != -1) {
            taskDetailsViewModel.loadTaskDetails(taskId);
        }


        editTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        taskDetailsViewModel.loadTaskDetails(taskId);
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
                binding.importanceTextView.setText(String.valueOf(task.getImportance()));
                binding.startDateTextView.setText(task.getStartDate().toString());
                binding.timeTextView.setText(task.getExecutionTime().toString());
                binding.oStatus.setText(String.valueOf(task.getStatus()));

                try {
                    int color = Color.parseColor(task.getCategoryColour());

                    binding.categoryColorView.setBackgroundColor(color);

                } catch (IllegalArgumentException e) {
                    binding.categoryColorView.setBackgroundColor(Color.BLACK);
                }

                OneTimeTaskStatus status = task.getStatus();
                boolean isTaskInThePast = task.getStartDate().isBefore(LocalDate.now());

                if(isTaskInThePast){
                    binding.editTaskButton.setVisibility(View.GONE);
                    binding.deleteTaskButton.setVisibility(View.GONE);
                }

                if (status == OneTimeTaskStatus.COMPLETED ||
                        status == OneTimeTaskStatus.INCOMPLETE || status == OneTimeTaskStatus.PAUSED_COMPLETED ||
                        status == OneTimeTaskStatus.CANCELED) {

                    binding.editTaskButton.setVisibility(View.GONE);
                    binding.deleteTaskButton.setVisibility(View.GONE);
                    binding.btnOtaskDone.setVisibility(View.GONE);
                    binding.btnOtaskCancel.setVisibility(View.GONE);
                    binding.btnOtaskPause.setVisibility(View.GONE);
                    binding.btnOtaskUnpause.setVisibility(View.GONE);


                } else if(!isTaskInThePast && status == OneTimeTaskStatus.ACTIVE ||
                        status == OneTimeTaskStatus.UNPAUSED) {
                    binding.editTaskButton.setVisibility(View.VISIBLE);
                    binding.deleteTaskButton.setVisibility(View.VISIBLE);
                    binding.btnOtaskDone.setVisibility(View.VISIBLE);
                    binding.btnOtaskCancel.setVisibility(View.VISIBLE);
                    binding.btnOtaskPause.setVisibility(View.VISIBLE);
                }
                else if(!isTaskInThePast && status == OneTimeTaskStatus.PAUSED) {
                    binding.editTaskButton.setVisibility(View.GONE);
                    binding.deleteTaskButton.setVisibility(View.GONE);
                    binding.btnOtaskDone.setVisibility(View.GONE);
                    binding.btnOtaskCancel.setVisibility(View.GONE);
                    binding.btnOtaskPause.setVisibility(View.GONE);
                    binding.btnOtaskUnpause.setVisibility(View.VISIBLE);
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

        binding.btnOtaskUnpause.setOnClickListener(v -> {

            taskDetailsViewModel.markTaskAsUnPaused();
        });

        binding.btnOtaskPause.setOnClickListener(v -> {

            taskDetailsViewModel.markTaskAsPaused();
        });

        binding.deleteTaskButton.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext(), R.style.AlertDialogWhiteText);
            dialog.setTitle("Delete?")
                    .setMessage("Are you sure you want to delete this recurring task?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            taskDetailsViewModel.deleteRecurringTask();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    });

            AlertDialog alert = dialog.create();
            if (alert.getWindow() != null) {
                alert.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.logincard));
            }
            alert.show();
        });

        taskDetailsViewModel.getTaskDeletedEvent().observe(getViewLifecycleOwner(), isDeleted -> {
            if (isDeleted != null && isDeleted) {
                Toast.makeText(getContext(), "Task successfully deleted.", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
                taskDetailsViewModel.onTaskDeletedEventHandled();
            }
        });

        taskDetailsViewModel.getTaskStatusUpdatedEvent().observe(getViewLifecycleOwner(), isUpdated -> {
            if (isUpdated != null && isUpdated) {
                Toast.makeText(getContext(), "Status updated!", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
                taskDetailsViewModel.onTaskStatusUpdatedEventHandled();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
