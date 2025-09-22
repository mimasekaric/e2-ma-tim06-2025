package com.example.myhobitapplication.fragments.tasksFragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import com.example.myhobitapplication.adapters.CategorySpinnerAdapter;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.FragmentRecurringTaskBinding;
import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.ProfileService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.categoryViewModels.CategoryViewModel;
import com.example.myhobitapplication.viewModels.taskViewModels.RecurringTaskViewModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class RecurringTaskFragment extends Fragment {

    private RecurringTaskViewModel taskViewModel;
    private CategoryViewModel categoryViewModel;
    private FragmentRecurringTaskBinding recurringTaskBinding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TaskRepository repository = new TaskRepository(requireContext());
        CategoryRepository categoryRepository = new CategoryRepository(requireContext());
        ProfileService profileService = new ProfileService();
        TaskService taskService = new TaskService(repository, profileService);
        CategoryService categoryService = new CategoryService(categoryRepository,repository);





        taskViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new RecurringTaskViewModel(taskService);
            }
        }).get(RecurringTaskViewModel.class);

        categoryViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new CategoryViewModel(categoryService);
            }
        }).get(CategoryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recurringTaskBinding = FragmentRecurringTaskBinding.inflate(inflater, container, false);
        return recurringTaskBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ScrollView scrollView = recurringTaskBinding.rtScrollView;
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setFocusable(false);

        long today = System.currentTimeMillis();
        recurringTaskBinding.rtDateStart.setMinDate(today);
        recurringTaskBinding.rtDateEnd.setMinDate(today);

        android.widget.NumberPicker numberPicker = recurringTaskBinding.etRecurrenceInterval;

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(99);

        numberPicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });



        taskViewModel.getDifficultyXp().observe(getViewLifecycleOwner(), difficultyValue -> {
            if (difficultyValue != null) {

                for (int i = 0; i < recurringTaskBinding.rgDifficultyOptions.getChildCount(); i++) {
                    View radioButton = recurringTaskBinding.rgDifficultyOptions.getChildAt(i);

                    if (radioButton.getTag() != null && radioButton.getTag().toString().equals(String.valueOf(difficultyValue))) {

                        ((android.widget.RadioButton) radioButton).setChecked(true);
                        break;
                    }
                }
            }
        });


        taskViewModel.getImportanceXp().observe(getViewLifecycleOwner(), importanceValue -> {
            if (importanceValue != null) {
                for (int i = 0; i < recurringTaskBinding.rgImportanceOptions.getChildCount(); i++) {
                    View radioButton = recurringTaskBinding.rgImportanceOptions.getChildAt(i);
                    if (radioButton.getTag() != null && radioButton.getTag().toString().equals(String.valueOf(importanceValue))) {
                        ((android.widget.RadioButton) radioButton).setChecked(true);
                        break;
                    }
                }
            }
        });



        taskViewModel.isFormValid().observe(getViewLifecycleOwner(), isValid -> {
            if (isValid != null) {
                recurringTaskBinding.btnRtask.setEnabled(isValid);
            }
        });

        taskViewModel.getTitleError().observe(getViewLifecycleOwner(), error -> {
            recurringTaskBinding.rtaskName.setError(error);
        });


        recurringTaskBinding.rgDifficultyOptions.setOnCheckedChangeListener((group, checkedId) -> {
            View radioButton = group.findViewById(checkedId);
            if (radioButton != null && radioButton.getTag() != null) {
                int xpValue = Integer.parseInt(radioButton.getTag().toString());
                taskViewModel.setDifficultyXp(xpValue);
            }
        });

        recurringTaskBinding.rgImportanceOptions.setOnCheckedChangeListener((group, checkedId) -> {
            View radioButton = group.findViewById(checkedId);
            if (radioButton != null && radioButton.getTag() != null) {
                int xpValue = Integer.parseInt(radioButton.getTag().toString());
                taskViewModel.setImportanceXp(xpValue);
            }
        });



        recurringTaskBinding.rtaskName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskViewModel.setTitle(s.toString());
            }

        });

        recurringTaskBinding.rtaskDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskViewModel.setDescription(s.toString());
            }

        });

        recurringTaskBinding.rtaskTime.setOnTimeChangedListener((timePickerView, hourOfDay, minute) -> {
            LocalTime selectedTime = LocalTime.of(hourOfDay, minute);
            taskViewModel.setExecutionTime(selectedTime);
        });

        recurringTaskBinding.etRecurrenceInterval.setOnValueChangedListener((picker, oldVal, newVal) -> {
            taskViewModel.setRecurrenceInterval(newVal);
        });


        recurringTaskBinding.spinnerRecurrenceUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RecurrenceUnit unit = null;
                switch (position) {
                    case 0:
                        unit = RecurrenceUnit.DAY;
                        break;
                    case 1:
                        unit = RecurrenceUnit.WEEK;
                        break;
                    case 2:
                        unit = RecurrenceUnit.MONTH;
                        break;

                }
                taskViewModel.setRecurrenceUnit(unit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recurringTaskBinding.rtDateEnd.init(

                recurringTaskBinding.rtDateEnd.getYear(),
                recurringTaskBinding.rtDateEnd.getMonth(),
                recurringTaskBinding.rtDateEnd.getDayOfMonth(),
                (picker, year, month, day) -> {
                    LocalDate selectedDate = LocalDate.of(year, month + 1, day);
                    taskViewModel.setEndDate(selectedDate);
                }
        );


        recurringTaskBinding.rtDateStart.init(
                recurringTaskBinding.rtDateStart.getYear(),
                recurringTaskBinding.rtDateStart.getMonth(),
                recurringTaskBinding.rtDateStart.getDayOfMonth(),
                (picker, year, month, day) -> {
                    LocalDate selectedDate = LocalDate.of(year, month + 1, day);
                    taskViewModel.setStartDate(selectedDate);
                }
        );

        recurringTaskBinding.btnRtask.setOnClickListener(v -> {

            taskViewModel.saveRecurringTask();
        });


        CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(requireContext(), new ArrayList<>());
        recurringTaskBinding.categorySpinner.setAdapter(adapter);


        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), newCategories -> {
            adapter.clear();
            adapter.addAll(newCategories);
            adapter.notifyDataSetChanged();
        });

        recurringTaskBinding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = (Category) parent.getItemAtPosition(position);
                taskViewModel.setCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        taskViewModel.getExecutionTimeError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
               // recurringTaskBinding.timeErrorTextView.setText(error);
              //  recurringTaskBinding.timeErrorTextView.setVisibility(View.VISIBLE);
            } else {
               // recurringTaskBinding.timeErrorTextView.setVisibility(View.GONE);
            }
        });

        taskViewModel.getSubmissionError().observe(getViewLifecycleOwner(), error -> {

            if (error != null && !error.isEmpty()) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Error with saving task!")
                        .setMessage(error)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        taskViewModel.getSaveSuccessEvent().observe(getViewLifecycleOwner(), isSuccess -> {

            if (isSuccess != null && isSuccess) {

                Toast.makeText(requireContext(), "Zadatak je uspešno kreiran!", Toast.LENGTH_SHORT).show();

                requireActivity().getSupportFragmentManager().setFragmentResult("taskAddedRequest", new Bundle());
                requireActivity().getSupportFragmentManager().setFragmentResult("taskUpdated_ForList", new Bundle());
                // Opciono: Očisti formu ili se vrati na prethodni ekran
                // getParentFragmentManager().popBackStack();
                taskViewModel.onSaveSuccessEventHandled();
            }
        });








    }






}
