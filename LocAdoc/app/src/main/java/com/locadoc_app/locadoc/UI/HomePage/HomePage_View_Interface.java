package com.locadoc_app.locadoc.UI.HomePage;

import android.content.Context;

/**
 * Created by AbhiJay_PC on 24/10/2017.
 */

public interface HomePage_View_Interface {
    boolean isMockSettingsON(Context context);
    //boolean areThereMockPermissionApps(Context context);
    void LogoutLastTime();
    void Logout();
}
