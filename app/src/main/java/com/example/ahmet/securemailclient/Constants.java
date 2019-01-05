package com.example.ahmet.securemailclient;

public class Constants {
    public static final String DELIMITER_KEY="#+%&";
    public static final int SEND_MAIL_MENU_ITEM_ID=12432523;
    public static final String SHARED_PREFERENCES_NAME="SecureEmail";
    public static final String PGP_PUBLIC_KEY_NAME="pgpPublic";
    public static final String PGP_SECRET_KEY_NAME="pgpSecret";
    public static final String PGP_PASSWORD_KEY_NAME="pgpPassword";
    public static final String EMAIL_NAME="email";
    public static final String PASSWORD_NAME="password";
    public static final String KEY_QR_CODE="keyqrcode";
    public static String pgpPublicKey;
    public static String pgpSecretKey;
    public static String pgpPassword;
    public static String email;
    public static String password;

    //Database
    public static final String DATABASE_NAME="accountDb";
    public static final int DATABASE_VERSION=1;


    public static final String ACCOUNT_DATABASE_CREATE = "create table if not exists Accounts (id integer primary key,mail text not null,pgpPublicKey text not null,pgpSecretKey text not null,pgpPassword text not null)";
    public static final String KEY_DATABASE_CREATE = "create table if not exists Keys (id integer primary key,mail text not null,pgpPublicKey text not null)";
}
