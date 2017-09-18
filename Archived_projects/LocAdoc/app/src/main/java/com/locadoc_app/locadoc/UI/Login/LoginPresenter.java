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
    public void onGoogleLoginClick()
    {
        Log.d("LocAdoc", "Google");
    }

    @Override
    public void onFacebookLoginClick()
    {

    }

    @Override
    public void onForgotPasswordClick()
    {
        loginAct.openForgotPasswordActivity();
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
            loginAct.setPassError("Password is 8-10 characters, contains a-z, A-Z, 0-9");
            return false;
        }

        return true;
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            //Log.e(TAG, "Auth Success");
            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.newDevice(device);
            loginAct.showDialogMessage("Login Success", "You are now logged in to LocAdoc");
            //closeWaitDialog();
            //launchUser();
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
           // getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            //closeWaitDialog();
            //mfaAuth(multiFactorAuthenticationContinuation);
        }

        @Override
        public void onFailure(Exception e) {
           /* closeWaitDialog();
            TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
            label.setText("Sign-in failed");
            inPassword.setBackground(getDrawable(R.drawable.text_border_error));

            label = (TextView) findViewById(R.id.textViewUserIdMessage);
            label.setText("Sign-in failed");
            inUsername.setBackground(getDrawable(R.drawable.text_border_error));
            */
            loginAct.closeWaitDialog();
            loginAct.showDialogMessage("Sign-in failed", AppHelper.formatException(e));
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
                loginAct.showDialogMessage("Login Success", "You are now logged in to LocAdoc");
                //closeWaitDialog();
                //firstTimeSignIn();
            }
        }
    };
}
