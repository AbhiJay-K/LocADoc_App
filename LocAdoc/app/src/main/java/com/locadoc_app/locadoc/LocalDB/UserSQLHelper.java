package com.locadoc_app.locadoc.LocalDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.helper.Encryption;

/**
 * Created by AbhiJay_PC on 22/9/2017.
 */

public class UserSQLHelper implements BaseColumns{
    public static final String TABLE_NAME = "user";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_LOGEDIN = "logedin";
    public static final String COLUMN_MACADD = "macaddress";
    public static final String COLUMN_PWD = "password";
    public static final String COLUMN_AREA = "adminarea";
    private static DBHelper dbHelper;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
            COLUMN_FIRST_NAME + " TEXT, " +
            COLUMN_LAST_NAME + " TEXT, " +
            COLUMN_LOGEDIN + " TEXT, " +
            COLUMN_MACADD + " TEXT, " +
            COLUMN_PWD + " INTEGER, " +
            COLUMN_AREA + " INTEGER, "+
            " FOREIGN KEY ("+COLUMN_PWD+") REFERENCES "+ PasswordSQLHelper.TABLE_NAME+"("+ PasswordSQLHelper._ID +
            "), " +
            " FOREIGN KEY ("+COLUMN_AREA+") REFERENCES "+ AreaSQLHelper.TABLE_NAME+"("+ AreaSQLHelper._ID +
            "))";
    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }
    public static long insert(User usr)
    {
        ContentValues values = new ContentValues();
        String[] args={String.valueOf(1)};
        Cursor crs = PasswordSQLHelper.getDbHelper().READ.rawQuery("SELECT * FROM password WHERE _ID = ?", args);
        crs.moveToFirst();
        String password1 = crs.getString(crs.getColumnIndex("password"));
        String salt = crs.getString(crs.getColumnIndex("salt"));
        Encryption en = Encryption.getInstance(password1,salt);
        values.put(UserSQLHelper.COLUMN_EMAIL, usr.getUser());
        values.put(UserSQLHelper.COLUMN_FIRST_NAME, en.encryptString(usr.getFirstname()));
        values.put(UserSQLHelper.COLUMN_LAST_NAME, en.encryptString(usr.getLastname()));
        values.put(UserSQLHelper.COLUMN_LOGEDIN, en.encryptString(usr.getLoggedin()));
        values.put(UserSQLHelper.COLUMN_MACADD, en.encryptString(usr.getMacaddress()));
        values.put(UserSQLHelper.COLUMN_PWD, 1);
        values.putNull(COLUMN_AREA);
        long newRowId = UserSQLHelper.getDbHelper().WRITE.insert(UserSQLHelper.TABLE_NAME, null, values);
        return newRowId;
    }
}
