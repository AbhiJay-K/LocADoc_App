package com.locadoc_app.locadoc.UI.Login;

import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.helper.CheckPassword;
import com.locadoc_app.locadoc.helper.EmailValidation;

import java.util.Locale;
import java.util.Map;

/**
 * Created by Admin on 9/12/2017.
 */

public class LoginPresenter implements LoginPresenterInterface
{
    private LoginViewInterface loginAct;
    //Continuations
    private MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation;
    private ForgotPasswordContinuation forgotPasswordContinuation;
    private NewPasswordContinuation newPasswordContinuation;

    public LoginPresenter (LoginViewInterface loginAct)
    {
        this.loginAct = loginAct;
    }

    @Override
    public void onLoginClick(String id, String password)
    {
        boolean isValid = true;
        if (!onChangeID(id) || !onChangePassword(password))
            isValid = false;

        if (isValid)
        {
            Log.d("LocAdoc", "Attempt Login");
            loginAct.showWaitDialog("Signing in...");
            String username = loginAct.getUserIDView().getText().toString();
            AppHelper.getPool().getUser(username).getSessionInBackground(authenticationHandler);
        }
    }

    @Override
    public void onForgotPasswordClick(String id)
    {
        if(onChangeID(id))
        {
            loginAct.openForgotPasswordActivity();
        }

    }

    @Override
    public boolean onChangeID(String id){
        if (!EmailValidation.isValidEmail(id)){
            loginAct.setIDError("Invalid Email Address");
            return false;
        }

        return true;
    }

    @Override
    public boolean onChangePassword(String pass){
        if (!CheckPassword.PWDCheck(pass)){
            loginAct.setPassError();
            return false;
        }

        return true;
    }

    public void continueWithFirstTimeSignIn() {
        newPasswordContinuation.setPassword(AppHelper.getPasswordForFirstTimeLogin());
        Map<String, String> newAttributes = AppHelper.getUserAttributesForFirstTimeLogin();
        if (newAttributes != null) {
            for(Map.Entry<String, String> attr: newAttributes.entrySet()) {
                newPasswordContinuation.setUserAttribute(attr.getKey(), attr.getValue());
            }
        }
        try {
            newPasswordContinuation.continueTask();
        } catch (Exception e) {
            loginAct.closeWaitDialog();
            loginAct.showDialogMessage("Sign-in failed", AppHelper.formatException(e));
        }
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            //Log.e(TAG, "Auth Success");
            loginAct.closeWaitDialog();
            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.newDevice(device);
            loginAct.openMainActivity();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            loginAct.closeWaitDialog();
            Locale.setDefault(Locale.US);
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(
                    loginAct.getUserIDView().getText().toString(),
                    loginAct.getPassView().getText().toString(), null);
            authenticationContinuation.setAuthenticationDetails(authenticationDetails);
            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            //closeWaitDialog();
            //mfaAuth(multiFactorAuthenticationContinuation);
        }

        @Override
        public void onFailure(Exception e) {
            loginAct.closeWaitDialog();
            if (AppHelper.formatException(e).equals("User is not confirmed. ")){
                loginAct.confirmUser();
            }
            else {
                loginAct.showDialogMessage("Sign-in failed", AppHelper.formatException(e));
            }
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            /**
             * For Custom authentication challenge, implement your logic to present challenge to the
             * user and pass the user's responses to the continuation.
             */
            if ("NEW_PASSWORD_REQUIRED".equals(continuation.getChallengeName())) {
                // This is the first sign-in attempt for an admin created user
                newPasswordContinuation = (NewPasswordContinuation) continuation;
                AppHelper.setUserAttributeForDisplayFirstLogIn(newPasswordContinuation.getCurrentUserAttributes(),
                        newPasswordContinuation.getRequiredAttributes());
                loginAct.closeWaitDialog();
                loginAct.firstTimeSignIn();
            }
        }
    };


}
