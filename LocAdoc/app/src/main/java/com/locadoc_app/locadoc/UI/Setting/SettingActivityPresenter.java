package com.locadoc_app.locadoc.UI.Setting;


import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.DynamoDB.AreaDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper;
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
import com.locadoc_app.locadoc.helper.CheckPassword;
import com.locadoc_app.locadoc.helper.Encryption;
import com.locadoc_app.locadoc.helper.Hash;

import java.util.List;

/**
 * Created by user on 10/17/2017.
 */

public class SettingActivityPresenter {

    private SettingActivity activity;

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

    public int validName(String firstName, String lastName, View builderView) {
        if(firstName.isEmpty()) {
            if(lastName.isEmpty())
                activity.setLabelFirstLastName("First and Last Name should not be Empty!", builderView);
            else
                activity.setLabelFirstName("First Name should not be Empty!", builderView);

            return 0;
        }

        if(lastName.isEmpty()) {
            if(firstName.isEmpty())
                activity.setLabelFirstLastName("First and Last Name should not be Empty!", builderView);
            else
                activity.setLabelLastName("Last Name should not be Empty!", builderView);

            return 1;
        }

        User user = getUser();
        if(firstName.equals(user.getFirstname()) && lastName.equals(user.getLastname())) {
            activity.setLabelNameOK("", builderView);
            Log.d("CHANGENAME", "SAMENAME");
            return 2;
        }

        activity.setLabelNameOK("", builderView);
        return 3;
    }

    public void changeToNewName(String firstName, String lastName, View builderView) {
        new changeNameSyn().execute(firstName, lastName);
    }

    private class changeNameSyn extends
            AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void result){
            User newNameUser = getUser();
            String userName = newNameUser.getFirstname().concat(" ").concat(newNameUser.getLastname());
            activity.setUserNameTextView(userName, newNameUser);

            activity.dismissProgresDialog();
        }

        @Override
        protected Void doInBackground(String... objects) {
            //  ------------------------------------------------------------------------
            //                          Update in SQLite
            //  ------------------------------------------------------------------------
            // user = UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());
            User user = getUser();
            user.setFirstname(objects[0]);
            user.setLastname(objects[1]);
            UserSQLHelper.UpdateRecord(user, Credential.getPassword());
            UserDynamoHelper.getInstance().insertToDB(user);

            return null;
        };

    }

    public User getUser() {
        return UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());
    }



}