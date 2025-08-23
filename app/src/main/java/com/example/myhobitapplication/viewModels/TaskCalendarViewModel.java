package com.example.myhobitapplication.viewModels;

import androidx.collection.MutableObjectList;
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

public class TaskCalendarViewModel extends ViewModel {

    private MutableLiveData<List<RecurringTask>> allTasks;
    private TaskService taskService;

    private final Map<LocalDate, List<RecurringTask>> scheduledTasks;

    public TaskCalendarViewModel(TaskService taskService){
        this.taskService = taskService;
        this.allTasks = new MutableLiveData<>();
        this.scheduledTasks = taskService.getScheduledTasks();
    }

    public MutableLiveData<List<RecurringTask>> getAllTasksLiveData() {
        return allTasks;
    }

    private void loadAllTasks() {
        allTasks.setValue(taskService.getRecurringTasks());
    }

    public List<RecurringTask> getTasksForDate(LocalDate date) {

        List<RecurringTask> tasks = scheduledTasks.get(date);
        if (tasks != null) {
            return tasks;
        } else {
            return Collections.emptyList();
        }
    }









}
