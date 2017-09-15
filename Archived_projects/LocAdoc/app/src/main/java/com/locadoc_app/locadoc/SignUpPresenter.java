package com.locadoc_app.locadoc;

import android.util.Log;
import android.util.Patterns;

import com.locadoc_app.locadoc.helper.CheckPassword;
import com.locadoc_app.locadoc.helper.EmailValidation;

/**
 * Created by AbhiJay_PC on 12/9/2017.
 */

public class SignUpPresenter  implements SignUPPresenterInterface{
    SignUp activity;
    //Error msg 0 - invalid , 1 - ok , 2 - empty field
    public int isValidPassword()
    {
        String password = activity.getPwdView().getText().toString();
        if(password.isEmpty())
        {
            return 2;
        }
        if(CheckPassword.PWDCheck(password))
        {
            return 1;
        }
        return 0;
    }
    public int checkPasswordSame()
    {
        String pwd1 = activity.getPwdView().getText().toString();
        String pwd2 = activity.getRPwdView().getText().toString();
        if(pwd2.isEmpty())
        {
            return 2;
        }
        if(pwd1.equals(pwd2))
        {
            return 1;
        }

        return 0;
    }
    public void setView(SignUp obj)
    {
        activity = obj;
    }
    public int checkEmail()
    {
        String email = activity.getEmailView().getText().toString();
        Log.d("LocAdoc", email);
        if(email.isEmpty())
        {
            return 2;
        }
        if(EmailValidation.isValidEmail(email))
        {
            return 1;
        }
        return 0;
    }
    public int CheckContactNum()
    {
        String contactNum = activity.getContactNoView().getText().toString();
        Log.d("LocAdoc", contactNum);
        if(contactNum.isEmpty())
        {
            return 2;
        }
        if(Patterns.PHONE.matcher((CharSequence)contactNum).matches())
        {
            return 1;
        }
        return 0;
    }
    public boolean checkFName()
    {
        String firstName = activity.getFNameView().getText().toString();
        if(firstName.isEmpty())
        {
            return false;
        }
        return true;
    }
    public boolean checkLName()
    {
        return true;
    }
    public void SignUpUser()
    {
        if(checkEmail() == 1 && isValidPassword() == 1 && checkPasswordSame() == 1 &&
                checkFName() && checkLName() && CheckContactNum() == 1)
        {
            activity.loadAlertDialog();
        }
    }
}
