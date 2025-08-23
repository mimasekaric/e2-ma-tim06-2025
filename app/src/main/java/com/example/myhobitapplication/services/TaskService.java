package com.example.myhobitapplication.services;

import com.example.myhobitapplication.databases.DataBaseRecurringTaskHelper;
import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.models.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskService {

    private final DataBaseRecurringTaskHelper db;
    private RecurringTask task;
    private final Map<LocalDate, List<RecurringTask>> scheduledTasks;
    public TaskService(DataBaseRecurringTaskHelper db){
        this.db = db;
        scheduledTasks = new HashMap<>();
        generateSchedule();

    }
    public long saveRecurringTask(RecurringTask task){
       return db.insertRecurringTask(task);
    }
    public List<RecurringTask> getRecurringTasks(){ return db.getAllRecurringTasks();}

    public void generateSchedule() {

        scheduledTasks.clear();


        List<RecurringTask> allTasks = db.getAllRecurringTasks();


        for (RecurringTask task : allTasks) {
            LocalDate currentDate = task.getStartDate();
            LocalDate endDate = task.getEndDate();


            LocalDate maxDate = LocalDate.now().plusMonths(12);

            while (!currentDate.isAfter(endDate) && !currentDate.isAfter(maxDate)) {

                List<RecurringTask> tasksForDate = scheduledTasks.getOrDefault(currentDate, new ArrayList<>());
                tasksForDate.add(task);
                scheduledTasks.put(currentDate, tasksForDate);


                currentDate = calculateNextDate(currentDate, task.getRecurrenceInterval(), task.getRecurrenceUnit());
            }
        }
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

    public Map<LocalDate, List<RecurringTask>> getScheduledTasks() {
        return scheduledTasks;
    }

}
