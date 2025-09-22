package com.example.myhobitapplication.viewModels.taskViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.dto.OneTimeTaskDTO;
import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.enums.OneTimeTaskStatus;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.exceptions.ValidationException;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.TaskService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class OneTimeTaskDetailsViewModel extends ViewModel {


    private final TaskService taskService;
    private final CategoryService categoryService;
    private final MutableLiveData<OneTimeTaskDTO> taskDetails = new MutableLiveData<>();


    private final MutableLiveData<Boolean> _taskDeletedEvent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _taskStatusUpdatedEvent = new MutableLiveData<>();
    public LiveData<Boolean> getTaskStatusUpdatedEvent() {
        return _taskStatusUpdatedEvent;
    }
    public void onTaskStatusUpdatedEventHandled() {
        _taskStatusUpdatedEvent.setValue(false);
    }

    public LiveData<Boolean> getTaskDeletedEvent() {
        return _taskDeletedEvent;
    }
    public OneTimeTaskDetailsViewModel(TaskService taskService, CategoryRepository categoryRepository, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.categoryService = new CategoryService(categoryRepository,taskRepository);

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

        if(currentTaskDto.getStatus().equals(OneTimeTaskStatus.ACTIVE) ){

            currentTaskDto.setStatus(OneTimeTaskStatus.COMPLETED);
            currentTaskDto.setFinishedDate(LocalDate.now());

            taskService.markOneTimeTaskAsDone(taskDetails.getValue().getId(), taskDetails.getValue().getUserUid());

            loadTaskDetails(currentTaskDto.getId());
            _taskStatusUpdatedEvent.setValue(true);

        } else if(currentTaskDto.getStatus().equals(OneTimeTaskStatus.UNPAUSED) ){

            currentTaskDto.setStatus(OneTimeTaskStatus.PAUSED_COMPLETED);
            currentTaskDto.setFinishedDate(LocalDate.now());

            try {
                taskService.editOneTimeTask(currentTaskDto);
            }
            catch (ValidationException e) {
                throw new RuntimeException(e);
            }
            loadTaskDetails(currentTaskDto.getId());
            _taskStatusUpdatedEvent.setValue(true);

        }
    }

    public void markTaskAsCanceled() {

        OneTimeTaskDTO currentTaskDto = taskDetails.getValue();

        if(!currentTaskDto.getStatus().equals(OneTimeTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(OneTimeTaskStatus.INCOMPLETE) && !currentTaskDto.getStatus().equals(OneTimeTaskStatus.COMPLETED)) {
            currentTaskDto.setStatus(OneTimeTaskStatus.CANCELED.CANCELED);

            try {
                taskService.editOneTimeTask(currentTaskDto);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }


            loadTaskDetails(currentTaskDto.getId());
            _taskStatusUpdatedEvent.setValue(true);
        }
    }

    public void markTaskAsPaused() {


        OneTimeTaskDTO currentTaskDto = taskDetails.getValue();

        if(!currentTaskDto.getStatus().equals(OneTimeTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(OneTimeTaskStatus.INCOMPLETE) && !currentTaskDto.getStatus().equals(OneTimeTaskStatus.PAUSED)) {
            currentTaskDto.setStatus(OneTimeTaskStatus.PAUSED);
            currentTaskDto.setRemainingTime(Duration.between(LocalDateTime.now(),currentTaskDto.getFinishDate()));

            try{
                taskService.editOneTimeTask(currentTaskDto);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }

            loadTaskDetails(currentTaskDto.getId());
            _taskStatusUpdatedEvent.setValue(true);
        }
    }

    public void markTaskAsUnPaused() {


        OneTimeTaskDTO currentTaskDto = taskDetails.getValue();

        if(currentTaskDto.getStatus().equals(OneTimeTaskStatus.PAUSED) || currentTaskDto.getStatus().equals(OneTimeTaskStatus.ACTIVE)) {
            currentTaskDto.setStatus(OneTimeTaskStatus.UNPAUSED);
            currentTaskDto.setFinishDate(LocalDateTime.now().plus(currentTaskDto.getRemainingTime()));
            currentTaskDto.setRemainingTime(Duration.between(LocalDateTime.now(),currentTaskDto.getFinishDate()));


            try{
                taskService.editOneTimeTask(currentTaskDto);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
            loadTaskDetails(currentTaskDto.getId());
            _taskStatusUpdatedEvent.setValue(true);
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
