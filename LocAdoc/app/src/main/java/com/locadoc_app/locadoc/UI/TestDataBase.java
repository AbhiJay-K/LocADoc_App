package com.locadoc_app.locadoc.UI;

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

        Password pwd2 = PasswordSQLHelper.getRecord(1);
        Log.d("testing passwordID",Integer.toString(pwd2.getPasswordid()));
        Log.d("testing password",pwd2.getPassword());
        Log.d("testing password",pwd2.getSalt());
        //===========================user=====================
        User usr = new User();
        usr.setUser("kabhijay@gmail.com");
        usr.setFirstname("Abhi Jay");
        usr.setLastname("Krishnan");
        usr.setMacaddress("1234234sdf");
        usr.setLoggedin("1");
        usr.setPasswordid(pwd2.getPasswordid());
        usr.setAdminareaid(1);
        Log.d("testing Add User",usr.getUser());
        Log.d("testing Add User",usr.getFirstname());
        Log.d("testing Add User",usr.getLastname());
        Log.d("testing Add User",usr.getMacaddress());
        Log.d("testing Add User",usr.getLoggedin());
        Log.d("testing Add User",Integer.toString(usr.getPasswordid()));
        Log.d("testing Add User",Integer.toString(usr.getAdminareaid()));

        //inserting user
        long l2 = UserSQLHelper.insert(usr);
        Log.d("User added ",Long.toString(l2));
        String [] args2 = {"kabhijay@gmail.com"};
        User s = UserSQLHelper.getRecord("kabhijay@gmail.com");
        Log.d("After adding user",s.getUser());
        Log.d("After adding user",s.getFirstname());
        Log.d("After adding user",s.getLastname());
        Log.d("After adding user",s.getLoggedin());
        Log.d("After adding user",s.getMacaddress());
        Log.d("After adding user",Integer.toString(s.getAdminareaid()));
        Log.d("After adding user",Integer.toString(s.getPasswordid()));
        //====================================================
        //crs.moveToNext();
        //String password2 = crs.getString(crs.getColumnIndex("password"));
        //String salt2 = crs.getString(crs.getColumnIndex("salt"));
        //Log.d("DB test",Long.toString(l));
        //Log.d("DB test",password2 + " " + salt2);
    }
}
