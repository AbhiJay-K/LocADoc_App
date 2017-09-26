package com.locadoc_app.locadoc.UI;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.locadoc_app.locadoc.LocalDB.DBHelper;
import com.locadoc_app.locadoc.LocalDB.PasswordSQLHelper;
import com.locadoc_app.locadoc.R;

public class TestDataBase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_data_base);
        DBHelper.init(getApplicationContext());
        //long l = PasswordSQLHelper.insert("testPassword","1231231");
        TextView t = (TextView) findViewById(R.id.test);
        String[] args={"testPassword2"};
        Cursor crs = PasswordSQLHelper.getDbHelper().getReadableDatabase().rawQuery("SELECT * FROM password WHERE password = ?", args);
        crs.moveToFirst();
        String password1 = crs.getString(crs.getColumnIndex("password"));
        String salt1 = crs.getString(crs.getColumnIndex("salt"));
        t.setText(password1 + " " + salt1);
        //crs.moveToNext();
        //String password2 = crs.getString(crs.getColumnIndex("password"));
        //String salt2 = crs.getString(crs.getColumnIndex("salt"));
        //Log.d("DB test",Long.toString(l));
        //Log.d("DB test",password2 + " " + salt2);
    }
}
