package com.example.myhobitapplication.viewModels.taskViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhobitapplication.databases.CategoryRepository;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.exceptions.ValidationException;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.services.CategoryService;
import com.example.myhobitapplication.services.TaskService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RecurringTaskDetailsViewModel extends ViewModel {

    private final TaskService taskService;
    private final CategoryService categoryService;
    private final MutableLiveData<RecurringTaskDTO> taskDetails = new MutableLiveData<>();


    private final MutableLiveData<Boolean> _taskDeletedEvent = new MutableLiveData<>();

    public LiveData<Boolean> getTaskDeletedEvent() {
        return _taskDeletedEvent;
    }
    public RecurringTaskDetailsViewModel(TaskService taskService, CategoryRepository categoryRepository, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.categoryService = new CategoryService(categoryRepository,taskRepository);

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

        if(!currentTaskDto.getStatus().equals(RecurringTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.INCOMPLETE) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.COMPLETED) ){

            currentTaskDto.setStatus(RecurringTaskStatus.COMPLETED);
            currentTaskDto.setFinishedDate(LocalDate.now());

            taskService.markRecurringTaskAsDone(taskDetails.getValue().getId(), taskDetails.getValue().getUserUid());

            loadTaskDetails(currentTaskDto.getId());

        }
        else if(!currentTaskDto.getStatus().equals(RecurringTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.INCOMPLETE) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.COMPLETED) && currentTaskDto.getStatus().equals(RecurringTaskStatus.PAUSED) ){

            currentTaskDto.setStatus(RecurringTaskStatus.PAUSED_COMPLETED);
            currentTaskDto.setFinishedDate(LocalDate.now());

            taskService.markRecurringTaskAsDone(taskDetails.getValue().getId(), taskDetails.getValue().getUserUid());

            loadTaskDetails(currentTaskDto.getId());

        }
    }

    public void markTaskAsCanceled() {

        RecurringTaskDTO currentTaskDto = taskDetails.getValue();

        if(!currentTaskDto.getStatus().equals(RecurringTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.INCOMPLETE) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.COMPLETED)) {
            currentTaskDto.setStatus(RecurringTaskStatus.CANCELED);

            try{
                taskService.editRecurringTask(currentTaskDto);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }


            loadTaskDetails(currentTaskDto.getId());
        }
    }

    public void markTaskAsPaused() {


        RecurringTaskDTO currentTaskDto = taskDetails.getValue();

        if(!currentTaskDto.getStatus().equals(RecurringTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.INCOMPLETE) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.PAUSED)) {
            currentTaskDto.setStatus(RecurringTaskStatus.PAUSED);
            currentTaskDto.setRemainingTime(Duration.between(LocalDateTime.now(),currentTaskDto.getFinishDate()));



            //currentTaskDto.setRemainingTime();

            try{
                taskService.editRecurringTask(currentTaskDto);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
            loadTaskDetails(currentTaskDto.getId());
        }
    }

    public void markTaskAsUnPaused() {


        RecurringTaskDTO currentTaskDto = taskDetails.getValue();

        if(!currentTaskDto.getStatus().equals(RecurringTaskStatus.CANCELED) && !currentTaskDto.getStatus().equals(RecurringTaskStatus.INCOMPLETE)) {
            currentTaskDto.setStatus(RecurringTaskStatus.UNPAUSED);
            currentTaskDto.setFinishDate(LocalDateTime.now().plus(currentTaskDto.getRemainingTime()));
            currentTaskDto.setRemainingTime(Duration.between(LocalDateTime.now(),currentTaskDto.getFinishDate()));


            try{
                taskService.editRecurringTask(currentTaskDto);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
            loadTaskDetails(currentTaskDto.getId());
        }
    }

    // --- NOVA METODA ZA BRISANJE ---
    public void deleteRecurringTask() {
        // Uzmi ID iz DTO-a koji je već učitan u ViewModel-u.
        // Ovo je bolje nego da Fragment šalje ID, jer ViewModel je vlasnik stanja.
        if (taskDetails.getValue() != null) {

            // Pozovi servis da obavi stvarni posao brisanja u bazi.
            // Pretpostavka je da se metoda u servisu zove deleteFutureRecurringTasks.
            taskService.deleteRecurringTask(taskDetails.getValue());

            // SADA POŠALJI SIGNAL!
            // Obavesti sve "posmatrače" (tvoj fragment) da je posao završen.
            _taskDeletedEvent.setValue(true);
        }
    }

    /**
     * Opciono, ali dobra praksa: Metoda za resetovanje event-a da se ne bi
     * ponovo aktivirao npr. nakon rotacije ekrana.
     */
    public void onTaskDeletedEventHandled() {
        _taskDeletedEvent.setValue(null); // Ili false, zavisno od logike
    }
}
