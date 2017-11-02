package com.locadoc_app.locadoc.UI.Setting;

import android.widget.EditText;

/**
 * Created by Dainomix on 10/5/2017.
 */

public interface ResetPasswordViewInterface {
    EditText getCurPwd();
    EditText getNewPwd();
    EditText getConfirmNewPwd();

    void setLabelCurPwd(String str);
    void setLabelCurPwdOK(String str);
    void setLabelNewPwd(String str);
    void setLabelNewPwdOK(String str);
    void setLabelConfirmNewPwd(String str);
    void setLabelConfirmNewPwdOK(String str);
    void showProgressDialog(String title, String msg);
    void dismissProgresDialog();

    void showDialogMessage(String title, String body, boolean status);
    void exit(boolean result);
}
