package com.example.myhobitapplication.viewModels.taskViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.OneTimeTaskDTO;
import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.services.TaskService;

public class OneTimeTaskEditViewModel extends ViewModel {


    private final MutableLiveData<Boolean> _isFormDirty = new MutableLiveData<>(false);

    private final TaskService taskService;
    private final MutableLiveData<OneTimeTaskDTO> taskDetails = new MutableLiveData<>();

    private final MutableLiveData<Integer> difficulty = new MutableLiveData<>();
    private final MutableLiveData<Integer> importance = new MutableLiveData<>();

    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<String> description = new MutableLiveData<>();
    public MutableLiveData<Integer> getDifficulty() {  return difficulty;}
    public MutableLiveData<Integer> getImportance() {  return importance;}
    public LiveData<String> getTitle() { return title; }
    public LiveData<String> getDescription() { return description; }
    public void setDifficulty(Integer difficultyData) { difficulty.setValue(difficultyData); }
    public void setImportance(Integer importanceData) { importance.setValue(importanceData); }
    public void setTitle(String newTitle) { title.setValue(newTitle); }
    public void setDescription(String newDescription) { description.setValue(newDescription); }
    public OneTimeTaskEditViewModel(TaskService taskService) {
        this.taskService = taskService;

    }

    public LiveData<Boolean> isFormDirty() {
        return _isFormDirty;
    }
    public LiveData<OneTimeTaskDTO> getTaskDetails() {
        return taskDetails;
    }

    public void loadTaskDetails(long taskId) {

        OneTimeTaskDTO oneTimeTaskDTO = taskService.getOneTimeTaskById(taskId);

        if (oneTimeTaskDTO != null) {


            taskDetails.setValue(oneTimeTaskDTO);
        }
    }

    public void editOneTimeTask(){

        OneTimeTaskDTO originalDto = taskDetails.getValue();

        if (originalDto == null) {
            return;
        }

        if (title.getValue() != null) {
            originalDto.setName(title.getValue());
        }


        if (description.getValue() != null) {
            originalDto.setDescription(description.getValue());
        }

        if (difficulty.getValue() != null) {
            originalDto.setDifficulty(difficulty.getValue());
        }

        if (importance.getValue() != null) {
            originalDto.setImportance(importance.getValue());
        }

        taskService.editOneTimeTask(taskDetails.getValue());


    }













}
