package com.locadoc_app.locadoc.UI.Setting;

/**
 * Created by Dainomix on 10/5/2017.
 */

public interface ResetPasswordPresenterInterface {
    int isValidCurPwd();
    int isValidNewPwd();
    int isValidPwdWithNewPwd();
    void changePassword();
}
