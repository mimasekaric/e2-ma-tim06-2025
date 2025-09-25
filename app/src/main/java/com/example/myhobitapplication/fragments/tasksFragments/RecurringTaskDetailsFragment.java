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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myhobitapplication.R;
import com.example.myhobitapplication.activities.TaskEditActivity;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.EquipmentRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.FragmentTaskDetailsBinding;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.services.BattleService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.EquipmentService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.ProfileViewModel;
import com.example.myhobitapplication.viewModels.taskViewModels.RecurringTaskDetailsViewModel;

import java.time.LocalDate;

public class RecurringTaskDetailsFragment extends Fragment {


    String categoryColour;
    String taskColour;
    TaskService taskService;
    TaskRepository taskRepository;
    ProfileViewModel profileViewModel;
    CategoryService categoryService;
    CategoryRepository categoryRepository;

    RecurringTaskDetailsViewModel taskDetailsViewModel;

    FragmentTaskDetailsBinding binding;



    private ActivityResultLauncher<Intent> editTaskLauncher;


    private static final String ARG_TASK_ID = "taskId";

    private int taskId;

    public static RecurringTaskDetailsFragment newInstance(int taskId) {
        RecurringTaskDetailsFragment fragment = new RecurringTaskDetailsFragment();
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
        profileViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ProfileViewModel(requireContext(),new BossService(new BossRepository(requireContext())),new EquipmentService(new EquipmentRepository(requireContext())));
            }
        }).get(ProfileViewModel.class);

        taskRepository = new TaskRepository(getContext());
        categoryRepository = new CategoryRepository(getContext());
        ProfileService profileService =  ProfileService.getInstance();
        BossRepository bossRepository = new BossRepository(getContext());
        BossService bossService = new BossService(bossRepository);
        BattleService battleService = new BattleService(bossService, profileService);
        taskService = new TaskService(taskRepository, profileService,battleService);

        taskDetailsViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new RecurringTaskDetailsViewModel(taskService,categoryRepository,taskRepository);
            }
        }).get(RecurringTaskDetailsViewModel.class);

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
        binding = FragmentTaskDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskDetailsViewModel.getTaskDetails().observe(getViewLifecycleOwner(), task -> {
            if (task != null) {

                binding.categoryName.setText(task.getCategoryColour());
                binding.taskTitleTextView.setText(task.getName());
                binding.taskDescriptionTextView.setText(task.getDescription());
                binding.difficultyTextView.setText(String.valueOf(task.getDifficulty()));
                binding.importanceTextView.setText(String.valueOf(task.getImportance()));
                binding.recurrenceTextView.setText(String.valueOf(task.getRecurrenceInterval()));
                binding.endDateTextView.setText(task.getEndDate().toString());
                binding.startDateTextView.setText(task.getStartDate().toString());
                binding.timeTextView.setText(task.getExecutionTime().toString());
                binding.rctStatus.setText(String.valueOf(task.getStatus()));

                try {
                   int color = Color.parseColor(task.getCategoryColour());

                  binding.categoryColorView.setBackgroundColor(color);

               } catch (IllegalArgumentException e) {
                   binding.categoryColorView.setBackgroundColor(Color.BLACK);
             }


                RecurringTaskStatus status = task.getStatus();
                boolean isTaskInThePast = task.getStartDate().isBefore(LocalDate.now());

                if(isTaskInThePast){
                    binding.editTaskButton.setVisibility(View.GONE);
                    binding.deleteTaskButton.setVisibility(View.GONE);
                }

                if (status == RecurringTaskStatus.COMPLETED || status == RecurringTaskStatus.PAUSED_COMPLETED ||
                        status == RecurringTaskStatus.INCOMPLETE ||
                        status == RecurringTaskStatus.CANCELED) {

                    binding.editTaskButton.setVisibility(View.GONE);
                    binding.deleteTaskButton.setVisibility(View.GONE);
                    binding.btnRctaskDone.setVisibility(View.GONE);
                    binding.btnRctaskCancel.setVisibility(View.GONE);
                    binding.btnRctaskPause.setVisibility(View.GONE);
                    binding.btnRctaskUnpause.setVisibility(View.GONE);

                }
                else if(!isTaskInThePast && status == RecurringTaskStatus.ACTIVE ||
                        status == RecurringTaskStatus.UNPAUSED) {
                    binding.editTaskButton.setVisibility(View.VISIBLE);
                    binding.deleteTaskButton.setVisibility(View.VISIBLE);
                    binding.btnRctaskDone.setVisibility(View.VISIBLE);
                    binding.btnRctaskCancel.setVisibility(View.VISIBLE);
                    binding.btnRctaskPause.setVisibility(View.VISIBLE);
                    binding.btnRctaskUnpause.setVisibility(View.GONE);
                }
                    else if(!isTaskInThePast && status == RecurringTaskStatus.PAUSED) {
                    binding.editTaskButton.setVisibility(View.GONE);
                    binding.deleteTaskButton.setVisibility(View.GONE);
                    binding.btnRctaskDone.setVisibility(View.GONE);
                    binding.btnRctaskCancel.setVisibility(View.GONE);
                    binding.btnRctaskPause.setVisibility(View.GONE);
                    binding.btnRctaskUnpause.setVisibility(View.VISIBLE);
                }
           }
        });

//        taskDetailsViewModel.getTaskDeletedEvent().observe(getViewLifecycleOwner(), isDeleted -> {
//
//            if (isDeleted != null && isDeleted) {
//
//                Toast.makeText(getContext(), "Zadatak uspješno obrisan.", Toast.LENGTH_SHORT).show();
//
//                requireActivity().getSupportFragmentManager().setFragmentResult("taskAddedRequest", new Bundle());
//
//                if (getActivity() != null) {
//                    getActivity().finish();
//                }
//            }
//        });
        //binding.editTaskButton.setVisibility();


        binding.editTaskButton.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), TaskEditActivity.class);
            intent.putExtra("TASK_ID_TO_EDIT", taskId);
            intent.putExtra("TASK_TYPE_EXTRA", "RECURRING");
            editTaskLauncher.launch(intent);

        });

        binding.btnRctaskDone.setOnClickListener(v -> {

            taskDetailsViewModel.markTaskAsDone();
        });

        binding.btnRctaskCancel.setOnClickListener(v -> {

            taskDetailsViewModel.markTaskAsCanceled();
        });

        binding.btnRctaskUnpause.setOnClickListener(v -> {

            taskDetailsViewModel.markTaskAsUnPaused();
        });

        binding.btnRctaskPause.setOnClickListener(v -> {

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
        profileViewModel.levelUpEvent.observe(getViewLifecycleOwner(), newLevel -> {
            if (newLevel != null) {
                showLevelUpDialog(newLevel);
                profileViewModel.onLevelUpEventHandled();
            }
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
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
