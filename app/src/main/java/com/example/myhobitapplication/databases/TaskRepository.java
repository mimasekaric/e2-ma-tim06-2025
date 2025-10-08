package com.example.myhobitapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.myhobitapplication.enums.OneTimeTaskStatus;
import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.enums.RecurringTaskStatus;
import com.example.myhobitapplication.enums.TaskQuote;
import com.example.myhobitapplication.models.OneTimeTask;
import com.example.myhobitapplication.models.RecurringTask;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        values.put(AppDataBaseHelper.COLUMN_FINISH_DATE, recurringTask.getFinishDate().toString());
        values.put(AppDataBaseHelper.COLUMN_REMAINING_TIME, recurringTask.getRemainingTime().toString());
        values.put(AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID, recurringTask.getFirstRecurringTaskId().toString());
        values.put(AppDataBaseHelper.COLUMN_USER_ID, recurringTask.getUserUid());
        values.put(AppDataBaseHelper.COLUMN_IS_AWARDED, recurringTask.isAwarded());

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
        values.put(AppDataBaseHelper.COLUMN_FINISH_DATE, recurringTask.getFinishDate().toString());
        values.put(AppDataBaseHelper.COLUMN_REMAINING_TIME, recurringTask.getRemainingTime().toString());
        values.put(AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID, recurringTask.getFirstRecurringTaskId().toString());
        values.put(AppDataBaseHelper.COLUMN_USER_ID, recurringTask.getUserUid());
        values.put(AppDataBaseHelper.COLUMN_IS_AWARDED, recurringTask.isAwarded());
        TaskQuote difficultyType = getDifficultyType(recurringTask.getDifficulty());
        TaskQuote importanceType = getImportanceType(recurringTask.getImportance());
        values.put(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE, difficultyType.toString());
        values.put(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE, importanceType.toString());

        long newRowId = database.insert(AppDataBaseHelper.TABLE_RECURRING_TASKS, null, values);
        dbHelper.close();
        return newRowId;
    }

