package com.example.myhobitapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.myhobitapplication.models.Boss;

import java.util.ArrayList;
import java.util.List;

public class BossRepository {

    private final AppDataBaseHelper dbHelper;
    private SQLiteDatabase database;

    public BossRepository(Context context){

        dbHelper = new AppDataBaseHelper(context);

    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }



    public long insertBoss(Boss boss){

        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppDataBaseHelper.COLUMN_HP, boss.getHP());
        values.put(AppDataBaseHelper.COLUMN_CURRENT_HP, boss.getCurrentHP());
        values.put(AppDataBaseHelper.COLUMN_IS_DEFEATED, boss.getDefeated());
        values.put(AppDataBaseHelper.COLUMN_USER_ID, boss.getUserId());
        values.put(AppDataBaseHelper.COLUMN_BOSS_LEVEL, boss.getBossLevel());
        values.put(AppDataBaseHelper.COLUMN_COINS_REWARD, boss.getCoinsReward());

        long newRowId = database.insert(AppDataBaseHelper.TABLE_BOSSES, null, values);
        database.close();
        return newRowId;

    }

    public List<Boss> getAllBossesForUser(int userId) {
        List<Boss> bosses = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ?";

        String[] selectionArgs = { String.valueOf(userId) };


        Cursor cursor = db.query(
                AppDataBaseHelper.TABLE_BOSSES,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Boss boss = new Boss();
                boss.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_BOSS_ID)));
                boss.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));
                boss.setHP(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_HP)));
                boss.setBossLevel(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_BOSS_LEVEL)));
                boss.setCurrentHP(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CURRENT_HP)));
                boss.setCoinsReward(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COINS_REWARD)));

                String isDefeated = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IS_DEFEATED));
                boss.setDefeated(Boolean.parseBoolean(isDefeated));


                bosses.add(boss);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return bosses;
    }


    public List<Boss> getAllUndefeatedBossesForUser(int userId) {
        List<Boss> bosses = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND " +
                AppDataBaseHelper.COLUMN_IS_DEFEATED + " = ?";

        String[] selectionArgs = {
                String.valueOf(userId),
                "0"
        };

        String orderBy = AppDataBaseHelper.COLUMN_BOSS_LEVEL + " ASC";


        Cursor cursor = db.query(
                AppDataBaseHelper.TABLE_BOSSES,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
        );

        if (cursor.moveToFirst()) {
            do {
                Boss boss = new Boss();
                boss.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_BOSS_ID)));
                boss.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));
                boss.setHP(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_HP)));
                boss.setBossLevel(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_BOSS_LEVEL)));
                boss.setCurrentHP(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CURRENT_HP)));
                boss.setCoinsReward(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COINS_REWARD)));


                String isDefeated = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IS_DEFEATED));
                boss.setDefeated(Boolean.parseBoolean(isDefeated));


                bosses.add(boss);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return bosses;
    }


    public Boss getPreviousBossForUser(int userId, int previousLevel) {
        Boss boss = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = AppDataBaseHelper.COLUMN_USER_ID + " = ? AND " +
                AppDataBaseHelper.COLUMN_BOSS_LEVEL + " = ?";

        String[] selectionArgs = {
                String.valueOf(userId),
                String.valueOf(previousLevel)
        };

        Cursor cursor = db.query(
                AppDataBaseHelper.TABLE_BOSSES,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );


        if (cursor != null && cursor.moveToFirst()) {



            boss = new Boss();


            boss.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_BOSS_ID)));
            boss.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_ID)));
            boss.setHP(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_HP)));
            boss.setBossLevel(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_BOSS_LEVEL)));
            boss.setCurrentHP(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CURRENT_HP)));
            boss.setCoinsReward(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COINS_REWARD)));

            int isDefeatedInt = cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IS_DEFEATED));
            boss.setDefeated(isDefeatedInt == 1);
        }


        if (cursor != null) {
            cursor.close();
        }
        db.close();


        return boss;
    }

    public long updateBoss(Boss boss) {

        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(AppDataBaseHelper.COLUMN_HP, boss.getHP());
        values.put(AppDataBaseHelper.COLUMN_CURRENT_HP, boss.getCurrentHP());
        values.put(AppDataBaseHelper.COLUMN_IS_DEFEATED, boss.getDefeated());
        values.put(AppDataBaseHelper.COLUMN_USER_ID, boss.getUserId());
        values.put(AppDataBaseHelper.COLUMN_BOSS_LEVEL, boss.getBossLevel());
        values.put(AppDataBaseHelper.COLUMN_COINS_REWARD, boss.getCoinsReward());

        String selection = AppDataBaseHelper.COLUMN_BOSS_ID + " = ?";
        String[] selectionArgs = { String.valueOf(boss.getId()) };

        int count = database.update(
                AppDataBaseHelper.TABLE_BOSSES,
                values,
                selection,
                selectionArgs);

        database.close();
        return count;

    }

}
