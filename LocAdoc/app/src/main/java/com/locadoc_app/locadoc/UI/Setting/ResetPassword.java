package com.locadoc_app.locadoc.UI.Setting;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.UI.ConfirmSignUp.SignUp_Confirm_Presenter;

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

        curPwd = (EditText) findViewById(R.id.editTextCurPwd);




    }
}
