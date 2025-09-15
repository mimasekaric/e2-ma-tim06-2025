package com.example.myhobitapplication.viewModels.taskViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.services.TaskService;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecurringTaskListViewModel extends ViewModel {

    public MutableLiveData<List<RecurringTaskDTO>> recurringTasks = new MutableLiveData<>();
    public MutableLiveData<List<RecurringTaskDTO>> getRecurringTasks() { return recurringTasks;}
    public void setRecurringTasks(List<RecurringTaskDTO> tasks) {  recurringTasks.setValue(tasks);}

    private final MutableLiveData<RecurringTaskDTO> _navigateToTaskDetails = new MutableLiveData<>();
    public LiveData<RecurringTaskDTO> getNavigateToTaskDetails() {
        return _navigateToTaskDetails;
    }
    private final TaskService taskService;

    private String userId;
    public RecurringTaskListViewModel(TaskService taskService){

        this.taskService = taskService;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.loadRecurringTasks();
    }

    public void loadRecurringTasks(){

        List<RecurringTask> allTaskModels = taskService.getAllRecurringTasks(userId);

        if (allTaskModels == null) {
            recurringTasks.setValue(new ArrayList<>());
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        List<RecurringTaskDTO> futureTaskDtos = allTaskModels.stream()

                .filter(task -> {

                    if (task.getStartDate() == null || task.getExecutionTime() == null) {
                        return false;
                    }

                    LocalDateTime taskDateTime = task.getStartDate().atTime(task.getExecutionTime());


                    return !taskDateTime.isBefore(now);
                })
                .map(RecurringTaskDTO::new)

                .collect(Collectors.toList());
        recurringTasks.setValue(futureTaskDtos);

    }

    public void refreshTasks() {
        Log.d("ViewModelDebug", "Pozvana je refreshTasks metoda.");
        loadRecurringTasks();
    }

    public void onTaskClicked(RecurringTaskDTO task) {
        _navigateToTaskDetails.setValue(task);
    }

    public void onTaskDetailsNavigated() {
        _navigateToTaskDetails.setValue(null);
    }
}
