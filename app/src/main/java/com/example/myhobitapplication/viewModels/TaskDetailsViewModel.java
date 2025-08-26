package com.example.myhobitapplication.viewModels;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.dto.TaskDetailsDTO;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.TaskService;

public class TaskDetailsViewModel extends ViewModel {

    private final TaskService taskService;
    private final CategoryService categoryService;
    private final MutableLiveData<RecurringTaskDTO> taskDetails = new MutableLiveData<>();

    public TaskDetailsViewModel(TaskService taskService, CategoryRepository categoryRepository) {
        this.taskService = taskService;
        this.categoryService = new CategoryService(categoryRepository);

    }

    public LiveData<RecurringTaskDTO> getTaskDetails() {
        return taskDetails;
    }

//    public void loadTaskDetails(long taskId) {
//        RecurringTask task = taskService.getTaskById(taskId);
//
//
////        TaskDetailsDTO taskDTO = new TaskDetailsDTO(
////                task.getId(),
////                task.getName(),
////                task.getDescription(),
////                task.getDifficulty(),
////                task.getImportance(),
////                task.getCategoryColour(),
////                task.getExecutionTime(),
////                6,
////                task.getRecurrenceUnit(),
////                task.getStartDate(),
////                task.getEndDate(),
////                task.getStatus(),
////                "#FFFF"
////        );
////
////        String c = getCategory(taskDTO.getCategoryId()).getColour();
////
////        taskDTO.setCategoryColour(c);
//
//        taskDetails.setValue(task);
//    }

    public Category getCategory(int id) {

      return  categoryService.getCategoryById(id);
    }

    public void loadTaskDetails(long taskId) {

        RecurringTaskDTO recurringTaskDTO = taskService.getTaskById(taskId);

        if (recurringTaskDTO != null) {



            taskDetails.setValue(recurringTaskDTO);
        }
    }

    public void markTaskAsDone() {

        RecurringTaskDTO currentTaskDto = taskDetails.getValue();

        if(!currentTaskDto.getStatus().equals(RecurringTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.INCOMPLETE)){

            currentTaskDto.setStatus(RecurringTaskStatus.COMPLETED);

            taskService.editRecurringTask(currentTaskDto);

            loadTaskDetails(currentTaskDto.getId());

        }
    }

    public void markTaskAsCanceled() {

        RecurringTaskDTO currentTaskDto = taskDetails.getValue();

        if(!currentTaskDto.getStatus().equals(RecurringTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.INCOMPLETE)) {
            currentTaskDto.setStatus(RecurringTaskStatus.CANCELED);

            taskService.editRecurringTask(currentTaskDto);

            loadTaskDetails(currentTaskDto.getId());
        }
    }

    public void markTaskAsPaused() {


        RecurringTaskDTO currentTaskDto = taskDetails.getValue();

        if(!currentTaskDto.getStatus().equals(RecurringTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.INCOMPLETE)) {
            currentTaskDto.setStatus(RecurringTaskStatus.PAUSED);

            taskService.editRecurringTask(currentTaskDto);

            loadTaskDetails(currentTaskDto.getId());
        }
    }

}
