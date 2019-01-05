package com.example.ahmet.securemailclient.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ahmet.securemailclient.database.helper.DatabaseHelper;

public class DatabaseManager {

    SQLiteOpenHelper helper;
    SQLiteDatabase database;
    static DatabaseManager mInstance;

    public static DatabaseManager getInstance() {
        return mInstance;
    }

    public DatabaseManager(Context context) {
        this.helper=new DatabaseHelper(context);
        database=helper.getWritableDatabase();
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
}
