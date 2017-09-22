package com.locadoc_app.locadoc.Model;

import android.provider.BaseColumns;

import com.locadoc_app.locadoc.LocalDB.DBHelper;

/**
 * Created by AbhiJay_PC on 22/9/2017.
 */

public class Password implements BaseColumns {
    public static final String TABLE_NAME = "password";
    public static final String COLUMN_PWD = "password";
    public static final String COLUMN_SALT = "salt";
    private static DBHelper dbHelper;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            _ID + " TEXT PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PWD + " TEXT, " +
            COLUMN_SALT + " TEXT " +
            ")";
    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }
}
