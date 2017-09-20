package com.locadoc_app.locadoc.UI.Login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.UI.Signup.SignUp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class LoginActivity extends AppCompatActivity implements LoginViewInterface
{
    private LoginPresenterInterface loginPres;
    private ProgressDialog waitDialog;
    private AlertDialog userDialog;
    private LoginButton loginButton;
    @BindView(R.id.UserID)
    AutoCompleteTextView userIDView;

    @BindView(R.id.Password)
    EditText passView;
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginPres = new LoginPresenter(this);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        AppHelper.init(getApplicationContext());
    }

    public void init()
    {
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        //loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                showWaitDialog(exception.getMessage().toString());
            }
        });
    }
    @OnClick (R.id.SignupButton)
    void onSignupClick(View v)
    {
        Log.d("LocAdoc", "Sign up");
        Intent signUp = new Intent(this, SignUp.class);
        startActivityForResult(signUp, 1);
    }

    @OnClick (R.id.LoginButton)
    void onLoginClick(View v)
    {
        loginPres.onLoginClick(userIDView.getText().toString(),
                                passView.getText().toString());
    }

    /*@OnClick (R.id.FacebookButton)
    void onFacebookLoginClick(View v)
    {
        loginPres.onFacebookLoginClick();
    }

    @OnClick (R.id.GoogleButton)
    void onGoogleLoginClick(View v)
    {
        loginPres.onGoogleLoginClick();
    }*/

    @OnClick (R.id.ForgotPasswordText)
    void onForgotPasswordClick(View v)
    {
        loginPres.onForgotPasswordClick();
    }

    @OnFocusChange (R.id.UserID)
    void onChangeID (View v)
    {
        userIDView.setError(null);
        loginPres.onChangeID(userIDView.getText().toString());
    }


    @OnFocusChange (R.id.Password)
    void onChangePassword (View v)
    {
        passView.setError(null);
        loginPres.onChangePassword(passView.getText().toString());
    }

    @Override
    public void setIDError(String message) { userIDView.setError(message);}

    @Override
    public void setPassError(String message) { passView.setError(message);}

    @Override
    public void openMainActivity()
    {

    }

    public void openForgotPasswordActivity() {
        Log.d("LocAdoc", "Open Forgot Password Activity");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            switch (requestCode) {
                case 1:
                    String name = data.getStringExtra("name");
                    if (!name.isEmpty()) {
                        userIDView.setText(name);
                        passView.setText("");
                        passView.requestFocus();
                    }
                    String userPasswd = data.getStringExtra("password");
                    if (!userPasswd.isEmpty()) {
                        passView.setText(userPasswd);
                    }
                    if (!name.isEmpty() && !userPasswd.isEmpty()) {
                        // We have the user details, so sign in!
                        loginPres.onLoginClick(name, userPasswd);
                    }
                    break;
            }
        }

    }

    public AutoCompleteTextView getUserIDView()
    {
        return userIDView;
    }

    public EditText getPassView()
    {
        return passView;
    }

    public void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    public void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    public void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }
}
