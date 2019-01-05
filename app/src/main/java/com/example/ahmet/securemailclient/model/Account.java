package com.example.ahmet.securemailclient.model;

import android.content.ContentValues;

import java.util.ArrayList;

public class Account {
    public static final String TABLE_NAME="ACCOUNTS";
    public static final ArrayList<Column> COLUMNS = new ArrayList<>();
    public static final Column ID = new Column("id", Column.Type.INTEGER);
    public static final Column EMAIL = new Column("email", Column.Type.TEXT);
    public static final Column PUBLIC_KEY = new Column("publicKey", Column.Type.TEXT);
    public static final Column SECRET_KEY = new Column("secretKey", Column.Type.TEXT);
    public static final Column PASSWORD_KEY = new Column("passwordKey", Column.Type.TEXT);

    private int id;
    private String email,publicKey,secretKey,passwordKey;

    static {
        ID.setAutoIncrement(true);
        ID.setPrimary(true);
        ID.setCanNullable(true);
        COLUMNS.add(ID);
        COLUMNS.add(EMAIL);
        COLUMNS.add(PUBLIC_KEY);
        COLUMNS.add(SECRET_KEY);
        COLUMNS.add(PASSWORD_KEY);
    }

    public Account() {

    }

    public Account(String email,String publicKey,String secretKey,String passwordKey) {
        this.id=-1;
        this.email=email;
        this.publicKey=publicKey;
        this.secretKey=secretKey;
        this.passwordKey=passwordKey;
    }

    public Account(int id,String email,String publicKey,String secretKey,String passwordKey) {
        this(email,publicKey,secretKey,passwordKey);
        this.id=id;
    }

    public ContentValues getContentValues() {
        ContentValues values=new ContentValues();
        values.put(ID.getName(),id);
        values.put(EMAIL.getName(),email);
        values.put(PUBLIC_KEY.getName(),publicKey);
        values.put(SECRET_KEY.getName(),secretKey);
        values.put(PASSWORD_KEY.getName(),passwordKey);
        return values;
    }
    //public static final String ACCOUNT_DATABASE_CREATE = "create table if not exists Accounts (id integer primary key,mail text not null,pgpPublicKey text not null,pgpSecretKey text not null,pgpPassword text not null)";
    //public static final String KEY_DATABASE_CREATE = "create table if not exists Keys (id integer primary key,mail text not null,pgpPublicKey text not null)";
    public static String createTableIfNotExists() {
        StringBuilder builder=new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS").append(" ");
        builder.append(TABLE_NAME).append(" (");
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

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPasswordKey() {
        return passwordKey;
    }

    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }
}
