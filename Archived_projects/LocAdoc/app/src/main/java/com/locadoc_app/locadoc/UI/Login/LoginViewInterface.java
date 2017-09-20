package com.locadoc_app.locadoc.UI.Login;

import android.widget.AutoCompleteTextView;
import android.widget.EditText;

/**
 * Created by Admin on 9/12/2017.
 */

public interface LoginViewInterface {
    //void hideLoading();
    //void openActivityOnTokenExpire();
    //void onError(String message);
    //void showMessage(String message);
    //boolean isNetworkConnected();
    //void hideKeyboard();
    void openMainActivity();
    void openForgotPasswordActivity();
    void setIDError(String message);
    void setPassError(String message);
    AutoCompleteTextView getUserIDView();
    EditText getPassView();
    void showWaitDialog(String message);
    void showDialogMessage(String title, String body);
    void closeWaitDialog();
}
