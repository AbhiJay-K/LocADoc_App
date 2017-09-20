package com.locadoc_app.locadoc.UI.NewPassword;

import android.widget.EditText;

/**
 * Created by Admin on 9/15/2017.
 */

public interface NewPasswordViewInterface {
    void exit(boolean continueWithSignIn);
    void setPwdErr (int n);
    void setRPwdErr (int n);
    EditText getPwdView();
    EditText getRPwdView();
}
