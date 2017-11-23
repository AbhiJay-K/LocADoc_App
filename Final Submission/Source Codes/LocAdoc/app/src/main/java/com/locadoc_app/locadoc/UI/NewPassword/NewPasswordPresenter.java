package com.locadoc_app.locadoc.UI.NewPassword;

import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.helper.CheckPassword;

/**
 * Created by Admin on 9/15/2017.
 */

public class NewPasswordPresenter implements NewPasswordPresenterInterface{
    private NewPasswordViewInterface activity;

    public NewPasswordPresenter(NewPasswordViewInterface activity)
    {
        this.activity = activity;
    }

    public int isValidPassword()
    {
        String password = activity.getPwdView().getText().toString();
        if(password.isEmpty())
        {
            activity.setPwdErr (2);
            return 2;
        }
        if(CheckPassword.PWDCheck(password))
        {
            return 1;
        }
        activity.setPwdErr (0);
        return 0;
    }

    public int checkPasswordSame()
    {
        String pwd1 = activity.getPwdView().getText().toString();
        String pwd2 = activity.getRPwdView().getText().toString();
        if(pwd2.isEmpty())
        {
            activity.setRPwdErr (2);
            return 2;
        }
        if(pwd1.equals(pwd2))
        {
            return 1;
        }
        activity.setRPwdErr (0);
        return 0;
    }

    public void newPassSubmit()
    {
        if (isValidPassword() == 1 && checkPasswordSame() == 1)
        {
            AppHelper.setPasswordForFirstTimeLogin(activity.getPwdView().getText().toString());
            activity.exit(true);
        }
    }
}
