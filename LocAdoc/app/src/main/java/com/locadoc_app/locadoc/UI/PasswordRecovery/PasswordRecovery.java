package com.locadoc_app.locadoc.UI.PasswordRecovery;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.locadoc_app.locadoc.R;

/**
 * Created by DainoMix on 9/18/2017.
 */

public class PasswordRecovery extends AppCompatActivity implements PasswordRecoveryViewInterface{

    private EditText password;
    private EditText confrimPassword;
    private EditText verifiCode;

    private String email;

    private Button submit;
    private Button resendCode;

    private AlertDialog userDialog;
    private PasswordRecoveryPresenterInterface presenter;

    /******************** Normal Method ********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordrecovery);
        setTitle("Forgot your Password?");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        presenter = new PasswordRecoveryPresenter(this);
        init();
    }

    public void init() {

        /* Update pwdrecovery_desc2 depends on the user's Email Address */
        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
            if (extras.containsKey("name")) {

                // Get the Email Address
                email = extras.getString("name");
                TextView message = (TextView) findViewById(R.id.pwdrecovery_desc2);
                String textToDisplay = "Code to set a new password was sent to "+ email;
                message.setText(textToDisplay);
            }
        }
        presenter.forgotPassword(email);
        /* New Password */
        password = (EditText) findViewById(R.id.pwdrecovery_pwd);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setLabelPassword("");
                password.setBackground(getDrawable(R.drawable.text_border_selector));
                presenter.isValidPassword();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setLabelPassword("");
                presenter.isValidPassword();
            }
        });

        /* Verification Code */
        confrimPassword = (EditText) findViewById(R.id.pwdrecovery_confrimpwd);
        confrimPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setLabelConfirmPassword("");
                confrimPassword.setBackground(getDrawable(R.drawable.text_border_selector));
                presenter.checkPasswordSame();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setLabelConfirmPassword("");
                presenter.checkPasswordSame();
            }
        });

        /* Confirm New Password */
        verifiCode = (EditText) findViewById(R.id.pwdrecovery_verificode);
        verifiCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewVerifiCodeLabel);    //Label
                    label.setText(verifiCode.getHint());
                    verifiCode.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewVerifiCodeLabel);
                    label.setText("");
                }
            }
        });

        /* Submit Button */
        submit = (Button) findViewById(R.id.pwdrecovery_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.continueTask(email, password.getText().toString(),
                verifiCode.getText().toString());
            }
        });

    }

    public void exit(int result) {
        Intent intent = new Intent();
        intent.putExtra("result",result);
        setResult(RESULT_OK, intent);
        finish();
    }

    /******************** Getter and Setter ********************/

    public void setLabelUserEmail(String str) {
        TextView label = (TextView) findViewById(R.id.textViewEmailMessage);
        label.setText(str);
        //username.setBackground(getDrawable(R.drawable.text_border_error));
    }

    public void setLabelPassword(String str) {
        TextView label = (TextView) findViewById(R.id.textViewNewPasswordMessage);
        label.setText(str);
        password.setBackground(getDrawable(R.drawable.text_border_error));
    }
    public void setLabelPasswordOK(String str) {
        TextView label = (TextView) findViewById(R.id.textViewNewPasswordMessage);
        label.setText(str);
        password.setBackground(getDrawable(R.drawable.text_border_selector));
    }
    public void setLabelConfirmPassword(String str) {
        TextView label = (TextView) findViewById(R.id.textViewConfirmNewPasswordMessage);
        label.setText(str);
        confrimPassword.setBackground(getDrawable(R.drawable.text_border_error));
    }
    public void setLabelConfirmPasswordOK(String str) {
        TextView label = (TextView) findViewById(R.id.textViewConfirmNewPasswordMessage);
        label.setText(str);
        confrimPassword.setBackground(getDrawable(R.drawable.text_border_selector));
    }
    public void setLabelVerifiCode(String str) {
        TextView label = (TextView) findViewById(R.id.textViewVerifiCodeMessage);
        label.setText(str);
        verifiCode.setBackground(getDrawable(R.drawable.text_border_error));
    }
    public String getEmail() {
        return this.email;
    }


    public EditText getPwdView() {
        return password;
    }
    public EditText getRPwdView() {
        return confrimPassword;
    }



}
