package com.locadoc_app.locadoc.LocalDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.locadoc_app.locadoc.Model.Password;

/**
 * Created by AbhiJay_PC on 22/9/2017.
 */

public class DBHelper extends SQLiteOpenHelper{
    //singleton pattern to prevent accidental modification
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LocAdoc_database";
    private static DBHelper dbHelper;
    public static SQLiteDatabase READ;
    public static SQLiteDatabase WRITE;
    //static block initialization for exception handling

    private DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        READ = getReadableDatabase();
        WRITE = getWritableDatabase();
        PasswordSQLHelper.setDbHelper(this);
        AreaSQLHelper.setDbHelper(this);
        UserSQLHelper.setDbHelper(this);
        FileSQLHelper.setDbHelper(this);
    };

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PasswordSQLHelper.CREATE_TABLE);
        sqLiteDatabase.execSQL(UserSQLHelper.CREATE_TABLE);
        sqLiteDatabase.execSQL(AreaSQLHelper.CREATE_TABLE);
        sqLiteDatabase.execSQL(FileSQLHelper.CREATE_TABLE);
        
    }

    // We don't want to delete user data.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FileSQLHelper.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AreaSQLHelper.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserSQLHelper.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    public static void init(Context context){
        if(dbHelper == null){
            dbHelper = new DBHelper(context);
        }
    }

}
