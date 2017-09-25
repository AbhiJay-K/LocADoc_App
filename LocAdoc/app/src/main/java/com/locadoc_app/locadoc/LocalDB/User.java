package com.locadoc_app.locadoc.LocalDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.locadoc_app.locadoc.helper.Encryption;

/**
 * Created by AbhiJay_PC on 22/9/2017.
 */

public class User implements BaseColumns{
    public static final String TABLE_NAME = "user";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_LOGEDIN = "logedin";
    public static final String COLUMN_MACADD = "macaddress";
    public static final String COLUMN_PWD = "password";
    private static DBHelper dbHelper;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
            COLUMN_FIRST_NAME + " TEXT, " +
            COLUMN_LAST_NAME + " TEXT, " +
            COLUMN_LOGEDIN + " INTEGER, " +
            COLUMN_MACADD + " TEXT, " +
            COLUMN_PWD + " INTEGER, " +
            " FOREIGN KEY ("+COLUMN_PWD+") REFERENCES "+ Password.TABLE_NAME+"("+ Password._ID +
            "))";
    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }
    public static long insert(String email,String firstname,String lastname,int logedin,String macAddress,int password)
    {
        ContentValues values = new ContentValues();
        String[] args={"1"};
        Cursor crs = Password.getDbHelper().READ.rawQuery("SELECT * FROM password WHERE _ID = ?", args);
        String password1 = crs.getString(crs.getColumnIndex("password"));
        String salt = crs.getString(crs.getColumnIndex("salt"));
        Encryption en = Encryption.getInstance(password1,salt);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_FIRST_NAME, en.encryptString(firstname));
        values.put(COLUMN_LAST_NAME, en.encryptString(lastname));
        values.put(COLUMN_LOGEDIN, en.encryptString(Integer.toString(logedin)));
        values.put(COLUMN_MACADD, en.encryptString(macAddress));
        values.put(COLUMN_PWD, en.encryptString(Integer.toString(1)));
        long newRowId = getDbHelper().WRITE.insert(Password.TABLE_NAME, null, values);
        return newRowId;
    }
}
