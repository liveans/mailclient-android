package com.example.ahmet.securemailclient.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ahmet.securemailclient.database.helper.DatabaseHelper;
import com.example.ahmet.securemailclient.model.Key;

public class DatabaseManager {

    SQLiteOpenHelper helper;
    SQLiteDatabase database;

    public DatabaseManager(Context context) {
        this.helper=new DatabaseHelper(context);
        database=helper.getWritableDatabase();
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public boolean checkIfExistsKey(String email) {
        Cursor cursor=getDatabase().rawQuery("select * from "+Key.TABLE_NAME+" where "+Key.EMAIL.getName()+"=?",new String[] {email});
        if (cursor!=null && cursor.getCount()>0) {
            return true;
        }

        return false;
    }

    public String getPublicKeyFromEmail(String email) {
        Cursor cursor=getDatabase().rawQuery("select * from "+Key.TABLE_NAME+" where "+Key.EMAIL.getName()+"=?",new String[] {email});
        if (cursor!=null && cursor.getCount()>0) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(Key.PUBLIC_KEY.getName()));
        }

        return null;
    }
}
