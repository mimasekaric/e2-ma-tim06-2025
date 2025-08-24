package com.example.myhobitapplication.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.dto.TaskDetailsDTO;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.TaskService;

public class TaskDetailsViewModel extends ViewModel {

    private final TaskService taskService;
    private final CategoryService categoryService;
    private final MutableLiveData<RecurringTask> taskDetails = new MutableLiveData<>();

    public TaskDetailsViewModel(TaskService taskService, CategoryRepository categoryRepository) {
        this.taskService = taskService;
        this.categoryService = new CategoryService(categoryRepository);

    }

    public LiveData<RecurringTask> getTaskDetails() {
        return taskDetails;
    }

    public void loadTaskDetails(long taskId) {
        RecurringTask task = taskService.getTaskById(taskId);


//        TaskDetailsDTO taskDTO = new TaskDetailsDTO(
//                task.getId(),
//                task.getName(),
//                task.getDescription(),
//                task.getDifficulty(),
//                task.getImportance(),
//                task.getCategoryColour(),
//                task.getExecutionTime(),
//                6,
//                task.getRecurrenceUnit(),
//                task.getStartDate(),
//                task.getEndDate(),
//                task.getStatus(),
//                "#FFFF"
//        );
//
//        String c = getCategory(taskDTO.getCategoryId()).getColour();
//
//        taskDTO.setCategoryColour(c);

        taskDetails.setValue(task);
    }

    public Category getCategory(int id) {

      return  categoryService.getCategoryById(id);
    }





}
