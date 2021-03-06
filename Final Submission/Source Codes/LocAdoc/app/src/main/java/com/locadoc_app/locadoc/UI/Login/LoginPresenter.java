package com.locadoc_app.locadoc.UI.Login;

import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.DynamoDB.AreaDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper;
import com.locadoc_app.locadoc.DynamoDB.FileDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.PasswordDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.UserDynamoHelper;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.LocalDB.ApplicationInstance;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.LocalDB.GuestSession;
import com.locadoc_app.locadoc.LocalDB.UserSQLHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.helper.CheckPassword;
import com.locadoc_app.locadoc.helper.EmailValidation;
import com.locadoc_app.locadoc.helper.Encryption;
import com.locadoc_app.locadoc.helper.Hash;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Admin on 9/12/2017.
 */

public class LoginPresenter implements LoginPresenterInterface
{
    private LoginViewInterface loginAct;
    //Continuations
    private NewPasswordContinuation newPasswordContinuation;
    private String password;
    private Password newPass;

    public LoginPresenter (LoginViewInterface loginAct)
    {
        this.loginAct = loginAct;
    }

    @Override
    public void onLoginClick(String id, String password)
    {
        boolean isValid = true;
        if (!onChangeID(id) || !onChangePassword(password))
            isValid = false;

        if (isValid)
        {
            loginAct.showWaitDialog("Signing in...");
            String username = loginAct.getUserIDView().getText().toString();
            AppHelper.getPool().getUser(username).getSessionInBackground(authenticationHandler);
        }
    }

    @Override
    public void onForgotPasswordClick(String id)
    {
        if(onChangeID(id))
        {
            loginAct.openForgotPasswordActivity();
        }

    }

    @Override
    public boolean onChangeID(String id){
        if (!EmailValidation.isValidEmail(id)){
            loginAct.setIDError("Invalid Email Address");
            return false;
        }

        return true;
    }

    @Override
    public boolean onChangePassword(String pass){
        if (!CheckPassword.PWDCheck(pass)){
            loginAct.setPassError();
            return false;
        }

        return true;
    }

    public void continueWithFirstTimeSignIn() {
        newPasswordContinuation.setPassword(AppHelper.getPasswordForFirstTimeLogin());
        Map<String, String> newAttributes = AppHelper.getUserAttributesForFirstTimeLogin();
        if (newAttributes != null) {
            for(Map.Entry<String, String> attr: newAttributes.entrySet()) {
                newPasswordContinuation.setUserAttribute(attr.getKey(), attr.getValue());
            }
        }
        try {
            newPasswordContinuation.continueTask();
        } catch (Exception e) {
            loginAct.closeWaitDialog();
            loginAct.showDialogMessage("Sign-in failed", AppHelper.formatException(e));
        }
    }

    GetDetailsHandler handler = new GetDetailsHandler() {
        @Override
        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
            String given_name = cognitoUserDetails.getAttributes().getAttributes().get("given_name");
            String email = cognitoUserDetails.getAttributes().getAttributes().get("email");
            String last_name = cognitoUserDetails.getAttributes().getAttributes().get("family_name");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
            String TimeStamp = simpleDateFormat.format(new Date());
            String random =  UUID.randomUUID().toString();
            String Instance = Hash.Hash(TimeStamp,random);
            ApplicationInstance.insert(Instance);

            if(Credential.getPassword() == null){
                Password p = new Password();
                String salt = Hash.SecureRandomGen();
                String pwdDigest = Hash.Hash(password, salt);
                p.setPasswordid(1);
                p.setPassword(pwdDigest);
                p.setSalt(salt);
                Credential.setPassword(p);
            }

            User usr = new User();
            usr.setUser(email);
            usr.setFirstname(given_name);
            usr.setLastname(last_name);
            usr.setPasswordid(1);
            UserSQLHelper.insert(usr,Credential.getPassword());

            Credential.setPassword(newPass);
            new DBSynchronise().execute(password);
        }

