package com.example.myhobitapplication.viewModels.taskViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.OneTimeTaskDTO;
import com.example.myhobitapplication.enums.OneTimeTaskStatus;
import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.OneTimeTask;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.services.TaskService;

import java.time.LocalDate;
import java.time.LocalTime;

public class OneTimeTaskViewModel extends ViewModel {

    private final TaskService taskService;
    private final MutableLiveData<String> title = new MutableLiveData<>("difoltni string");
    private final MutableLiveData<String> description = new MutableLiveData<>("difoltni string");
    private final MutableLiveData<Integer> difficultyXp = new MutableLiveData<>(0);

    private final MutableLiveData<Integer> importanceXp = new MutableLiveData<>(0);
    private final MutableLiveData<LocalTime> executionTime = new MutableLiveData<>(LocalTime.now());

    private final MutableLiveData<Category> category = new MutableLiveData<>();

    private final MutableLiveData<LocalDate> startDate = new MutableLiveData<LocalDate>();

    public MutableLiveData<LocalTime> getStartDate() {  return executionTime;}

    public void setStartDate(LocalDate date) { startDate.setValue(date); }

    public LiveData<String> getTitle() { return title; }
    public LiveData<String> getDescription() { return description; }
    public LiveData<Integer> getDifficultyXp() { return difficultyXp; }

    public MutableLiveData<Integer> getImportanceXp() {  return importanceXp; }

    public MutableLiveData<LocalTime> getExecutionTime() {  return executionTime;}

    public MutableLiveData<Category> getCategory() {  return category;}


    public void setTitle(String newTitle) { title.setValue(newTitle); }
    public void setDescription(String newDescription) { description.setValue(newDescription); }
    public void setDifficultyXp(int newXp) { difficultyXp.setValue(newXp); }
    public void setImportanceXp(int impXp) { importanceXp.setValue(impXp);}
    public void setExecutionTime(LocalTime time) { executionTime.setValue(time); }
    public void setCategory(Category categoryData) { category.setValue(categoryData); }


    public OneTimeTaskViewModel(TaskService taskService){
        this.taskService = taskService;
    }


    public void saveRecurringTask() {

        OneTimeTaskDTO task = new OneTimeTaskDTO(
                title.getValue(),
                description.getValue(),
                difficultyXp.getValue(),
                category.getValue().getColour(),
                importanceXp.getValue(),
                executionTime.getValue(),
                LocalDate.now(),
                OneTimeTaskStatus.ACTIVE,
                LocalDate.now(),
                startDate.getValue()
        );
        taskService.createOneTimeTask(task);
    }
}
