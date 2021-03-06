package com.locadoc_app.locadoc.UI.Login;

import android.content.Context;
import android.widget.EditText;

/**
 * Created by Admin on 9/12/2017.
 */

public interface LoginViewInterface {
    void openMainActivity();
    void openForgotPasswordActivity();
    void setIDError(String message);
    void setPassError();
    EditText getUserIDView();
    EditText getPassView();
    void showWaitDialog(String message);
    void showDialogMessage(String title, String body);
    void closeWaitDialog();
    void firstTimeSignIn();
    void confirmUser();
    boolean checkLogin();
    void startProgressDialog();
    void dismissProgressDialog();
    void clearFocus();
    void startDelay();
    Context getAppContext();
}
