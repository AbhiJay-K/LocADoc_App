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

import static android.R.attr.password;

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
        /*
        // Current Password + SQLHelper Need to compare
        curPwd = (EditText) findViewById(R.id.editTextCurPwd);
        curPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.editTextCurPwd);    //Label
                    label.setText(curPwd.getHint());
                    curPwd.setBackground(getDrawable(R.drawable.text_border_selector));
                    presenter.isValidPwd();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.editTextCurPwd);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.editTextCurPwd);
                    label.setText("");
                }
            }
        });


        // New Password
        newPwd = (EditText) findViewById(R.id.editTextNewPwd);
        newPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.editTextNewPwd);
                    label.setText(newPwd.getHint());
                    newPwd.setBackground(getDrawable(R.drawable.text_border_selected));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView label = (TextView) findViewById(R.id.editTextNewPwd);
                label.setText("");
            }
        });

        // Confrim New Password
        confirmNewPwd = (EditText) findViewById(R.id.editTextConfirmNewPwd);
        confirmNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.editTextConfirmNewPwd);
                    label.setText(newPwd.getHint());
                    newPwd.setBackground(getDrawable(R.drawable.text_border_selected));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView label = (TextView) findViewById(R.id.editTextConfirmNewPwd);
                label.setText("");
            }
        });


        // Confirmation Code
        confirmCode = (EditText) findViewById(R.id.editTextConfirmCode);
        confirmCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                TextView label = (TextView) findViewById(R.id.editTextConfirmCode);
                label.setText(newPwd.getHint());
                newPwd.setBackground(getDrawable(R.drawable.text_border_selected));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView label = (TextView) findViewById(R.id.editTextConfirmCode);
                label.setText("");
            }
        });

                /* Submit Button */
        submit = (Button) findViewById(R.id.reset_pwd_button);
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

    public void setLabelCurPwd(String str) {
        TextView label = (TextView) findViewById(R.id.textViewConfirmCurPwdMessage);
        label.setText(str);
        curPwd.setBackground(getDrawable(R.drawable.text_border_error));
    }

    public void setLabelCurPwdOK(String str) {
        TextView label = (TextView) findViewById(R.id.textViewConfirmCurPwdMessage);
        label.setText(str);
        curPwd.setBackground(getDrawable(R.drawable.text_border_selector));
    }


}
