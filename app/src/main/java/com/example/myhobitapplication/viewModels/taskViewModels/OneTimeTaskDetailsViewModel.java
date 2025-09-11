package com.example.myhobitapplication.viewModels.taskViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.dto.OneTimeTaskDTO;
import com.example.myhobitapplication.enums.OneTimeTaskStatus;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.TaskService;

import java.time.LocalDate;

public class OneTimeTaskDetailsViewModel extends ViewModel {


    private final TaskService taskService;
    private final CategoryService categoryService;
    private final MutableLiveData<OneTimeTaskDTO> taskDetails = new MutableLiveData<>();


    private final MutableLiveData<Boolean> _taskDeletedEvent = new MutableLiveData<>();

    public LiveData<Boolean> getTaskDeletedEvent() {
        return _taskDeletedEvent;
    }
    public OneTimeTaskDetailsViewModel(TaskService taskService, CategoryRepository categoryRepository) {
        this.taskService = taskService;
        this.categoryService = new CategoryService(categoryRepository);

    }

    public LiveData<OneTimeTaskDTO> getTaskDetails() {
        return taskDetails;
    }


    public Category getCategory(int id) {

        return  categoryService.getCategoryById(id);
    }

    public void loadTaskDetails(long taskId) {

        OneTimeTaskDTO oneTimeTaskDTO = taskService.getOneTimeTaskById(taskId);

        if (oneTimeTaskDTO != null) {



            taskDetails.setValue(oneTimeTaskDTO);
        }
    }

    public void markTaskAsDone() {

        OneTimeTaskDTO currentTaskDto = taskDetails.getValue();

        if(!currentTaskDto.getStatus().equals(RecurringTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.INCOMPLETE) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.COMPLETED) ){

            currentTaskDto.setStatus(OneTimeTaskStatus.COMPLETED);
            currentTaskDto.setFinishedDate(LocalDate.now());

            taskService.markOneTimeTaskAsDone(taskDetails.getValue().getId(), taskDetails.getValue().getUserUid());

            loadTaskDetails(currentTaskDto.getId());

        }
    }

    public void markTaskAsCanceled() {

        OneTimeTaskDTO currentTaskDto = taskDetails.getValue();

        if(!currentTaskDto.getStatus().equals(OneTimeTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(OneTimeTaskStatus.INCOMPLETE) && !currentTaskDto.getStatus().equals(OneTimeTaskStatus.COMPLETED)) {
            currentTaskDto.setStatus(OneTimeTaskStatus.CANCELED.CANCELED);

            taskService.editOneTimeTask(currentTaskDto);

            loadTaskDetails(currentTaskDto.getId());
        }
    }

    public void markTaskAsPaused() {


        OneTimeTaskDTO currentTaskDto = taskDetails.getValue();

        if(!currentTaskDto.getStatus().equals(OneTimeTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(OneTimeTaskStatus.INCOMPLETE) && !currentTaskDto.getStatus().equals(OneTimeTaskStatus.PAUSED)) {
            currentTaskDto.setStatus(OneTimeTaskStatus.PAUSED);

            taskService.editOneTimeTask(currentTaskDto);

            loadTaskDetails(currentTaskDto.getId());
        }
    }


    public void deleteRecurringTask() {

        if (taskDetails.getValue() != null) {


            taskService.deleteOneTimeTask(taskDetails.getValue().getId());

            _taskDeletedEvent.setValue(true);
        }
    }

    public void onTaskDeletedEventHandled() {
        _taskDeletedEvent.setValue(null);
    }





}
