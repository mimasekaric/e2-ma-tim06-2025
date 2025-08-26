package com.example.myhobitapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myhobitapplication.models.RecurringTask;

public class AppDataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyHobitApplicationDB.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_RECURRING_TASKS = "recurring_tasks";
    public static final String COLUMN_RECURRING_TASK_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DIFFICULTY_XP = "difficulty_xp";
    public static final String COLUMN_IMPORTANCE_XP = "importance_xp";
    public static final String COLUMN_CTG_ID = "category_id";
    public static final String COLUMN_RECURRENCE_INTERVAL = "recurrence_interval";
    public static final String COLUMN_RECURRENCE_UNIT = "recurrence_unit";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";

    public static final String COLUMN_EXECUTION_TIME = "execution_time";
    public static final String COLUMN_STATUS = "status";


    public static final String TABLE_CATEGORIES = "categories";

    public static final String COLUMN_CATEGORY_ID = "id";
    public static final String COLUMN_NAME = "name";

    public static final String COLUMN_COLOUR = "colour";

    public static final String COLUMN_FIRST_REC_TASK_ID = "first_rec_task_id";
    public AppDataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_COLOUR + " TEXT" + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);

        String CREATE_RECURRING_TASKS_TABLE = "CREATE TABLE " + TABLE_RECURRING_TASKS + "("
                + COLUMN_RECURRING_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DIFFICULTY_XP + " INTEGER,"
                + COLUMN_IMPORTANCE_XP + " INTEGER,"
                + COLUMN_CTG_ID + " INTEGER,"
                + COLUMN_RECURRENCE_INTERVAL + " INTEGER,"
                + COLUMN_RECURRENCE_UNIT + " TEXT," // Enum se čuva kao String
                + COLUMN_EXECUTION_TIME + " TEXT," // <-- Ponovo je dodata sa razmakom
                + COLUMN_STATUS+ " TEXT," // <-- Ponovo je dodata sa razmakom
                + COLUMN_START_DATE + " TEXT," // LocalDate se čuva kao String
                + COLUMN_FIRST_REC_TASK_ID + " TEXT,"
                + COLUMN_END_DATE + " TEXT" + ")";
        db.execSQL(CREATE_RECURRING_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECURRING_TASKS);

        onCreate(db);


    }

}
