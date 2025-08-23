package com.example.myhobitapplication.fragments;

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
import com.example.myhobitapplication.databases.DataBaseRecurringTaskHelper;
import com.example.myhobitapplication.databinding.FragmentRecurringTaskBinding;
import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.services.TaskService;
import com.example.myhobitapplication.viewModels.TaskViewModel;

import java.time.LocalDate;
import java.time.LocalTime;

public class RecurringTaskFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private FragmentRecurringTaskBinding recurringTaskBinding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBaseRecurringTaskHelper db = new DataBaseRecurringTaskHelper(requireContext());
        TaskService taskService = new TaskService(db);



        taskViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new TaskViewModel(taskService);
            }
        }).get(TaskViewModel.class);
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
                // Godina, mesec i dan se automatski postavljaju
                recurringTaskBinding.rtDateEnd.getYear(),
                recurringTaskBinding.rtDateEnd.getMonth(),
                recurringTaskBinding.rtDateEnd.getDayOfMonth(),
                (picker, year, month, day) -> {
                    // Zapamti da je mesec u DatePickeru 0-baziran (0-11)
                    // Stoga dodajemo +1 da bismo ga konvertovali u LocalDate format (1-12)
                    LocalDate selectedDate = LocalDate.of(year, month + 1, day);
                    taskViewModel.setEndDate(selectedDate);
                }
        );

        // Listener za krajnji datum
        recurringTaskBinding.rtDateStart.init(
                // Godina, mesec i dan se automatski postavljaju
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

            Toast.makeText(requireContext(), "Zadatak je uspešno kreiran!", Toast.LENGTH_SHORT).show();
        });


    }
}
