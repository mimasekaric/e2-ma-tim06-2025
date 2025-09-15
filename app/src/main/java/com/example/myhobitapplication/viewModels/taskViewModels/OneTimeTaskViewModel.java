package com.example.myhobitapplication.viewModels.taskViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.OneTimeTaskDTO;
import com.example.myhobitapplication.enums.OneTimeTaskStatus;
import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.exceptions.ValidationException;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.OneTimeTask;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.services.TaskService;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.LocalTime;

public class OneTimeTaskViewModel extends ViewModel {

    private final TaskService taskService;
    private final MutableLiveData<String> title = new MutableLiveData<>("");
    private final MutableLiveData<String> description = new MutableLiveData<>("");
    private final MutableLiveData<Integer> difficultyXp = new MutableLiveData<>(1);

    private final MutableLiveData<Integer> importanceXp = new MutableLiveData<>(1);
    private final MutableLiveData<LocalTime> executionTime = new MutableLiveData<>(LocalTime.now());

    private final MutableLiveData<Category> category = new MutableLiveData<>();

    private final MutableLiveData<LocalDate> startDate = new MutableLiveData<LocalDate>(LocalDate.now());

    public MutableLiveData<LocalTime> getStartDate() {  return executionTime; }

    public void setStartDate(LocalDate date) { startDate.setValue(date); validateForm(); }

    public LiveData<String> getTitle() { return title; }
    public LiveData<String> getDescription() { return description; }
    public LiveData<Integer> getDifficultyXp() { return difficultyXp; }

    public MutableLiveData<Integer> getImportanceXp() {  return importanceXp; }

    public MutableLiveData<LocalTime> getExecutionTime() {  return executionTime;}

    public MutableLiveData<Category> getCategory() {  return category;}


    public void setTitle(String newTitle) { title.setValue(newTitle); validateForm(); }
    public void setDescription(String newDescription) { description.setValue(newDescription); }
    public void setDifficultyXp(int newXp) { difficultyXp.setValue(newXp); }
    public void setImportanceXp(int impXp) { importanceXp.setValue(impXp);}
    public void setExecutionTime(LocalTime time) { executionTime.setValue(time); validateForm(); }
    public void setCategory(Category categoryData) { category.setValue(categoryData); validateForm(); }

    private final MutableLiveData<String> _titleError = new MutableLiveData<>(null);
    public LiveData<String> getTitleError() { return _titleError; }

    private final MutableLiveData<String> _executionTimeError = new MutableLiveData<>(null);
    public LiveData<String> getExecutionTimeError() { return _executionTimeError; }

    private final MutableLiveData<String> _categoryError = new MutableLiveData<>(null); // Opciono, za Toast
    public LiveData<String> getCategoryError() { return _categoryError; }

    private final MutableLiveData<Boolean> _isFormValid = new MutableLiveData<>(false);
    public LiveData<Boolean> isFormValid() { return _isFormValid; }

    private final MutableLiveData<String> _submissionError = new MutableLiveData<>();
    public LiveData<String> getSubmissionError() { return _submissionError; }

    private final MutableLiveData<Boolean> _saveSuccessEvent = new MutableLiveData<>();
    public LiveData<Boolean> getSaveSuccessEvent() { return _saveSuccessEvent; }
    public void onSaveSuccessEventHandled() {
        _saveSuccessEvent.setValue(null);
    }
    private String userUid;


    public OneTimeTaskViewModel(TaskService taskService){
        this.taskService = taskService;
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        validateForm();
    }


    public void saveRecurringTask() {

        validateForm();
        if (_isFormValid.getValue() == null || !_isFormValid.getValue()) {
            return;
        }
        OneTimeTaskDTO task = new OneTimeTaskDTO(
                title.getValue(),
                description.getValue(),
                difficultyXp.getValue(),
                category.getValue().getColour(),
                importanceXp.getValue(),
                executionTime.getValue(),
                startDate.getValue(),
                OneTimeTaskStatus.ACTIVE,
                LocalDate.now(),
                LocalDate.now(),
                userUid
        );
        try {
            taskService.createOneTimeTask(task);
            _submissionError.setValue(null);
            _saveSuccessEvent.setValue(true);
        } catch (ValidationException e) {
            _submissionError.setValue(e.getMessage());
        }
    }

    private void validateForm() {
        String currentTitle = title.getValue();
        Category currentCategory = category.getValue();
        Integer currentDifficulty = difficultyXp.getValue();
        Integer currentImportance = importanceXp.getValue();
        LocalDate currentTaskDate = startDate.getValue();
        LocalTime currentExecutionTime = executionTime.getValue();

        boolean isTitleValid = currentTitle != null && !currentTitle.trim().isEmpty();
        _titleError.setValue(isTitleValid ? null : "Name is required.");
        boolean isCategoryValid = currentCategory != null;
        boolean isDifficultyValid = currentDifficulty != null;
        boolean isImportanceValid = currentImportance != null;

        boolean isTimeValid = true;
        if (currentTaskDate != null && currentExecutionTime != null) {
            if (currentTaskDate.isEqual(LocalDate.now()) && currentExecutionTime.isBefore(LocalTime.now())) {
                isTimeValid = false;
            }
        }
        _executionTimeError.setValue(isTimeValid ? null : "Time has already passed!");

        _isFormValid.setValue(isTitleValid && isCategoryValid && isTimeValid);
    }
}
