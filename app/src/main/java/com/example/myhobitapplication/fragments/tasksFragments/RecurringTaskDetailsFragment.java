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
import com.example.myhobitapplication.databinding.FragmentTaskDetailsBinding;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.taskViewModels.RecurringTaskDetailsViewModel;

import java.time.LocalDate;

public class RecurringTaskDetailsFragment extends Fragment {


    String categoryColour;
    String taskColour;
    TaskService taskService;
    TaskRepository taskRepository;

    CategoryService categoryService;
    CategoryRepository categoryRepository;

    RecurringTaskDetailsViewModel taskDetailsViewModel;

    FragmentTaskDetailsBinding binding;


    private ActivityResultLauncher<Intent> editTaskLauncher;


    private static final String ARG_TASK_ID = "taskId";

    private int taskId;

//    private final ActivityResultLauncher<Intent> editTaskLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                // OVAJ KOD SE IZVRŠAVA KADA SE VRATIMO IZ EditTaskActivity
//
//                // Proveravamo da li je rezultat "USPEŠAN"
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    // To je naš signal da su podaci promenjeni!
//                    Toast.makeText(getContext(), "Ažuriranje detalja...", Toast.LENGTH_SHORT).show();
//
//                    // Ponovo učitaj podatke da bi se prikaz osvežio
//                    taskDetailsViewModel.loadTaskDetails(taskId);
//
//                    requireActivity().getSupportFragmentManager().setFragmentResult("taskAddedRequest", new Bundle());
//                }
//                // Ako je resultCode bio RESULT_CANCELED (korisnik pritisnuo back), ne radimo ništa.
//            }
//    );


//    private final ActivityResultLauncher<Intent> editTaskLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    // Podaci su izmenjeni, osveži ovaj ekran
//                    taskDetailsViewModel.loadTaskDetails(taskId);
//
//                    // --- KLJUČNI DEO: PROSLEDI REZULTAT NAZAD ---
//                    // Javi TaskDetailActivity-ji da postavi svoj rezultat na OK
//                    if (getActivity() != null) {
//                        getActivity().setResult(Activity.RESULT_OK);
//                    }
//                }
//            }
//    );

    // Isto uradi i za brisanje!
//    taskDetailsViewModel.getTaskDeletedEvent().observe(getViewLifecycleOwner(), isDeleted -> {
//        if (isDeleted != null && isDeleted) {
//            Toast.makeText(getContext(), "Zadatak uspješno obrisan.", Toast.LENGTH_SHORT).show();
//
//            // --- KLJUČNI DEO: PROSLEDI REZULTAT NAZAD ---
//            if (getActivity() != null) {
//                // Postavi rezultat pre nego što se aktivnost zatvori
//                getActivity().setResult(Activity.RESULT_OK);
//                getActivity().finish();
//            }
//        }
//    });



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

        taskRepository = new TaskRepository(getContext());
        categoryRepository = new CategoryRepository(getContext());
        ProfileService profileService = new ProfileService();
        taskService = new TaskService(taskRepository, profileService);

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

                if (status == RecurringTaskStatus.COMPLETED ||
                        status == RecurringTaskStatus.INCOMPLETE ||
                        status == RecurringTaskStatus.CANCELED) {

                    binding.editTaskButton.setVisibility(View.GONE);
                    binding.deleteTaskButton.setVisibility(View.GONE);
                    binding.btnRctaskDone.setVisibility(View.GONE);
                    binding.btnRctaskCancel.setVisibility(View.GONE);
                    binding.btnRctaskPause.setVisibility(View.GONE);

                } else if(!isTaskInThePast && status == RecurringTaskStatus.ACTIVE ||
                        status == RecurringTaskStatus.PAUSED) {
                    binding.editTaskButton.setVisibility(View.VISIBLE);
                    binding.deleteTaskButton.setVisibility(View.VISIBLE);
                    binding.btnRctaskDone.setVisibility(View.VISIBLE);
                    binding.btnRctaskCancel.setVisibility(View.VISIBLE);
                    binding.btnRctaskPause.setVisibility(View.VISIBLE);
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

        binding.btnRctaskPause.setOnClickListener(v -> {

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
                taskDetailsViewModel.onTaskDeletedEventHandled(); // Resetuj event
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