//    public List<RecurringTask> getAllRecurringTasks(String userUid) {
//        List<RecurringTask> taskList = new ArrayList<>();
//
//        String selectQuery = "SELECT * FROM " + AppDataBaseHelper.TABLE_RECURRING_TASKS;
//
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                RecurringTask task = new RecurringTask();
//                task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRING_TASK_ID)));
//                task.setName(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_TITLE)));
//                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DESCRIPTION)));
//                task.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_XP)));
//                task.setImportance(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_XP)));
//                task.setCategoryColour(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CTG_ID)));
//                task.setRecurrenceInterval(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRENCE_INTERVAL)));
//                task.setFirstRecurringTaskId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID)));
//
//                String status = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_STATUS));
//                task.setStatus(RecurringTaskStatus.valueOf(status));
//
//                String recurrenceUnitString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRENCE_UNIT));
//                task.setRecurrenceUnit(RecurrenceUnit.valueOf(recurrenceUnitString));
//
//                String executionTimeString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EXECUTION_TIME));
//                task.setExecutionTime(LocalTime.parse(executionTimeString));
//
//                String startDateString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_START_DATE));
//                task.setStartDate(LocalDate.parse(startDateString));
//
//                String endDateString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_END_DATE));
//                task.setEndDate(LocalDate.parse(endDateString));
//
//                String creationDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CREATION_DATE));
//                task.setCreationDate(LocalDate.parse(creationDate));
//
//                String finishedDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISHED_DATE));
//                task.setFinishedDate(LocalDate.parse(finishedDate));
//
//                task.setUserUid(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));
//
//                taskList.add(task);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//
//        return taskList;
//    }

    public List<RecurringTask> getAllRecurringTasks(String userUid) {
        if (userUid == null || userUid.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<RecurringTask> taskList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();


            String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ?";


            String[] selectionArgs = { userUid };

            cursor = db.query(
                    AppDataBaseHelper.TABLE_RECURRING_TASKS,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );


            if (cursor != null && cursor.moveToFirst()) {
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

                    String finishDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISH_DATE));
                    task.setFinishDate(LocalDateTime.parse(finishDate));

                    String remainingTime = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_REMAINING_TIME));
                    task.setRemainingTime(Duration.parse(remainingTime));

                    task.setUserUid(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));

                    String isAwarded = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IS_AWARDED));


                    if(isAwarded.equals("1")){
                        task.setAwarded(true);
                    }else{
                        task.setAwarded(false);
                    }

                    String difficultyType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE));
                    task.setDifficultyQuota(TaskQuote.valueOf(difficultyType));

                    String importanceType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE));
                    task.setImportanceQuota(TaskQuote.valueOf(importanceType));

                    taskList.add(task);

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

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


            String finishDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISH_DATE));
            task.setFinishDate(LocalDateTime.parse(finishDate));

            String remainingTime = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_REMAINING_TIME));
            task.setRemainingTime(Duration.parse(remainingTime));


            String status = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_STATUS));
            task.setStatus(RecurringTaskStatus.valueOf(status));

            task.setUserUid(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));

            String isAwarded = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IS_AWARDED));
            task.setAwarded(Boolean.parseBoolean(isAwarded));

            String difficultyType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE));
            task.setDifficultyQuota(TaskQuote.valueOf(difficultyType));

            String importanceType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE));
            task.setImportanceQuota(TaskQuote.valueOf(importanceType));

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
        values.put(AppDataBaseHelper.COLUMN_FINISH_DATE, task.getFinishDate().toString());
        values.put(AppDataBaseHelper.COLUMN_REMAINING_TIME, task.getRemainingTime().toString());

        values.put(AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID, task.getFirstRecurringTaskId().toString());
        values.put(AppDataBaseHelper.COLUMN_USER_ID, task.getUserUid());
        values.put(AppDataBaseHelper.COLUMN_IS_AWARDED, task.isAwarded());

        TaskQuote difficultyType = getDifficultyType(task.getDifficulty());
        TaskQuote importanceType = getImportanceType(task.getImportance());
        values.put(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE, difficultyType.toString());
        values.put(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE, importanceType.toString());

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
                values.put(AppDataBaseHelper.COLUMN_FINISH_DATE, recurringTask.getFinishDate().toString());
                values.put(AppDataBaseHelper.COLUMN_REMAINING_TIME, recurringTask.getRemainingTime().toString());
                values.put(AppDataBaseHelper.COLUMN_FIRST_REC_TASK_ID, recurringTask.getFirstRecurringTaskId().toString());
                values.put(AppDataBaseHelper.COLUMN_USER_ID, recurringTask.getUserUid());
                values.put(AppDataBaseHelper.COLUMN_IS_AWARDED, recurringTask.isAwarded());
                TaskQuote difficultyType = getDifficultyType(recurringTask.getDifficulty());
                TaskQuote importanceType = getImportanceType(recurringTask.getImportance());
                values.put(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE, difficultyType.toString());
                values.put(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE, importanceType.toString());
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

