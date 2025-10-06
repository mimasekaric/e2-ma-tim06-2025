package com.example.myhobitapplication.fragments.tasksFragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhobitapplication.adapters.DifficultySpinnerAdapter;
import com.example.myhobitapplication.adapters.ImportanceSpinnerAdapter;
import com.example.myhobitapplication.databases.BossRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.FragmentRecurringTaskEditBinding;
import com.example.myhobitapplication.services.AllianceMissionService;
import com.example.myhobitapplication.services.BattleService;
import com.example.myhobitapplication.services.BossService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.services.UserService;
import com.example.myhobitapplication.viewModels.taskViewModels.RecurringTaskEditViewModel;

import java.time.LocalTime;

public class RecurringTaskEditFragment extends Fragment {

    TaskService taskService;
    TaskRepository taskRepository;


    RecurringTaskEditViewModel taskEditViewModel;

    FragmentRecurringTaskEditBinding binding;



    private static final String ARG_TASK_TO_EDIT_ID = "taskId";

    private int taskId;


    public static RecurringTaskEditFragment newInstance(int taskId) {
        RecurringTaskEditFragment fragment = new RecurringTaskEditFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TASK_TO_EDIT_ID, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getInt(ARG_TASK_TO_EDIT_ID);

        }

        taskRepository = new TaskRepository(getContext());
        ProfileService profileService =  ProfileService.getInstance();
        BossRepository bossRepository = new BossRepository(getContext());
        BossService bossService = new BossService(bossRepository);
        BattleService battleService = new BattleService(bossService, profileService);
        AllianceMissionService missionService = new AllianceMissionService(profileService);
        taskService =  TaskService.getInstance(taskRepository, profileService, battleService, missionService);


        taskEditViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new RecurringTaskEditViewModel(taskService);
            }
        }).get(RecurringTaskEditViewModel.class);

        if (taskId != -1) {
            taskEditViewModel.loadTaskDetails(taskId);
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecurringTaskEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.editTaskButton.setEnabled(false);
        taskEditViewModel.isFormValid().observe(getViewLifecycleOwner(), isValid -> {
            if (isValid != null) {
                binding.editTaskButton.setEnabled(isValid);
            }
        });

        taskEditViewModel.getTitleError().observe(getViewLifecycleOwner(), error -> {
            binding.taskTitleTextView.setError(error);
        });

        DifficultySpinnerAdapter difficultyAdapter = new DifficultySpinnerAdapter(requireContext());
        binding.difficultySpinner.setAdapter(difficultyAdapter);

        ImportanceSpinnerAdapter importanceAdapter = new ImportanceSpinnerAdapter(requireContext());
        binding.importanceSpinner.setAdapter(importanceAdapter);

        taskEditViewModel.getTaskDetails().observe(getViewLifecycleOwner(), task -> {
            if (task != null) {


                Integer currentDifficulty = task.getDifficulty();

                int difficultyPosition = difficultyAdapter.getPosition(currentDifficulty);

                if (difficultyPosition != -1) {
                    binding.difficultySpinner.setSelection(difficultyPosition);
                }

                Integer currentImportance = task.getImportance();
                int importancePosition = importanceAdapter.getPosition(currentImportance);
                if (importancePosition != -1) {
                    binding.importanceSpinner.setSelection(importancePosition);
                }

                binding.taskTitleTextView.setText(task.getName());
                binding.taskDescriptionTextView.setText(task.getDescription());
              //  binding.difficultyTextView.setText(String.valueOf(task.getDifficulty()));
              //  binding.importanceTextView.setText("");
                LocalTime initialTime = task.getExecutionTime();
                if (initialTime != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        binding.executionTimePicker.setHour(initialTime.getHour());
                        binding.executionTimePicker.setMinute(initialTime.getMinute());
                    } else {
                        binding.executionTimePicker.setCurrentHour(initialTime.getHour());
                        binding.executionTimePicker.setCurrentMinute(initialTime.getMinute());
                    }
                }

                try {
                    int color = Color.parseColor(task.getCategoryColour());

                    binding.categoryColorView.setBackgroundColor(color);

                } catch (IllegalArgumentException e) {
                    binding.categoryColorView.setBackgroundColor(Color.BLACK);
                }
            }
        });

        binding.difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer selectedDifficulty = (Integer) parent.getItemAtPosition(position);
                taskEditViewModel.setDifficulty(selectedDifficulty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.importanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer selectedImportance = (Integer) parent.getItemAtPosition(position);
                taskEditViewModel.setImportance(selectedImportance);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.taskTitleTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskEditViewModel.setTitle(s.toString());
            }

        });


        binding.taskDescriptionTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskEditViewModel.setDescription(s.toString());
            }

        });

        binding.editTaskButton.setOnClickListener(v -> {

            taskEditViewModel.editRecurringTask();

            Toast.makeText(requireContext(), "Task edited!", Toast.LENGTH_SHORT).show();

            requireActivity().getSupportFragmentManager().setFragmentResult("taskAddedRequest", new Bundle());
            requireActivity().getSupportFragmentManager().setFragmentResult("for_list_signal", new Bundle());
            // --- NOVI DEO: VRATI REZULTAT ---
            // Kreiraj prazan Intent. Ne trebaju nam podaci, samo signal.
            Intent resultIntent = new Intent();

            // Postavi rezultat na "USPEŠNO" (RESULT_OK) i priloži Intent
            // getActivity() se odnosi na EditTaskActivity
            getActivity().setResult(Activity.RESULT_OK, resultIntent);

            // Zatvori EditTaskActivity. Ovo će automatski poslati rezultat nazad.
            getActivity().finish();
        });

        binding.executionTimePicker.setOnTimeChangedListener((timePickerView, hourOfDay, minute) -> {
            LocalTime selectedTime = LocalTime.of(hourOfDay, minute);
            taskEditViewModel.setExecutionTime(selectedTime);
        });





    }



}
