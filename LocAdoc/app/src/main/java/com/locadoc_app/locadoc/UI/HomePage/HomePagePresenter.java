package com.locadoc_app.locadoc.UI.HomePage;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.locadoc_app.locadoc.DynamoDB.UserDynamoHelper;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.LocalDB.ApplicationInstance;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.User;

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
            homepage.Logout();
        }
    }
    //Check GPS Spoofing
    private boolean checkSpoofing()
    {
        if(homepage.isMockSettingsON(LocAdocApp.getContext()) || homepage.areThereMockPermissionApps(LocAdocApp.getContext()))
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
}
