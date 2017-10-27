package com.locadoc_app.locadoc.UI.HomePage;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.DynamoDB.AreaDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.FileDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.PasswordDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.UserDynamoHelper;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.LocalDB.ApplicationInstance;
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
 * Created by AbhiJay_PC on 24/10/2017.
 */

public class HomePagePresenter {
    private HomePage_View_Interface homepage;
    int count;
    private final int delay = 1000;
    private long startTime;
    private String DBInstanceID;
    private boolean sameID;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            count++;
            if(count == 30)
            {
                Log.d("Logout checkInstanceID","30 sec");
                new DBSynchronise().execute();
            }
            timerHandler.postDelayed(this, delay);
        }
    };
    public HomePagePresenter(HomePage_View_Interface home)
    {
        homepage = home;
        count = 0;
        startTime = 0;
        startTime = System.currentTimeMillis();
        sameID = false;
        timerHandler.postDelayed(timerRunnable, 0);

    }
    private void doCheck()
    {
        if(sameID && checkSpoofing())
        {
            count = 0;
        }
        else{
            stopTimer();
            homepage.Logout();
        }
    }
    public void stopTimer()
    {
        timerHandler.removeCallbacks(timerRunnable);
    }
    //Check GPS Spoofing
    private boolean checkSpoofing()
    {
        if(homepage.isMockSettingsON(LocAdocApp.getContext()))
        {
            Log.d("Logout checkSpoofing","False");
            return false;
        }
        Log.d("Logout checkInstanceID","True");
        return true;
    }
    /*private boolean checkInstanceID()
    {
        boolean n = DB.execute().get();
        return false;
    }*/
    private class DBSynchronise extends
            AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... objects) {
            Log.d("Logout checkInstanceID","Check start");
            User usr = UserDynamoHelper.getInstance().getUserFromDB(Credential.getEmail());
            DBInstanceID = usr.getInstanceID();
            String InstanceID = ApplicationInstance.getRecord();
            if(InstanceID.equals(DBInstanceID))
            {
                Log.d("Logout checkInstanceID","True");
                sameID = true;
            }
            else
            {
                sameID = false;
            }
            Log.d("Logout checkInstanceID",InstanceID);
            Log.d("Logout checkInstanceID",DBInstanceID);
            //Log.d("Logout checkInstanceID","False");
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            doCheck();
        }
    };
    public void deleteAccount()
    {
        new DeleteAccount().execute();
    }

    private class DeleteAccount extends
            AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... objects) {
            List<Integer> fileList = FileSQLHelper.getAllFileID();
            // 1. Delete all the files localy and in cloud
            // 2. Delete all file information
            for(int id:fileList)
            {
                File file = FileSQLHelper.getFile(id, Credential.getPassword());
                S3Helper.getHelper().removeFile(file.getCurrentfilename());
                java.io.File src = new java.io.File(LocAdocApp.getContext().getFilesDir().getAbsolutePath() +
                        "/vault/" + file.getCurrentfilename());
                if(src.exists()) {
                    src.delete();
                }
                FileDynamoHelper.getInstance().deleteFromDB(file);
            }
            FileSQLHelper.clearRecord();
            // 3. Delete all area record from local Db and cloud DB
            List<Area> AreaList = AreaSQLHelper.getAllRecord(Credential.getPassword());
            for(Area ar: AreaList)
            {
                AreaDynamoHelper.getInstance().deleteFromDB(ar);
            }
            AreaSQLHelper.clearRecord();

            // 4. Delete all password record from cloud DB
            List<Password> passwordList = PasswordDynamoHelper.getInstance().getAllPassword();
            for(Password pwd: passwordList)
            {
                PasswordDynamoHelper.getInstance().deleteFromDB(pwd);
            }

            //Delete all user record from cloud and local
            User user = UserDynamoHelper.getInstance().getUserFromDB(Credential.getEmail());
            UserDynamoHelper.getInstance().deleteFromDB(user);
            UserSQLHelper.deleteRecord(Credential.getEmail());

            //Remove user from Cognito
            AppHelper.getPool().getCurrentUser().deleteUserInBackground(delHandler);
            return null;
        }
        @Override
        protected void onPostExecute(Void v){
        }
    };

    GenericHandler delHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            homepage.Logout();
        }

        @Override
        public void onFailure(Exception exception) {

        }
    };
}
