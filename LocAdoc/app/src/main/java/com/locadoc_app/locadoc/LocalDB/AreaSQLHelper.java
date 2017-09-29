package com.locadoc_app.locadoc.LocalDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.helper.Encryption;

/**
 * Created by AbhiJay_PC on 22/9/2017.
 */

public class AreaSQLHelper implements BaseColumns {
    public static final String TABLE_NAME = "area";
    public static final String COLUMN_NAME = "areaname";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_RADIUS = "radius";
    private static DBHelper dbHelper;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_LONGITUDE + " TEXT, " +
            COLUMN_LATITUDE + " TEXT, " +
            COLUMN_RADIUS + " TEXT " +
            ")";
    public static DBHelper getDbHelper() {
        return dbHelper;
    }
    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }
    public static long insert(Area ar,Password pwd)
    {
        ContentValues values = new ContentValues();
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        values.put(AreaSQLHelper.COLUMN_LATITUDE, en.encryptString(ar.getLatitude()));
        values.put(AreaSQLHelper.COLUMN_LONGITUDE, en.encryptString(ar.getLongitude()));
        values.put(AreaSQLHelper.COLUMN_RADIUS, en.encryptString(ar.getRadius()));
        long newRowId = AreaSQLHelper.getDbHelper().WRITE.insert(AreaSQLHelper.TABLE_NAME, null, values);
        return newRowId;
    }
    public static Area getRecord(int AreaID,Password pwd)
    {
        String [] args = {String.valueOf(AreaID)};
        Cursor crs = dbHelper.READ.rawQuery("SELECT * FROM area WHERE _id = ?", args);
        if(crs != null) {
            crs.moveToFirst();
            int id = crs.getInt(crs.getColumnIndex("_id"));
            String Long = crs.getString(crs.getColumnIndex("longitude"));
            String Lat = crs.getString(crs.getColumnIndex("latitude"));
            String Rad = crs.getString(crs.getColumnIndex("radius"));
            crs.close();
            Encryption en = Encryption.getInstance(pwd.getPassword(), pwd.getSalt());
            Area ar = new Area();
            ar.setAreaId(id);
            ar.setLatitude(en.decrypttString(Lat));
            ar.setLongitude(en.decrypttString(Long));
            ar.setRadius(en.decrypttString(Rad));
            return ar;
        }
        else
        {
            crs.close();
            Area a = new Area();
            a.setAreaId(-1);
            return a;
        }
    }
    public static long updateRecord(Area ar,Password pwd)
    {
        ContentValues values = new ContentValues();
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        values.put(AreaSQLHelper.COLUMN_LATITUDE, en.encryptString(ar.getLatitude()));
        values.put(AreaSQLHelper.COLUMN_LONGITUDE, en.encryptString(ar.getLongitude()));
        values.put(AreaSQLHelper.COLUMN_RADIUS, en.encryptString(ar.getRadius()));
        String [] arg = {String.valueOf(ar.getAreaId())};
        long newRowId = AreaSQLHelper.getDbHelper().WRITE.update(AreaSQLHelper.TABLE_NAME,values,"_id=?",arg);
        return newRowId;
    }
    public static long getNumberofRecords()
    {
        String countQuery = "SELECT  * FROM " + AreaSQLHelper.TABLE_NAME;
        Cursor cursor = dbHelper.READ.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
    public static int deleteRecord(int AreaID)
    {
        String [] arg = {String.valueOf(AreaID)};
        int deleted = AreaSQLHelper.getDbHelper().WRITE.delete(AreaSQLHelper.TABLE_NAME,"_id = ?",arg);
        return deleted;
    }
}
