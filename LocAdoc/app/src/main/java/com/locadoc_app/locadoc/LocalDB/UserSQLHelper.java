package com.locadoc_app.locadoc.LocalDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.locadoc_app.locadoc.Model.Password;
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
    public static final String COLUMN_TOTAL_SIZE_USED = "totalsizeused";
    public static final String COLUMN_PWD = "password";
    public static final String COLUMN_AREA = "adminarea";
    private static DBHelper dbHelper;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
            COLUMN_FIRST_NAME + " TEXT, " +
            COLUMN_LAST_NAME + " TEXT, " +
            COLUMN_TOTAL_SIZE_USED + " TEXT, " +
            COLUMN_PWD + " INTEGER, " +
            COLUMN_AREA + " INTEGER, "+
            " FOREIGN KEY ("+COLUMN_AREA+") REFERENCES "+ AreaSQLHelper.TABLE_NAME+"("+ AreaSQLHelper._ID +
            "))";
    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    public static void setDbHelper(DBHelper Helper) {
        dbHelper = Helper;
    }
    public static long insert(User usr, Password pwd)
    {
        ContentValues values = new ContentValues();
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        values.put(UserSQLHelper.COLUMN_EMAIL, usr.getUser());
        values.put(UserSQLHelper.COLUMN_FIRST_NAME, en.encryptString(usr.getFirstname()));
        values.put(UserSQLHelper.COLUMN_LAST_NAME, en.encryptString(usr.getLastname()));
        values.put(UserSQLHelper.COLUMN_TOTAL_SIZE_USED, en.encryptString(usr.getTotalsizeused()));
        values.put(UserSQLHelper.COLUMN_PWD, usr.getPasswordid());
        values.put(COLUMN_AREA,usr.getAdminareaid());
        long newRowId = UserSQLHelper.getDbHelper().WRITE.insert(UserSQLHelper.TABLE_NAME, null, values);
        return newRowId;
    }
    public static User getRecord(String usrID,Password pwd)
    {
        User user = new User();
        String [] args = {usrID};
        Cursor crs = dbHelper.READ.rawQuery("SELECT * FROM user WHERE email = ?", args);
        if(crs != null && crs.moveToFirst())
        {
            String email = crs.getString(crs.getColumnIndex("email"));
            String fn = crs.getString(crs.getColumnIndex("firstname"));
            String ln = crs.getString(crs.getColumnIndex("lastname"));
            String totSizeUsed = crs.getString(crs.getColumnIndex("totalsizeused"));
            int pwdID = crs.getInt(crs.getColumnIndex("password"));
            int admarea = crs.getInt(crs.getColumnIndex("adminarea"));

            Encryption en = Encryption.getInstance(pwd.getPassword(), pwd.getSalt());
            user.setUser(email);
            user.setFirstname(en.decrypttString(fn));
            user.setLastname(en.decrypttString(ln));
            user.setTotalsizeused(en.decrypttString(totSizeUsed));
            user.setPasswordid(pwdID);
            user.setAdminareaid(admarea);
        }
        return user;
    }
    public static String getUser()
    {
        Cursor crs = dbHelper.READ.rawQuery("SELECT email FROM user ",null);
        String email = new String();
        if(crs != null && crs.moveToFirst()) {
            email = crs.getString(crs.getColumnIndex(UserSQLHelper.COLUMN_EMAIL));
        }
        return email;
    }
    public static int getPWDID(){
        int ID = 0;
        Cursor crs = dbHelper.READ.rawQuery("SELECT password FROM user ",null);
        if(crs != null && crs.moveToFirst()) {
            ID = crs.getInt(crs.getColumnIndex(UserSQLHelper.COLUMN_PWD));
        }
        return ID;
    }
    public static long UpdateRecord(User usr,Password pwd)
    {
        ContentValues values = new ContentValues();
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        values.put(UserSQLHelper.COLUMN_FIRST_NAME, en.encryptString(usr.getFirstname()));
        values.put(UserSQLHelper.COLUMN_LAST_NAME, en.encryptString(usr.getLastname()));
        values.put(UserSQLHelper.COLUMN_TOTAL_SIZE_USED, en.encryptString(usr.getTotalsizeused()));
        values.put(UserSQLHelper.COLUMN_PWD,usr.getPasswordid());
        values.put(UserSQLHelper.COLUMN_AREA,usr.getAdminareaid());
        String [] arg = {usr.getUser()};
        long newRowId = UserSQLHelper.dbHelper.WRITE.update(UserSQLHelper.TABLE_NAME,values,UserSQLHelper.COLUMN_EMAIL+" = ?",arg);
        return newRowId;
    }
    public static long getNumberofRecords()
    {
        String countQuery = "SELECT  * FROM " + UserSQLHelper.TABLE_NAME;
        Cursor cursor = UserSQLHelper.dbHelper.READ.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
    public static int deleteRecord(String user)
    {
        String [] arg = {user};
        int deleted = UserSQLHelper.dbHelper.WRITE.delete(UserSQLHelper.TABLE_NAME,UserSQLHelper.COLUMN_EMAIL + " = ?",arg);
        return deleted;
    }
    public static void clearRecord()
    {
        dbHelper.WRITE.execSQL("delete from "+ TABLE_NAME);
        //dbHelper.WRITE.execSQL("delete from sqlite_sequence where name='" + TABLE_NAME + "'");
    }
}
