package com.locadoc_app.locadoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper;
import com.locadoc_app.locadoc.DynamoDB.UserDynamoHelper;
import com.locadoc_app.locadoc.Model.User;

public class Test extends AppCompatActivity {

    private static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Bundle extra = getIntent().getExtras();
        username = extra.getString("name");
        DynamoDBHelper.init(getApplicationContext());
        //test();
    }

    public static void test()
    {
        User user = new User();
        user.setUser(username);
        user.setFirstname("hoho");
        user.setLastname("hehe");
        user.setAdminareaid(1);
        user.setLoggedin("hmm");
        user.setMacaddress("AE:ED:...:FE");
        user.setPasswordid(1);
        UserDynamoHelper.getInstance().insert(user);
    }
}
