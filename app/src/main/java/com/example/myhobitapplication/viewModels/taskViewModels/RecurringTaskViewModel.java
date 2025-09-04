package com.example.myhobitapplication.viewModels.taskViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.exceptions.ValidationException;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.services.TaskService;

import java.time.LocalDate;
import java.time.LocalTime;

public class RecurringTaskViewModel extends ViewModel {

    private final TaskService taskService;
    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<String> description = new MutableLiveData<>();
    private final MutableLiveData<Integer> difficultyXp = new MutableLiveData<>(1);

    private final MutableLiveData<Integer> importanceXp = new MutableLiveData<>(1);
    private final MutableLiveData<LocalTime> executionTime = new MutableLiveData<>(LocalTime.now());

    private final MutableLiveData<LocalDate> startDate = new MutableLiveData<LocalDate>(LocalDate.now());
    private final MutableLiveData<LocalDate> endDate = new MutableLiveData<LocalDate>(LocalDate.now());

    private final MutableLiveData<Integer> recurrenceInterval = new MutableLiveData<>(0);
    private final MutableLiveData<RecurrenceUnit> recurrenceUnit = new MutableLiveData<>(RecurrenceUnit.DAY);

    private final MutableLiveData<Category> category = new MutableLiveData<>(null);

    public LiveData<String> getTitle() { return title; }
    public LiveData<String> getDescription() { return description; }
    public LiveData<Integer> getDifficultyXp() { return difficultyXp; }
    public LiveData<Integer> getRecurrenceInterval() { return recurrenceInterval; }
    public LiveData<RecurrenceUnit> getRecurrenceUnit() { return recurrenceUnit; }

    public MutableLiveData<Integer> getImportanceXp() {  return importanceXp; }

    public MutableLiveData<LocalTime> getExecutionTime() {  return executionTime;}
    public MutableLiveData<LocalDate> getStartDate() {  return startDate;}
    public MutableLiveData<LocalDate> getEndDate() {  return endDate;}

    public MutableLiveData<Category> getCategory() {  return category;}


    public void setTitle(String newTitle) { title.setValue(newTitle);  validateForm(); }
    public void setDescription(String newDescription) { description.setValue(newDescription); }
    public void setDifficultyXp(int newXp) { difficultyXp.setValue(newXp); }
    public void setImportanceXp(int impXp) { importanceXp.setValue(impXp);}
    public void setRecurrenceInterval(int interval) { recurrenceInterval.setValue(interval); }
    public void setRecurrenceUnit(RecurrenceUnit unit) { recurrenceUnit.setValue(unit); }
    public void setExecutionTime(LocalTime time) { executionTime.setValue(time); }
    public void setStartDate(LocalDate date) { startDate.setValue(date); }
    public void setEndDate(LocalDate date) { endDate.setValue(date); validateForm(); }
    public void setCategory(Category categoryData) { category.setValue(categoryData); validateForm(); }

    private final MutableLiveData<String> _titleError = new MutableLiveData<>(null);
    public LiveData<String> getTitleError() { return _titleError; }

    private final MutableLiveData<Boolean> _isFormValid = new MutableLiveData<>(false);
    public LiveData<Boolean> isFormValid() { return _isFormValid; }
    private final MutableLiveData<String> _submissionError = new MutableLiveData<>();
    public LiveData<String> getSubmissionError() { return _submissionError; }
    private final MutableLiveData<Boolean> _saveSuccessEvent = new MutableLiveData<>();
    public LiveData<Boolean> getSaveSuccessEvent() { return _saveSuccessEvent; }
    private final MutableLiveData<String> _categoryError = new MutableLiveData<>(null);
    public LiveData<String> getCategoryError() { return _categoryError; }


    public RecurringTaskViewModel(TaskService taskService){
        this.taskService = taskService;
        validateForm();
    }


    public void saveRecurringTask() {

        validateForm();
        if (_isFormValid.getValue() == null || !_isFormValid.getValue()) {
            return;
        }

        RecurringTask task = new RecurringTask(
                title.getValue(),
                description.getValue(),
                difficultyXp.getValue(),
                importanceXp.getValue(),
                category.getValue().getColour(),
                executionTime.getValue(),
                6,
                recurrenceUnit.getValue(),
                startDate.getValue(),
                endDate.getValue(),
                RecurringTaskStatus.ACTIVE,
                -1,
                LocalDate.now(),
                LocalDate.now()
        );

        try {
            taskService.createRecurringTaskSeries(task);

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

        boolean isTitleValid = currentTitle != null && !currentTitle.trim().isEmpty();
        if (!isTitleValid) {
            _titleError.setValue("Task name is required.");
        } else {
            _titleError.setValue(null);
        }

        boolean isCategoryValid = currentCategory != null;
        if (!isCategoryValid) {
            _categoryError.setValue("Category is required.");
        } else {
            _categoryError.setValue(null);
        }

        boolean isDifficultyValid = currentDifficulty != null;

        boolean isImportanceValid = currentImportance != null;

        boolean areDatesValid = startDate.getValue() != null && endDate.getValue() != null && !endDate.getValue().isBefore(startDate.getValue());
        _isFormValid.setValue(isTitleValid && areDatesValid && isCategoryValid);
    }

}


