package com.example.myhobitapplication.services;

import com.example.myhobitapplication.databases.DataBaseRecurringTaskHelper;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.models.Task;

public class TaskService {

    private final DataBaseRecurringTaskHelper db;
    private RecurringTask task;
    public TaskService(DataBaseRecurringTaskHelper db){
        this.db = db;

    }
    public long saveRecurringTask(RecurringTask task){
       return db.insertRecurringTask(task);
    }

}
