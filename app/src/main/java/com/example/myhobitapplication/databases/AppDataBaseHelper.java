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
    public static final String COLUMN_FINISHED_DATE = "finished_date";
    public static final String COLUMN_CREATION_DATE = "creation_date";

    public static final String TABLE_BOSSES = "bosses";

    public static final String COLUMN_BOSS_ID = "boss_id";

    public static final String COLUMN_USER_ID = "user_id";

    public static final String COLUMN_CURRENT_HP = "currentHP";
    public static final String COLUMN_HP = "hp";

    public static final String COLUMN_IS_DEFEATED = "is_defeated";

    public static final String COLUMN_BOSS_LEVEL = "boss_level";
    public static final String COLUMN_COINS_REWARD = "coins_reward";

    public static final String TABLE_ONE_TIME_TASKS = "one_time_tasks";

    public static final String COLUMN_ONE_TIME_TASK_ID = "id";
    public static final String COLUMN_IS_AWARDED = "is_awarded";



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
                + COLUMN_RECURRENCE_UNIT + " TEXT,"
                + COLUMN_EXECUTION_TIME + " TEXT,"
                + COLUMN_STATUS+ " TEXT,"
                + COLUMN_START_DATE + " TEXT,"
                + COLUMN_FIRST_REC_TASK_ID + " TEXT,"
                + COLUMN_CREATION_DATE + " TEXT,"
                + COLUMN_FINISHED_DATE + " TEXT,"
                + COLUMN_USER_ID + " TEXT,"
                + COLUMN_END_DATE + " TEXT,"
                + COLUMN_IS_AWARDED + " TEXT" + ")";
        db.execSQL(CREATE_RECURRING_TASKS_TABLE);

        String CREATE_ONE_TIME_TASK_TABLE = "CREATE TABLE " + TABLE_ONE_TIME_TASKS + "("
                + COLUMN_ONE_TIME_TASK_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DIFFICULTY_XP + " INTEGER,"
                + COLUMN_IMPORTANCE_XP + " INTEGER,"
                + COLUMN_CTG_ID + " INTEGER,"
                + COLUMN_EXECUTION_TIME + " TEXT,"
                + COLUMN_STATUS+ " TEXT,"
                + COLUMN_CREATION_DATE + " TEXT,"
                + COLUMN_START_DATE + " TEXT,"
                + COLUMN_USER_ID + " TEXT,"
                + COLUMN_FINISHED_DATE + " TEXT,"
                + COLUMN_IS_AWARDED + " TEXT" + ")";
        db.execSQL(CREATE_ONE_TIME_TASK_TABLE);

        String CREATE_BOSS_TABLE = "CREATE TABLE " + TABLE_BOSSES + "("
                + COLUMN_BOSS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " TEXT,"
                + COLUMN_IS_DEFEATED + " TEXT,"
                + COLUMN_BOSS_LEVEL + " TEXT,"
                + COLUMN_HP + " TEXT,"
                + COLUMN_COINS_REWARD + " TEXT,"
                + COLUMN_CURRENT_HP + " TEXT" + ")";
        db.execSQL(CREATE_BOSS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECURRING_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ONE_TIME_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOSSES);


        onCreate(db);
    }

}
