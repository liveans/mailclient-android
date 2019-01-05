package com.example.ahmet.securemailclient.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ahmet.securemailclient.Constants;
import com.example.ahmet.securemailclient.model.Account;
import com.example.ahmet.securemailclient.model.Key;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context,Constants.DATABASE_NAME,null,Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Account.createTableIfNotExists());
        db.execSQL(Key.createTableIfNotExists());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
