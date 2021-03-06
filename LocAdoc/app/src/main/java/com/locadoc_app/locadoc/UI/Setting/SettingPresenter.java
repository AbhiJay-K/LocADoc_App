package com.locadoc_app.locadoc.UI.Setting;

import android.os.AsyncTask;
import android.view.View;

import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.DynamoDB.UserDynamoHelper;
import com.locadoc_app.locadoc.LocalDB.UserSQLHelper;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.User;

/**
 * Created by user on 10/17/2017.
 */

public class SettingPresenter implements SettingPresenterInterface {

    private SettingActivity activity;

    public SettingPresenter(SettingActivity activity) {
        this.activity = activity;
    }

    public void profileName(String firstName,String lastName) {
        String nameInitial = new String();

        // Initial in LastName
        if(!lastName.isEmpty())
            nameInitial = nameInitial.concat(Character.toString(lastName.charAt(0)));

        String[] arrayOfFirst =  firstName.split("\\s+");

        // Initial in FirtName
        for(int i=0; i<arrayOfFirst.length; i++)
            if(!arrayOfFirst[i].isEmpty())
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
            return 2;
        }

        activity.setLabelNameOK("", builderView);
        return 3;
    }

    public void changeToNewName(String firstName, String lastName) {
        new changeNameSyn().execute(firstName, lastName);
    }

    private class changeNameSyn extends
            AsyncTask<String, Void, Void> {

        private String errorMessage;
        private boolean status = true;

        @Override
        protected void onPostExecute(Void result){
            if(status) {
                User newNameUser = getUser();
                String userName = newNameUser.getFirstname().concat(" ").concat(newNameUser.getLastname());
                activity.setUserNameTextView(userName, newNameUser);
            }
            else {
                activity.showDialogMessage("Change Name Error", errorMessage);
            }

            activity.dismissProgresDialog();
        }

        @Override
        protected Void doInBackground(String... objects) {
            //  ------------------------------------------------------------------------
            //                          Update in SQLite
            //  ------------------------------------------------------------------------
            User userDynamo, userSqlite;
            try {
                userDynamo = UserDynamoHelper.getInstance().getUserFromDB(Credential.getEmail());
                userSqlite = UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());

                userDynamo.setFirstname(objects[0]);
                userDynamo.setLastname(objects[1]);

                userSqlite.setFirstname(objects[0]);
                userSqlite.setLastname(objects[1]);
            }
            catch (Exception ex) {

                errorMessage = AppHelper.formatException(ex);
                status = false;

                return null;
            }

            try {
                UserDynamoHelper.getInstance().insertToDB(userDynamo);
                UserSQLHelper.UpdateRecord(userSqlite, Credential.getPassword());
            }
            catch (Exception ex) {
                errorMessage = AppHelper.formatException(ex);
                status = false;

                return null;
            }

            return null;
        };

    }

    public User getUser() {
        User s =UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());
        return s;
    }

    public String getEmail() {
        return Credential.getEmail();
    }
}