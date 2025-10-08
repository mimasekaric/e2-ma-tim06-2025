package com.example.myhobitapplication.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myhobitapplication.databases.TaskRepository;

public class TaskWorker extends Worker {

    public TaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {


        try {

            TaskRepository repository = new TaskRepository(getApplicationContext());


            int updatedRows = repository.updateOutdatedTasksToNotDone();

            return Result.success();

        } catch (Exception e) {

            return Result.failure();
        }
    }


}
