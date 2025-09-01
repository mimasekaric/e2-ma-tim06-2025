package com.example.myhobitapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.myhobitapplication.enums.OneTimeTaskStatus;
import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.models.OneTimeTask;
import com.example.myhobitapplication.models.RecurringTask;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    private final AppDataBaseHelper dbHelper;
    private SQLiteDatabase database;

    public TaskRepository(Context context){

        dbHelper = new AppDataBaseHelper(context);

    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public long insertRecurringTask(RecurringTask recurringTask){

        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppDataBaseHelper.COLUMN_TITLE, recurringTask.getName());
        values.put(AppDataBaseHelper.COLUMN_DESCRIPTION, recurringTask.getDescription());
        values.put(AppDataBaseHelper.COLUMN_DIFFICULTY_XP, recurringTask.getDifficulty());
        values.put(AppDataBaseHelper.COLUMN_IMPORTANCE_XP, recurringTask.getImportance());
        values.put(AppDataBaseHelper.COLUMN_CTG_ID, recurringTask.getCategoryColour());
        values.put(AppDataBaseHelper.COLUMN_RECURRENCE_INTERVAL, recurringTask.getRecurrenceInterval());
        values.put(AppDataBaseHelper.COLUMN_RECURRENCE_UNIT, recurringTask.getRecurrenceUnit().name());
        values.put(AppDataBaseHelper.COLUMN_EXECUTION_TIME, recurringTask.getExecutionTime().toString());
        values.put(AppDataBaseHelper.COLUMN_STATUS, recurringTask.getStatus().toString());
        values.put(AppDataBaseHelper.COLUMN_START_DATE, recurringTask.getStartDate().toString());
        values.put(AppDataBaseHelper.COLUMN_END_DATE, recurringTask.getEndDate().toString());
        values.put(AppDataBaseHelper.COLUMN_CREATION_DATE, recurringTask.getCreationDate().toString());
        values.put(AppDataBaseHelper.COLUMN_FINISHED_DATE, recurringTask.getFinishedDate().toString());
        values.put(AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID, recurringTask.getFirstRecurringTaskId().toString());

        long newRowId = database.insert(AppDataBaseHelper.TABLE_RECURRING_TASKS, null, values);
        database.close();
        return newRowId;

    }

    public long insertSingleRecurringTask(RecurringTask recurringTask) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(AppDataBaseHelper.COLUMN_TITLE, recurringTask.getName());
        values.put(AppDataBaseHelper.COLUMN_DESCRIPTION, recurringTask.getDescription());
        values.put(AppDataBaseHelper.COLUMN_DIFFICULTY_XP, recurringTask.getDifficulty());
        values.put(AppDataBaseHelper.COLUMN_IMPORTANCE_XP, recurringTask.getImportance());
        values.put(AppDataBaseHelper.COLUMN_CTG_ID, recurringTask.getCategoryColour());
        values.put(AppDataBaseHelper.COLUMN_RECURRENCE_INTERVAL, recurringTask.getRecurrenceInterval());
        values.put(AppDataBaseHelper.COLUMN_RECURRENCE_UNIT, recurringTask.getRecurrenceUnit().name());
        values.put(AppDataBaseHelper.COLUMN_EXECUTION_TIME, recurringTask.getExecutionTime().toString());
        values.put(AppDataBaseHelper.COLUMN_STATUS, recurringTask.getStatus().toString());
        values.put(AppDataBaseHelper.COLUMN_START_DATE, recurringTask.getStartDate().toString());
        values.put(AppDataBaseHelper.COLUMN_END_DATE, recurringTask.getEndDate().toString());
        values.put(AppDataBaseHelper.COLUMN_CREATION_DATE, recurringTask.getCreationDate().toString());
        values.put(AppDataBaseHelper.COLUMN_FINISHED_DATE, recurringTask.getFinishedDate().toString());
        values.put(AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID, recurringTask.getFirstRecurringTaskId().toString());

        long newRowId = database.insert(AppDataBaseHelper.TABLE_RECURRING_TASKS, null, values);
        dbHelper.close();
        return newRowId;
    }

    public List<RecurringTask> getAllRecurringTasks() {
        List<RecurringTask> taskList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + AppDataBaseHelper.TABLE_RECURRING_TASKS;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                RecurringTask task = new RecurringTask();
                task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRING_TASK_ID)));
                task.setName(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DESCRIPTION)));
                task.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_XP)));
                task.setImportance(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_XP)));
                task.setCategoryColour(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CTG_ID)));
                task.setRecurrenceInterval(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRENCE_INTERVAL)));
                task.setFirstRecurringTaskId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID)));

                String status = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_STATUS));
                task.setStatus(RecurringTaskStatus.valueOf(status));

                String recurrenceUnitString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRENCE_UNIT));
                task.setRecurrenceUnit(RecurrenceUnit.valueOf(recurrenceUnitString));

                String executionTimeString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EXECUTION_TIME));
                task.setExecutionTime(LocalTime.parse(executionTimeString));

                String startDateString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_START_DATE));
                task.setStartDate(LocalDate.parse(startDateString));

                String endDateString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_END_DATE));
                task.setEndDate(LocalDate.parse(endDateString));

                String creationDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CREATION_DATE));
                task.setCreationDate(LocalDate.parse(creationDate));

                String finishedDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISHED_DATE));
                task.setFinishedDate(LocalDate.parse(finishedDate));

                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return taskList;
    }

    public RecurringTask getTaskById(long id) {
        RecurringTask task = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDataBaseHelper.TABLE_RECURRING_TASKS,
                null,
                AppDataBaseHelper.COLUMN_RECURRING_TASK_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {

            task = new RecurringTask();

            task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRING_TASK_ID)));
            task.setName(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_TITLE)));
            task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DESCRIPTION)));
            task.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_XP)));
            task.setImportance(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_XP)));
            task.setCategoryColour(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CTG_ID)));
            task.setFirstRecurringTaskId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID)));
            task.setRecurrenceInterval(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRENCE_INTERVAL)));

            String recurrenceUnitString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRENCE_UNIT));
            task.setRecurrenceUnit(RecurrenceUnit.valueOf(recurrenceUnitString));

            String executionTimeString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EXECUTION_TIME));
            task.setExecutionTime(LocalTime.parse(executionTimeString));

            String startDateString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_START_DATE));
            task.setStartDate(LocalDate.parse(startDateString));

            String endDateString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_END_DATE));
            task.setEndDate(LocalDate.parse(endDateString));

            String creationDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CREATION_DATE));
            task.setCreationDate(LocalDate.parse(creationDate));

            String finishedDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISHED_DATE));
            task.setFinishedDate(LocalDate.parse(finishedDate));


            String status = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_STATUS));
            task.setStatus(RecurringTaskStatus.valueOf(status));

            cursor.close();
        }
        db.close();
        return task;
    }


    public long updateRecurringTask(RecurringTask task) {

        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(AppDataBaseHelper.COLUMN_TITLE, task.getName());
        values.put(AppDataBaseHelper.COLUMN_DESCRIPTION, task.getDescription());
        values.put(AppDataBaseHelper.COLUMN_DIFFICULTY_XP, task.getDifficulty());
        values.put(AppDataBaseHelper.COLUMN_IMPORTANCE_XP, task.getImportance());
        values.put(AppDataBaseHelper.COLUMN_CTG_ID, task.getCategoryColour());
        values.put(AppDataBaseHelper.COLUMN_RECURRENCE_INTERVAL, task.getRecurrenceInterval());
        values.put(AppDataBaseHelper.COLUMN_RECURRENCE_UNIT, task.getRecurrenceUnit().name());
        values.put(AppDataBaseHelper.COLUMN_EXECUTION_TIME, task.getExecutionTime().toString());
        values.put(AppDataBaseHelper.COLUMN_STATUS, task.getStatus().toString());
        values.put(AppDataBaseHelper.COLUMN_START_DATE, task.getStartDate().toString());
        values.put(AppDataBaseHelper.COLUMN_END_DATE, task.getEndDate().toString());
        values.put(AppDataBaseHelper.COLUMN_CREATION_DATE, task.getCreationDate().toString());
        values.put(AppDataBaseHelper.COLUMN_FINISHED_DATE, task.getFinishedDate().toString());
        values.put(AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID, task.getFirstRecurringTaskId().toString());

        String selection = AppDataBaseHelper.COLUMN_RECURRING_TASK_ID + " = ?";
        String[] selectionArgs = { String.valueOf(task.getId()) };

        int count = database.update(
                AppDataBaseHelper.TABLE_RECURRING_TASKS,
                values,
                selection,
                selectionArgs);

        database.close();
        return count;

    }

    public void insertRecurringTaskBatch(List<RecurringTask> instances) {
        database = dbHelper.getWritableDatabase();
       // ContentValues values = new ContentValues();
        database.beginTransaction();
        try {
            for (RecurringTask recurringTask : instances) {
                ContentValues values = new ContentValues();
                values.put(AppDataBaseHelper.COLUMN_TITLE, recurringTask.getName());
                values.put(AppDataBaseHelper.COLUMN_DESCRIPTION, recurringTask.getDescription());
                values.put(AppDataBaseHelper.COLUMN_DIFFICULTY_XP, recurringTask.getDifficulty());
                values.put(AppDataBaseHelper.COLUMN_IMPORTANCE_XP, recurringTask.getImportance());
                values.put(AppDataBaseHelper.COLUMN_CTG_ID, recurringTask.getCategoryColour());
                values.put(AppDataBaseHelper.COLUMN_RECURRENCE_INTERVAL, recurringTask.getRecurrenceInterval());
                values.put(AppDataBaseHelper.COLUMN_RECURRENCE_UNIT, recurringTask.getRecurrenceUnit().name());
                values.put(AppDataBaseHelper.COLUMN_EXECUTION_TIME, recurringTask.getExecutionTime().toString());
                values.put(AppDataBaseHelper.COLUMN_STATUS, recurringTask.getStatus().toString());
                values.put(AppDataBaseHelper.COLUMN_START_DATE, recurringTask.getStartDate().toString());
                values.put(AppDataBaseHelper.COLUMN_END_DATE, recurringTask.getEndDate().toString());
                values.put(AppDataBaseHelper.COLUMN_CREATION_DATE, recurringTask.getCreationDate().toString());
                values.put(AppDataBaseHelper.COLUMN_FINISHED_DATE, recurringTask.getFinishedDate().toString());
                values.put(AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID, recurringTask.getFirstRecurringTaskId().toString());
                database.insert(AppDataBaseHelper.TABLE_RECURRING_TASKS, null, values);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        database.close();
    }

    public void updateFirstRecurringTaskId(long taskId, long firstId) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID, firstId);
        database.update(AppDataBaseHelper.TABLE_RECURRING_TASKS, values, AppDataBaseHelper.COLUMN_RECURRING_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
        database.close();
    }

    public int updateOutdatedTasksToNotDone() {
        database = dbHelper.getWritableDatabase();

        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
        String threeDaysAgoString = threeDaysAgo.toString();

        ContentValues values = new ContentValues();
        values.put(AppDataBaseHelper.COLUMN_STATUS, RecurringTaskStatus.INCOMPLETE.name());

        String selection = AppDataBaseHelper.COLUMN_STATUS + " = ? AND " + AppDataBaseHelper.COLUMN_START_DATE + " < ?";

        String[] selectionArgs = { RecurringTaskStatus.ACTIVE.name(), threeDaysAgoString };

        int count = database.update(
                AppDataBaseHelper.TABLE_RECURRING_TASKS,
                values,
                selection,
                selectionArgs);

        database.close();

        return count;
    }


    public int deleteRecurringTaskAndFutureInstances(RecurringTask taskToDelete) {

        if (taskToDelete == null || taskToDelete.getFirstRecurringTaskId() == null || taskToDelete.getStartDate() == null) {
            return 0;
        }

        database = dbHelper.getWritableDatabase();
        int deletedRows = 0;

        database.beginTransaction();
        try {

            String whereClause = AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID + " = ? AND " +
                    AppDataBaseHelper.COLUMN_START_DATE + " >= ?";


            String[] whereArgs = {
                    String.valueOf(taskToDelete.getFirstRecurringTaskId()),
                    taskToDelete.getStartDate().toString()
            };

            deletedRows = database.delete(AppDataBaseHelper.TABLE_RECURRING_TASKS, whereClause, whereArgs);

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        database.close();

        return deletedRows;
    }

    public int countTasksByStatusInDateRange(RecurringTaskStatus status, LocalDate startDate, LocalDate endDate) {

        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = AppDataBaseHelper.COLUMN_STATUS + " = ? AND " +
                AppDataBaseHelper.COLUMN_FINISHED_DATE + " BETWEEN ? AND ?";


        String[] selectionArgs = {
                status.name(),
                startDate.toString(),
                endDate.toString()
        };

        String countQuery = "SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_RECURRING_TASKS +
                " WHERE " + selection;

        Cursor cursor = db.rawQuery(countQuery, selectionArgs);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();

        return count;
    }

    public int countTasksByDateRange(LocalDate startDate, LocalDate endDate) {

        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = AppDataBaseHelper.COLUMN_CREATION_DATE + " BETWEEN ? AND ?";



        String[] selectionArgs = {
                startDate.toString(),
                endDate.toString()
        };

        String countQuery = "SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_RECURRING_TASKS +
                " WHERE " + selection;

        Cursor cursor = db.rawQuery(countQuery, selectionArgs);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();

        return count;
    }

    public long insertOneTimeTask(OneTimeTask oneTimeTask){

        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppDataBaseHelper.COLUMN_TITLE, oneTimeTask.getName());
        values.put(AppDataBaseHelper.COLUMN_DESCRIPTION, oneTimeTask.getDescription());
        values.put(AppDataBaseHelper.COLUMN_DIFFICULTY_XP, oneTimeTask.getDifficulty());
        values.put(AppDataBaseHelper.COLUMN_IMPORTANCE_XP, oneTimeTask.getImportance());
        values.put(AppDataBaseHelper.COLUMN_CTG_ID, oneTimeTask.getCategoryColour());
        values.put(AppDataBaseHelper.COLUMN_EXECUTION_TIME, oneTimeTask.getExecutionTime().toString());
        values.put(AppDataBaseHelper.COLUMN_STATUS, oneTimeTask.getStatus().toString());
        values.put(AppDataBaseHelper.COLUMN_CREATION_DATE, oneTimeTask.getCreationDate().toString());
        values.put(AppDataBaseHelper.COLUMN_FINISHED_DATE, oneTimeTask.getFinishedDate().toString());
        values.put(AppDataBaseHelper.COLUMN_START_DATE, oneTimeTask.getStartDate().toString());

        long newRowId = database.insert(AppDataBaseHelper.TABLE_ONE_TIME_TASKS, null, values);
        database.close();
        return newRowId;
    }

    public long updateOneTimeTask(OneTimeTask task) {

        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(AppDataBaseHelper.COLUMN_TITLE, task.getName());
        values.put(AppDataBaseHelper.COLUMN_DESCRIPTION, task.getDescription());
        values.put(AppDataBaseHelper.COLUMN_DIFFICULTY_XP, task.getDifficulty());
        values.put(AppDataBaseHelper.COLUMN_IMPORTANCE_XP, task.getImportance());
        values.put(AppDataBaseHelper.COLUMN_CTG_ID, task.getCategoryColour());
        values.put(AppDataBaseHelper.COLUMN_EXECUTION_TIME, task.getExecutionTime().toString());
        values.put(AppDataBaseHelper.COLUMN_STATUS, task.getStatus().toString());
        values.put(AppDataBaseHelper.COLUMN_CREATION_DATE, task.getCreationDate().toString());
        values.put(AppDataBaseHelper.COLUMN_FINISHED_DATE, task.getFinishedDate().toString());
        values.put(AppDataBaseHelper.COLUMN_START_DATE, task.getStartDate().toString());

        String selection = AppDataBaseHelper.COLUMN_ONE_TIME_TASK_ID + " = ?";
        String[] selectionArgs = { String.valueOf(task.getId()) };

        int count = database.update(
                AppDataBaseHelper.TABLE_ONE_TIME_TASKS,
                values,
                selection,
                selectionArgs);

        database.close();
        return count;
    }


    public List<OneTimeTask> getAllOneTimeTasks() {
        List<OneTimeTask> taskList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + AppDataBaseHelper.TABLE_ONE_TIME_TASKS;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                OneTimeTask task = new OneTimeTask();
                task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_ONE_TIME_TASK_ID)));
                task.setName(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DESCRIPTION)));
                task.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_XP)));
                task.setImportance(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_XP)));
                task.setCategoryColour(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CTG_ID)));

                String status = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_STATUS));
                task.setStatus(OneTimeTaskStatus.valueOf(status));


                String executionTimeString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EXECUTION_TIME));
                task.setExecutionTime(LocalTime.parse(executionTimeString));


                String creationDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CREATION_DATE));
                task.setCreationDate(LocalDate.parse(creationDate));

                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_START_DATE));
                task.setStartDate(LocalDate.parse(startDate));

                String finishedDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISHED_DATE));
                task.setFinishedDate(LocalDate.parse(finishedDate));

                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return taskList;
    }

    public OneTimeTask getOneTimeTaskById(long id) {
        OneTimeTask task = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDataBaseHelper.TABLE_ONE_TIME_TASKS,
                null,
                AppDataBaseHelper.COLUMN_ONE_TIME_TASK_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {

            task = new OneTimeTask();

            task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_ONE_TIME_TASK_ID)));
            task.setName(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_TITLE)));
            task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DESCRIPTION)));
            task.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_XP)));
            task.setImportance(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_XP)));
            task.setCategoryColour(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CTG_ID)));

            String status = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_STATUS));
            task.setStatus(OneTimeTaskStatus.valueOf(status));

            String executionTimeString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EXECUTION_TIME));
            task.setExecutionTime(LocalTime.parse(executionTimeString));


            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_START_DATE));
            task.setStartDate(LocalDate.parse(startDate));

            String creationDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CREATION_DATE));
            task.setCreationDate(LocalDate.parse(creationDate));

            String finishedDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISHED_DATE));
            task.setFinishedDate(LocalDate.parse(finishedDate));

            cursor.close();
        }
        db.close();
        return task;
    }


    public int deleteOneTimeTask(long taskId) {



        database = dbHelper.getWritableDatabase();
        int deletedRows = 0;


            String whereClause = AppDataBaseHelper.COLUMN_ONE_TIME_TASK_ID + " = ? AND ";


            String[] whereArgs = {
                    String.valueOf(taskId),
            };

            deletedRows = database.delete(AppDataBaseHelper.TABLE_ONE_TIME_TASKS, whereClause, whereArgs);

        database.close();

        return deletedRows;
    }
}
