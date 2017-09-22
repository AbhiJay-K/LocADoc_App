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
    public static final String COLUMN_PWD = "password";
    public static final String COLUMN_AREA = "area";
    private static DBHelper dbHelper;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            _ID + " TEXT PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_CURRENT_NAME + " TEXT, " +
            COLUMN_ORIGINAL_NAME + " TEXT, " +
            COLUMN_LONGITUDE_CREATED + " TEXT, " +
            COLUMN_LATITUDE_CREATED + " TEXT, " +
            COLUMN_PWD_DIGEST + " TEXT, " +
            COLUMN_MODIFIED + " INTEGER, " +
            COLUMN_PWD + " INTEGER, " +
            COLUMN_AREA + " INTEGER, "+
            " FOREIGN KEY ("+COLUMN_PWD+") REFERENCES "+Password.TABLE_NAME+"("+ Password._ID +
            "), " +
            " FOREIGN KEY ("+COLUMN_AREA+") REFERENCES "+Area.TABLE_NAME+"("+ Area._ID +
            ")) ";
    public static DBHelper getDbHelper() {
        return dbHelper;
    }
    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }
}
