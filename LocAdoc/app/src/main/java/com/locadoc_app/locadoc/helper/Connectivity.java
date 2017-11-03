package com.locadoc_app.locadoc.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.locadoc_app.locadoc.LocAdocApp;

/**
 * Created by AbhiJay_PC on 3/11/2017.
 */

public class Connectivity {
    public static boolean isNetworkAvailable() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) LocAdocApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getActiveNetworkInfo();
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;
    }
}
