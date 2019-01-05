package com.example.ahmet.securemailclient.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;

public class DatabaseManager {

    SQLiteOpenHelper helper;
    SQLiteDatabase database;

    public DatabaseManager(Context context, SQLiteOpenHelper helper) {
        this.helper=helper;
        database=helper.getWritableDatabase();
    }

    public void insert(ContentValues values,String tableName) {
        database.insert(tableName,null,values);
    }

    public void update(ContentValues values,String tableName) {
        //database.update("","","","");
    }
}
