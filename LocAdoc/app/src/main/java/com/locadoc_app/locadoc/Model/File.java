package com.locadoc_app.locadoc.Model;

import android.provider.BaseColumns;

import com.locadoc_app.locadoc.LocalDB.DBHelper;

/**
 * Created by AbhiJay_PC on 22/9/2017.
 */

public class File implements BaseColumns {
    public static final String TABLE_NAME = "file";
    public static final String COLUMN_CURRENT_NAME = "currentfilename";
    public static final String COLUMN_ORIGINAL_NAME = "originalfilename";
    public static final String COLUMN_LONGITUDE_CREATED = "longitudecreated";
    public static final String COLUMN_LATITUDE_CREATED = "latitudecreated";
    public static final String COLUMN_PWD_DIGEST = "pwddigest";
    public static final String COLUMN_MODIFIED = "modified";
    public static final String COLUMN_PASSWORD = "pwddigest";
    public static final String COLUMN_AREA = "area";
    private static DBHelper dbHelper;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            _ID +
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