//    public int updateOutdatedTasksToNotDone() {
//        database = dbHelper.getWritableDatabase();
//
//        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
//        String threeDaysAgoString = threeDaysAgo.toString();
//
//        ContentValues values = new ContentValues();
//        values.put(AppDataBaseHelper.COLUMN_STATUS, RecurringTaskStatus.INCOMPLETE.name());
//
//        String selection = AppDataBaseHelper.COLUMN_STATUS + " = ? AND " + AppDataBaseHelper.COLUMN_START_DATE + " < ?";
//
//        String[] selectionArgs = { RecurringTaskStatus.ACTIVE.name(), threeDaysAgoString };
//
//        int count = database.update(
//                AppDataBaseHelper.TABLE_RECURRING_TASKS,
//                values,
//                selection,
//                selectionArgs);
//
//        database.close();
//
//        return count;
//    }


    public int updateOutdatedTasksToNotDone() {
        database = dbHelper.getWritableDatabase();

        LocalDateTime today = LocalDateTime.now();

        ContentValues values = new ContentValues();
        values.put(AppDataBaseHelper.COLUMN_STATUS, RecurringTaskStatus.INCOMPLETE.name());

        String selection = AppDataBaseHelper.COLUMN_STATUS + " = ? AND " +
                "datetime(" + AppDataBaseHelper.COLUMN_FINISH_DATE  + ")" +
                " < datetime(?)";

        String[] selectionArgs = {
                RecurringTaskStatus.ACTIVE.name(),
                today.toString()
        };

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

    public int countRecurringTasksByStatusInDateRange(RecurringTaskStatus status, LocalDate startDate, LocalDate endDate, String userUid) {

        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND " +
                AppDataBaseHelper.COLUMN_STATUS + " = ? AND " +
                AppDataBaseHelper.COLUMN_IS_AWARDED + " = ? AND " +
                AppDataBaseHelper.COLUMN_FINISHED_DATE + " BETWEEN ? AND ?";


        String[] selectionArgs = {
                userUid,
                status.name(),
                "1",
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

    public int countRecurringTasksByDateRange(LocalDate startDate, LocalDate endDate, String userUid) {

        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        RecurringTaskStatus statusOne = RecurringTaskStatus.CANCELED;
        RecurringTaskStatus statusTwo = RecurringTaskStatus.PAUSED;

        String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND " +
                AppDataBaseHelper.COLUMN_STATUS + " != ? AND " +
                AppDataBaseHelper.COLUMN_STATUS + " != ? AND " +
                AppDataBaseHelper.COLUMN_CREATION_DATE + " BETWEEN ? AND ?";



        String[] selectionArgs = {
                userUid,
                statusOne.name(),
                statusTwo.name(),
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
    public int countRecurringTasksCreatedBeforeThisLevel(LocalDate startDate, LocalDate endDate, String userUid) {

        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        RecurringTaskStatus statusOne = RecurringTaskStatus.COMPLETED;

        String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND " +
                AppDataBaseHelper.COLUMN_STATUS + " = ? AND " +
                AppDataBaseHelper.COLUMN_IS_AWARDED + " = ? AND " +
                AppDataBaseHelper.COLUMN_CREATION_DATE + " < ? AND "  +
                AppDataBaseHelper.COLUMN_FINISHED_DATE + " BETWEEN ? AND ?";



        String[] selectionArgs = {
                userUid,
                statusOne.name(),
                "1",
                startDate.toString(),
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


    public int countOneTimeTasksByStatusInDateRange(RecurringTaskStatus status, LocalDate startDate, LocalDate endDate, String userUid) {

        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND " +
                AppDataBaseHelper.COLUMN_STATUS + " = ? AND " +
                AppDataBaseHelper.COLUMN_IS_AWARDED + " = ? AND " +
                AppDataBaseHelper.COLUMN_FINISHED_DATE + " BETWEEN ? AND ?";


        String[] selectionArgs = {
                userUid,
                status.name(),
                "1",
                startDate.toString(),
                endDate.toString()
        };

        String countQuery = "SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_ONE_TIME_TASKS +
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

    public int countOneTimeTasksByDateRange(LocalDate startDate, LocalDate endDate, String userUid) {

        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        OneTimeTaskStatus statusOne = OneTimeTaskStatus.CANCELED;
        OneTimeTaskStatus statusTwo = OneTimeTaskStatus.PAUSED;

        String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND " +
                AppDataBaseHelper.COLUMN_STATUS + " != ? AND " +
                AppDataBaseHelper.COLUMN_STATUS + " != ? AND " +
                AppDataBaseHelper.COLUMN_CREATION_DATE + " BETWEEN ? AND ?";



        String[] selectionArgs = {
                userUid,
                statusOne.name(),
                statusTwo.name(),
                startDate.toString(),
                endDate.toString()
        };

        String countQuery = "SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_ONE_TIME_TASKS +
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
    public int countOneTimeTasksCreatedBeforeThisLevel(LocalDate startDate, LocalDate endDate, String userUid) {

        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        OneTimeTaskStatus statusOne = OneTimeTaskStatus.COMPLETED;

        String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND " +
                AppDataBaseHelper.COLUMN_STATUS + " = ? AND " +
                AppDataBaseHelper.COLUMN_IS_AWARDED + " = ? AND " +
                AppDataBaseHelper.COLUMN_CREATION_DATE + " < ? AND "  +
                AppDataBaseHelper.COLUMN_FINISHED_DATE + " BETWEEN ? AND ?";



        String[] selectionArgs = {
                userUid,
                statusOne.name(),
                "1",
                startDate.toString(),
                startDate.toString(),
                endDate.toString()
        };

        String countQuery = "SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_ONE_TIME_TASKS +
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
        values.put(AppDataBaseHelper.COLUMN_FINISH_DATE, oneTimeTask.getFinishDate().toString());
        values.put(AppDataBaseHelper.COLUMN_REMAINING_TIME, oneTimeTask.getRemainingTime().toString());
        values.put(AppDataBaseHelper.COLUMN_START_DATE, oneTimeTask.getStartDate().toString());
        values.put(AppDataBaseHelper.COLUMN_USER_ID, oneTimeTask.getUserUid());
        values.put(AppDataBaseHelper.COLUMN_IS_AWARDED, oneTimeTask.isAwarded());
        TaskQuote difficultyType = getDifficultyType(oneTimeTask.getDifficulty());
        TaskQuote importanceType = getImportanceType(oneTimeTask.getImportance());
        values.put(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE, difficultyType.toString());
        values.put(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE, importanceType.toString());


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
        values.put(AppDataBaseHelper.COLUMN_FINISH_DATE, task.getFinishDate().toString());
        values.put(AppDataBaseHelper.COLUMN_REMAINING_TIME, task.getRemainingTime().toString());
        values.put(AppDataBaseHelper.COLUMN_START_DATE, task.getStartDate().toString());
        values.put(AppDataBaseHelper.COLUMN_USER_ID, task.getUserUid());
        values.put(AppDataBaseHelper.COLUMN_IS_AWARDED, task.isAwarded());
        TaskQuote difficultyType = getDifficultyType(task.getDifficulty());
        TaskQuote importanceType = getImportanceType(task.getImportance());
        values.put(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE, difficultyType.toString());
        values.put(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE, importanceType.toString());

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


    public List<OneTimeTask> getAllOneTimeTasks(String userUid) {
        List<OneTimeTask> taskList = new ArrayList<>();
        Cursor cursor = null;

        if (userUid == null || userUid.trim().isEmpty()) {
            return new ArrayList<>();
        }



        try {
            database = dbHelper.getReadableDatabase();

            String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ?";

            String[] selectionArgs = { userUid };

            cursor = database.query(
                    AppDataBaseHelper.TABLE_ONE_TIME_TASKS,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
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


                    String finishDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISH_DATE));
                    task.setFinishDate(LocalDateTime.parse(finishDate));

                    String remainingTime = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_REMAINING_TIME));
                    task.setRemainingTime(Duration.parse(remainingTime));


                    task.setUserUid(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));

                    String isAwarded = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IS_AWARDED));


                    if (isAwarded.equals("1")){
                        task.setAwarded(true);
                    }else{
                        task.setAwarded(false);
                    }

                    String difficultyType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE));
                    task.setDifficultyQuota(TaskQuote.valueOf(difficultyType));

                    String importanceType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE));
                    task.setImportanceQuota(TaskQuote.valueOf(importanceType));

                    taskList.add(task);
                } while (cursor.moveToNext());
            }
        } finally {

            if (cursor != null) {
                cursor.close();
            }
            if (database != null) {
                database.close();
            }
        }

        return taskList;

        //String selectQuery = "SELECT * FROM " + AppDataBaseHelper.TABLE_ONE_TIME_TASKS;

