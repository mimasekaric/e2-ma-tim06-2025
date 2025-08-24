package com.example.myhobitapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.RecurringTask;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    private final AppDataBaseHelper dbHelper;
    private SQLiteDatabase database;

    public CategoryRepository(Context context){

        dbHelper = new AppDataBaseHelper(context);

    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertCategory(Category category) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppDataBaseHelper.COLUMN_NAME, category.getName());
        values.put(AppDataBaseHelper.COLUMN_COLOUR, category.getColour());

        long newRowId = database.insert(AppDataBaseHelper.TABLE_CATEGORIES, null, values);
        database.close();
        return newRowId;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + AppDataBaseHelper.TABLE_CATEGORIES;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CATEGORY_ID)));
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_NAME)));
                category.setColour(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COLOUR)));


                categories.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return categories;
    }

    public Category getCategoryById(long id) {
            Category category = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDataBaseHelper.TABLE_CATEGORIES,
                null,
                AppDataBaseHelper.COLUMN_CATEGORY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {

            category = new Category();

            category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_CATEGORY_ID)));
            category.setName(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_NAME)));
            category.setColour(cursor.getString(cursor.getColumnIndexOrThrow(AppDataBaseHelper.COLUMN_COLOUR)));
            cursor.close();
        }
        db.close();
        return category;
    }
}
