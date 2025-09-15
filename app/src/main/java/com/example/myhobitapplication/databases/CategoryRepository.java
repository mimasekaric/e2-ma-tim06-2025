package com.example.myhobitapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.OneTimeTask;
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

    public long updateCategory(Category category) {

        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(AppDataBaseHelper.COLUMN_NAME, category.getName());
        values.put(AppDataBaseHelper.COLUMN_COLOUR, category.getColour());

        String selection = AppDataBaseHelper.COLUMN_CATEGORY_ID + " = ?";
        String[] selectionArgs = { String.valueOf(category.getId()) };

        int count = database.update(
                AppDataBaseHelper.TABLE_CATEGORIES,
                values,
                selection,
                selectionArgs);

        database.close();
        return count;
    }

    public boolean doesCategoryNameExist(String name) {
        database = dbHelper.getWritableDatabase();

        String query = "SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_CATEGORIES + " WHERE " + AppDataBaseHelper.COLUMN_NAME + " = ? COLLATE NOCASE";
        Cursor cursor = database.rawQuery(query, new String[]{name.trim()});

        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        database.close();

        return count > 0;
    }
    public boolean doesUpdateCategoryNameExist(String name, @Nullable Integer categoryIdToIgnore) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int count = 0;

        try {
            db = dbHelper.getReadableDatabase();

            String query;
            String[] selectionArgs;

            if (categoryIdToIgnore != null && categoryIdToIgnore > 0) {
                query = "SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_CATEGORIES +
                        " WHERE " + AppDataBaseHelper.COLUMN_NAME + " = ? COLLATE NOCASE" +
                        " AND " + AppDataBaseHelper.COLUMN_CATEGORY_ID + " != ?";

                selectionArgs = new String[]{name.trim(), String.valueOf(categoryIdToIgnore)};

            } else {

                query = "SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_CATEGORIES +
                        " WHERE " + AppDataBaseHelper.COLUMN_NAME + " = ? COLLATE NOCASE";

                selectionArgs = new String[]{name.trim()};
            }

            cursor = db.rawQuery(query, selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return count > 0;
    }
    public boolean doesUpdateCategoryColourExist(String colour, @Nullable Integer categoryIdToIgnore) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int count = 0;

        try {
            db = dbHelper.getReadableDatabase();

            String query;
            String[] selectionArgs;

            if (categoryIdToIgnore != null && categoryIdToIgnore > 0) {
                query = "SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_CATEGORIES +
                        " WHERE " + AppDataBaseHelper.COLUMN_COLOUR + " = ? COLLATE NOCASE" +
                        " AND " + AppDataBaseHelper.COLUMN_CATEGORY_ID + " != ?";

                selectionArgs = new String[]{colour.trim(), String.valueOf(categoryIdToIgnore)};

            } else {

                query = "SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_CATEGORIES +
                        " WHERE " + AppDataBaseHelper.COLUMN_COLOUR + " = ? COLLATE NOCASE";

                selectionArgs = new String[]{colour.trim()};
            }

            cursor = db.rawQuery(query, selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return count > 0;
    }

    public boolean doesCategoryColourExists(String colour) {
        database = dbHelper.getWritableDatabase();

        String query = "SELECT COUNT(*) FROM " + AppDataBaseHelper.TABLE_CATEGORIES + " WHERE " + AppDataBaseHelper.COLUMN_COLOUR + " = ? COLLATE NOCASE";
        Cursor cursor = database.rawQuery(query, new String[]{colour.trim()});

        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        database.close();

        return count > 0;
    }

    public void updateCategoryAndTasksTransactional(Category categoryToUpdate, String oldColour, TaskRepository taskRepository) {

        database = dbHelper.getWritableDatabase();


        database.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(AppDataBaseHelper.COLUMN_NAME, categoryToUpdate.getName());
            values.put(AppDataBaseHelper.COLUMN_COLOUR, categoryToUpdate.getColour());

            String selection = AppDataBaseHelper.COLUMN_CATEGORY_ID + " = ?";
            String[] selectionArgs = { String.valueOf(categoryToUpdate.getId()) };

            database.update(AppDataBaseHelper.TABLE_CATEGORIES, values, selection, selectionArgs);


            taskRepository.updateTaskCategoryColour(oldColour, categoryToUpdate.getColour(), database);


            database.setTransactionSuccessful();

        } finally {

            database.endTransaction();
            database.close();
        }
    }
}
