package com.locadoc_app.locadoc;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

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
    //Presenter
    private SignUpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
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
        int n = presenter.checkEmail();
        if(n == 2){
            EmailView.setError(getString(R.string.error_field_required));
        }
        else if(n == 0) {
            EmailView.setError(getString(R.string.error_invalid_email));
        }
    }
    @OnFocusChange(R.id.firstname_signupform)
    public void FirstNameFocusChange()
    {
        FNameView.setError(null);
        if(!presenter.checkFName())
        {
            FNameView.setError(getString(R.string.error_field_required));
        }
    }
    @OnFocusChange(R.id.pwd_signupform)
    public void PwdFocusChange()
    {
        PwdView.setError(null);
        int n = presenter.isValidPassword();
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
        int n = presenter.checkPasswordSame();
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
        int n = presenter.CheckContactNum();
        if(n == 2)
        {
            contactNoView.setError(getString(R.string.error_field_required));
        }
        else if(n == 0)
        {
            contactNoView.setError(getString(R.string.error_invalid_contact));
        }
    }
    public void loadAlertDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(SignUp.this).create();
        alertDialog.setTitle(getString(R.string.SignUp_success_title));
        alertDialog.setMessage(getString(R.string.SignUp_success_body));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
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

}
