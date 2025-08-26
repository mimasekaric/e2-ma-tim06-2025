package com.example.myhobitapplication.services;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databases.DataBaseRecurringTaskHelper;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.models.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskService {

    private final TaskRepository repository;
    private RecurringTask task;
    private final Map<LocalDate, List<RecurringTask>> scheduledTasks;
    public TaskService(TaskRepository repository){
        this.repository = repository;
        scheduledTasks = new HashMap<>();

    }
    public long saveRecurringTask(RecurringTask task){
       return repository.insertRecurringTask(task);
    }
    public List<RecurringTask> getRecurringTasks(){ return repository.getAllRecurringTasks();}

    public void createRecurringTaskSeries(RecurringTask taskTemplate) {

        taskTemplate.setStatus(RecurringTaskStatus.ACTIVE);

        long firstTaskId = repository.insertSingleRecurringTask(taskTemplate);


        if (firstTaskId == -1) {

            return;
        }

        List<RecurringTask> remainingInstances = new ArrayList<>();
        LocalDate currentDate = calculateNextDate(taskTemplate.getStartDate(),
                taskTemplate.getRecurrenceInterval(),
                taskTemplate.getRecurrenceUnit());

        LocalDate endDate = taskTemplate.getEndDate();

        while (!currentDate.isAfter(endDate)) {
            RecurringTask instance = new RecurringTask(
                    null,
                    taskTemplate.getName(),
                    taskTemplate.getDescription(),
                    taskTemplate.getDifficulty(),
                    taskTemplate.getImportance(),
                    taskTemplate.getCategoryColour(),
                    taskTemplate.getExecutionTime(),
                    taskTemplate.getRecurrenceInterval(),
                    taskTemplate.getRecurrenceUnit(),
                    currentDate,
                    taskTemplate.getEndDate(),
                    RecurringTaskStatus.ACTIVE,
                    (int) firstTaskId
            );

            remainingInstances.add(instance);

            currentDate = calculateNextDate(currentDate, taskTemplate.getRecurrenceInterval(), taskTemplate.getRecurrenceUnit());
        }

        if (!remainingInstances.isEmpty()) {
            repository.insertRecurringTaskBatch(remainingInstances);
        }


        repository.updateFirstRecurringTaskId(firstTaskId, firstTaskId);
    }






    public Map<LocalDate, List<RecurringTask>> getScheduledTasks() {

        Map<LocalDate, List<RecurringTask>> newScheduledTasks = new HashMap<>();
        List<RecurringTask> allTasks = repository.getAllRecurringTasks();

        for (RecurringTask task : allTasks) {
            LocalDate currentDate = task.getStartDate();
            LocalDate endDate = task.getEndDate();
            LocalDate maxDate = LocalDate.now().plusMonths(12);

            while (!currentDate.isAfter(endDate) && !currentDate.isAfter(maxDate)) {
                List<RecurringTask> tasksForDate = newScheduledTasks.getOrDefault(currentDate, new ArrayList<>());
                tasksForDate.add(task);
                newScheduledTasks.put(currentDate, tasksForDate);
                currentDate = calculateNextDate(currentDate, task.getRecurrenceInterval(), task.getRecurrenceUnit());
            }
        }

        return newScheduledTasks;
    }

    private LocalDate calculateNextDate(LocalDate date, int interval, RecurrenceUnit unit) {
        switch (unit) {
            case DAY:
                return date.plusDays(interval);
            case WEEK:
                return date.plusWeeks(interval);
            case MONTH:
                return date.plusMonths(interval);
            default:
                return date;
        }
    }


    public List<RecurringTask> getAllTasks() {
        return repository.getAllRecurringTasks();
    }

    public RecurringTaskDTO getTaskById(long id) {
        RecurringTask taskModel =  repository.getTaskById(id);

        RecurringTaskDTO taskDto = new RecurringTaskDTO(taskModel);

        return taskDto;
    }


    public long editRecurringTask(RecurringTaskDTO recurringTaskDTO){


        RecurringTask recurringTask = new RecurringTask(

                recurringTaskDTO.getId(),
                recurringTaskDTO.getName(),
                recurringTaskDTO.getDescription(),
                recurringTaskDTO.getDifficulty(),
                recurringTaskDTO.getImportance(),
                recurringTaskDTO.getCategoryColour(),
                recurringTaskDTO.getExecutionTime(),
                recurringTaskDTO.getRecurrenceInterval(),
                recurringTaskDTO.getRecurrenceUnit(),
                recurringTaskDTO.getStartDate(),
                recurringTaskDTO.getEndDate(),
                recurringTaskDTO.getStatus(),
                recurringTaskDTO.getFirstRecurringTaskId()
        );

        long editedRow = repository.updateRecurringTask(recurringTask);



            return editedRow;





    }

    public int updateOutdatedTasksToNotDone() {

        return repository.updateOutdatedTasksToNotDone();
    }

    public void updateUserXP(RecurringTaskStatus status){

        if(status.equals(RecurringTaskStatus.COMPLETED)){

            //TODO:apdejtuj XP useru
        }

    }

}
