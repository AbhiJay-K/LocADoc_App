package com.locadoc_app.locadoc.UI.Signup;

import android.util.Log;
import android.util.Patterns;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.locadoc_app.locadoc.Cognito.AppHelper;
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
            activity.setEmailError(2);
            return 2;
        }
        if(EmailValidation.isValidEmail(email))
        {
            return 1;
        }
        activity.setEmailError(0);
        return 0;
    }

    public int CheckContactNum()
    {
        String contactNum = activity.getContactNoView().getText().toString();
        Log.d("LocAdoc", contactNum);
        if(contactNum.isEmpty())
        {
            activity.setContactNumErr (2);
            return 2;
        }
        if(Patterns.PHONE.matcher((CharSequence)contactNum).matches())
        {
            return 1;
        }
        activity.setContactNumErr (0);
        return 0;
    }

    public boolean checkFName()
    {
        String firstName = activity.getFNameView().getText().toString();
        if(firstName.isEmpty())
        {
            activity.setFNameErr();
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
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        if(checkEmail() == 1 && isValidPassword() == 1 && checkPasswordSame() == 1 &&
                checkFName() && checkLName() && CheckContactNum() == 1)
        {
            String password = activity.getPwdView().getText().toString();
            String email = activity.getEmailView().getText().toString();
            userAttributes.addAttribute(AppHelper.getSignUpFieldsC2O().get("Email").toString(), email);
            userAttributes.addAttribute(AppHelper.getSignUpFieldsC2O().get("Phone number").toString(),
                    activity.getContactNoView().getText().toString());
            userAttributes.addAttribute(AppHelper.getSignUpFieldsC2O().get("Given name").toString(),
                    activity.getFNameView().getText().toString());
            userAttributes.addAttribute(AppHelper.getSignUpFieldsC2O().get("Family name").toString(),
                    activity.getLNameView().getText().toString());
            activity.showWaitDialog("Signing up...");
            AppHelper.getPool().signUpInBackground(email, password, userAttributes, null, signUpHandler);
        }
    }

    SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Check signUpConfirmationState to see if the user is already confirmed
            activity.closeWaitDialog();
            Boolean regState = signUpConfirmationState;
            if (signUpConfirmationState) {
                // User is already confirmed
                activity.showDialogMessage("Sign up successful!",
                        activity.getEmailView().getText().toString() + " has been Confirmed", true);
            }
            else {
                // User is not confirmed
                activity.confirmSignUp(cognitoUserCodeDeliveryDetails);
            }
        }

        @Override
        public void onFailure(Exception exception) {
            activity.closeWaitDialog();
            activity.showDialogMessage("Sign up failed",AppHelper.formatException(exception),false);
        }
    };
}