        @Override
        public void onFailure(Exception exception) {
            loginAct.closeWaitDialog();
            loginAct.showDialogMessage("Sign-in failed", AppHelper.formatException(exception));
        }
    };

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            GuestSession.ResetReacord();
            loginAct.closeWaitDialog();
            loginAct.startProgressDialog();
            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.newDevice(device);
            Credential.setEmail(loginAct.getUserIDView().getText().toString());
            loginAct.checkLogin();
            new CheckDetails().execute();
        }


        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            Locale.setDefault(Locale.US);
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(
                    loginAct.getUserIDView().getText().toString(),
                    loginAct.getPassView().getText().toString(), null);

            password = loginAct.getPassView().getText().toString();
            loginAct.getPassView().setText("");
            loginAct.clearFocus();

            authenticationContinuation.setAuthenticationDetails(authenticationDetails);
            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {}

        @Override
        public void onFailure(Exception e) {
            loginAct.closeWaitDialog();

            if (AppHelper.formatException(e).equals("User is not confirmed. ")){
                loginAct.confirmUser();
            }
            else {
                //Add delay if the user guest try password many times
                long [] grecord = GuestSession.getRecord();
                grecord[0]++;
                GuestSession.updateNumTries(grecord);
                if(grecord[0] >= 3L)
                {
                    if(grecord[1] <= 180000L) {
                        grecord[1] = 10000L * (grecord[0] - 2);
                        GuestSession.updateNumTries(grecord);
                    }
                    else{
                        grecord[1] = 180000L;
                        GuestSession.updateNumTries(grecord);
                    }
                    loginAct.startDelay();
                }
                else{
                    loginAct.showDialogMessage("Sign-in failed", AppHelper.formatException(e));
                }
            }

        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            /**
             * For Custom authentication challenge, implement your logic to present challenge to the
             * user and pass the user's responses to the continuation.
             */
            if ("NEW_PASSWORD_REQUIRED".equals(continuation.getChallengeName())) {
                // This is the first sign-in attempt for an admin created user
                newPasswordContinuation = (NewPasswordContinuation) continuation;
                AppHelper.setUserAttributeForDisplayFirstLogIn(newPasswordContinuation.getCurrentUserAttributes(),
                        newPasswordContinuation.getRequiredAttributes());
                loginAct.closeWaitDialog();
                loginAct.firstTimeSignIn();
            }
        }
    };

    private class CheckDetails extends
            AsyncTask<Void, Void, Void> {
        boolean getDetails;

        @Override
        protected Void doInBackground(Void... objects) {
            DynamoDBHelper.init(LocAdocApp.getContext());
            newPass = Credential.getPassword();
            User usr = UserDynamoHelper.getInstance().getUserFromDB(Credential.getEmail());
            long numberUser = UserSQLHelper.getNumberofRecords();

            if(usr == null && numberUser <= 0){
                getDetails = true;
            } else{
                getDetails = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(getDetails){
                AppHelper.getPool().getCurrentUser().getDetailsInBackground(handler);
            } else{
                Credential.setPassword(newPass);
                new DBSynchronise().execute(password);
            }
        }
    }

    private class DBSynchronise extends
            AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... objects) {
            DynamoDBHelper.init(LocAdocApp.getContext());

            Password newCredentialPwd = new Password();
            if(Credential.getPassword() != null && Credential.getPassword().getPasswordid() == -1)
                newCredentialPwd = Credential.getPassword();

            // CREDENTIAL Password is changed to Old Password in getUerFromDB in UerDynamoHelper
            User usr = UserDynamoHelper.getInstance().getUserFromDB(Credential.getEmail());
            long numberUser = UserSQLHelper.getNumberofRecords();

            //-------- Common Login
                if(numberUser > 0 &&  usr != null) {
                    // ---------------------------------------------------------------------------------------------
                    //                          RESET PASSWORD IN COMMON LOGIN
                    // ---------------------------------------------------------------------------------------------
                    // Current Credential Password: old Password Info
                    if(Credential.getPassword() != null && newCredentialPwd.getPasswordid() == -1) {
                        Encryption en = Encryption.getInstance(Credential.getPassword().getPassword(), Credential.getPassword().getSalt());
                        int oldPwdID = Credential.getPassword().getPasswordid();

                        FileSQLHelper.clearRecord();
                        AreaSQLHelper.clearRecord();

                        // ---------------------------------------------------------------------------------------------
                        //                              SQLITE (LOCAL DATABASE) DECRYPTION
                        // ---------------------------------------------------------------------------------------------
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

                        // ---------------------------------------------------------------------------------------------
                        //                       DYNAMODB AND SQLITE ENCRYPTION AND UPDATE
                        // ---------------------------------------------------------------------------------------------

                        // SET New Password into Credential and New Encryption Key AS New Password
                        Credential.setPassword(newCredentialPwd);
                        en.setKey(Credential.getPassword().getPassword(), Credential.getPassword().getSalt());

                        // UPDATE Password ID Credential based on DYNAMODB Password ID
                        Credential.getPassword().setPasswordid(oldPwdID + 1);

                        // Update SQLite: user with new password ID encrypted by new Password
                        // Update DynamoDB: user with new password ID and new Password
                        userInSQLite.setPasswordid(oldPwdID + 1);
                        UserSQLHelper.UpdateRecord(userInSQLite, Credential.getPassword());
                        userInSQLite.setInstanceID(ApplicationInstance.getRecord());

                        // ===================================================================================== Check
                        UserDynamoHelper.getInstance().insert(userInSQLite);
                        PasswordDynamoHelper.getInstance().insert(Credential.getPassword());

                        // ReEncryption AREA and FILE
                        for(Area ar : areaList) {
                            // encryption
                            ar.setDescription(en.encryptString(ar.getDescription()));
                            ar.setLatitude(en.encryptString(ar.getLatitude()));
                            ar.setLongitude(en.encryptString(ar.getLongitude()));
                            ar.setRadius(en.encryptString(ar.getRadius()));
                            ar.setName(en.encryptString(ar.getName()));

                            // INSERT AREA INTO localDB
                            AreaSQLHelper.insertWithoutEncryption(ar,Credential.getPassword());

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

                        // ---------------------------------------------------------------------------------------------
                        //                Common Login Process with Different Device After ForgetPassword
                        // ---------------------------------------------------------------------------------------------

                    }
                    else {
                        Password pwd = PasswordDynamoHelper.getInstance().getPasswordFromDB(usr.getPasswordid());
                        Credential.setPassword(pwd);
                        String instanceId = ApplicationInstance.getRecord();

                        if(!instanceId.equals(usr.getInstanceID())){
                            FileSQLHelper.clearRecord();
                            AreaSQLHelper.clearRecord();

                            List<Area> areaList = AreaDynamoHelper.getInstance().getAllArea();
                            for(Area ar : areaList) {
                                AreaSQLHelper.insertWithoutEncryption(ar,Credential.getPassword());
                            }

                            List<File> fileList = FileDynamoHelper.getInstance().getAllFile();
                            for(File file : fileList) {
                                FileSQLHelper.insertWithoutEncryption(file, Credential.getPassword());
                            }

                            usr.setInstanceID(instanceId);
                            UserSQLHelper.UpdateRecord(usr, Credential.getPassword());
                            UserDynamoHelper.getInstance().insert(usr);
                        }
                    }
            }   //-------- First Login
            else if(usr == null && numberUser > 0){
                User newusr = UserSQLHelper.getRecord(Credential.getEmail(),Credential.getPassword());
                String instance = ApplicationInstance.getRecord();
                newusr.setInstanceID(instance);
                UserDynamoHelper.getInstance().insert(newusr);

                if(Credential.getPassword() == null){
                    Password p = new Password();
                    String salt = Hash.SecureRandomGen();
                    String pwdDigest = Hash.Hash(objects[0],salt);
                    p.setPasswordid(1);
                    p.setPassword(pwdDigest);
                    p.setSalt(salt);
                    Credential.setPassword(p);
                }

                PasswordDynamoHelper.getInstance().insert(Credential.getPassword());
            }   //-------- Common Login with Different Device
            else if(usr != null && numberUser <= 0) {
                // ---------------------------------------------------------------------------------------------
                //                     RESET PASSWORD IN COMMON LOGIN WITH DIFFERENT DEVICE
                // ---------------------------------------------------------------------------------------------

                if(Credential.getPassword() != null && newCredentialPwd.getPasswordid() == -1) {
                        // User usr = UserDynamoHelper.getInstance().getUserFromDB(Credential.getEmail());
                        // long numberUser = UserSQLHelper.getNumberofRecords();
                        // ---------------------------------------------------------------------------------------------
                        //                                  UPDATE USER FROM DYNAMODB
                        // ---------------------------------------------------------------------------------------------
                        //Password oldPwdDynamoDB = PasswordDynamoHelper.getInstance().getPasswordFromDB(usr.getPasswordid());
                        Encryption en = Encryption.getInstance(Credential.getPassword().getPassword(), Credential.getPassword().getSalt());

                        // Update new InstanceID into User
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                        String TimeStamp = simpleDateFormat.format(new Date());
                        String random =  UUID.randomUUID().toString();
                        String Instance = Hash.Hash(TimeStamp,random);
                        ApplicationInstance.insert(Instance);
                        usr.setInstanceID(Instance);

                        FileSQLHelper.clearRecord();
                        AreaSQLHelper.clearRecord();

                        // ---------------------------------------------------------------------------------------------
                        //                              SQLITE (LOCAL DATABASE) DECRYPTION
                        // ---------------------------------------------------------------------------------------------
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

                        // ---------------------------------------------------------------------------------------------
                        //                       DYNAMODB AND SQLITE ENCRYPTION AND UPDATE
                        // ---------------------------------------------------------------------------------------------
                        // SET New Encryption Key AS New Password
                        Credential.setPassword(newCredentialPwd);

                        en.setKey(Credential.getPassword().getPassword(), Credential.getPassword().getSalt());

                        // UPDATE Password ID Credential based on DYNAMODB Password ID
                        Credential.getPassword().setPasswordid(usr.getPasswordid() + 1);

                        // Update SQLite: user with new password ID encrypted by new Password
                        // Update DynamoDB: user with new password ID and new Password
                        usr.setPasswordid(usr.getPasswordid() + 1);

                        // ===================================================================================== Check
                        UserSQLHelper.insert(usr, Credential.getPassword());
                        UserDynamoHelper.getInstance().insertToDB(usr);

                        PasswordDynamoHelper.getInstance().insert(Credential.getPassword());

                        // ReEncryption AREA and FILE
                        for(Area ar : areaList) {
                            // encryption
                            ar.setDescription(en.encryptString(ar.getDescription()));
                            ar.setLatitude(en.encryptString(ar.getLatitude()));
                            ar.setLongitude(en.encryptString(ar.getLongitude()));
                            ar.setRadius(en.encryptString(ar.getRadius()));
                            ar.setName(en.encryptString(ar.getName()));

                            // INSERT AREA INTO localDB
                            AreaSQLHelper.insertWithoutEncryption(ar,Credential.getPassword());

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
                        // ---------------------------------------------------------------------------------------------
                        //                Common Login Process with Different Device After ForgetPassword
                        // ---------------------------------------------------------------------------------------------
                    }
                    else {
                        Password pwd = PasswordDynamoHelper.getInstance().getPasswordFromDB(usr.getPasswordid());
                        Credential.setPassword(pwd);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                        String TimeStamp = simpleDateFormat.format(new Date());
                        String random =  UUID.randomUUID().toString();
                        String Instance = Hash.Hash(TimeStamp,random);
                        ApplicationInstance.insert(Instance);

                        usr.setInstanceID(Instance);
                        UserDynamoHelper.getInstance().insert(usr);
                        UserSQLHelper.insert(usr, Credential.getPassword());

                        List<Area> areaList = AreaDynamoHelper.getInstance().getAllArea();
                        for(Area ar : areaList) {
                            AreaSQLHelper.insertWithoutEncryption(ar,Credential.getPassword());
                        }
                        List<File> fileList = FileDynamoHelper.getInstance().getAllFile();
                        for(File file : fileList) {
                            FileSQLHelper.insertWithoutEncryption(file, Credential.getPassword());
                        }
                    }
            }

            // remove deleted file from local storage
            java.io.File dir = new java.io.File(loginAct.getAppContext().getFilesDir().getAbsolutePath()+"/vault");
            if (dir.exists())
            {
                List<String> allFilesCurrName = FileSQLHelper.getAllFilesName(Credential.getPassword());
                String[] allFiles = dir.list();
                boolean found;

                for (int i = 0; i < allFiles.length; i++)
                {
                    found = false;
                    for (int j = 0; j < allFilesCurrName.size() && !found; j++){
                        if(allFiles[i].equals(allFilesCurrName.get(j))){
                            found = true;
                        }
                    }

                    if(!found){
                        new java.io.File(dir, allFiles[i]).delete();
                    }
                }
            }

            // get all old password
            Credential.setOldPasswords(PasswordDynamoHelper.getInstance().getAllPassword());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            loginAct.dismissProgressDialog();
            loginAct.openMainActivity();
        }
    }
}
