package com.locadoc_app.locadoc.UI;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.locadoc_app.locadoc.LocalDB.DBHelper;
import com.locadoc_app.locadoc.LocalDB.PasswordSQLHelper;
import com.locadoc_app.locadoc.LocalDB.UserSQLHelper;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.R;

public class TestDataBase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_data_base);
        getApplicationContext().deleteDatabase("LocAdoc_database");
        DBHelper.init(getApplicationContext());
        //long l = PasswordSQLHelper.insert("testPassword","1231231");
        TextView t = (TextView) findViewById(R.id.test);

        //======================password========================
        Password p = new Password();
        p.setPassword("TestPWD");

        long l = PasswordSQLHelper.insert(p);
        Log.d("Password added ",Long.toString(l));

        String password1 = crs.getString(crs.getColumnIndex("password"));
        String salt1 = crs.getString(crs.getColumnIndex("salt"));
        Log.d("testing _ID", Integer.toString(crs.getInt(crs.getColumnIndex("_id"))));
        Log.d("testing password",password1);
        Log.d("testing salt",salt1);

        //===========================user=====================
        User usr = new User();
        usr.setUser("kabhijay@gmail.com");
        usr.setFirstname("Abhi Jay");
        usr.setLastname("Krishnan");
        usr.setMacaddress("1234234sdf");
        usr.setLoggedin("1");
        usr.setPasswordid(Integer.toString(crs.getInt(crs.getColumnIndex("_id"))));
        usr.setAdminareaid("");
        Log.d("testing Add User",usr.getUser());
        Log.d("testing Add User",usr.getFirstname());
        Log.d("testing Add User",usr.getLastname());
        Log.d("testing Add User",usr.getMacaddress());
        Log.d("testing Add User",usr.getLoggedin());
        Log.d("testing Add User",usr.getPasswordid());
        Log.d("testing Add User",usr.getAdminareaid());

        //inserting user
        long l2 = UserSQLHelper.insert(usr);
        Log.d("User added ",Long.toString(l2));
        String [] args2 = {"kabhijay@gmail.com"};

        //reading from database
        Cursor crs2 = UserSQLHelper.getDbHelper().READ.rawQuery("SELECT * FROM user WHERE email = ?", args2);
        Log.d("Number of data",Integer.toString(crs2.getCount()));
        crs2.moveToFirst();
        String email = crs2.getString(crs2.getColumnIndex("email"));
        String fn = crs2.getString(crs2.getColumnIndex("firstname"));
        String ln = crs2.getString(crs2.getColumnIndex("lastname"));
        String logIn = crs2.getString(crs2.getColumnIndex("logedin"));
        String Mcadd = crs2.getString(crs2.getColumnIndex("macaddress"));
        int pwdID = crs2.getInt(crs2.getColumnIndex("password"));
        int admarea = crs2.getInt(crs2.getColumnIndex("adminarea"));
        Log.d("After adding user",email);
        Log.d("After adding user",fn);
        Log.d("After adding user",ln);
        Log.d("After adding user",Mcadd);
        Log.d("After adding user",logIn);
        Log.d("After adding user",Integer.toString(admarea));
        Log.d("After adding user",Integer.toString(pwdID));
        //====================================================
        //crs.moveToNext();
        //String password2 = crs.getString(crs.getColumnIndex("password"));
        //String salt2 = crs.getString(crs.getColumnIndex("salt"));
        //Log.d("DB test",Long.toString(l));
        //Log.d("DB test",password2 + " " + salt2);
    }
}
