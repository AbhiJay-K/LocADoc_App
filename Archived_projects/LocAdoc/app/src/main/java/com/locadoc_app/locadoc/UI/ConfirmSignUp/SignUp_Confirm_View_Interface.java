package com.locadoc_app.locadoc.UI.ConfirmSignUp;

/**
 * Created by AbhiJay_PC on 15/9/2017.
 */

public interface SignUp_Confirm_View_Interface {
    public void showDialogMessage(String title, String body, final boolean exitActivity);
    public void setLabelConfirmUserID(String str);
    public void setLabelConfirmCode(String str);
    public String getConfCode();
    public String getUsername();
    public void SetConfhandlerMessage();
    public void SetReConfCodeHandlerSuccessMessage();
    public void SetReConfCodeHandlerFailMessage();
}
