package com.locadoc_app.locadoc.UI.Setting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.R;

import static android.R.attr.label;

public class ResetPassword extends AppCompatActivity implements ResetPasswordViewInterface {

    private EditText curPwd, newPwd, confirmNewPwd;
    private Button submit;
    private ResetPasswordPresenter presenter;
    private AlertDialog aDialog;
    private ProgressDialog pDialog;

    private boolean confPwdStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setTitle("Reset Password");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        presenter = new ResetPasswordPresenter(this);

        TextView text = (TextView) findViewById(R.id.resetPwd_textViewConfirmSubtext_1);
        text.setText("Reset Password in " + Credential.getEmail());

        Log.d("SQLITEHELPER","SettingActivity to ResetPasswordPresenter--------------------------------------------------------------");
        Log.d("SQLITEHELPER","User Credential Password: " + Credential.getPassword().getPassword());
        Log.d("SQLITEHELPER","SettingActivity to ResetPasswordPresenter--------------------------------------------------------------");


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

                Log.d("SUBMIT BUTTON CALL", "FOR RESET PASSWORD");


                if(confPwdStatus) {
                    Toast.makeText(ResetPassword.this, "Clicked Submit Button", Toast.LENGTH_SHORT).show();
                    presenter.changePassword();
                }
                else {
                    Toast.makeText(ResetPassword.this, "Confirmed New Password is not match with New Password", Toast.LENGTH_SHORT).show();
                }
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
        newPwd.setBackground(getDrawable(R.drawable.text_border_error));
    }

    public void setLabelNewPwdOK(String str) {
        TextView label = (TextView) findViewById(R.id.resetPwd_textViewNewPwdMessage);
        label.setText(str);
        newPwd.setBackground(getDrawable(R.drawable.text_border_selector));
    }

    public void setLabelConfirmNewPwd(String str) {

        confPwdStatus = false;

        TextView label = (TextView) findViewById(R.id.resetPwd_textViewConfirmNewPwdMessage);
        label.setText(str);
        confirmNewPwd.setBackground(getDrawable(R.drawable.text_border_error));
    }

    public void setLabelConfirmNewPwdOK(String str) {

        confPwdStatus = true;

        TextView label = (TextView) findViewById(R.id.resetPwd_textViewConfirmNewPwdMessage);
        label.setText(str);
        confirmNewPwd.setBackground(getDrawable(R.drawable.text_border_selector));
    }

    //  ---------------------------------------------------------------------------------
    public void showProgressDialog(String title, String msg) {
        Log.d("PROGRESSDIALOG","Progress Dialog is executed");

        pDialog = new ProgressDialog(ResetPassword.this);
        pDialog.setTitle(title);
        pDialog.setMessage(msg);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void dismissProgresDialog() {
        if(pDialog.isShowing()){
            Log.d("PROGRESSDIALOG","Progress Dialog is quit");
            pDialog.dismiss();
        }
    }

    public void ToastMessage(Exception e) {
        Toast.makeText(ResetPassword.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    //  ---------------------------------------------------------------------------------

    public void showDialogMessage(String title, String body, boolean status) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final boolean result = status;

        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    aDialog.dismiss();
                    exit(result);
                } catch (Exception e) {
                    //
                }
            }
        });
        aDialog = builder.create();
        aDialog.show();
    }

    public void exit(boolean result) {
        if(result) {
            Intent intent = new Intent();
            intent.putExtra("result",result);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}
