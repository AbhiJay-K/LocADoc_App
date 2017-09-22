package com.locadoc_app.locadoc.LocalDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;

/**
 * Created by AbhiJay_PC on 22/9/2017.
 */

public class DBHelper extends SQLiteOpenHelper{
    //singleton pattern to prevent accidental modification
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LocAdoc_database";
    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        User.setDbHelper(this);
        Password.setDbHelper(this);
        Area.setDbHelper(this);
        File.setDbHelper(this);
    };
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Password.CREATE_TABLE);
        sqLiteDatabase.execSQL(User.CREATE_TABLE);
        sqLiteDatabase.execSQL(Area.CREATE_TABLE);
        sqLiteDatabase.execSQL(File.CREATE_TABLE);
    }

    // We don't want to delete user data.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


}
