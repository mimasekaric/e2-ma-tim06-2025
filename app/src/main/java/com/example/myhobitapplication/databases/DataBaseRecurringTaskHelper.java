package com.example.myhobitapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.models.RecurringTask;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DataBaseRecurringTaskHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyHobitApplication.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_RECURRING_TASKS = "recurring_tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DIFFICULTY_XP = "difficulty_xp";
    private static final String COLUMN_RECURRENCE_INTERVAL = "recurrence_interval";
    private static final String COLUMN_RECURRENCE_UNIT = "recurrence_unit";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_END_DATE = "end_date";

    private static final String COLUMN_EXECUTION_TIME = "execution_time";

    public DataBaseRecurringTaskHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_RECURRING_TASKS_TABLE = "CREATE TABLE " + TABLE_RECURRING_TASKS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DIFFICULTY_XP + " INTEGER,"
                + COLUMN_RECURRENCE_INTERVAL + " INTEGER,"
                + COLUMN_RECURRENCE_UNIT + " TEXT," // Enum se čuva kao String
                + COLUMN_EXECUTION_TIME + " TEXT," // <-- Ponovo je dodata sa razmakom
                + COLUMN_START_DATE + " TEXT," // LocalDate se čuva kao String
                + COLUMN_END_DATE + " TEXT" + ")";
        db.execSQL(CREATE_RECURRING_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECURRING_TASKS);
        onCreate(db);
    }

    public List<RecurringTask> getAllRecurringTasks() {
        List<RecurringTask> taskList = new ArrayList<>();
        // SQL upit za dobijanje svih redova iz tabele
        String selectQuery = "SELECT * FROM " + TABLE_RECURRING_TASKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Iteriranje kroz sve redove i dodavanje u listu
        if (cursor.moveToFirst()) {
            do {
                RecurringTask task = new RecurringTask();
                task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                task.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                task.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY_XP)));
                task.setRecurrenceInterval(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECURRENCE_INTERVAL)));

                // Konverzija String u RecurrenceUnit enum
                String recurrenceUnitString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECURRENCE_UNIT));
                task.setRecurrenceUnit(RecurrenceUnit.valueOf(recurrenceUnitString));

                // Konverzija String u LocalTime
                String executionTimeString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXECUTION_TIME));
                task.setExecutionTime(LocalTime.parse(executionTimeString));

                // Konverzija String u LocalDate
                String startDateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
                task.setStartDate(LocalDate.parse(startDateString));

                String endDateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE));
                task.setEndDate(LocalDate.parse(endDateString));

                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return taskList;
    }

    // Metoda za dodavanje zadatka u bazu
    public long insertRecurringTask(RecurringTask task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getName());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DIFFICULTY_XP, task.getDifficulty());
        values.put(COLUMN_RECURRENCE_INTERVAL, task.getRecurrenceInterval());
        values.put(COLUMN_RECURRENCE_UNIT, task.getRecurrenceUnit().name());
        values.put(COLUMN_EXECUTION_TIME, task.getExecutionTime().toString()); // <-- Ponovo je dodata
        values.put(COLUMN_START_DATE, task.getStartDate().toString());
        values.put(COLUMN_END_DATE, task.getEndDate().toString());

        long newRowId = db.insert(TABLE_RECURRING_TASKS, null, values);
        db.close();
        return newRowId;
    }




}
