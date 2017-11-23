package com.locadoc_app.locadoc.UI.Login;

/**
 * Created by Admin on 9/12/2017.
 */

public interface LoginPresenterInterface {
    void onLoginClick(String id, String password);
    void onForgotPasswordClick(String id);
    boolean onChangeID(String id);
    boolean onChangePassword(String pass);
    void continueWithFirstTimeSignIn();
}
