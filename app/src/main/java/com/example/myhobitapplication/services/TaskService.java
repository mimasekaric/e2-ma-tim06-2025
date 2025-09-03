package com.example.myhobitapplication.services;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databases.DataBaseRecurringTaskHelper;
import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.dto.OneTimeTaskDTO;
import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.exceptions.ValidationException;
import com.example.myhobitapplication.models.OneTimeTask;
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

    public void createRecurringTaskSeries(RecurringTask taskTemplate) throws ValidationException{

        validateRequeeingTaskTemplate(taskTemplate);

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
                    (int) firstTaskId,
                    taskTemplate.getFinishedDate(),
                    taskTemplate.getCreationDate()
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


    public List<RecurringTask> getAllRecurringTasks() {
        return repository.getAllRecurringTasks();
    }

    public List<Task> getAllTasks(){


        List<Task> taskList = new ArrayList<>();


        List<RecurringTask> recurringTasks = getAllRecurringTasks();
        taskList.addAll(recurringTasks);

        List<OneTimeTask> oneTimeTasks = getAllOneTimeTasks();
        taskList.addAll(oneTimeTasks);

        return taskList;
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
                recurringTaskDTO.getFirstRecurringTaskId(),
                recurringTaskDTO.getFinishedDate(),
                recurringTaskDTO.getCreationDate()
        );

        long editedRow = repository.updateRecurringTask(recurringTask);


            return editedRow;
    }

    public void deleteRecurringTask(RecurringTaskDTO recurringTaskDTO){

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
                recurringTaskDTO.getFirstRecurringTaskId(),
                recurringTaskDTO.getFinishedDate(),
                recurringTaskDTO.getCreationDate()
        );

        if (recurringTask != null) {
            repository.deleteRecurringTaskAndFutureInstances(recurringTask);
        }
    }

    public int updateOutdatedTasksToNotDone() {

        return repository.updateOutdatedTasksToNotDone();
    }

    public void updateUserXP(RecurringTaskStatus status){

        if(status.equals(RecurringTaskStatus.COMPLETED)){

            //TODO:apdejtuj XP useru
        }

    }

    public int countCreatedTasksForDateRange(LocalDate previousLevelDate, LocalDate currentLevelDate){

        return repository.countTasksByDateRange(previousLevelDate,currentLevelDate);
    }

    public int countFinishedTasksForDateRange(LocalDate previousLevelDate, LocalDate currentLevelDate){

        return repository.countTasksByStatusInDateRange(RecurringTaskStatus.COMPLETED,previousLevelDate,currentLevelDate);
    }

    public void createOneTimeTask(OneTimeTaskDTO taskDTO){

        OneTimeTask oneTimeTask = new OneTimeTask(

                taskDTO.getId(),
                taskDTO.getName(),
                taskDTO.getDescription(),
                taskDTO.getDifficulty(),
                taskDTO.getImportance(),
                taskDTO.getCategoryColour(),
                taskDTO.getExecutionTime(),
                taskDTO.getStatus(),
                taskDTO.getCreationDate(),
                taskDTO.getFinishedDate(),
                taskDTO.getStartDate()
        );

         repository.insertOneTimeTask(oneTimeTask);
    }


    public List<OneTimeTask> getAllOneTimeTasks() {
        return repository.getAllOneTimeTasks();
    }

    public void editOneTimeTask(OneTimeTaskDTO taskDTO){


        OneTimeTask oneTimeTask = new OneTimeTask(

                taskDTO.getId(),
                taskDTO.getName(),
                taskDTO.getDescription(),
                taskDTO.getDifficulty(),
                taskDTO.getImportance(),
                taskDTO.getCategoryColour(),
                taskDTO.getExecutionTime(),
                taskDTO.getStatus(),
                taskDTO.getCreationDate(),
                taskDTO.getFinishedDate(),
                taskDTO.getStartDate()
        );
        repository.updateOneTimeTask(oneTimeTask);
    }

    public OneTimeTaskDTO getOneTimeTaskById(long taskId){

        OneTimeTask oneTimeTask = new OneTimeTask();
        oneTimeTask = repository.getOneTimeTaskById(taskId);

        OneTimeTaskDTO oneTimeTaskDTO = new OneTimeTaskDTO(oneTimeTask);

        return oneTimeTaskDTO;
    }

    public long deleteOneTimeTask(long taskId){

        return repository.deleteOneTimeTask(taskId);
    }

    private void validateRequeeingTaskTemplate(RecurringTask taskTemplate) throws ValidationException {

        if (taskTemplate.getName() == null || taskTemplate.getName().trim().isEmpty()) {
            throw new ValidationException("Task name is required.");
        }

//        if (taskTemplate.getDifficulty() <= 0) {
//            throw new ValidationException("Morate odabrati težinu zadatka.");
//        }
//        if (taskTemplate.getImportance() <= 0) {
//            throw new ValidationException("Morate odabrati važnost zadatka.");
//        }

//        // Pravilo 3: Kategorija mora biti izabrana i mora postojati u bazi
//        if (taskTemplate.getCategoryId() == null) {
//            throw new ValidationException("Kategorija mora biti izabrana.");
//        }
//        // Provera da li kategorija sa tim ID-jem zaista postoji
//        Category category = categoryRepository.getCategoryById(taskTemplate.getCategoryId());
//        if (category == null) {
//            throw new ValidationException("Izabrana kategorija nije validna.");
//        }


        if (taskTemplate.getStartDate() == null || taskTemplate.getEndDate() == null) {
            throw new ValidationException("Start date and End date are required.");
        }
        if (taskTemplate.getEndDate().isBefore(taskTemplate.getStartDate())) {
            throw new ValidationException("End date can not be before Start date");
        }

        if (taskTemplate.getRecurrenceInterval() < 1) {
            throw new ValidationException("Recurring interval must be positive number.");
        }
    }

}
