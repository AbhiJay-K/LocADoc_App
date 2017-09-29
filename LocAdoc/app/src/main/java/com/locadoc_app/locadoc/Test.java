package com.locadoc_app.locadoc;

import android.os.Bundle;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.locadoc_app.locadoc.DynamoDB.AreaDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper;
import com.locadoc_app.locadoc.DynamoDB.FileDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.PasswordDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.UserDynamoHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        DynamoDBHelper.init(getApplicationContext());
        Bundle extras = getIntent().getExtras();
            test();

    }

    public static void test()
    {
        User user = new User();
        user.setUser("testing1");
        user.setFirstname("999");
        user.setLastname("hehe");
        user.setAdminareaid(12);
        user.setLoggedin("hmm");
        user.setMacaddress("AE:ED:...:FE");
        user.setPasswordid(6);
        //UserDynamoHelper.getInstance().insert(user);
        UserDynamoHelper.getInstance().delete(user);
        //UserDynamoHelper.getInstance().getUser("testing1");


        Password pass = null;
        //for (int i = 0; i < 8; i++) {
            pass = new Password();
            pass.setPassword("998");
            //pass.setPasswordid(i);
        pass.setPasswordid(2);
            pass.setSalt("oiyhnin98354n");
            //PasswordDynamoHelper.getInstance().insert(pass);
            //PasswordDynamoHelper.getInstance().insert(pass);
        //}
        //PasswordDynamoHelper.getInstance().delete(pass);
        //PasswordDynamoHelper.getInstance().getPassword(3);
        //PasswordDynamoHelper.getInstance().getAll(username);

        Area area = new Area();
        area.setAreaId(6);
        area.setLatitude("999");
        area.setLongitude("orkg");
        area.setRadius("rijgrig");
        //AreaDynamoHelper.getInstance().insert(area);
        AreaDynamoHelper.getInstance().delete(area);
        //AreaDynamoHelper.getInstance().getArea(6);
        //AreaDynamoHelper.getInstance().getAll();

        File file = new File();
        file.setAreaId(6);
        file.setCurrentfilename("999");
        file.setFileId(3);
        file.setModified("fniej");
        file.setOriginalfilename("eijfief");
        file.setPasswordId(9);
        //FileDynamoHelper.getInstance().insert(file);
        FileDynamoHelper.getInstance().delete(file);
        //FileDynamoHelper.getInstance().getFile(3);
        //FileDynamoHelper.getInstance().getAll();
    }
}
