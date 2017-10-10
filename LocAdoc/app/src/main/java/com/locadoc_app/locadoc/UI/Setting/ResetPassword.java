package com.locadoc_app.locadoc.UI.Setting;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.locadoc_app.locadoc.R;

public class ResetPassword extends AppCompatActivity {

    private EditText curPwd, newPwd, confirmNewPwd, confirmCode;
    private Button submit;
    private ResetPasswordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setTitle("Reset Password");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        presenter = new ResetPasswordPresenter(this);
        init();
    }

    void init() {

        // Current Password + SQLHelper Need to compare
        curPwd = (EditText) findViewById(R.id.resetPwd_editTextCurPwd);
        curPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    // When finishing text input
                    setLabelCurPwd("");
                    curPwd.setBackground(getDrawable(R.drawable.text_border_selector));
                    presenter.isValidCurPwd();
                }
                else {
                    setLabelCurPwd("");
                    presenter.isValidCurPwd();
                }

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // When text is changed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    // Before the text input
                    setLabelCurPwd("");
                    presenter.isValidCurPwd();
                }
            }
        });


        // New Password
        newPwd = (EditText) findViewById(R.id.resetPwd_editTextNewPwd);
        newPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    // When finishing text input
                    setLabelNewPwd("");
                    curPwd.setBackground(getDrawable(R.drawable.text_border_selector));
                    presenter.isValidNewPwd();
                }
                else {
                    setLabelNewPwd("");
                    presenter.isValidCurPwd();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // When text is changed
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Before the text input
                setLabelNewPwd("");
                presenter.isValidNewPwd();
            }
        });

        // Confrim New Password
        confirmNewPwd = (EditText) findViewById(R.id.resetPwd_editTextConfirmNewPwd);
        confirmNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    // When finishing text input
                    setLabelConfirmNewPwd("");
                    curPwd.setBackground(getDrawable(R.drawable.text_border_selector));
                    presenter.isValidPwdWithNewPwd();
                }
                else {
                    setLabelConfirmNewPwd("");
                    presenter.isValidPwdWithNewPwd();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Before the text input
                setLabelConfirmNewPwd("");
                presenter.isValidPwdWithNewPwd();
            }
        });

                /* Submit Button */
        submit = (Button) findViewById(R.id.resetPwd_reset_pwd_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ResetPassword.this, "Clicked Submit Button", Toast.LENGTH_SHORT).show();
            }
        });

    }



    /* Getter and Settor */
    public EditText getCurPwd(){
        return this.curPwd;
    }
    public EditText getNewPwd() {
        return this.newPwd;
    }
    public EditText getConfirmNewPwd() {
        return this.confirmNewPwd;
    }

    public void setLabelCurPwd(String str) {
        TextView label = (TextView) findViewById(R.id.resetPwd_textViewCurPwdMessage);
        label.setText(str);
        curPwd.setBackground(getDrawable(R.drawable.text_border_error));
    }

    public void setLabelCurPwdOK(String str) {
        TextView label = (TextView) findViewById(R.id.resetPwd_textViewCurPwdMessage);
        label.setText(str);
        curPwd.setBackground(getDrawable(R.drawable.text_border_selector));
    }

    public void setLabelNewPwd(String str) {
        TextView label = (TextView) findViewById(R.id.resetPwd_textViewNewPwdMessage);
        label.setText(str);
        curPwd.setBackground(getDrawable(R.drawable.text_border_error));
    }

    public void setLabelNewPwdOK(String str) {
        TextView label = (TextView) findViewById(R.id.resetPwd_textViewNewPwdMessage);
        label.setText(str);
        curPwd.setBackground(getDrawable(R.drawable.text_border_selector));
    }

    public void setLabelConfirmNewPwd(String str) {
        TextView label = (TextView) findViewById(R.id.resetPwd_textViewConfirmNewPwdMessage);
        label.setText(str);
        curPwd.setBackground(getDrawable(R.drawable.text_border_error));
    }

    public void setLabelConfirmNewPwdOK(String str) {
        TextView label = (TextView) findViewById(R.id.resetPwd_textViewConfirmNewPwdMessage);
        label.setText(str);
        curPwd.setBackground(getDrawable(R.drawable.text_border_selector));
    }


}
