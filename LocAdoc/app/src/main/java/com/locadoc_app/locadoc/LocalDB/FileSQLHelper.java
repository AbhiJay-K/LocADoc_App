package com.locadoc_app.locadoc.LocalDB;

import android.provider.BaseColumns;

/**
 * Created by AbhiJay_PC on 22/9/2017.
 */

public class FileSQLHelper implements BaseColumns {
    public static final String TABLE_NAME = "file";
    public static final String COLUMN_CURRENT_NAME = "currentfilename";
    public static final String COLUMN_ORIGINAL_NAME = "originalfilename";
    public static final String COLUMN_MODIFIED = "modified";
    public static final String COLUMN_PWD = "password";
    public static final String COLUMN_AREA = "area";
    private static DBHelper dbHelper;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_CURRENT_NAME + " TEXT, " +
            COLUMN_ORIGINAL_NAME + " TEXT, " +
            COLUMN_MODIFIED + " TEXT, " +
            COLUMN_PWD + " INTEGER, " +
            COLUMN_AREA + " INTEGER, "+
            " FOREIGN KEY ("+COLUMN_PWD+") REFERENCES "+ PasswordSQLHelper.TABLE_NAME+"("+ PasswordSQLHelper._ID +
            "), " +
            " FOREIGN KEY ("+COLUMN_AREA+") REFERENCES "+ AreaSQLHelper.TABLE_NAME+"("+ AreaSQLHelper._ID +
            ")) ";
    public static DBHelper getDbHelper() {
        return dbHelper;
    }
    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }
}
