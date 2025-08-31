package com.example.myhobitapplication.fragments.tasksFragments;

import android.os.Bundle;
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

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.adapters.CategorySpinnerAdapter;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.databinding.FragmentOnetimeTaskBinding;
import com.example.myhobitapplication.databinding.FragmentRecurringTaskBinding;
import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.CategoryViewModel;
import com.example.myhobitapplication.viewModels.taskViewModels.OneTimeTaskViewModel;
import com.example.myhobitapplication.viewModels.taskViewModels.RecurringTaskViewModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class OneTimeTaskFragment extends Fragment {


    private OneTimeTaskViewModel taskViewModel;
    private CategoryViewModel categoryViewModel;
    private FragmentOnetimeTaskBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TaskRepository repository = new TaskRepository(requireContext());
        CategoryRepository categoryRepository = new CategoryRepository(requireContext());
        TaskService taskService = new TaskService(repository);
        CategoryService categoryService = new CategoryService(categoryRepository);



        taskViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new OneTimeTaskViewModel(taskService);
            }
        }).get(OneTimeTaskViewModel.class);

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
        binding = FragmentOnetimeTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ScrollView scrollView = binding.rtScrollView;
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setFocusable(false);


        binding.rbDifficultyOptions.setOnCheckedChangeListener((group, checkedId) -> {
            View radioButton = group.findViewById(checkedId);
            if (radioButton != null && radioButton.getTag() != null) {
                int xpValue = Integer.parseInt(radioButton.getTag().toString());
                taskViewModel.setDifficultyXp(xpValue);
            }
        });

        binding.rgImportanceOptions.setOnCheckedChangeListener((group, checkedId) -> {
            View radioButton = group.findViewById(checkedId);
            if (radioButton != null && radioButton.getTag() != null) {
                int xpValue = Integer.parseInt(radioButton.getTag().toString());
                taskViewModel.setImportanceXp(xpValue);
            }
        });



        binding.otaskName.addTextChangedListener(new TextWatcher() {
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

        binding.otaskDescription.addTextChangedListener(new TextWatcher() {
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

        binding.otaskTime.setOnTimeChangedListener((timePickerView, hourOfDay, minute) -> {
            LocalTime selectedTime = LocalTime.of(hourOfDay, minute);
            taskViewModel.setExecutionTime(selectedTime);
        });




        binding.otDateStart.init(
                binding.otDateStart.getYear(),
                binding.otDateStart.getMonth(),
                binding.otDateStart.getDayOfMonth(),
                (picker, year, month, day) -> {
                    LocalDate selectedDate = LocalDate.of(year, month + 1, day);
                    taskViewModel.setStartDate(selectedDate);
                }
        );

        binding.btnOtask.setOnClickListener(v -> {


            taskViewModel.saveRecurringTask();

            Toast.makeText(requireContext(), "Zadatak je uspešno kreiran!", Toast.LENGTH_SHORT).show();

            getParentFragmentManager().setFragmentResult("taskAddedRequest", new Bundle());

        });


        CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(requireContext(), new ArrayList<>());
        binding.categorySpinner.setAdapter(adapter);


        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), newCategories -> {
            adapter.clear();
            adapter.addAll(newCategories);
            adapter.notifyDataSetChanged();
        });

        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = (Category) parent.getItemAtPosition(position);
                taskViewModel.setCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }







}
