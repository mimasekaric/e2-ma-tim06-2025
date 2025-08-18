package com.example.myhobitapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SQliteConnection extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Hobit.db";
    private static final int DATABASE_VERSION = 1;

    //users table example
    private static final String TABLE_NAME = "users";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SURNAME = "surname";

    public SQliteConnection(@Nullable Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        String query = "CREATE TABLE " + TABLE_NAME + " (" +COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_NAME + " TEXT, " + COLUMN_SURNAME + " TEXT);";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    void addUser(String name, String surname){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put( COLUMN_NAME, name);
        cv.put( COLUMN_SURNAME, surname);
        long result = db.insert(TABLE_NAME,null, cv );
        if( result == -1){
            Toast.makeText(context, "Failed to insert User", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(context, "User inserted successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
