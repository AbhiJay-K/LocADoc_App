package com.locadoc_app.locadoc.UI.Setting;


import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.DynamoDB.AreaDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.FileDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.PasswordDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.UserDynamoHelper;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.LocalDB.UserSQLHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.S3.S3Helper;

import java.util.List;

/**
 * Created by user on 10/17/2017.
 */

public class SettingActivityPresenter {

    private SettingActivity activity;
    private User user;

    public SettingActivityPresenter(SettingActivity activity) {
        this.activity = activity;
    }

    public void profileName(String firstName,String lastName) {
        String nameInitial = "";
        if(!lastName.isEmpty()) {
            nameInitial = Character.toString(lastName.charAt(0));
        }
        String[] arrayOfFirst =  firstName.split("\\s+");

        for(int i=0; i<arrayOfFirst.length; i++)
            nameInitial = nameInitial.concat(Character.toString(arrayOfFirst[i].charAt(0)));

        activity.setProfileInitial(nameInitial);
    }
}