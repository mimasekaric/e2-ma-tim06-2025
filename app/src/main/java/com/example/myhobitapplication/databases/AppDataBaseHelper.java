package com.example.myhobitapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myhobitapplication.enums.EquipmentTypes;
import com.example.myhobitapplication.models.Clothing;
import com.example.myhobitapplication.models.Potion;
import com.example.myhobitapplication.models.RecurringTask;
import com.example.myhobitapplication.models.Weapon;
import com.example.myhobitapplication.staticData.ClothingList;
import com.example.myhobitapplication.staticData.PotionList;
import com.example.myhobitapplication.staticData.WeaponList;

import java.util.List;

public class AppDataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyHobitApplicationDB.db";
    public static final int DATABASE_VERSION = 4;
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
    public static final String COLUMN_COINS_REWARD_PERCENT = "coins_reward_percent";

    public static final String TABLE_EQUIPMENT = "equipment";
    public static final String TABLE_ONE_TIME_TASKS = "one_time_tasks";

    public static final String COLUMN_EQUIPMENT_ID = "id";
    public static final String COLUMN_ACTIVATED = "activated";
    public static final String COLUMN_EQUIPMENT_TYPE = "equipment_type";
    public static final String COLUMN_SPECIFIC_TYPE = "specific_type";
    public static final String COLUMN_ONE_TIME_TASK_ID = "id";
    public static final String COLUMN_IS_AWARDED = "is_awarded";

    public static final String COLUMN_POWER_PERCENTAGE = "power_percentage";

    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_COEF = "coef";
    public static final String COLUMN_IS_PERMANENT = "is_permanent";
    public static final String COLUMN_FIGHTS_COUNTER = "fights_counter";
    public static final String TABLE_USER_EQUIPMENT = "user_equipment";
    public static final String COLUMN_USER_EQUIPMENT_ID = "id";
    public static final String COLUMN_EQUIPMENT_EID = "equipment_id";
    public static final String COLUMN_EQUIPMENT_UID = "user_id";




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
                + COLUMN_COINS_REWARD + " INTEGER,"
                + COLUMN_COINS_REWARD_PERCENT + " REAL,"
                + COLUMN_CURRENT_HP + " TEXT" + ")";
        db.execSQL(CREATE_BOSS_TABLE);

        String CREATE_EQUIPMENT_TABLE = "CREATE TABLE " + TABLE_EQUIPMENT + "("
                + COLUMN_EQUIPMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EQUIPMENT_TYPE + " TEXT,"
                + COLUMN_SPECIFIC_TYPE + " TEXT,"
                + COLUMN_POWER_PERCENTAGE + " REAL,"
                + COLUMN_IMAGE + " INTEGER,"
                + COLUMN_COEF + " REAL,"
                + COLUMN_IS_PERMANENT + " INTEGER"
                 + ")";
        db.execSQL(CREATE_EQUIPMENT_TABLE);

        String CREATE_USER_EQUIPMENT_TABLE = "CREATE TABLE " + TABLE_USER_EQUIPMENT + "("
                + COLUMN_USER_EQUIPMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EQUIPMENT_EID + " TEXT,"
                + COLUMN_EQUIPMENT_UID + " TEXT,"
                + COLUMN_COEF + " REAL,"
                + COLUMN_ACTIVATED + " INTEGER,"
                + COLUMN_FIGHTS_COUNTER + " INTEGER" + ")";
        db.execSQL(CREATE_USER_EQUIPMENT_TABLE);


        insertInitialData(db);
    }

    private void insertInitialData(SQLiteDatabase db) {

        List<Potion> initialPotions = PotionList.getPotionList();
        for (Potion potion : initialPotions) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EQUIPMENT_TYPE, EquipmentTypes.POTION.name());
            values.put(COLUMN_SPECIFIC_TYPE, potion.getType().name());
            values.put(COLUMN_POWER_PERCENTAGE, potion.getpowerPercentage());
            values.put(COLUMN_IMAGE, potion.getImage());
            values.put(COLUMN_COEF, potion.getCoef());
            values.put(COLUMN_IS_PERMANENT, potion.isPermanent() ? 1 : 0);
            db.insert(TABLE_EQUIPMENT, null, values);
        }

        List<Weapon> initialWeapons = WeaponList.getWeaponList();
        for (Weapon weapon : initialWeapons) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EQUIPMENT_TYPE, EquipmentTypes.WEAPON.name());
            values.put(COLUMN_SPECIFIC_TYPE, weapon.getType().name());
            values.put(COLUMN_POWER_PERCENTAGE, weapon.getpowerPercentage());
            values.put(COLUMN_IMAGE, weapon.getImage());

            values.putNull(COLUMN_COEF);
            values.putNull(COLUMN_IS_PERMANENT);

            db.insert(TABLE_EQUIPMENT, null, values);
        }

        List<Clothing> initialClothing = ClothingList.getClothingList();
        for (Clothing clothing : initialClothing) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EQUIPMENT_TYPE, EquipmentTypes.CLOTHING.name());
            values.put(COLUMN_SPECIFIC_TYPE, clothing.getType().name());
            values.put(COLUMN_POWER_PERCENTAGE, clothing.getpowerPercentage());
            values.put(COLUMN_IMAGE, clothing.getImage());
            values.put(COLUMN_COEF, clothing.getCoef());

            values.putNull(COLUMN_IS_PERMANENT);

            db.insert(TABLE_EQUIPMENT, null, values);
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECURRING_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ONE_TIME_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOSSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EQUIPMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_EQUIPMENT);


        onCreate(db);
    }

}
