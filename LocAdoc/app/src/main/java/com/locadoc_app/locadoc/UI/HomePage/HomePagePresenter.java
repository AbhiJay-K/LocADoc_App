package com.locadoc_app.locadoc.UI.HomePage;

import android.os.AsyncTask;
import android.os.Handler;

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
    long startTime;
    String DBInstanceID;
    DBSynchronise DB;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            count++;
            if(count == 30)
            {
                doCheck();
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
        DB = new DBSynchronise();
        timerHandler.postDelayed(timerRunnable, 0);

    }
    private void doCheck()
    {
        if(checkInstanceID() && checkSpoofing())
        {
            count = 0;
        }
        else{

        }
    }
    //Check GPS Spoofing
    private boolean checkSpoofing()
    {
        if(homepage.isMockSettingsON(LocAdocApp.getContext()) || homepage.isMockSettingsON(LocAdocApp.getContext()))
        {
            return false;
        }
        return true;
    }
    private boolean checkInstanceID()
    {
        String InstanceID = ApplicationInstance.getRecord();
        DB.execute();
        if(InstanceID.equals(DBInstanceID))
        {
            return true;
        }
        return false;
    }
    private class DBSynchronise extends
            AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... objects) {
            User usr = UserDynamoHelper.getInstance().getUserFromDB(Credential.getEmail());
            DBInstanceID = usr.getInstanceID();
            return null;
        }
    };
}
