package com.locadoc_app.locadoc.LocalDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.helper.Encryption;
import com.locadoc_app.locadoc.helper.Hash;

/**
 * Created by AbhiJay_PC on 3/10/2017.
 */

public class ApplicationInstance implements BaseColumns {
    public static final String TABLE_NAME = "applicationinstance";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INSTANCE = "instance";
    private static DBHelper dbHelper;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_INSTANCE + " TEXT " +
            ")";
    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }
    public static long insert(String Instance) {
        if(getNumberofRecords() > 0){
            deleteRecord();
        }

        ContentValues values = new ContentValues();
        values.put(ApplicationInstance.COLUMN_ID, 1);
        values.put(ApplicationInstance.COLUMN_INSTANCE, Instance);
        long newRowId = ApplicationInstance.getDbHelper().WRITE.insert(ApplicationInstance.TABLE_NAME, null, values);
        return newRowId;
    }
    public static String getRecord()
    {
        Cursor crs = ApplicationInstance.dbHelper.READ.rawQuery("SELECT * FROM applicationinstance",null);
        String instance = new String();
        if (crs != null &&  crs.moveToFirst()) {
                instance = crs.getString(crs.getColumnIndex(ApplicationInstance.COLUMN_INSTANCE));
            Log.d("CHECK INSTANCE ID", "INSTANCE: " + instance);
                crs.close();
                return instance;
        }
        else
        {
            crs.close();
            return instance;
        }
    }
    public static long updateRecord(String Instance)
    {
        ContentValues values = new ContentValues();
        values.put(ApplicationInstance.COLUMN_INSTANCE,Instance);
        String [] arg = {String.valueOf(1)};
        long newRowId = ApplicationInstance.getDbHelper().WRITE.update(ApplicationInstance.TABLE_NAME,values, COLUMN_ID + " = ?",arg);
        return newRowId;
    }
    public static long getNumberofRecords()
    {
        String countQuery = "SELECT  * FROM " + ApplicationInstance.TABLE_NAME;
        Cursor cursor = ApplicationInstance.dbHelper.READ.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
    public static int deleteRecord()
    {
        String [] arg = {String.valueOf(1)};
        int deleted = ApplicationInstance.getDbHelper().WRITE.delete(ApplicationInstance.TABLE_NAME, COLUMN_ID + " = ?",arg);
        return deleted;
    }
}
