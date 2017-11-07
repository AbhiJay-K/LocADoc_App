package com.locadoc_app.locadoc.UI.Login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.DynamoDB.FileDynamoHelper;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.LocalDB.ApplicationInstance;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.DBHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.LocalDB.GuestSession;
import com.locadoc_app.locadoc.LocalDB.UserSQLHelper;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.S3.S3Helper;
import com.locadoc_app.locadoc.Test;
import com.locadoc_app.locadoc.UI.ConfirmSignUp.Activity_SignUp_Confirm;
import com.locadoc_app.locadoc.UI.HomePage.HomePageActivity;
import com.locadoc_app.locadoc.UI.NewPassword.NewPasswordActivity;
import com.locadoc_app.locadoc.UI.PasswordRecovery.PasswordRecovery;
import com.locadoc_app.locadoc.UI.Signup.SignUp;
import com.locadoc_app.locadoc.helper.Connectivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class LoginActivity extends AppCompatActivity implements LoginViewInterface
{
    private LoginPresenterInterface loginPres;
    private ProgressDialog waitDialog;
    private AlertDialog userDialog;

    private ProgressDialog progress;
    private static boolean curUSer = false;
    @BindView(R.id.UserID)
    EditText userIDView;
    @BindView(R.id.Password)
    EditText passView;
    @BindView(R.id.SignupButton)
    Button signupButton;
    Button chngUser;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AppEventsLogger.activateApp(this);
        loginPres = new LoginPresenter(this);
        setContentView(R.layout.activity_login);
        chngUser = (Button) findViewById(R.id.Login_Change_User_BTN);
        chngUser.setVisibility(View.GONE);
        DBHelper.init(getApplicationContext());
        if(GuestSession.getNumberofRecords() <= 0)
        {
            GuestSession.insert();
        }
        else
        {
            long [] grecord = GuestSession.getRecord();
            if(grecord[1] != 0L)
            {
                startDelay();
            }

        }
        ButterKnife.bind(this);
        checkLogin();
        AppHelper.init(getApplicationContext());
        // wipe data
        AppHelper.getPool().getCurrentUser().signOut();
    }

    @OnClick (R.id.SignupButton)
    void onSignupClick(View v)
    {
        if(Connectivity.isNetworkAvailable()) {
            Log.d("LocAdoc", "Sign up");
            Intent signUp = new Intent(this, SignUp.class);
            startActivityForResult(signUp, 1);
        }else{
            remindUserDialog();
        }

    }
    @OnClick (R.id.Login_Change_User_BTN)
    void onChangeUserClick(View v)
    {

        if(FileSQLHelper.getNumberofRecords() > 0)
        {
            java.io.File dir = new java.io.File(getApplicationContext().getFilesDir().getAbsolutePath()+"/vault");
            if (dir.exists())
            {
                String[] allFiles = dir.list();
                for (int i = 0; i < allFiles.length; i++)
                {
                    new java.io.File(dir, allFiles[i]).delete();
                }
            }
            FileSQLHelper.clearRecord();
        }
        if(AreaSQLHelper.getNumberofRecords() > 0){
            AreaSQLHelper.clearRecord();
        }
        if(ApplicationInstance.getNumberofRecords() > 0)
        {
            ApplicationInstance.deleteRecord();
        }
        if(UserSQLHelper.getNumberofRecords() > 0)
        {
            UserSQLHelper.clearRecord();
        }

        Credential.clearAll();
        userIDView.setText("");
        userIDView.setEnabled(true);
        passView.setText("");
        userIDView.setFocusableInTouchMode(true);
        userIDView.setFocusable(true);
        signupButton.setVisibility(View.VISIBLE);
        chngUser.setVisibility(View.GONE);
        chngUser.setFocusableInTouchMode(false);
        chngUser.setFocusable(false);
    }

    @OnClick (R.id.LoginButton)
    void onLoginClick(View v)
    {
        if(Connectivity.isNetworkAvailable()) {
            loginPres.onLoginClick(userIDView.getText().toString(),
                    passView.getText().toString());
        }
        else{
            remindUserDialog();
        }
    }
    //will be called if there is no network connection
    public void remindUserDialog(){
        AlertDialog.Builder builder = new  AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("There is no network connection");
        builder.setMessage("Make sure you are connected to a Wi-Fi or" +
                "mobile network and try again");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @OnClick (R.id.ForgotPasswordText)
    void onForgotPasswordClick(View v)
    {
        if(Connectivity.isNetworkAvailable()) {
            userIDView.setError(null);
            loginPres.onForgotPasswordClick(userIDView.getText().toString());
        }else{
            remindUserDialog();
        }
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
    public void setPassError() { passView.setError(getString(R.string.error_invalid_password));}

    @Override
    public void openMainActivity()
    {
        Intent homeActivity = new Intent(this, HomePageActivity.class);
        homeActivity.putExtra("name", userIDView.getText().toString());
        startActivityForResult(homeActivity,4);
    }

    public void startProgressDialog()
    {
        progress = new ProgressDialog(this);
        progress.setTitle("Setup");
        progress.setMessage("Setting up services...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.show();
    }
    public void dismissProgressDialog()
    {
        progress.dismiss();
    }
    public boolean checkLogin()
    {
        Log.d("Number of User ",String.valueOf(UserSQLHelper.getNumberofRecords()));
        if(UserSQLHelper.getNumberofRecords() > 0){
            Log.d("Number of User ",String.valueOf(UserSQLHelper.getNumberofRecords()));
            String email = UserSQLHelper.getUser();
            chngUser.setVisibility(View.VISIBLE);
            userIDView.setText(email);
            userIDView.setEnabled(false);
            userIDView.setFocusableInTouchMode(false);
            userIDView.setFocusable(false);
            signupButton.setVisibility(View.GONE);
            curUSer = true;
            return true;
        }
        return false;
    }
    public static boolean isCurUSer() {
        return curUSer;
    }
    public void openForgotPasswordActivity() {
        Intent ForgetPasswordActivity = new Intent(this, PasswordRecovery.class);
        ForgetPasswordActivity.putExtra("name", userIDView.getText().toString());
        startActivityForResult(ForgetPasswordActivity, 3);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);
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
                case 2:
                    loginPres.onLoginClick(userIDView.getText().toString(),
                            passView.getText().toString());
                    break;
                case 3:
                    int result = data.getIntExtra("result", -1);

                    if(result == 0) {
                        String newPass = data.getStringExtra("new_password");
                        passView.setText(newPass);
                        loginPres.onLoginClick(userIDView.getText().toString(), newPass);
                    }
                    else{
                        showDialogMessage("Password Recovery","Recovery failed");
                    }
                case 4:
                    int logoutType = data.getIntExtra("LogoutResult",-1);
                    if(logoutType == 2)
                    {
                        chngUser.setVisibility(View.GONE);
                        signupButton.setVisibility(View.VISIBLE);
                        userIDView.setText("");
                        userIDView.setEnabled(true);
                        userIDView.setFocusableInTouchMode(true);
                        userIDView.setFocusable(true);
                    }
                    else if(logoutType == 1)
                    {
                        userIDView.setEnabled(false);
                        userIDView.setFocusableInTouchMode(false);
                        userIDView.setFocusable(false);
                        passView.setText("");
                        signupButton.setVisibility(View.GONE);
                        chngUser.setVisibility(View.VISIBLE);

                    }
                    break;
                case 6:
                    //New password
                    closeWaitDialog();
                    Boolean continueSignIn = data.getBooleanExtra("continueSignIn", false);
                    if (continueSignIn) {
                        loginPres.continueWithFirstTimeSignIn();
                    }
            }

        }

    }

    public void confirmUser() {
        Intent confirmActivity = new Intent(this, Activity_SignUp_Confirm.class);
        confirmActivity.putExtra("name", userIDView.getText().toString());
        startActivityForResult(confirmActivity, 2);
    }

    public void firstTimeSignIn() {
        Intent newPasswordActivity = new Intent(this, NewPasswordActivity.class);
        newPasswordActivity.putExtra("user name", userIDView.getText().toString());
        startActivityForResult(newPasswordActivity, 6);
    }

    public EditText getUserIDView()
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

    public void onPostExecute(String response) {
        dialog.dismiss();
        if (response != null) {
            showDialogMessage("Facebook login", "Hello " + response);
        } else {
            showDialogMessage("Facebook login", "Unable to get user name from Facebook");
        }
    }

    public void clearFocus(){
        View current = getCurrentFocus();
        if (current != null){
            current.clearFocus();
        }
    }

    public void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }
    public void startDelay()
    {
        final AlertDialog lockDialog = new AlertDialog.Builder(LoginActivity.this).create();
        lockDialog.setCancelable(false);
        long [] grecord = GuestSession.getRecord();
        Log.e("Delay check3", String.valueOf(grecord[0]) + " " + String.valueOf(grecord[1]));
        long milliseconds = grecord[1];
        long seconds = milliseconds/1000;
        long minutes = seconds / 60;
        seconds     = seconds % 60;
        lockDialog.setTitle("Too many password attempts!");
        lockDialog.setMessage("You can try again after "+minutes+" min "+seconds+ " sec");
        lockDialog.show();
        new CountDownTimer(milliseconds, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                long [] grecord2 = GuestSession.getRecord();
                grecord2[1] = millisUntilFinished;
                GuestSession.updateNumTries(grecord2);
                long milliseconds = millisUntilFinished;
                long seconds = milliseconds/1000;
                long minutes = seconds / 60;
                seconds     = seconds % 60;
                lockDialog.setMessage("You can try again after "+minutes+" min "+seconds+ " sec");
            }

            @Override
            public void onFinish() {
                lockDialog.dismiss();
            }
        }.start();
    }
}
