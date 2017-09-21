package com.locadoc_app.locadoc.UI.PasswordRecovery;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.helper.CheckPassword;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DainoMix on 9/18/2017.
 */

public class PasswordRecoveryPresenter implements PasswordRecoveryPresenterInterface{

    private PasswordRecoveryViewInterface activity;
    private ForgotPasswordContinuation forgotPasswordContinuation;


    public PasswordRecoveryPresenter(PasswordRecovery newActivity) {
        this.activity = newActivity;
    }

    public int isValidPassword()
    {
        String password = activity.getPwdView().getText().toString();
        if(password.isEmpty())
        {
            activity.setLabelPassword("Password cannot be empty!");
            return 2;
        }
        if(CheckPassword.PWDCheck(password))
        {
            activity.setLabelPasswordOK("");
            return 1;
        }
        activity.setLabelPassword("Password is 8-12 charecters. Contains A-Z, a-z, 0-9");
        return 0;
    }

    public int checkPasswordSame()
    {
        String pwd1 = activity.getPwdView().getText().toString();
        String pwd2 = activity.getRPwdView().getText().toString();
        if(pwd2.isEmpty())
        {
            activity.setLabelConfirmPassword("Confirm password cannot be empty!");
            return 2;
        }
        if(pwd1.equals(pwd2))
        {
            activity.setLabelConfirmPasswordOK("");
            return 1;
        }
        activity.setLabelConfirmPassword("Password does not match");
        return 0;
    }


    // Sends Verification code to AWS
    public void continueTask(String email, String password, String verifiCode) {
        if (isValidPassword() != 1 && checkPasswordSame() != 1){
            return;
        }
        if(verifiCode == null || verifiCode.length() < 1) {
            activity.setLabelVerifiCode("Verification code cannot be empty!");
            return;
        }
        forgotPasswordContinuation.setPassword(password);
        forgotPasswordContinuation.setVerificationCode(verifiCode);
        forgotPasswordContinuation.continueTask();
    }

    // ForMat checking with Regular Expression pattern
    public boolean patternCheck(String password, String pattern) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(password);
        return m.find();
    }


    public void forgotPassword(String email)
    {
        AppHelper.getPool().getUser(email).forgotPasswordInBackground(forgotPasswordHandler);
    }

    // Callbacks
    ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
        @Override
        public void onSuccess() {
            activity.exit(0);
        }

        @Override
        public void getResetCode(ForgotPasswordContinuation forgotPassContinuation) {
            forgotPasswordContinuation = forgotPassContinuation;
        }

        @Override
        public void onFailure(Exception e) {
            activity.exit(-1);
        }
    };

}
