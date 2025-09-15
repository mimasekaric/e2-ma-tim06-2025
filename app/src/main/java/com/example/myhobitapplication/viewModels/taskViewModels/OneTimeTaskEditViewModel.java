package com.example.myhobitapplication.viewModels.taskViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.OneTimeTaskDTO;
import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.exceptions.ValidationException;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.services.TaskService;

import java.time.LocalDate;
import java.time.LocalTime;

public class OneTimeTaskEditViewModel extends ViewModel {


    private final MutableLiveData<Boolean> _isFormDirty = new MutableLiveData<>(false);

    private final TaskService taskService;
    private final MutableLiveData<OneTimeTaskDTO> taskDetails = new MutableLiveData<>();

    private final MutableLiveData<Integer> difficulty = new MutableLiveData<>();
    private final MutableLiveData<Integer> importance = new MutableLiveData<>();

    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> startDate = new MutableLiveData<LocalDate>(LocalDate.now());
    private final MutableLiveData<LocalTime> executionTime = new MutableLiveData<>(LocalTime.now());

    private final MutableLiveData<String> description = new MutableLiveData<>();
    public MutableLiveData<Integer> getDifficulty() {  return difficulty;}
    public MutableLiveData<Integer> getImportance() {  return importance;}
    public LiveData<String> getTitle() { return title; }
    public LiveData<String> getDescription() { return description; }
    public MutableLiveData<LocalDate> getStartDate() {  return startDate;}
    public void setDifficulty(Integer difficultyData) { difficulty.setValue(difficultyData); }
    public void setImportance(Integer importanceData) { importance.setValue(importanceData); }
    public void setTitle(String newTitle) { title.setValue(newTitle); validateForm(); }
    public void setDescription(String newDescription) { description.setValue(newDescription); }
    public void setStartDate(LocalDate date) { startDate.setValue(date); validateForm();}
    private final MutableLiveData<String> _executionTimeError = new MutableLiveData<>(null);
    public LiveData<String> getExecutionTimeError() { return _executionTimeError; }
    private final MutableLiveData<String> _titleError = new MutableLiveData<>(null);
    public LiveData<String> getTitleError() { return _titleError; }
    private final MutableLiveData<Boolean> _isFormValid = new MutableLiveData<>(false);
    public LiveData<Boolean> isFormValid() { return _isFormValid; }

    private final MutableLiveData<String> _submissionError = new MutableLiveData<>();
    public LiveData<String> getSubmissionError() { return _submissionError; }

    private final MutableLiveData<Boolean> _saveSuccessEvent = new MutableLiveData<>();
    public LiveData<Boolean> getSaveSuccessEvent() { return _saveSuccessEvent; }
    public void onSaveSuccessEventHandled() {
        _saveSuccessEvent.setValue(null);
    }

    public void setExecutionTime(LocalTime time) { executionTime.setValue(time); validateForm(); }
    public MutableLiveData<LocalTime> getExecutionTime() {  return executionTime;}

    public OneTimeTaskEditViewModel(TaskService taskService) {
        this.taskService = taskService;
        validateForm();

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
            startDate.setValue(oneTimeTaskDTO.getStartDate());
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

        if(executionTime.getValue()!=null){
            originalDto.setExecutionTime(executionTime.getValue());
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

        try{
            taskService.editOneTimeTask(taskDetails.getValue());
            _submissionError.setValue(null);
            _saveSuccessEvent.setValue(true);
        }catch (ValidationException e){
            _submissionError.setValue(e.getMessage());
        }



    }

    private void validateForm() {
        String currentTitle = title.getValue();
        LocalTime currentExecutionTime = executionTime.getValue();
        LocalDate currentStartDate = startDate.getValue();

        boolean isTitleValid = currentTitle != null && !currentTitle.trim().isEmpty();
        _titleError.setValue(isTitleValid ? null : "Name is required.");


        boolean isTimeValid = true;
        if (currentStartDate != null && currentExecutionTime != null) {
            if ((currentStartDate.isEqual(LocalDate.now()) && currentExecutionTime.isBefore(LocalTime.now()))) {
                isTimeValid = false;
            }
        }
        _executionTimeError.setValue(isTimeValid ? null : "Time has already passed!");

        _isFormValid.setValue(isTitleValid &&  isTimeValid);
    }













}
