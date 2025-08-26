package com.example.myhobitapplication.viewModels;

import android.util.Log;

import androidx.collection.MutableObjectList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.services.TaskService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskCalendarViewModel extends ViewModel {

    private final TaskService taskService;

    private final MutableLiveData<List<RecurringTask>> _scheduledTasksLiveData = new MutableLiveData<>();

    public LiveData<List<RecurringTask>> getScheduledTasksLiveData() {
        return _scheduledTasksLiveData;
    }

    private final MutableLiveData<LocalDate> _selectedDate = new MutableLiveData<>();

    public LiveData<LocalDate> getSelectedDate() {
        return _selectedDate;
    }

    public TaskCalendarViewModel(TaskService taskService) {
        this.taskService = taskService;
        loadAllTasks();
    }

    private void loadAllTasks() {
        List<RecurringTask> allTasks = taskService.getAllTasks();
        _scheduledTasksLiveData.setValue(allTasks);
        Log.d("ViewModelDebug", "LiveData osvežen. Novi broj zadataka: " + (allTasks != null ? allTasks.size() : 0));
    }

    public void refreshScheduledTasks() {
        Log.d("ViewModelDebug", "Pozvana je refreshScheduledTasks metoda.");
        loadAllTasks();
    }

    public void selectDate(LocalDate date) {
        _selectedDate.setValue(date);
    }

    public List<RecurringTask> getTasksForDate(LocalDate date) {

        List<RecurringTask> currentTasks = _scheduledTasksLiveData.getValue();

        if (currentTasks == null || date == null) {
            return Collections.emptyList();
        }

        return currentTasks.stream()
                .filter(task -> date.equals(task.getStartDate()))
                .collect(Collectors.toList());
    }

}

