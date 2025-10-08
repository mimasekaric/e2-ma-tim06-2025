package com.example.myhobitapplication.databases;
import com.example.myhobitapplication.models.UserEquipment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UserEquipmentRepository {
    private final AppDataBaseHelper dbHelper;


    public UserEquipmentRepository(Context context){

        dbHelper = new AppDataBaseHelper(context);
    }

    public void open() throws SQLException {
    }

    public void close() {
        dbHelper.close();
    }
    public long insertUserEquipment(UserEquipment ue) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppDataBaseHelper.COLUMN_EQUIPMENT_EID, ue.getEquipmentId());
        values.put(AppDataBaseHelper.COLUMN_EQUIPMENT_UID, ue.getUserId());
        values.put(AppDataBaseHelper.COLUMN_ACTIVATED, ue.getActivated() ? 1 : 0);
        values.put(AppDataBaseHelper.COLUMN_FIGHTS_COUNTER, ue.getFightsCounter());
        values.put(AppDataBaseHelper.COLUMN_COEF, ue.getCoef());
        values.put(AppDataBaseHelper.COLUMN_EFFECT, ue.getEffect());


        long res= database.insert(AppDataBaseHelper.TABLE_USER_EQUIPMENT, null, values);
        return res;
    }

    public int updateUserEquipment(UserEquipment ue) {
        Log.d("DB_DEBUG", "Trying to update UE: id=" + ue.getId()
                + " activated=" + ue.getActivated()
                + " counter=" + ue.getFightsCounter()
                + " coef=" + ue.getCoef()
                + " effect=" + ue.getEffect());

        if (ue == null || ue.getId() == null) return 0;

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AppDataBaseHelper.COLUMN_ACTIVATED, ue.getActivated() ? 1 : 0);
        values.put(AppDataBaseHelper.COLUMN_FIGHTS_COUNTER, ue.getFightsCounter());
        values.put(AppDataBaseHelper.COLUMN_COEF, ue.getCoef());
        values.put(AppDataBaseHelper.COLUMN_EFFECT, ue.getEffect());
        Log.d("DB_DEBUG", "Updating UE with id=" + ue.getId());

        UserEquipment testUE = getById(ue.getId());
        if (testUE == null) {
            Log.e("DB_DEBUG", "⚠️ No record found with that ID in DB!");
        } else {
            Log.d("DB_DEBUG", "✅ Record exists before update: effect=" + testUE.getEffect());
        }

        int rows = database.update(
                AppDataBaseHelper.TABLE_USER_EQUIPMENT,
                values,
                AppDataBaseHelper.COLUMN_USER_EQUIPMENT_ID + "=?",
                new String[]{String.valueOf(ue.getId())}
        );

        Log.d("DB_UPDATE", "Rows updated: " + rows);
        return rows;
    }



    public List<UserEquipment> getAllByUserId(String userId) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        List<UserEquipment> list = new ArrayList<>();
        Cursor cursor = database.query(
                AppDataBaseHelper.TABLE_USER_EQUIPMENT,
                null,
                AppDataBaseHelper.COLUMN_EQUIPMENT_UID + "=?",
                new String[]{userId},
                null,
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                UserEquipment ue = new UserEquipment();
                ue.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_EQUIPMENT_ID)));
                ue.setEquipmentId(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EQUIPMENT_EID)));
                ue.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EQUIPMENT_UID)));
                ue.setActivated(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_ACTIVATED)) == 1);
                ue.setFightsCounter(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FIGHTS_COUNTER)));
                ue.setCoef(cursor.getDouble(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COEF)));
                ue.setEffect(cursor.getDouble(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EFFECT)));
                list.add(ue);
            }
            cursor.close();
        }
        return list;
    }

    public UserEquipment getByEquipmentId(String id) { /// samo za Weapon
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        UserEquipment ue = null;
        Cursor cursor = database.query(
                AppDataBaseHelper.TABLE_USER_EQUIPMENT,
                null,
                AppDataBaseHelper.COLUMN_EQUIPMENT_EID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ue = new UserEquipment();
                ue.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_EQUIPMENT_ID)));
                ue.setEquipmentId(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EQUIPMENT_EID)));
                ue.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EQUIPMENT_UID)));
                ue.setActivated(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_ACTIVATED)) == 1);
                ue.setFightsCounter(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FIGHTS_COUNTER)));
                ue.setCoef(cursor.getDouble(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COEF)));
                ue.setEffect(cursor.getDouble(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EFFECT)));
            }
            cursor.close();
           // database.close();
        }
        return ue;
    }

    public UserEquipment getById(long id) {
        UserEquipment ue = null;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                AppDataBaseHelper.TABLE_USER_EQUIPMENT,
                null,
                AppDataBaseHelper.COLUMN_USER_EQUIPMENT_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ue = new UserEquipment();
                ue.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_USER_EQUIPMENT_ID)));
                ue.setEquipmentId(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EQUIPMENT_EID)));
                ue.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EQUIPMENT_UID)));
                ue.setActivated(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_ACTIVATED)) == 1);
                ue.setFightsCounter(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FIGHTS_COUNTER)));
                ue.setCoef(cursor.getDouble(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COEF)));
                ue.setEffect(cursor.getDouble(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EFFECT)));
            }
            cursor.close();
        }
        return ue;
    }

    public void delete(UserEquipment userEquipment){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(
                AppDataBaseHelper.TABLE_USER_EQUIPMENT,
                AppDataBaseHelper.COLUMN_USER_EQUIPMENT_ID + "=?",
                new String[]{String.valueOf(userEquipment.getId())}
        );
    }

}
