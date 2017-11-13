package com.locadoc_app.locadoc.LocalDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import com.locadoc_app.locadoc.Model.File;
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

public class FileSQLHelper implements BaseColumns {
    public static final String TABLE_NAME = "file";
    public static final String COLUMN_CURRENT_NAME = "currentfilename";
    public static final String COLUMN_ORIGINAL_NAME = "originalfilename";
    public static final String COLUMN_BACKEDUP = "backedup";
    public static final String COLUMN_FILE_SIZE = "filesize";
    public static final String COLUMN_PWD = "password";
    public static final String COLUMN_AREA = "area";
    private static DBHelper dbHelper;

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY, "+
            COLUMN_CURRENT_NAME + " TEXT, " +
            COLUMN_ORIGINAL_NAME + " TEXT, " +
            COLUMN_BACKEDUP + " TEXT, " +
            COLUMN_FILE_SIZE + " TEXT, " +
            COLUMN_PWD + " INTEGER, " +
            COLUMN_AREA + " INTEGER, "+
            " FOREIGN KEY ("+COLUMN_AREA+") REFERENCES "+ AreaSQLHelper.TABLE_NAME+"("+ AreaSQLHelper._ID +
            ")) ";

    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }

    public static long insert(File file,Password pwd)
    {
        ContentValues values = new ContentValues();
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        values.put(FileSQLHelper._ID, file.getFileId());
        values.put(FileSQLHelper.COLUMN_CURRENT_NAME, en.encryptString(file.getCurrentfilename()));
        values.put(FileSQLHelper.COLUMN_ORIGINAL_NAME, en.encryptString(file.getOriginalfilename()));
        values.put(FileSQLHelper.COLUMN_BACKEDUP, en.encryptString(file.getBackedup()));
        values.put(FileSQLHelper.COLUMN_FILE_SIZE, en.encryptString(file.getFilesize()));
        values.put(FileSQLHelper.COLUMN_PWD, file.getPasswordId());
        values.put(FileSQLHelper.COLUMN_AREA, file.getAreaId());
        long newRowId = FileSQLHelper.getDbHelper().WRITE.insert(FileSQLHelper.TABLE_NAME, null, values);
        return newRowId;
    }

    public static long insertWithoutEncryption(File file,Password pwd)
    {
        ContentValues values = new ContentValues();
        values.put(FileSQLHelper._ID, file.getFileId());
        values.put(FileSQLHelper.COLUMN_CURRENT_NAME, file.getCurrentfilename());
        values.put(FileSQLHelper.COLUMN_ORIGINAL_NAME, file.getOriginalfilename());
        values.put(FileSQLHelper.COLUMN_BACKEDUP, file.getBackedup());
        values.put(FileSQLHelper.COLUMN_FILE_SIZE, file.getFilesize());
        values.put(FileSQLHelper.COLUMN_PWD, file.getPasswordId());
        values.put(FileSQLHelper.COLUMN_AREA, file.getAreaId());
        long newRowId = FileSQLHelper.getDbHelper().WRITE.insert(FileSQLHelper.TABLE_NAME, null, values);
        return newRowId;
    }

    public static int maxID()
    {
        Cursor crs = FileSQLHelper.getDbHelper().READ.rawQuery("SELECT * FROM file WHERE _id = ( SELECT MAX(_id) FROM file)",null);
        int ID = 0;
        if (crs != null && crs.moveToFirst()) {
            ID = crs.getInt(crs.getColumnIndex(_ID));
        }
        crs.close();
        return ID;
    }

    //Query to search file table using original file name
    public static File getFile(int FileID,Password pwd)
    {
        User user = new User();
        String [] args = {String.valueOf(FileID)};
        Cursor crs = FileSQLHelper.dbHelper.READ.rawQuery("SELECT * FROM file WHERE _id = ?", args);
        if(crs != null)
        {
            crs.moveToFirst();
            int id = crs.getInt(crs.getColumnIndex("_id"));
            String crFileN = crs.getString(crs.getColumnIndex(COLUMN_CURRENT_NAME));
            String orgFileN = crs.getString(crs.getColumnIndex(COLUMN_ORIGINAL_NAME));
            String backedup = crs.getString(crs.getColumnIndex(COLUMN_BACKEDUP));
            String fileSize = crs.getString(crs.getColumnIndex(COLUMN_FILE_SIZE));
            int PwdId = crs.getInt(crs.getColumnIndex(COLUMN_PWD));
            int AreaID = crs.getInt(crs.getColumnIndex(COLUMN_AREA));
            Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
            File file = new File();
            file.setFileId(id);
            file.setCurrentfilename(en.decrypttString(crFileN));
            file.setOriginalfilename(en.decrypttString(orgFileN));
            file.setBackedup(en.decrypttString(backedup));
            file.setFilesize(en.decrypttString(fileSize));
            file.setPasswordId(PwdId);
            file.setAreaId(AreaID);
            return file;
        }
        else
        {
            File file = new File();
            file.setFileId(-1);
            return file;
        }
    }

    //Query to search file table using original area id and returns a list
    public static Map<String,Integer> getFilesInArea(int AreaID, Password pwd)
    {
        String [] args = {String.valueOf(AreaID)};
        Cursor crs = FileSQLHelper.dbHelper.READ.rawQuery("SELECT * FROM file WHERE area = ?", args);
        Map<String,Integer> FileMap = new HashMap<String,Integer>();
        if (crs != null && crs.moveToFirst()) {
            do {
                int id = crs.getInt(crs.getColumnIndex("_id"));
                String orgFileN = crs.getString(crs.getColumnIndex(COLUMN_ORIGINAL_NAME));
                Encryption en = Encryption.getInstance(pwd.getPassword(), pwd.getSalt());
                FileMap.put(en.decrypttString(orgFileN),id);
            } while (crs.moveToNext());
            crs.close();
            return FileMap;
        }
        else {
            crs.close();
            return FileMap;
        }

    }

    //Query to get all file id
    public static List<Integer> getAllFileID()
    {
        Cursor crs = FileSQLHelper.dbHelper.READ.rawQuery("SELECT _id FROM file", null);
        List<Integer> allId = new ArrayList<>();
        if (crs != null && crs.moveToFirst()) {
            do {
                int id = crs.getInt(crs.getColumnIndex("_id"));
                allId.add(id);
            } while (crs.moveToNext());
            crs.close();
            return allId;
        }
        else {
            crs.close();
            return allId;
        }

    }

    //Query to get all file id
    public static List<String> getAllFilesName(Password password)
    {
        Encryption en = Encryption.getInstance(password.getPassword(), password.getSalt());
        Cursor crs = FileSQLHelper.dbHelper.READ.rawQuery("SELECT " + COLUMN_CURRENT_NAME + " FROM file", null);
        List<String> allCurrName = new ArrayList<>();
        if (crs != null && crs.moveToFirst()) {
            do {
                String fileName = crs.getString(crs.getColumnIndex(COLUMN_CURRENT_NAME));
                allCurrName.add(en.decrypttString(fileName));
            } while (crs.moveToNext());
            crs.close();
            return allCurrName;
        }
        else {
            crs.close();
            return allCurrName;
        }

    }

    //Query to search file table using original area id and returns a list
    public static boolean checkFilesInAreaExist(int AreaID, Password pwd)
    {
        String [] args = {String.valueOf(AreaID)};
        Cursor crs = FileSQLHelper.dbHelper.READ.rawQuery("SELECT * FROM file WHERE area = ?", args);
        if (crs != null && crs.moveToFirst()) {
            crs.close();
            return true;
        }
        else {
            crs.close();
            return false;
        }

    }

    public static long updateRecord(File file,Password pwd)
    {
        ContentValues values = new ContentValues();
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        values.put(FileSQLHelper.COLUMN_CURRENT_NAME, en.encryptString(file.getCurrentfilename()));
        values.put(FileSQLHelper.COLUMN_ORIGINAL_NAME, en.encryptString(file.getOriginalfilename()));
        values.put(FileSQLHelper.COLUMN_BACKEDUP, String.valueOf(file.getBackedup()));
        values.put(FileSQLHelper.COLUMN_PWD, String.valueOf(file.getPasswordId()));
        values.put(FileSQLHelper.COLUMN_AREA, String.valueOf(file.getAreaId()));
        String [] arg = {String.valueOf(file.getFileId())};
        long newRowId = FileSQLHelper.getDbHelper().WRITE.update(FileSQLHelper.TABLE_NAME,values,"_id = ?",arg);

        return newRowId;
    }

    public static long getNumberofRecords()
    {
        String countQuery = "SELECT  * FROM " + FileSQLHelper.TABLE_NAME;
        Cursor cursor = dbHelper.READ.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public static int deleteRecord(int fileid)
    {
        String [] arg = {String.valueOf(fileid)};
        int deleted = FileSQLHelper.getDbHelper().WRITE.delete(FileSQLHelper.TABLE_NAME,"_id = ?",arg);
        return deleted;
    }

    public static int checkFileNameExist(String name,Password pwd)
    {
        Cursor crs = FileSQLHelper.dbHelper.READ.rawQuery("SELECT originalfilename FROM file",null);
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        int count = 0;
        if (crs != null && crs.moveToFirst()) {
            do {
                String fname = crs.getString(crs.getColumnIndex(FileSQLHelper.COLUMN_ORIGINAL_NAME));
                String fname2 = en.decrypttString(fname);
                if(fname2.equals(name)) {
                    count++;
                }
            }while(crs.moveToNext());
            crs.close();
            return count;
        }
        return -1;
    }

    public static int checkFileNameInAnAreaExist(String name, int areaid, Password pwd)
    {
        Cursor crs = FileSQLHelper.dbHelper.READ.rawQuery("SELECT originalfilename FROM file where " +
                COLUMN_AREA + " = " + areaid, null);
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        int count = 0;
        if (crs != null && crs.moveToFirst()) {
            do {
                String fname = crs.getString(crs.getColumnIndex(FileSQLHelper.COLUMN_ORIGINAL_NAME));
                fname = en.decrypttString(fname);
                if(fname.equals(name)) {
                    return 1;
                }
            }while(crs.moveToNext());
            crs.close();
            return count;
        }
        return 0;
    }

    public static void clearRecord()
    {
        dbHelper.WRITE.execSQL("delete from "+ TABLE_NAME);
    }
}