//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                OneTimeTask task = new OneTimeTask();
//                task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_ONE_TIME_TASK_ID)));
//                task.setName(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_TITLE)));
//                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DESCRIPTION)));
//                task.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_XP)));
//                task.setImportance(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_XP)));
//                task.setCategoryColour(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CTG_ID)));
//
//                String status = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_STATUS));
//                task.setStatus(OneTimeTaskStatus.valueOf(status));
//
//
//                String executionTimeString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EXECUTION_TIME));
//                task.setExecutionTime(LocalTime.parse(executionTimeString));
//
//
//                String creationDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CREATION_DATE));
//                task.setCreationDate(LocalDate.parse(creationDate));
//
//                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_START_DATE));
//                task.setStartDate(LocalDate.parse(startDate));
//
//                String finishedDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISHED_DATE));
//                task.setFinishedDate(LocalDate.parse(finishedDate));
//
//                task.setUserUid(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));
//
//                taskList.add(task);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//
//        return taskList;
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


            String finishDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISH_DATE));
            task.setFinishDate(LocalDateTime.parse(finishDate));

            String remainingTime = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_REMAINING_TIME));
            task.setRemainingTime(Duration.parse(remainingTime));

            task.setUserUid(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));

            String isAwarded = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IS_AWARDED));
            task.setAwarded(Boolean.parseBoolean(isAwarded));

            String difficultyType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE));
            task.setDifficultyQuota(TaskQuote.valueOf(difficultyType));

            String importanceType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE));
            task.setImportanceQuota(TaskQuote.valueOf(importanceType));

            cursor.close();
        }
        db.close();
        return task;
    }


    public int deleteOneTimeTask(long taskId) {

        database = dbHelper.getWritableDatabase();

        String selection = AppDataBaseHelper.COLUMN_ONE_TIME_TASK_ID + " = ?";
        String[] selectionArgs  = {String.valueOf(taskId)};

        int deletedRows = database.delete(AppDataBaseHelper.TABLE_ONE_TIME_TASKS, selection, selectionArgs);
        database.close();
        return deletedRows;
    }

    public int countCompletedRecurringTasksWithDifficultyXpInCategory(TaskQuote quote, LocalDate startDate, LocalDate endDate, String userUid) {

        if (quote == null || startDate == null || endDate == null || userUid == null) {
            return 0;
        }

        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();


            String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND " +
                    AppDataBaseHelper.COLUMN_STATUS + " = ? AND " +
                    AppDataBaseHelper.COLUMN_IS_AWARDED + " = 1 AND " +
                    AppDataBaseHelper.COLUMN_FINISHED_DATE + " BETWEEN ? AND ?";

            String[] selectionArgs = {
                    userUid,
                    RecurringTaskStatus.COMPLETED.name(),
                    startDate.toString(),
                    endDate.toString()
            };

            cursor = db.query(
                    AppDataBaseHelper.TABLE_RECURRING_TASKS,
                    null,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {

                    RecurringTask task = new RecurringTask();

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


                    String finishDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISH_DATE));
                    task.setFinishDate(LocalDateTime.parse(finishDate));

                    String remainingTime = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_REMAINING_TIME));
                    task.setRemainingTime(Duration.parse(remainingTime));

                    String status = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_STATUS));
                    task.setStatus(RecurringTaskStatus.valueOf(status));

                    task.setUserUid(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));

                    String isAwarded = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IS_AWARDED));
                    task.setAwarded(Boolean.parseBoolean(isAwarded));

                    String difficultyType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE));
                    task.setDifficultyQuota(TaskQuote.valueOf(difficultyType));

                    String importanceType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE));
                    task.setImportanceQuota(TaskQuote.valueOf(importanceType));


                    if (task.getDifficultyQuota() == quote) {
                        count++;
                    }
                } while (cursor.moveToNext());
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return count;
    }
    public int countCompletedRecurringTasksWithImportanceXpInCategory(TaskQuote quote, LocalDate startDate, LocalDate endDate, String userUid) {

        if (quote == null || startDate == null || endDate == null || userUid == null) {
            return 0;
        }

        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();


            String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND " +
                    AppDataBaseHelper.COLUMN_STATUS + " = ? AND " +
                    AppDataBaseHelper.COLUMN_IS_AWARDED + " = 1 AND " +
                    AppDataBaseHelper.COLUMN_FINISHED_DATE + " BETWEEN ? AND ?";

            String[] selectionArgs = {
                    userUid,
                    RecurringTaskStatus.COMPLETED.name(),
                    startDate.toString(),
                    endDate.toString()
            };

            cursor = db.query(
                    AppDataBaseHelper.TABLE_RECURRING_TASKS,
                    null,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {

                    RecurringTask task = new RecurringTask();

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


                    String finishDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISH_DATE));
                    task.setFinishDate(LocalDateTime.parse(finishDate));

                    String remainingTime = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_REMAINING_TIME));
                    task.setRemainingTime(Duration.parse(remainingTime));

                    String status = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_STATUS));
                    task.setStatus(RecurringTaskStatus.valueOf(status));

                    task.setUserUid(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));

                    String isAwarded = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IS_AWARDED));
                    task.setAwarded(Boolean.parseBoolean(isAwarded));

                    String difficultyType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE));
                    task.setDifficultyQuota(TaskQuote.valueOf(difficultyType));

                    String importanceType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE));
                    task.setImportanceQuota(TaskQuote.valueOf(importanceType));


                    if (task.getImportanceQuota() == quote) {
                        count++;
                    }
                } while (cursor.moveToNext());
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return count;
    }

    public int countCompletedOneTimeTasksWithDifficultyXpInCategory(TaskQuote quote, LocalDate startDate, LocalDate endDate, String userUid) {

        if (quote == null || startDate == null || endDate == null || userUid == null) {
            return 0;
        }

        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();


            String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND " +
                    AppDataBaseHelper.COLUMN_STATUS + " = ? AND " +
                    AppDataBaseHelper.COLUMN_IS_AWARDED + " = 1 AND " +
                    AppDataBaseHelper.COLUMN_FINISHED_DATE + " BETWEEN ? AND ?";

            String[] selectionArgs = {
                    userUid,
                    RecurringTaskStatus.COMPLETED.name(),
                    startDate.toString(),
                    endDate.toString()
            };

            cursor = db.query(
                    AppDataBaseHelper.TABLE_ONE_TIME_TASKS,
                    null,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {

                    OneTimeTask task = new OneTimeTask();

                    task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRING_TASK_ID)));
                    task.setName(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_TITLE)));
                    task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DESCRIPTION)));
                    task.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_XP)));
                    task.setImportance(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_XP)));
                    task.setCategoryColour(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CTG_ID)));

                   // String recurrenceUnitString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRENCE_UNIT));


                    String executionTimeString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EXECUTION_TIME));
                    task.setExecutionTime(LocalTime.parse(executionTimeString));

                    String startDateString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_START_DATE));
                    task.setStartDate(LocalDate.parse(startDateString));

                   // String endDateString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_END_DATE));

                    String creationDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CREATION_DATE));
                    task.setCreationDate(LocalDate.parse(creationDate));

                    String finishedDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISHED_DATE));
                    task.setFinishedDate(LocalDate.parse(finishedDate));


                    String status = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_STATUS));
                    task.setStatus(OneTimeTaskStatus.valueOf(status));

                    task.setUserUid(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));

                    String isAwarded = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IS_AWARDED));
                    task.setAwarded(Boolean.parseBoolean(isAwarded));

                    String difficultyType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE));
                    task.setDifficultyQuota(TaskQuote.valueOf(difficultyType));

                    String importanceType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE));
                    task.setImportanceQuota(TaskQuote.valueOf(importanceType));


                    if (task.getImportanceQuota() == quote) {
                        count++;
                    }
                } while (cursor.moveToNext());
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return count;
    }
    public int countCompletedOneTimeTasksWithImportanceXpInCategory(TaskQuote quote, LocalDate startDate, LocalDate endDate, String userUid) {

        if (quote == null || startDate == null || endDate == null || userUid == null) {
            return 0;
        }

        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();


            String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND " +
                    AppDataBaseHelper.COLUMN_STATUS + " = ? AND " +
                    AppDataBaseHelper.COLUMN_IS_AWARDED + " = 1 AND " +
                    AppDataBaseHelper.COLUMN_FINISHED_DATE + " BETWEEN ? AND ?";

            String[] selectionArgs = {
                    userUid,
                    RecurringTaskStatus.COMPLETED.name(),
                    startDate.toString(),
                    endDate.toString()
            };

            cursor = db.query(
                    AppDataBaseHelper.TABLE_ONE_TIME_TASKS,
                    null,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {

                    OneTimeTask task = new OneTimeTask();

                    task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRING_TASK_ID)));
                    task.setName(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_TITLE)));
                    task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DESCRIPTION)));
                    task.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_XP)));
                    task.setImportance(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_XP)));
                    task.setCategoryColour(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CTG_ID)));

                    // String recurrenceUnitString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_RECURRENCE_UNIT));


                    String executionTimeString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EXECUTION_TIME));
                    task.setExecutionTime(LocalTime.parse(executionTimeString));

                    String startDateString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_START_DATE));
                    task.setStartDate(LocalDate.parse(startDateString));

                    // String endDateString = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_END_DATE));

                    String creationDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CREATION_DATE));
                    task.setCreationDate(LocalDate.parse(creationDate));

                    String finishedDate = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FINISHED_DATE));
                    task.setFinishedDate(LocalDate.parse(finishedDate));


                    String status = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_STATUS));
                    task.setStatus(OneTimeTaskStatus.valueOf(status));

                    task.setUserUid(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));

                    String isAwarded = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IS_AWARDED));
                    task.setAwarded(Boolean.parseBoolean(isAwarded));

                    String difficultyType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_DIFFICULTY_TYPE));
                    task.setDifficultyQuota(TaskQuote.valueOf(difficultyType));

                    String importanceType = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMPORTANCE_TYPE));
                    task.setImportanceQuota(TaskQuote.valueOf(importanceType));

                    if (task.getImportanceQuota() == quote) {
                        count++;
                    }
                } while (cursor.moveToNext());
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return count;
    }

    public void updateTaskCategoryColour(String oldColourHex, String newColourHex, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(AppDataBaseHelper.COLUMN_CTG_ID, newColourHex);

        String whereClause = AppDataBaseHelper.COLUMN_CTG_ID + " = ?";
        String[] whereArgs = { oldColourHex };

        db.update(AppDataBaseHelper.TABLE_RECURRING_TASKS, values, whereClause, whereArgs);

        db.update(AppDataBaseHelper.TABLE_ONE_TIME_TASKS, values, whereClause, whereArgs);
    }

    public TaskQuote getDifficultyType(int difficulty){

       if (difficulty == 1) {
            return TaskQuote.VERY_EASY;
       }
        if (difficulty == 3) {
            return TaskQuote.EASY;
        }
       if (difficulty == 7) {
            return TaskQuote.HARD;
       }
        if (difficulty == 20) {
            return TaskQuote.EXTREMELY_HARD;
        }
        return TaskQuote.NO_QUOTA;
    }
    public TaskQuote getImportanceType(int importance){

        if (importance == 1) {
            return TaskQuote.NORMAL;
        }
        if (importance == 3) {
            return TaskQuote.IMPORTANT;
        }
        if (importance == 10) {
           return TaskQuote.EXTREMELY_IMPORTANT;
        }
        if (importance == 100) {
            return TaskQuote.SPECIAL;
        }
        return TaskQuote.NO_QUOTA;
    }


    public List<LocalDate> getAllActivityDates(String userUid) {
        List<LocalDate> dates = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT " + AppDataBaseHelper.COLUMN_CREATION_DATE + ", " +
                    AppDataBaseHelper.COLUMN_FINISHED_DATE +
                    " FROM " + AppDataBaseHelper.TABLE_RECURRING_TASKS +
                    " WHERE " + AppDataBaseHelper.COLUMN_USER_ID + " = ?" +
                    " UNION ALL " +
                    "SELECT " + AppDataBaseHelper.COLUMN_CREATION_DATE + ", " +
                    AppDataBaseHelper.COLUMN_FINISHED_DATE +
                    " FROM " + AppDataBaseHelper.TABLE_ONE_TIME_TASKS +
                    " WHERE " + AppDataBaseHelper.COLUMN_USER_ID + " = ?";

            cursor = db.rawQuery(query, new String[]{userUid, userUid});

            if (cursor.moveToFirst()) {
                do {
                    String creationDateStr = cursor.getString(0);
                    String finishedDateStr = cursor.getString(1);

                    if (creationDateStr != null && !creationDateStr.isEmpty()) {
                        dates.add(LocalDate.parse(creationDateStr));
                    }
                    if (finishedDateStr != null && !finishedDateStr.isEmpty()) {
                        dates.add(LocalDate.parse(finishedDateStr));
                    }

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return dates;
    }

    public int countOverdueOneTimeTasks(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {"COUNT(*)"};

        String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND (" +
                AppDataBaseHelper.COLUMN_STATUS + " = ? OR " +
                AppDataBaseHelper.COLUMN_STATUS + " = ?) AND " +
                AppDataBaseHelper.COLUMN_FINISHED_DATE + " < ?";

        String today = LocalDate.now().toString();

        String[] selectionArgs = {
                userId,
                OneTimeTaskStatus.ACTIVE.name(),
                OneTimeTaskStatus.PAUSED.name(),
                today
        };

        Cursor cursor = db.query(AppDataBaseHelper.TABLE_ONE_TIME_TASKS, columns, selection, selectionArgs, null, null, null);

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();

        return count;
    }

    public int countOverdueRecurringTasks(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {"COUNT(*)"};

        String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND (" +
                AppDataBaseHelper.COLUMN_STATUS + " = ? OR " +
                AppDataBaseHelper.COLUMN_STATUS + " = ?) AND " +
                AppDataBaseHelper.COLUMN_FINISHED_DATE + " < ?";

        String today = LocalDate.now().toString();

        String[] selectionArgs = {
                userId,
                RecurringTaskStatus.ACTIVE.name(),
                RecurringTaskStatus.PAUSED.name(),
                today
        };

        Cursor cursor = db.query(AppDataBaseHelper.TABLE_RECURRING_TASKS, columns, selection, selectionArgs, null, null, null);

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();

        return count;
    }

    public boolean isCategoryInUse(String categoryColour) {

        if (categoryColour == null || categoryColour.isEmpty()) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String query = "SELECT " +
                "  (SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_ONE_TIME_TASKS +
                "   WHERE " + AppDataBaseHelper.COLUMN_CTG_ID + " = ?) " +
                " + " +
                "  (SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_RECURRING_TASKS +
                "   WHERE " + AppDataBaseHelper.COLUMN_CTG_ID + " = ?)";

        String[] selectionArgs = { categoryColour, categoryColour };

        Cursor cursor = db.rawQuery(query, selectionArgs);

        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return count > 0;
    }

}
