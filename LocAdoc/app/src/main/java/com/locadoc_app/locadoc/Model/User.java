package com.locadoc_app.locadoc.Model;

import android.provider.BaseColumns;

import com.locadoc_app.locadoc.LocalDB.DBHelper;

/**
 * Created by AbhiJay_PC on 22/9/2017.
 */

public class User implements BaseColumns{
    public static final String TABLE_NAME = "user";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_LOGEDIN = "logedin";
    public static final String COLUMN_MACADD = "macaddress";
    public static final String COLUMN_PWD = "password";

    private static DBHelper dbHelper;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
            COLUMN_FIRST_NAME + " TEXT, " +
            COLUMN_LAST_NAME + " TEXT, " +
            COLUMN_LOGEDIN + " INTEGER, " +
            COLUMN_MACADD + " TEXT, " +
            COLUMN_PWD + " INTEGER, " +
            " FOREIGN KEY ("+COLUMN_PWD+") REFERENCES "+Password.TABLE_NAME+"("+ Password._ID +
            "))";
    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }
}
