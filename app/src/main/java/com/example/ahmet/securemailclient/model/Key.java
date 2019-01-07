package com.example.ahmet.securemailclient.model;

import android.content.ContentValues;

import java.util.ArrayList;

public class Key {
    public static final String TABLE_NAME="KEYS";
    public static final ArrayList<Column> COLUMNS = new ArrayList<>();
    public static final Column ID = new Column("id", Column.Type.INTEGER);
    public static final Column EMAIL = new Column("email", Column.Type.TEXT);
    public static final Column PUBLIC_KEY = new Column("publicKey", Column.Type.TEXT);

    public int id;
    public String email,publicKey;

    static {
        ID.setAutoIncrement(true);
        ID.setPrimary(true);
        ID.setCanNullable(true);
        COLUMNS.add(ID);
        COLUMNS.add(EMAIL);
        COLUMNS.add(PUBLIC_KEY);
    }

    public Key(String email,String publicKey) {
        this.id=-1;
        this.email=email;
        this.publicKey=publicKey;
    }

    public Key(int id,String email,String publicKey) {
        this(email,publicKey);
        this.id=id;
    }

    public ContentValues getContentValues() {
        ContentValues values=new ContentValues();
        values.put(EMAIL.getName(),email);
        values.put(PUBLIC_KEY.getName(),publicKey);
        return values;
    }

    public static String createTableIfNotExists() {
        StringBuilder builder=new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS").append(" ");
        builder.append(Key.TABLE_NAME).append(" (");
        int i = 0;
        while(i<COLUMNS.size()) {
            builder.append(COLUMNS.get(i).toString());
            i++;
            if (i!=COLUMNS.size()) {
                builder.append(",");
            } else {
                builder.append(")");
            }
        }
        return builder.toString();
    }
}
