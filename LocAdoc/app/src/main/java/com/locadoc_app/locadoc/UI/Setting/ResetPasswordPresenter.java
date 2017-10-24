package com.locadoc_app.locadoc.UI.Setting;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
import com.locadoc_app.locadoc.UI.ConfirmSignUp.Activity_SignUp_Confirm;
import com.locadoc_app.locadoc.helper.CheckPassword;
import com.locadoc_app.locadoc.helper.Encryption;
import com.locadoc_app.locadoc.helper.Hash;

import java.util.List;
import java.util.Map;

import static android.R.attr.description;
import static android.R.attr.password;
import static java.sql.DriverManager.println;

/**
 * Created by Dainomix on 10/4/2017.
 */

public class ResetPasswordPresenter {

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
            activity.setLabelCurPwd("PasswordSQLHelper: cannot be empty!");
            return 2;
        }

        // Check Password Format
        if(!CheckPassword.PWDCheck(curPwd)) {
            activity.setLabelCurPwd("PasswordSQLHelper: have to be 8-12 characters. Contains A-Z, a-z, 0-9");
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
            activity.setLabelNewPwd("PasswordSQLHelper: cannot be empty!");
            return 2;
        }

        // check new Password Format
        if(CheckPassword.PWDCheck(newPwd))
            activity.setLabelNewPwdOK("");
        else
            activity.setLabelNewPwd("PasswordSQLHelper: have to be 8-12 characters. Contains A-Z, a-z, 0-9");

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
            activity.setLabelConfirmNewPwd("PasswordSQLHelper: Confirm Password is not same with New Password");

        return 0;
    }

    public void changePassword() {
        // oldUserPassword, newUserPassword, CallBack:GeneralHandler
        Log.d("APPHELPER CALL", "changePassword (" + activity.getCurPwd().getText().toString() + ", " + activity.getNewPwd().getText().toString() + ", Handler) And the Credential PWD " + Credential.getPassword());
        // Should password be hashed one?
        // Old Pwd as Credential
        // activity.showProgressDialog("Reset Password","Changing Password...");

        AppHelper.getPool().getUser(Credential.getEmail()).changePasswordInBackground(activity.getCurPwd().getText().toString(), activity.getNewPwd().getText().toString(), changePwdHandler);
        Log.d("UPDATE IN COGNITO","UPDATE IN " + Credential.getEmail() + " | OLDPWD: " +  activity.getCurPwd().getText().toString() + " | NEWPWD " + activity.getNewPwd().getText().toString());

        // activity.dismissProgresDialog();

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
            Log.d("SUCCESS_RESET_PWD","UPDATE IN DYNAMODB IN CLOUD SERVER");
            Log.d("SEPERATE", "SUCCESS--------------------------------------------------------------------------------");

            String newPassword = activity.getNewPwd().getText().toString();
            new resetPwdSyn().execute(newPassword);

            resultStatus = true;
        }

        @Override
        public void onFailure(Exception exception) {
            // Failure is due to Wrong OldPwd(or Current Pwd)
            // activity.setLabelCurPwd("PasswordSQLHelper: incorrect Current Password");
            Log.d("FAIL_RESET_PWD","UNMATCH OLD PWD");
            Log.d("EXCEPTION MESSAGE", exception.toString());
            Log.d("SEPERATE", "FAIL--------------------------------------------------------------------------------");

            activity.setLabelCurPwd("PasswordSQLHelper: incorrect Current Password");

            resultStatus = false;

            activity.showDialogMessage("FAIL","FAIL TO CHANGE PASSWORD\n" + AppHelper.formatException(exception), resultStatus);
        }
    };

    private class resetPwdSyn extends
            AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void result){
            activity.dismissProgresDialog();
            activity.showDialogMessage("SUCCESS","SUCCESS TO CHANGE PASSWORD", resultStatus);
            //super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(String... objects) {
            //  ------------------------------------------------------------------------
            //                          Update in DynamoDB
            //  ------------------------------------------------------------------------
            // Get Info of user and pwd
            User user = UserDynamoHelper.getInstance().getUserFromDB(Credential.getEmail());
            Password password = Credential.getPassword();

            int pwdIDUserTable = user.getPasswordid();
            int pwdIDPwdTable = password.getPasswordid();

            Log.d("PWDID IN USER", "User Password ID in USER TABLE: " + pwdIDUserTable++);
            Log.d("PWDID IN USER", "User Password ID in PASSWORD TABLE: " + pwdIDPwdTable++);

            // Check same Value B2 user and Password?
            // IF Different, Priority b2 User and Password: User
            if(pwdIDUserTable != pwdIDPwdTable) {
                pwdIDPwdTable = pwdIDUserTable;
                Log.d("PWDID UNMATCH", "Password ID in USER TABLE and PASSWORD TABLE is NOT MATCH");
            }

            // Update passwordID in User Table
            user.setPasswordid(pwdIDUserTable);

            // Update passwordID and new Password with new salt in Password Table
            Password newPassword = new Password();
            newPassword.setPasswordid(pwdIDPwdTable);
            String salt = Hash.SecureRandomGen();
            newPassword.setSalt(salt);
            newPassword.setPassword(Hash.Hash(objects[0], salt));

            // Update user and password in DynamoDB
            PasswordDynamoHelper.getInstance().insert(newPassword);
            UserDynamoHelper.getInstance().insert(user);

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
                file.setModified(en.decrypttString(file.getModified()));
            }

            // Get User Data from Local DB
            Log.d("SQLITEHELPER","BEFORE UPDATE, NUMBER OF USER Data: " + UserSQLHelper.getNumberofRecords());
            User userInSQLite = UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());
            Log.d("SQLITEHELPER","User Email: " + userInSQLite.getUser() + " | User Name: " + userInSQLite.getLastname() + " " + userInSQLite.getFirstname());

            // SET New Encryption Key AS New Password
            en.setKey(newPassword.getPassword(), newPassword.getSalt());

            // Update and Insert user data encrypted with newPassword
            userInSQLite.setPasswordid(userInSQLite.getPasswordid() + 1);
            UserSQLHelper.UpdateRecord(userInSQLite, newPassword);
            Log.d("SQLITEHELPER","AFTER UPDATE, NUMBER OF USER Data: " + UserSQLHelper.getNumberofRecords());

            User confrimUser = UserSQLHelper.getRecord(Credential.getEmail(), newPassword);
            Log.d("SQLITEHELPER","User Email: " + confrimUser.getUser() + " | User Name: " + confrimUser.getLastname() + " " + confrimUser.getFirstname());

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
                AreaDynamoHelper.getInstance().insert(ar);
            }

            for(File file : fileList) {
                // encryption
                file.setCurrentfilename(en.encryptString(file.getCurrentfilename()));
                file.setOriginalfilename(en.encryptString(file.getOriginalfilename()));
                file.setModified(en.encryptString(file.getModified()));

                // INSERT FILE INTO localDB
                FileSQLHelper.insertWithoutEncryption(file, Credential.getPassword());

                // INSERT FILE INTO DynamoDB
                FileDynamoHelper.getInstance().insert(file);
            }

            Log.d("BEFORE UPDATE CRDL", "OLD PWDID: " + Credential.getPassword().getPasswordid() + "| OLD PWD: " + Credential.getPassword().getPassword() + " | OLD SALT" + Credential.getPassword().getSalt());

            Log.d("CREDENTIALCHECK","BEFORE CHANGE PASSWORD Email: " + Credential.getEmail() + "\t Password: " + Credential.getPassword().getPassword());

            // Update in Credential in App
            Credential.setPassword(newPassword);

            Log.d("AFTER UPDATE CRDL", "NEW PWDID: " + Credential.getPassword().getPasswordid() + "| NEW PWD: " + Credential.getPassword().getPassword() + " | NEW SALT" + Credential.getPassword().getSalt());
            Log.d("CREDENTIALCHECK","AFTER CHANGE PASSWORD Email: " + Credential.getEmail() + "\t Password: " + Credential.getPassword().getPassword());

            return null;
        };


    }

}
