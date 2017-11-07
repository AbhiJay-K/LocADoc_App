package com.locadoc_app.locadoc.UI.PasswordRecovery;

import android.widget.EditText;

/**
 * Created by user on 9/18/2017.
 */

public interface PasswordRecoveryViewInterface {
    void setLabelUserEmail(String str);
    void setLabelPassword(String str);
    void setLabelConfirmPassword(String str);
    void setLabelVerifiCode(String str);
    void setLabelPasswordOK(String str);
    void setLabelConfirmPasswordOK(String str);
    String getEmail();
    EditText getPwdView();
    EditText getRPwdView();
    void exit(int result, String errorMessage);

}
