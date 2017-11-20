package com.locadoc_app.locadoc.UI.Setting;

import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.DynamoDB.AreaDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.FileDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.PasswordDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.UserDynamoHelper;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.LocalDB.UserSQLHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.helper.CheckPassword;
import com.locadoc_app.locadoc.helper.Encryption;
import com.locadoc_app.locadoc.helper.Hash;

import java.util.List;

/**
 * Created by Dainomix on 10/4/2017.
 */

public class ResetPasswordPresenter implements ResetPasswordPresenterInterface {

    private ResetPassword activity;
    private boolean resultStatus;

    public ResetPasswordPresenter(ResetPassword activity) {
        this.activity = activity;
    }

    // Current Pwd (Old Pwd) Checking
    public int isValidCurPwd() {
        String curPwd = activity.getCurPwd().getText().toString();

        // Check Password Empty
        if(curPwd.isEmpty()) {
            activity.setLabelCurPwd("Password cannot be empty!");
            return 2;
        }

        // Check Password Format
        if(!CheckPassword.PWDCheck(curPwd)) {
            activity.setLabelCurPwd("Password have to be 8-12 characters. Contains A-Z, a-z, 0-9");
            return 1;
        }
        else {
            activity.setLabelCurPwdOK("");
        }

        return 0;
    }

    // New Pwd Checking
    public int isValidNewPwd() {

        String newPwd = activity.getNewPwd().getText().toString();

        // check Password Empty
        if(newPwd.isEmpty()) {
            activity.setLabelNewPwd("Password cannot be empty!");
            return 2;
        }

        // check new Password Format
        if(CheckPassword.PWDCheck(newPwd))
            activity.setLabelNewPwdOK("");
        else
            activity.setLabelNewPwd("Password have to be 8-12 characters. Contains A-Z, a-z, 0-9");

        return 0;
    }

    // Confirm New Pwd with New Pwd
    public int isValidPwdWithNewPwd() {

        String newPwd = activity.getNewPwd().getText().toString();
        String confirmNewPwd = activity.getConfirmNewPwd().getText().toString();

        // Check The difference betweeen new password and Confirm new password
        if(confirmNewPwd.equals(newPwd))
            activity.setLabelConfirmNewPwdOK("");
        else
            activity.setLabelConfirmNewPwd("Confirm Password is not same with New Password");

        return 0;
    }

    public void changePassword() {
        AppHelper.getPool().getUser(Credential.getEmail()).changePasswordInBackground(activity.getCurPwd().getText().toString(),
                activity.getNewPwd().getText().toString(), changePwdHandler);
    }


    GenericHandler changePwdHandler = new GenericHandler() {
        @Override
        public void onSuccess() {

            activity.showProgressDialog("Change Password", "Changing password...");

            /* Update DynamoDB
            for DynamoDB:
                - change password id in user table (use insert)
                - add new password record (use Hash.generateRandomGen for salt, hashed password need salt)  // InsertToDB
            Note in creating new password record:
            new password id: get password id in user table, increment the password id by 1
             */
            String newPassword = activity.getNewPwd().getText().toString();
            new resetPwdSyn().execute(newPassword);

            resultStatus = true;
        }

        @Override
        public void onFailure(Exception exception) {
            // Failure is due to Wrong OldPwd(or Current Pwd)
            resultStatus = false;
            activity.showDialogMessage("Fail","Fail to change password\n" + AppHelper.formatException(exception), resultStatus);
        }
    };

