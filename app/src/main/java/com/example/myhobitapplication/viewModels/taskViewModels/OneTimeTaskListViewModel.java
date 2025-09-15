package com.example.myhobitapplication.viewModels.taskViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.dto.OneTimeTaskDTO;
import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.models.OneTimeTask;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.services.TaskService;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OneTimeTaskListViewModel extends ViewModel{

    public MutableLiveData<List<OneTimeTaskDTO>> oneTimeTasks = new MutableLiveData<>();
    public MutableLiveData<List<OneTimeTaskDTO>> getOneTimeTasks() { return oneTimeTasks;}
    public void setOneTimeTasks(List<OneTimeTaskDTO> tasks) {  oneTimeTasks.setValue(tasks);}
    private final MutableLiveData<OneTimeTaskDTO> _navigateToTaskDetails = new MutableLiveData<>();

    private final TaskService taskService;

    private String userId;

    public LiveData<OneTimeTaskDTO> getNavigateToTaskDetails() {
        return _navigateToTaskDetails;
    }
    public OneTimeTaskListViewModel(TaskService taskService){

        this.taskService = taskService;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.loadRecurringTasks();
    }

    public void loadRecurringTasks(){

        List<OneTimeTask> allTaskModels = taskService.getAllOneTimeTasks(userId);

        if (allTaskModels == null) {
            oneTimeTasks.setValue(new ArrayList<>());
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        List<OneTimeTaskDTO> futureTaskDtos = allTaskModels.stream()

                .filter(task -> {

                    if (task.getStartDate() == null || task.getExecutionTime() == null) {
                        return false;
                    }

                    LocalDateTime taskDateTime = task.getStartDate().atTime(task.getExecutionTime());


                    return !taskDateTime.isBefore(now);
                })
                .map(OneTimeTaskDTO::new)

                .collect(Collectors.toList());
        oneTimeTasks.setValue(futureTaskDtos);

    }

    public void onTaskClicked(OneTimeTaskDTO task) {
        _navigateToTaskDetails.setValue(task);
    }

    public void onTaskDetailsNavigated() {
        _navigateToTaskDetails.setValue(null);
    }
}
