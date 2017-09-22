package com.locadoc_app.locadoc.LocalDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    };
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(User.CREATE_TABLE);
    }

    // We don't want to delete user data.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


}
