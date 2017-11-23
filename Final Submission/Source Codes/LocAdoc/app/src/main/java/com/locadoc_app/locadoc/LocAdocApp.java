package com.locadoc_app.locadoc;

import android.app.Application;
import android.content.Context;

/**
 * Created by AbhiJay_PC on 2/10/2017.
 */

public class LocAdocApp extends Application{

        private static Context mContext;
        @Override
        public void onCreate() {
            super.onCreate();
            mContext = this;
        }

        public static Context getContext(){
            return mContext;
        }
}
