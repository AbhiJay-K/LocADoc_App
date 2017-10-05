package com.locadoc_app.locadoc.UI.Setting;

import com.locadoc_app.locadoc.UI.ConfirmSignUp.Activity_SignUp_Confirm;
import com.locadoc_app.locadoc.helper.CheckPassword;

import static android.R.attr.password;

/**
 * Created by user on 10/4/2017.
 */

public class ResetPasswordPresenter {

    private ResetPassword activity;

    public ResetPasswordPresenter(ResetPassword activity) {
        this.activity = activity;
    }

    public int isValidPwd() {

        String curPwd = activity.getCurPwd().getText().toString();

        if(curPwd.isEmpty()) {
            activity.setLabelCurPwd("PasswordSQLHelper cannot be empty!");
            return 2;
        }

        /* Need to Add fucntion for checking with DATABASE *************************************************************************************/

        if(CheckPassword.PWDCheck(curPwd)) {
            activity.setLabelCurPwdOK("");
            return 1;
        }

        activity.setLabelCurPwd("PasswordSQLHelper is 8-12 charecters. Contains A-Z, a-z, 0-9");

        return 0;
    }

}
