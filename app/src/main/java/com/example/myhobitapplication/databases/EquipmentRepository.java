package com.example.myhobitapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.myhobitapplication.enums.EquipmentTypes;
import com.example.myhobitapplication.models.Clothing;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.Potion;
import com.example.myhobitapplication.models.Weapon;

public class EquipmentRepository {

    private final AppDataBaseHelper dbHelper;
    private SQLiteDatabase database;

    public EquipmentRepository(Context context){

        dbHelper = new AppDataBaseHelper(context);

    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


        public int updateEquipment(Equipment eq) {
            ContentValues values = new ContentValues();
            values.put(AppDataBaseHelper.COLUMN_ACTIVATED, eq.getActivated() ? 1 : 0);
            values.put(AppDataBaseHelper.COLUMN_EQUIPMENT_TYPE, eq.getequipmentType().name());
            values.put(AppDataBaseHelper.COLUMN_POWER_PERCENTAGE, eq.getpowerPercentage());
            values.put(AppDataBaseHelper.COLUMN_IMAGE, eq.getImage());


            if (eq instanceof Potion) {
                Potion p = (Potion) eq;
                values.put(AppDataBaseHelper.COLUMN_COEF, p.getCoef());
                values.put(AppDataBaseHelper.COLUMN_IS_PERMANENT, p.isPermanent() ? 1 : 0);
                values.putNull(AppDataBaseHelper.COLUMN_FIGHTS_COUNTER);
                values.put(AppDataBaseHelper.COLUMN_SPECIFIC_TYPE, p.getType().name());
            } else if (eq instanceof Weapon) {
                Weapon w = (Weapon) eq;
                values.putNull(AppDataBaseHelper.COLUMN_DESCRIPTION);
                values.putNull(AppDataBaseHelper.COLUMN_COEF);
                values.putNull(AppDataBaseHelper.COLUMN_IS_PERMANENT);
                values.putNull(AppDataBaseHelper.COLUMN_FIGHTS_COUNTER);
                values.put(AppDataBaseHelper.COLUMN_SPECIFIC_TYPE, w.getType().name());
            } else if (eq instanceof Clothing) {
                Clothing c = (Clothing) eq;
                values.putNull(AppDataBaseHelper.COLUMN_DESCRIPTION);
                values.put(AppDataBaseHelper.COLUMN_COEF, c.getCoef());
                values.putNull(AppDataBaseHelper.COLUMN_IS_PERMANENT);
                values.put(AppDataBaseHelper.COLUMN_FIGHTS_COUNTER, c.getFightsCounter());
                values.put(AppDataBaseHelper.COLUMN_SPECIFIC_TYPE, c.getType().name());
            }

            return database.update(
                    AppDataBaseHelper.TABLE_EQUIPMENT,
                    values,
                    AppDataBaseHelper.COLUMN_EQUIPMENT_ID + "=?",
                    new String[]{String.valueOf(eq.getId())});
        }


        public Equipment getEquipmentById(long id) {
            Cursor cursor = database.query(
                    AppDataBaseHelper.TABLE_EQUIPMENT,
                    null,
                    AppDataBaseHelper.COLUMN_EQUIPMENT_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                String typeStr = cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EQUIPMENT_TYPE));
                EquipmentTypes type = EquipmentTypes.valueOf(typeStr);

                Equipment eq;
                switch (type) {
                    case POTION:
                        Potion p = new Potion();
                        p.setCoef(cursor.getDouble(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COEF)));
                        p.setPermanent(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IS_PERMANENT)) == 1);
                        eq = p;
                        break;
                    case WEAPON:
                        eq = new Weapon();
                        break;
                    case CLOTHING:
                        Clothing c = new Clothing();
                        c.setCoef(cursor.getDouble(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COEF)));
                        c.setFightsCounter(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_FIGHTS_COUNTER)));
                        eq = c;
                        break;
                    default:
                        eq = new Equipment();
                        break;
                }
                eq.setId(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_EQUIPMENT_ID)));
                eq.setActivated(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_ACTIVATED)) == 1);
                eq.setequipmentType(type);
                eq.setpowerPercentage(cursor.getDouble(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_POWER_PERCENTAGE)));
                eq.setImage(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_IMAGE)));

                cursor.close();
                return eq;
            }

            if (cursor != null) cursor.close();
            return null;
        }


}
