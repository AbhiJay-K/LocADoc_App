package com.locadoc_app.locadoc.LocalDB;

import android.provider.BaseColumns;

/**
 * Created by AbhiJay_PC on 22/9/2017.
 */

public class AreaSQLHelper implements BaseColumns {
    public static final String TABLE_NAME = "area";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_RADIUS = "radius";
    private static DBHelper dbHelper;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_LONGITUDE + " TEXT, " +
            COLUMN_LATITUDE + " TEXT, " +
            COLUMN_RADIUS + " TEXT, " +
            ")";
    public static DBHelper getDbHelper() {
        return dbHelper;
    }
    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }
}
