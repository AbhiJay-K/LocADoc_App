package com.locadoc_app.locadoc.UI.NewPassword;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.locadoc_app.locadoc.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class NewPasswordActivity extends AppCompatActivity implements NewPasswordViewInterface {

    @BindView(R.id.NewPassword)
    EditText PwdView;
    @BindView(R.id.ReNewPassword)
    EditText RPwdView;
    @BindView(R.id.ConfirmButton)
    Button confirmButton;
    @BindView(R.id.TextNewPassLabel)
    TextView passLabel;
    @BindView(R.id.TextReNewPassLabel)
    TextView rePassLabel;
    @BindView(R.id.UserName)
    TextView userName;
    private NewPasswordPresenterInterface presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        ButterKnife.bind(this);
        presenter = new NewPasswordPresenter(this);
        Bundle extras = getIntent().getExtras();
        userName.setText(extras.getString("user name"));
    }

    @OnClick(R.id.ConfirmButton)
    public void ConfirmButtonClick(View v) {
        presenter.newPassSubmit();
    }

    @OnTextChanged (R.id.NewPassword)
    public void afterPassTextChanged(Editable s) {
        passLabel.setText("");
        presenter.isValidPassword();
    }

    public void setPwdErr (int n)
    {
        if(n == 2)
        {
            passLabel.setText(getString(R.string.error_field_required));
        }
        else if(n == 0)
        {
            passLabel.setText(getString(R.string.error_invalid_password));
        }
    }

    @OnTextChanged (R.id.ReNewPassword)
    public void afterRePassTextChanged(Editable s) {
        rePassLabel.setText("");
        presenter.checkPasswordSame();
    }

    public void setRPwdErr (int n)
    {
        if(n == 2)
        {
            rePassLabel.setText(getString(R.string.error_field_required));
        }
        if(n == 0)
        {
            rePassLabel.setText(getString(R.string.error_RePassword_noMatch));
        }
    }

    public EditText getPwdView() {
        return PwdView;
    }
    public EditText getRPwdView() {
        return RPwdView;
    }

    public void exit(boolean continueWithSignIn)
    {
        Intent intent = new Intent();
        intent.putExtra("continueSignIn", continueWithSignIn);
        setResult(RESULT_OK, intent);
        finish();
    }
}