    private class resetPwdSyn extends
            AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void result){
            activity.dismissProgresDialog();
            activity.showDialogMessage("Success","Change password success", resultStatus);
        }

        @Override
        protected Void doInBackground(String... objects) {
            //  ------------------------------------------------------------------------
            //                          Update in DynamoDB
            //  ------------------------------------------------------------------------
            // Get Info of user and pwd
            User user = UserDynamoHelper.getInstance().getUserFromDB(Credential.getEmail());
            Password password = Credential.getPassword();

            int pwdIDUserTable = user.getPasswordid() + 1;
            int pwdIDPwdTable = password.getPasswordid() + 1;

            // Check same Value B2 user and Password?
            // IF Different, Priority b2 User and Password: User
            if(pwdIDUserTable != pwdIDPwdTable) {
                pwdIDPwdTable = pwdIDUserTable;
            }

            // Update passwordID in User Table
            user.setPasswordid(pwdIDUserTable);

            // Update passwordID and new Password with new salt in Password Table
            Password newPassword = new Password();
            newPassword.setPasswordid(pwdIDPwdTable);
            String salt = Hash.SecureRandomGen();
            newPassword.setSalt(salt);
            newPassword.setPassword(Hash.Hash(objects[0], salt));

            // Update password in DynamoDB
            PasswordDynamoHelper.getInstance().insert(newPassword);

            //  ------------------------------------------------------------------------
            //              Update in SQLite, Encryption, Credential
            //  ------------------------------------------------------------------------

            // CLEAR LOCAL DATABASE: AREA and FILE
            AreaSQLHelper.clearRecord();
            FileSQLHelper.clearRecord();

            // GET ENCRYPTION based on Current Password
            Encryption en = Encryption.getInstance(password.getPassword(), password.getSalt());

            // AREA DECRYPTION
            List<Area> areaList = AreaDynamoHelper.getInstance().getAllArea();
            for(Area ar : areaList) {
                ar.setDescription(en.decrypttString(ar.getDescription()));
                ar.setLatitude(en.decrypttString(ar.getLatitude()));
                ar.setLongitude(en.decrypttString(ar.getLongitude()));
                ar.setRadius(en.decrypttString(ar.getRadius()));
                ar.setName(en.decrypttString(ar.getName()));
            }

            // FILE DECRYPTION
            List<File> fileList = FileDynamoHelper.getInstance().getAllFile();
            for(File file : fileList) {
                file.setCurrentfilename(en.decrypttString(file.getCurrentfilename()));
                file.setOriginalfilename(en.decrypttString(file.getOriginalfilename()));
                file.setBackedup(en.decrypttString(file.getBackedup()));
                file.setFilesize(en.decrypttString(file.getFilesize()));
            }

            // Get User Data from Local DB
            User userInSQLite = UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());

            // SET New Encryption Key AS New Password
            en.setKey(newPassword.getPassword(), newPassword.getSalt());

            // Update and Insert user data encrypted with newPassword
            userInSQLite.setPasswordid(userInSQLite.getPasswordid() + 1);
            UserSQLHelper.UpdateRecord(userInSQLite, newPassword);

            // Update user dynamo
            UserDynamoHelper.getInstance().insert(user);

            // ReEncryption AREA and FILE
            for(Area ar : areaList) {
                // encryption
                ar.setDescription(en.encryptString(ar.getDescription()));
                ar.setLatitude(en.encryptString(ar.getLatitude()));
                ar.setLongitude(en.encryptString(ar.getLongitude()));
                ar.setRadius(en.encryptString(ar.getRadius()));
                ar.setName(en.encryptString(ar.getName()));

                // INSERT AREA INTO localDB
                AreaSQLHelper.insertWithoutEncryption(ar,newPassword);

                // INSERT Area INTO DynamoDB
                AreaDynamoHelper.getInstance().insertToDBWithoutEncryption(ar);
            }

            for(File file : fileList) {
                // encryption
                file.setCurrentfilename(en.encryptString(file.getCurrentfilename()));
                file.setOriginalfilename(en.encryptString(file.getOriginalfilename()));
                file.setBackedup(en.encryptString(file.getBackedup()));
                file.setFilesize(en.encryptString(file.getFilesize()));

                // INSERT FILE INTO localDB
                FileSQLHelper.insertWithoutEncryption(file, Credential.getPassword());

                // INSERT FILE INTO DynamoDB
                FileDynamoHelper.getInstance().insertToDBWithoutEncryption(file);
            }

            // Update in Credential in App
            Credential.addAnOldPass(Credential.getPassword());
            Credential.setPassword(newPassword);

            return null;
        }
    }
}
