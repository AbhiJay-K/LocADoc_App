package com.locadoc_app.locadoc.UI.Signup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.UI.ConfirmSignUp.Activity_SignUp_Confirm;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;


public class SignUp extends AppCompatActivity implements SignUPViewInterface{


    //Views bind to the user interface
    @BindView(R.id.email_signupform)
    AutoCompleteTextView EmailView;
    @BindView(R.id.firstname_signupform)
    AutoCompleteTextView FNameView;
    @BindView(R.id.lastname_signupform)
    AutoCompleteTextView LNameView;
    @BindView(R.id.pwd_signupform)
    EditText PwdView;
    @BindView(R.id.rpwd_signupform)
    EditText RPwdView;
    @BindView (R.id.cn_signupform)
    AutoCompleteTextView contactNoView;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;
    //Presenter
    private SignUpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button signUpBtn = (Button) findViewById(R.id.sign_up_btn);
        ButterKnife.bind(this);
        informPrecentor();
    }
    private void informPrecentor()
    {
        presenter = new SignUpPresenter();
        presenter.setView(this);
    }

    @OnClick(R.id.sign_up_btn)
   public void SignUpBtnClick(View v) {
        presenter.SignUpUser();
    }

    //on focus change events
    @OnFocusChange(R.id.email_signupform)
    public void EmailFocusChange(View v)
    {
        EmailView.setError(null);
        presenter.checkEmail();
    }

    public void setEmailError (int n)
    {
        if(n == 2)
        {
            EmailView.setError(getString(R.string.error_field_required));
        }
        else if(n == 0)
        {
            EmailView.setError(getString(R.string.error_invalid_email));
        }
    }

    @OnFocusChange(R.id.firstname_signupform)
    public void FirstNameFocusChange()
    {
        FNameView.setError(null);
        presenter.checkFName();
    }

    public void setFNameErr()
    {
        FNameView.setError(getString(R.string.error_field_required));
    }

    @OnFocusChange(R.id.pwd_signupform)
    public void PwdFocusChange()
    {
        PwdView.setError(null);
        presenter.isValidPassword();
    }

    public void setPwdErr (int n)
    {
        if(n == 2)
        {
            PwdView.setError(getString(R.string.error_field_required));
        }
        else if(n == 0)
        {
            PwdView.setError(getString(R.string.error_invalid_password));
        }
    }

    @OnFocusChange(R.id.rpwd_signupform)
    public void RPwdFocusChange()
    {
        RPwdView.setError(null);
        presenter.checkPasswordSame();
    }

    public void setRPwdErr (int n)
    {
        if(n == 2)
        {
            RPwdView.setError(getString(R.string.error_field_required));
        }
        if(n == 0)
        {
            RPwdView.setError(getString(R.string.error_RePassword_noMatch));
        }
    }

    @OnFocusChange(R.id.cn_signupform)
    public void ContactNumFocusChange()
    {
        contactNoView.setError(null);
        presenter.CheckContactNum();
    }

    public void setContactNumErr (int n)
    {
        if(n == 2)
        {
            contactNoView.setError(getString(R.string.error_field_required));
        }
        else if(n == 0)
        {
            contactNoView.setError(getString(R.string.error_invalid_contact));
        }
    }

    public void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if(exit) {
                        exit(EmailView.getText().toString(), null);
                    }
                } catch (Exception e) {
                    if(exit) {
                        exit(EmailView.getText().toString(), null);
                    }
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    public void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    public void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }

    public void confirmSignUp(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
        Intent intent = new Intent(this, Activity_SignUp_Confirm.class);
        intent.putExtra("source","signup");
        intent.putExtra("name", EmailView.getText().toString());
        intent.putExtra("destination", cognitoUserCodeDeliveryDetails.getDestination());
        intent.putExtra("deliveryMed", cognitoUserCodeDeliveryDetails.getDeliveryMedium());
        intent.putExtra("attribute", cognitoUserCodeDeliveryDetails.getAttributeName());
        startActivityForResult(intent, 10);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if(resultCode == RESULT_OK){
                String name = null;
                if(data.hasExtra("name")) {
                    name = data.getStringExtra("name");
                }
                exit(name, PwdView.getText().toString());
            }
        }
    }

    public AutoCompleteTextView getEmailView() {
        return EmailView;
    }

    public void setEmailView(AutoCompleteTextView emailView) {
        EmailView = emailView;
    }
    public AutoCompleteTextView getFNameView() {
        return FNameView;
    }

    public void setFNameView(AutoCompleteTextView FNameView) {
        this.FNameView = FNameView;
    }
    public AutoCompleteTextView getLNameView() {
        return LNameView;
    }

    public void setLNameView(AutoCompleteTextView LNameView) {
        this.LNameView = LNameView;
    }
    public EditText getPwdView() {
        return PwdView;
    }

    public void setPwdView(EditText pwdView) {
        PwdView = pwdView;
    }
    public EditText getRPwdView() {
        return RPwdView;
    }

    public void setRPwdView(EditText RPwdView) {
        this.RPwdView = RPwdView;
    }
    public AutoCompleteTextView getContactNoView() {
        return contactNoView;
    }

    public void setContactNoView(AutoCompleteTextView contactNoView) {
        this.contactNoView = contactNoView;
    }

    public void exit(String uname, String password) {
        Intent intent = new Intent();
        if (uname == null) {
            uname = "";
        }
        if (password == null) {
            password = "";
        }
        intent.putExtra("name", uname);
        intent.putExtra("password", password);
        setResult(RESULT_OK, intent);
        finish();
    }
}
