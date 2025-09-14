package com.example.myhobitapplication.databases;
import com.example.myhobitapplication.models.UserEquipment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserEquipmentRepository {
    private final AppDataBaseHelper dbHelper;
    private SQLiteDatabase database;

    public UserEquipmentRepository(Context context){

        dbHelper = new AppDataBaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }
    public long insertUserEquipment(UserEquipment ue) {
        ContentValues values = new ContentValues();
        values.put(AppDataBaseHelper.COLUMN_EQUIPMENT_EID, ue.getEquipmentId());
        values.put(AppDataBaseHelper.COLUMN_EQUIPMENT_UID, ue.getUserId());
        values.put(AppDataBaseHelper.COLUMN_ACTIVATED, ue.getActivated() ? 1 : 0);
        values.put(AppDataBaseHelper.COLUMN_FIGHTS_COUNTER, ue.getFightsCounter());
        values.put(AppDataBaseHelper.COLUMN_COEF, ue.getCoef());
        return database.insert(AppDataBaseHelper.TABLE_USER_EQUIPMENT, null, values);
    }

    public int updateUserEquipment(UserEquipment ue) {
        ContentValues values = new ContentValues();
        values.put(AppDataBaseHelper.COLUMN_ACTIVATED, ue.getActivated() ? 1 : 0);
        values.put(AppDataBaseHelper.COLUMN_FIGHTS_COUNTER, ue.getFightsCounter());
        values.put(AppDataBaseHelper.COLUMN_COEF, ue.getCoef());


        String whereClause = AppDataBaseHelper.COLUMN_USER_EQUIPMENT_ID + "=?";
        String[] whereArgs = { String.valueOf(ue.getId()) };

        return database.update(
                AppDataBaseHelper.TABLE_USER_EQUIPMENT,
                values,
                whereClause,
                whereArgs
        );
    }

    public List<UserEquipment> getAllByUserId(String userId) {
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
                ue.setFightsCounter(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COEF)));
                list.add(ue);
            }
            cursor.close();
        }
        return list;
    }


    public UserEquipment getById(long id) {
        UserEquipment ue = null;
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
                ue.setFightsCounter(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COEF)));
            }
            cursor.close();
        }
        return ue;
    }

    public void delete(UserEquipment userEquipment){
        database.delete(
                AppDataBaseHelper.TABLE_USER_EQUIPMENT,
                AppDataBaseHelper.COLUMN_USER_EQUIPMENT_ID + "=?",
                new String[]{String.valueOf(userEquipment.getId())}
        );
    }
    public UserEquipment getByEquipmentId(String id) {
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
                ue.setFightsCounter(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COEF)));
            }
            cursor.close();
        }
        return ue;
    }

}
