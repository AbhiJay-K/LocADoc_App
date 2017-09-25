package com.locadoc_app.locadoc.LocalDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.helper.Encryption;
import com.locadoc_app.locadoc.helper.Hash;

/**
 * Created by AbhiJay_PC on 22/9/2017.
 */

public class PasswordSQLHelper implements BaseColumns {
    public static final String TABLE_NAME = "password";
    //public static final String COLUMN_PWDID = "pwdid";
    public static final String COLUMN_PWD = "password";
    public static final String COLUMN_SALT = "salt";
    private static DBHelper dbHelper;
    private static Encryption encryption;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PWD + " TEXT, " +
            COLUMN_SALT + " TEXT " +
            ")";
    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }

    public static long insert(Password password)
    {
        ContentValues values = new ContentValues();
        String salt = Hash.SecureRandomGen();
        password.setSalt(salt);
        String PasswordDigest = Hash.Hash(password.getPassword(),password.getSalt());
        password.setPassword(PasswordDigest);
        values.put(PasswordSQLHelper.COLUMN_SALT, salt);
        values.put(PasswordSQLHelper.COLUMN_PWD, PasswordDigest);
        long newRowId = PasswordSQLHelper.getDbHelper().WRITE.insert(PasswordSQLHelper.TABLE_NAME, null, values);
        return newRowId;
    }
    public static Password getRecord(int id)
    {
        Password pwd = new Password();
        String [] args = {String.valueOf(id)};
        Cursor crs = PasswordSQLHelper.getDbHelper().READ.rawQuery("SELECT * FROM password WHERE _ID = ?", args);
        crs.moveToFirst();
        pwd.setPasswordid(crs.getInt(crs.getColumnIndex("_id")));
        pwd.setPassword(crs.getString(crs.getColumnIndex("password")));
        pwd.setSalt(crs.getString(crs.getColumnIndex("salt")));
        return pwd;
    }
}
