package com.locadoc_app.locadoc.UI.Signup;

import android.view.View;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;

/**
 * Created by AbhiJay_PC on 12/9/2017.
 */

public interface SignUPViewInterface {
    void SignUpBtnClick(View v);
    void EmailFocusChange(View v);
    void FirstNameFocusChange();
    void exit(String name, String pass);
    void confirmSignUp(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails);
    void setEmailError (int n);
    void setContactNumErr (int n);
    void setFNameErr();
    void setPwdErr (int n);
    void setRPwdErr (int n);
    void showWaitDialog(String message);
    void closeWaitDialog();
    void showDialogMessage(String title, String body, final boolean exit);

}
