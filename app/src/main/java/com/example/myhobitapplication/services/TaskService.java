package com.example.myhobitapplication.services;

import android.util.Log;

import com.example.myhobitapplication.databases.TaskRepository;
import com.example.myhobitapplication.dto.OneTimeTaskDTO;
import com.example.myhobitapplication.dto.RecurringTaskDTO;
import com.example.myhobitapplication.enums.OneTimeTaskStatus;
import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.enums.TaskQuote;
import com.example.myhobitapplication.exceptions.ValidationException;
import com.example.myhobitapplication.models.OneTimeTask;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.models.Task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskService {

    private final TaskRepository repository;
    private final ProfileService profileService;
    private RecurringTask task;
    private final Map<LocalDate, List<RecurringTask>> scheduledTasks;
    public TaskService(TaskRepository repository, ProfileService profileService){
        this.repository = repository;
        this.profileService = profileService;
        scheduledTasks = new HashMap<>();

    }


    public void createRecurringTaskSeries(RecurringTask taskTemplate) throws ValidationException{

        validateRecurringTask(taskTemplate);

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
                    taskTemplate.getCreationDate(),
                    taskTemplate.getUserUid()
            );

            remainingInstances.add(instance);

            currentDate = calculateNextDate(currentDate, taskTemplate.getRecurrenceInterval(), taskTemplate.getRecurrenceUnit());
        }

        if (!remainingInstances.isEmpty()) {
            repository.insertRecurringTaskBatch(remainingInstances);
        }


        repository.updateFirstRecurringTaskId(firstTaskId, firstTaskId);
    }






    public Map<LocalDate, List<RecurringTask>> getScheduledTasks(String useruid) {

        Map<LocalDate, List<RecurringTask>> newScheduledTasks = new HashMap<>();
        List<RecurringTask> allTasks = repository.getAllRecurringTasks(useruid);

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


    public List<RecurringTask> getAllRecurringTasks(String userUid) {
        return repository.getAllRecurringTasks(userUid);
    }

    public List<Task> getAllTasks(String userUid){


        List<Task> taskList = new ArrayList<>();


        List<RecurringTask> recurringTasks = getAllRecurringTasks(userUid);
        taskList.addAll(recurringTasks);

        List<OneTimeTask> oneTimeTasks = getAllOneTimeTasks(userUid);
        taskList.addAll(oneTimeTasks);

        return taskList;
    }
    public RecurringTaskDTO getTaskById(long id) {
        RecurringTask taskModel =  repository.getTaskById(id);

        RecurringTaskDTO taskDto = new RecurringTaskDTO(taskModel);

        return taskDto;
    }


    public long editRecurringTask(RecurringTaskDTO recurringTaskDTO) throws ValidationException {



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
                recurringTaskDTO.getCreationDate(),
                recurringTaskDTO.getUserUid(),
                recurringTaskDTO.getFinishDate(),
                recurringTaskDTO.getRemainingTime()
        );
        validateRecurringTask(recurringTask);

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
                recurringTaskDTO.getCreationDate(),
                recurringTaskDTO.getUserUid()
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

    public int countCreatedTasksForDateRange(LocalDate previousLevelDate, LocalDate currentLevelDate, String userUid){

        return repository.countTasksByDateRange(previousLevelDate,currentLevelDate, userUid);
    }

    public int countFinishedTasksForDateRange(LocalDate previousLevelDate, LocalDate currentLevelDate, String userUid){

        return repository.countTasksByStatusInDateRange(RecurringTaskStatus.COMPLETED,previousLevelDate,currentLevelDate, userUid);
    }

    public void createOneTimeTask(OneTimeTaskDTO taskDTO) throws ValidationException {

        validateOneTimeTask(taskDTO);
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
                taskDTO.getStartDate(),
                taskDTO.getUserUid()
        );

         repository.insertOneTimeTask(oneTimeTask);
    }


    public List<OneTimeTask> getAllOneTimeTasks(String userUid) {
        return repository.getAllOneTimeTasks(userUid);
    }

    public void editOneTimeTask(OneTimeTaskDTO taskDTO) throws ValidationException {

        validateOneTimeTask(taskDTO);

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
                taskDTO.getStartDate(),
                taskDTO.getUserUid()
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

    private void validateRecurringTask(RecurringTask taskTemplate) throws ValidationException {

        if (taskTemplate.getName() == null || taskTemplate.getName().trim().isEmpty()) {
            throw new ValidationException("Task name is required.");
        }

        if (taskTemplate.getCategoryColour() == null) {
            throw new ValidationException("You must choose category.");
        }

        if (taskTemplate.getStartDate() == null || taskTemplate.getEndDate() == null) {
            throw new ValidationException("Start date and End date are required.");
        }
        if (taskTemplate.getEndDate().isBefore(taskTemplate.getStartDate())) {
            throw new ValidationException("End date can not be before Start date");
        }

        if (taskTemplate.getRecurrenceInterval() < 1) {
            throw new ValidationException("Recurring interval must be positive number.");
        }

        if(taskTemplate.getExecutionTime() == null){
            throw new ValidationException("You must choose execution time!");
        }

        LocalDate danas = LocalDate.now();
        LocalTime sada = LocalTime.now();

        if (taskTemplate.getStartDate().isEqual(danas)) {

            if (taskTemplate.getExecutionTime().isBefore(sada)) {

                throw new ValidationException("You can not create task today for a time that has already passed!.");
            }
        }
    }

    private void validateOneTimeTask(OneTimeTaskDTO taskTemplate) throws ValidationException {

        if (taskTemplate.getName() == null || taskTemplate.getName().trim().isEmpty()) {
            throw new ValidationException("Task name is required.");
        }

        if (taskTemplate.getCategoryColour() == null) {
            throw new ValidationException("You must choose category.");
        }

        if(taskTemplate.getExecutionTime() == null){
            throw new ValidationException("You must choose execution time!");
        }

        LocalDate danas = LocalDate.now();
        LocalTime sada = LocalTime.now();

        if (taskTemplate.getStartDate().isEqual(danas)) {

            if (taskTemplate.getExecutionTime().isBefore(sada)) {

                throw new ValidationException("You can not create task today for a time that has already passed!.");
            }
        }
    }

    public void markRecurringTaskAsDone(int taskId, String userId) {

        RecurringTask task = repository.getTaskById(taskId);
        if (task == null || task.getStatus() != RecurringTaskStatus.ACTIVE) {
            return;
        }

        TaskQuote quote = task.getQuotaCategory();
        if (quote == TaskQuote.NO_QUOTA) {

            //int xpGained = task.getDifficulty() + task.getImportance();
            int xpGained=task.getTotalXp();
            task.setAwarded(true);
            task.setStatus(RecurringTaskStatus.COMPLETED);
            repository.updateRecurringTask(task);
            profileService.incrementProfileFieldValue(userId, "xp", xpGained) .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "XP uspešno ažuriran!");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Greška pri ažuriranju XP-a", e);
                    });
            return;
        }

        LocalDate today = task.getStartDate();
        LocalDate startDate = getStartDateForQuota(quote, today);
        LocalDate endDate = getEndDateForQuota(quote, today);
        int limit = getLimitForCategory(quote);

        int completedCountRecurringTasks = repository.countCompletedRecurringTasksWithXpInCategory(quote, startDate, endDate, userId);
        int completedCountOneTimeTasks = repository.countCompletedOneTimeTasksWithXpInCategory(quote, startDate, endDate, userId);
        int completedCount = completedCountOneTimeTasks + completedCountRecurringTasks;

        boolean shouldAwardXp = completedCount < limit;
        int xpGained = 0;

        if (shouldAwardXp) {
            task.setAwarded(true);
            xpGained = task.getDifficulty() + task.getImportance();
            profileService.incrementProfileFieldValue(userId, "xp", xpGained) .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "XP uspješno ažuriran!");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Greška pri ažuriranju XP-a", e);
                    });
        }

        task.setStatus(RecurringTaskStatus.COMPLETED);
        repository.updateRecurringTask(task);
    }

    public void markOneTimeTaskAsDone(int taskId, String userId) {

        OneTimeTask oneTimeTask = repository.getOneTimeTaskById(taskId);
        if (oneTimeTask == null || oneTimeTask.getStatus() != OneTimeTaskStatus.ACTIVE) {
            return;
        }

        TaskQuote quote = oneTimeTask.getQuotaCategory();
        if (quote == TaskQuote.NO_QUOTA) {

            //int xpGained = oneTimeTask.getDifficulty() + task.getImportance();
            int xpGained=oneTimeTask.getTotalXp();
            oneTimeTask.setAwarded(true);
            oneTimeTask.setStatus(OneTimeTaskStatus.COMPLETED);
            repository.updateOneTimeTask(oneTimeTask);
            profileService.incrementProfileFieldValue(userId, "xp", xpGained) .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "XP uspješno ažuriran!");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Greška pri ažuriranju XP-a", e);
                    });
            return;
        }

        LocalDate today = oneTimeTask.getStartDate();
        LocalDate startDate = getStartDateForQuota(quote, today);
        LocalDate endDate = getEndDateForQuota(quote, today);
        int limit = getLimitForCategory(quote);

        int completedCountRecurringTasks = repository.countCompletedRecurringTasksWithXpInCategory(quote, startDate, endDate, userId);
        int completedCountOneTimeTasks = repository.countCompletedOneTimeTasksWithXpInCategory(quote, startDate, endDate, userId);
        int completedCount = completedCountOneTimeTasks + completedCountRecurringTasks;

        boolean shouldAwardXp = completedCount < limit;
        int xpGained = 0;

        if (shouldAwardXp) {
            oneTimeTask.setAwarded(true);
            xpGained = oneTimeTask.getDifficulty() + oneTimeTask.getImportance();
            profileService.incrementProfileFieldValue(userId, "xp", xpGained) .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "XP uspešno ažuriran!");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Greška pri ažuriranju XP-a", e);
                    });
        }

        oneTimeTask.setStatus(OneTimeTaskStatus.COMPLETED);
        repository.updateOneTimeTask(oneTimeTask);
    }

    private int getLimitForCategory(TaskQuote quote) {

        switch (quote) {
            case EASY_NORMAL:
            case EASY_IMPORTANT:

                return 5;

            case HARD_EXTREME:
                return 2;

            case EXTREMELY_HARD:
                return 1;

            case SPECIAL:
                return 1;

            case NO_QUOTA:
            default:
                return Integer.MAX_VALUE;
        }
    }

    private LocalDate getStartDateForQuota(TaskQuote quote, LocalDate taskDate) {
        if (quote == null) {
            return taskDate;
        }

        switch (quote) {
            case EXTREMELY_HARD:
                return taskDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

            case SPECIAL:
                return taskDate.withDayOfMonth(1);

            case EASY_NORMAL:
            case EASY_IMPORTANT:
            case HARD_EXTREME:
            case NO_QUOTA:
            default:
                return taskDate;
        }
    }

    private LocalDate getEndDateForQuota(TaskQuote quote, LocalDate taskDate) {
        if (quote == null) {
            return taskDate;
        }

        switch (quote) {
            case EXTREMELY_HARD:
                return taskDate.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

            case SPECIAL:
                return taskDate.withDayOfMonth(taskDate.lengthOfMonth());

            case EASY_NORMAL:
            case EASY_IMPORTANT:
            case HARD_EXTREME:
            case NO_QUOTA:
            default:
                return taskDate;
        }
    }







}
