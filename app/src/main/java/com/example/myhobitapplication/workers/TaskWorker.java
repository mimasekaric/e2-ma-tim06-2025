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
        // Ovaj kod se izvršava u pozadini, na posebnom thread-u.

        try {
            // 1. Dobij instancu repozitorijuma.
            //    getApplicationContext() je siguran način da se dobije Context ovde.
            TaskRepository repository = new TaskRepository(getApplicationContext());

            // 2. Pozovi metodu u repozitorijumu koja radi sav posao.
            int updatedRows = repository.updateOutdatedTasksToNotDone();

            // Logiraj rezultat (opciono, ali korisno za debagovanje)
          //  Log.d("UpdateTaskStatusWorker", "Broj zadataka ažuriranih u 'NOT_DONE': " + updatedRows);

            // 3. Javi sistemu da je posao uspešno završen.
            return Result.success();

        } catch (Exception e) {
            // Ako se desi bilo kakva greška, javi sistemu da posao nije uspeo.
            // WorkManager može pokušati ponovo kasnije.
           // Log.e("UpdateTaskStatusWorker", "Greška prilikom ažuriranja statusa zadataka", e);
            return Result.failure();
        }
    }


}
