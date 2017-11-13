package com.locadoc_app.locadoc.UI.ConfirmSignUp;

/**
 * Created by AbhiJay_PC on 15/9/2017.
 */

public interface SignUp_Confirm_View_Interface {
    void showDialogMessage(String title, String body, final boolean exitActivity);
    void setLabelConfirmUserID(String str);
    void setLabelConfirmCode(String str);
    String getUsername();
    void SetConfhandlerMessage();
    void SetReConfCodeHandlerSuccessMessage();
    void SetReConfCodeHandlerFailMessage();
    String getUserName();
    String getPassword();
    String getfName();
    String getlName();
}
