package com.locadoc_app.locadoc.UI.ConfirmSignUp;

/**
 * Created by AbhiJay_PC on 15/9/2017.
 */

public interface SignUp_Confirm_Presenter_Interface {
    void sendConfCode(String userName, String confirmCode, String uhint, String Chint);
    void reqConfCode(String userName,String uhint);

}
