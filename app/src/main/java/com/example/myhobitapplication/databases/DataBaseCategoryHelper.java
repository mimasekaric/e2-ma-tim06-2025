package com.example.myhobitapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myhobitapplication.enums.RecurrenceUnit;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.RecurringTask;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DataBaseCategoryHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyHobitApplication.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CATEGORIES = "categories";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    private static final String COLUMN_COLOUR = "colour";

    public DataBaseCategoryHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_COLOUR + " TEXT" + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    public long insertCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, category.getName());
        values.put(COLUMN_COLOUR, category.getColour());

        long newRowId = db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return newRowId;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                category.setColour(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLOUR)));


                categories.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return categories;
    }










}
