package com.locadoc_app.locadoc.LocalDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.provider.BaseColumns;
import android.util.Log;

import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.helper.Encryption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            COLUMN_NAME + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
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

    public static long insert(Area ar, Password pwd) {
        ContentValues values = new ContentValues();
        Encryption en = Encryption.getInstance(pwd.getPassword(), pwd.getSalt());
        values.put(AreaSQLHelper.COLUMN_NAME, en.encryptString(ar.getName()));
        values.put(AreaSQLHelper.COLUMN_DESCRIPTION, en.encryptString(ar.getDescription()));
        values.put(AreaSQLHelper.COLUMN_LATITUDE, en.encryptString(ar.getLatitude()));
        values.put(AreaSQLHelper.COLUMN_LONGITUDE, en.encryptString(ar.getLongitude()));
        values.put(AreaSQLHelper.COLUMN_RADIUS, en.encryptString(ar.getRadius()));
        long newRowId = AreaSQLHelper.getDbHelper().WRITE.insert(AreaSQLHelper.TABLE_NAME, null, values);
        return newRowId;
    }
    public static int maxID()
    {
        Cursor crs = AreaSQLHelper.getDbHelper().READ.rawQuery("SELECT * FROM area WHERE _id = ( SELECT MAX(_id) FROM area)",null);
        int ID = 0;
        if (crs != null && crs.moveToFirst()) {
            ID = crs.getInt(crs.getColumnIndex(_ID));
        }
        crs.close();
        return ID;
    }
    public static Area getRecord(int AreaID, Password pwd) {
        String[] args = {String.valueOf(AreaID)};
        Cursor crs = dbHelper.READ.rawQuery("SELECT * FROM area WHERE _id = ?", args);
        if (crs != null) {
            crs.moveToFirst();
            int id = crs.getInt(crs.getColumnIndex("_id"));
            String name = crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_NAME));
            String description = crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_DESCRIPTION));
            String Long = crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_LONGITUDE));
            String Lat = crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_LATITUDE));
            String Rad = crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_RADIUS));
            crs.close();
            Encryption en = Encryption.getInstance(pwd.getPassword(), pwd.getSalt());
            Area ar = new Area();
            ar.setAreaId(id);
            ar.setName(en.decrypttString(name));
            ar.setDescription(en.decrypttString(description));
            ar.setLatitude(en.decrypttString(Lat));
            ar.setLongitude(en.decrypttString(Long));
            ar.setRadius(en.decrypttString(Rad));
            return ar;
        } else {
            crs.close();
            Area a = new Area();
            a.setAreaId(-1);
            return a;
        }
    }
    public static List<Area> getAllRecord(Password pwd) {
        Cursor crs = AreaSQLHelper.dbHelper.READ.rawQuery("SELECT * FROM area",null);
        List<Area> arList = new ArrayList<Area>();
        if (crs != null &&  crs.moveToFirst()) {
           do {
               int id = crs.getInt(crs.getColumnIndex("_id"));
               String name = crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_NAME));
               String description = crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_DESCRIPTION));
               String Long = crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_LONGITUDE));
               String Lat = crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_LATITUDE));
               String Rad = crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_RADIUS));
               Encryption en = Encryption.getInstance(pwd.getPassword(), pwd.getSalt());
               Area ar = new Area();
               ar.setAreaId(id);
               ar.setName(en.decrypttString(name));
               ar.setDescription(en.decrypttString(description));
               ar.setLatitude(en.decrypttString(Lat));
               ar.setLongitude(en.decrypttString(Long));
               ar.setRadius(en.decrypttString(Rad));
               arList.add(ar);
           }while(crs.moveToNext());
            crs.close();
            return arList;
        } else {
            crs.close();
            Area a = new Area();
            a.setAreaId(-1);
            return arList;
        }
    }
    public static Map<String,Integer> getAreaNameInLoc(Location l2,Password pwd)
    {
        Cursor crs = AreaSQLHelper.dbHelper.READ.rawQuery("SELECT * FROM area",null);
        Map<String,Integer> AreaMap = new HashMap<String,Integer>();
        if (crs != null && crs.moveToFirst()) {
            do {
                Encryption en = Encryption.getInstance(pwd.getPassword(), pwd.getSalt());
                int id = crs.getInt(crs.getColumnIndex("_id"));
                String AreaName = en.decrypttString(crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_NAME)));
                Double longitude = Double.parseDouble(en.decrypttString(crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_LONGITUDE))));
                Double latitude = Double.parseDouble(en.decrypttString(crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_LATITUDE))));
                float radius = Float.parseFloat(en.decrypttString(crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_RADIUS))));
                Location l1 = new Location("");
                l1.setLatitude(latitude);
                l1.setLongitude(longitude);
                float rad2 = l1.distanceTo(l2);
                if(rad2 <= radius)
                {
                    AreaMap.put(AreaName,id);
                }
            } while (crs.moveToNext());
            crs.close();
            return AreaMap;
        }
        else {
            crs.close();
            return AreaMap;
        }
    }
    public static int checkLocExist(Area ar,Password pwd)
    {
        Cursor crs = AreaSQLHelper.dbHelper.READ.rawQuery("SELECT * FROM area",null);
        if (crs != null && crs.moveToFirst()) {
            do {
                Encryption en = Encryption.getInstance(pwd.getPassword(), pwd.getSalt());
                int id = crs.getInt(crs.getColumnIndex("_id"));
                String AreaName = en.decrypttString(crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_NAME)));
                Double longitude = Double.parseDouble(en.decrypttString(crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_LONGITUDE))));
                Double latitude = Double.parseDouble(en.decrypttString(crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_LATITUDE))));
                Location l1 = new Location("");
                l1.setLatitude(latitude);
                l1.setLongitude(longitude);
                Location l2 = new Location("");
                l2.setLongitude(Double.parseDouble(ar.getLongitude()));
                l2.setLatitude(Double.parseDouble(ar.getLatitude()));
                if(l1.distanceTo(l2) == 0.0f)
                {
                    Log.d("Distance Error","Same distance");
                    return 1;
                }
            } while (crs.moveToNext());
            crs.close();
            return 0;
        }
        else {
            crs.close();
            return -1;
        }
    }
    public static int checkAreaNameExist(String name,Password pwd)
    {
        Cursor crs = AreaSQLHelper.dbHelper.READ.rawQuery("SELECT areaname FROM area",null);
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        int count = 0;
        if (crs != null && crs.moveToFirst()) {
            do {
                String arname = crs.getString(crs.getColumnIndex(AreaSQLHelper.COLUMN_NAME));
                String arname2 = en.decrypttString(arname);
                if(arname2.equals(name)) {
                    count++;
                }
            }while(crs.moveToNext());
            crs.close();
            return count;
        }
        return -1;
    }
    public static long updateRecord(Area ar,Password pwd)
    {
        ContentValues values = new ContentValues();
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        values.put(AreaSQLHelper.COLUMN_NAME, en.encryptString(ar.getName()));
        values.put(AreaSQLHelper.COLUMN_DESCRIPTION, en.encryptString(ar.getDescription()));
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
        Cursor cursor = AreaSQLHelper.dbHelper.READ.rawQuery(countQuery, null);
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
