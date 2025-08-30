package com.example.myhobitapplication.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.services.TaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TaskViewModel extends ViewModel {

    // LiveData za podatke o zadatku
    private final TaskService taskService;
    private final MutableLiveData<String> title = new MutableLiveData<>("difoltni string");
    private final MutableLiveData<String> description = new MutableLiveData<>("difoltni string");
    private final MutableLiveData<Integer> difficultyXp = new MutableLiveData<>(0);

    private final MutableLiveData<Integer> importanceXp = new MutableLiveData<>(0);
    private final MutableLiveData<LocalTime> executionTime = new MutableLiveData<>(LocalTime.now());

    private final MutableLiveData<LocalDate> startDate = new MutableLiveData<LocalDate>();
    private final MutableLiveData<LocalDate> endDate = new MutableLiveData<LocalDate>();

    // LiveData za ponavljajuće zadatke
    private final MutableLiveData<Integer> recurrenceInterval = new MutableLiveData<>(0);
    private final MutableLiveData<RecurrenceUnit> recurrenceUnit = new MutableLiveData<>(RecurrenceUnit.DAY);

    private final MutableLiveData<Category> category = new MutableLiveData<>();
    // Expose LiveData za fragmente
    public LiveData<String> getTitle() { return title; }
    public LiveData<String> getDescription() { return description; }
    public LiveData<Integer> getDifficultyXp() { return difficultyXp; }
    public LiveData<Integer> getRecurrenceInterval() { return recurrenceInterval; }
    public LiveData<RecurrenceUnit> getRecurrenceUnit() { return recurrenceUnit; }

    public MutableLiveData<Integer> getImportanceXp() {  return importanceXp; }

    public MutableLiveData<LocalTime> getExecutionTime() {  return executionTime;}
    public MutableLiveData<LocalTime> getStartDate() {  return executionTime;}
    public MutableLiveData<LocalTime> getEndDate() {  return executionTime;}

    public MutableLiveData<Category> getCategory() {  return category;}


    public void setTitle(String newTitle) { title.setValue(newTitle); }
    public void setDescription(String newDescription) { description.setValue(newDescription); }
    public void setDifficultyXp(int newXp) { difficultyXp.setValue(newXp); }
    public void setImportanceXp(int impXp) { importanceXp.setValue(impXp);}
    public void setRecurrenceInterval(int interval) { recurrenceInterval.setValue(interval); }
    public void setRecurrenceUnit(RecurrenceUnit unit) { recurrenceUnit.setValue(unit); }
    public void setExecutionTime(LocalTime time) { executionTime.setValue(time); }
    public void setStartDate(LocalDate date) { startDate.setValue(date); }
    public void setEndDate(LocalDate date) { endDate.setValue(date); }
    public void setCategory(Category categoryData) { category.setValue(categoryData); }




    public TaskViewModel(TaskService taskService){
        this.taskService = taskService;
    }


    public void saveRecurringTask() {

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
        taskService.createRecurringTaskSeries(task);
    }


}
