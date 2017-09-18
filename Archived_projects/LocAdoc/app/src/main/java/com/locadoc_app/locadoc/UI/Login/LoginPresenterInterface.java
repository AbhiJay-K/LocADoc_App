package com.locadoc_app.locadoc.UI.Login;

import android.content.Intent;

/**
 * Created by Admin on 9/12/2017.
 */

public interface LoginPresenterInterface {
    void onLoginClick(String id, String password);
    void onGoogleLoginClick();
    void onFacebookLoginClick();
    void onForgotPasswordClick();
    boolean onChangeID(String id);
    boolean onChangePassword(String pass);
}
