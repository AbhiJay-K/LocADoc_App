package com.locadoc_app.locadoc.UI.Setting;

import android.util.Log;

import com.locadoc_app.locadoc.Cognito.AppHelper;
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


    public int isValidCurPwd() {
        String curPwd = activity.getCurPwd().getText().toString();

        // Check Password Empty
        if(curPwd.isEmpty()) {
            activity.setLabelCurPwd("PasswordSQLHelper: cannot be empty!");
            return 2;
        }

        // Check Password Format
        if(!CheckPassword.PWDCheck(curPwd)) {
            activity.setLabelCurPwd("PasswordSQLHelper: have to be 8-12 characters. Contains A-Z, a-z, 0-9");
            return 1;
        }
        else {
            activity.setLabelCurPwdOK("");
        }


        // Check with DATABASE
        /*
        if() {

        }
        else {

        }
         */

        return 0;
    }

    public int isValidNewPwd() {

        String newPwd = activity.getNewPwd().getText().toString();

        // check Password Empty
        if(newPwd.isEmpty()) {
            activity.setLabelNewPwd("PasswordSQLHelper: cannot be empty!");
            return 2;
        }

        // check new Password Format
        if(CheckPassword.PWDCheck(newPwd))
            activity.setLabelNewPwdOK("");
        else
            activity.setLabelNewPwd("PasswordSQLHelper: have to be 8-12 characters. Contains A-Z, a-z, 0-9");

        return 0;
    }

    public int isValidPwdWithNewPwd() {

        String newPwd = activity.getNewPwd().getText().toString();
        String confirmNewPwd = activity.getConfirmNewPwd().getText().toString();

        // Check The difference betweeen new password and Confirm new password
        if(confirmNewPwd.equals(newPwd))
            activity.setLabelConfirmNewPwdOK("");
        else
            activity.setLabelConfirmNewPwd("PasswordSQLHelper: Confirm Password is not same with New Password");

        return 0;
    }

    public void changePassword() {
        // oldUserPassword, newUserPassword, CallBack:GeneralHandler
        // AppHelper.getPool().getUser().changePassword();

    }





}
