package com.locadoc_app.locadoc.LocalDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by AbhiJay_PC on 17/10/2017.
 */

public class GuestSession implements BaseColumns {
    public static final String TABLE_NAME = "guestsession";
    public static final String COLUMN_TRY = "numtry";
    public static final String COLUMN_COUNTDOWN = "countdown";
    private static DBHelper dbHelper;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY, " +
            COLUMN_TRY + " INTEGER, " +
            COLUMN_COUNTDOWN + " INTEGER " +
            ")";
    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }

    public static long insert()
    {
        ContentValues values = new ContentValues();
        values.put(GuestSession.COLUMN_TRY, 0);
        values.put(GuestSession.COLUMN_COUNTDOWN, 0);
        long newRowId = GuestSession.getDbHelper().WRITE.insert(GuestSession.TABLE_NAME, null, values);
        return newRowId;
    }

    public static long ResetReacord() {
        ContentValues values = new ContentValues();
        values.put(GuestSession.COLUMN_TRY, 0L);
        values.put(GuestSession.COLUMN_COUNTDOWN, 0L);
        String [] arg = {String.valueOf(1)};
        long newRowId = GuestSession.getDbHelper().WRITE.update(GuestSession.TABLE_NAME,values,"_id=?",arg);
        return newRowId;
    }

    public static long updateNumTries(long [] val) {
        ContentValues values = new ContentValues();
        values.put(GuestSession.COLUMN_TRY, val[0]);
        values.put(GuestSession.COLUMN_COUNTDOWN,val[1]);
        String [] arg = {String.valueOf(1)};
        long newRowId = GuestSession.getDbHelper().WRITE.update(GuestSession.TABLE_NAME,values,"_id=?",arg);
        return newRowId;
    }

    public static long [] getRecord() {
        String[] args = {String.valueOf(1)};
        Cursor crs = dbHelper.READ.rawQuery("SELECT * FROM guestsession WHERE _id = ?", args);
        long [] val = new long[2];
        if (crs != null) {
            crs.moveToFirst();
            val[0] = crs.getLong(crs.getColumnIndex(COLUMN_TRY));
            val[1] = crs.getLong(crs.getColumnIndex(COLUMN_COUNTDOWN));
        }
        crs.close();
        return val;
    }

    public static long getNumberofRecords()
    {
        String countQuery = "SELECT  * FROM " + GuestSession.TABLE_NAME;
        Cursor cursor = dbHelper.READ.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
}
