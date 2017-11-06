package com.locadoc_app.locadoc.UI.PasswordRecovery;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.DynamoDB.PasswordDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.UserDynamoHelper;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.helper.CheckPassword;
import com.locadoc_app.locadoc.helper.Hash;

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

    public void forgotPassword(String email) {
        AppHelper.getPool().getUser(email).forgotPasswordInBackground(forgotPasswordHandler);
    }

    public void forgotPwdCredential(String userEmail, String newPassword) {
        Log.d("FORGOTPWD","======================================================================");
        Log.d("FORGOTPWD","ForgotPwd Credential is Executed");

        Password newPwd = new Password();
        newPwd.setPasswordid(-1);
        String salt = Hash.SecureRandomGen();
        newPwd.setSalt(salt);

        newPwd.setUser(userEmail);
        newPwd.setPassword(Hash.Hash(newPassword, salt));

        Credential.setEmail(userEmail);
        Credential.setPassword(newPwd);

        Log.d("FORGOTPWD", "Email: " + Credential.getEmail());
        Log.d("FORGOTPWD", "PasswordID: " + Credential.getPassword().getPasswordid() + " | Password: " + Credential.getPassword().getPassword());

        Log.d("FORGOTPWD", "NEW SALT:" + salt);
        Log.d("FORGOTPWD","======================================================================");

    }

    // Callbacks
    ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
        @Override
        public void onSuccess() {
            String userEmail = activity.getEmail();
            String newPassword = activity.getPwdView().getText().toString();

            forgotPwdCredential(userEmail, newPassword);

            activity.exit(0);
        }

        @Override
        public void onFailure(Exception e) {
            Log.d("FORGOTPWD", e.getMessage());
            activity.exit(-1);
        }
        @Override
        public void getResetCode(ForgotPasswordContinuation forgotPassContinuation) {
            forgotPasswordContinuation = forgotPassContinuation;
        }
    };
}
