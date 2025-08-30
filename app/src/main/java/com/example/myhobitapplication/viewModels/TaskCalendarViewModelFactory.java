package com.example.myhobitapplication.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.myhobitapplication.services.TaskService;

public class TaskCalendarViewModelFactory implements ViewModelProvider.Factory {

    private final TaskService taskService;


    public TaskCalendarViewModelFactory(TaskService taskService) {
        this.taskService = taskService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(TaskCalendarViewModel.class)) {

            return (T) new TaskCalendarViewModel(taskService);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}