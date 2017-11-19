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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.helper.Connectivity;
import com.locadoc_app.locadoc.helper.Hash;

public class ResetPassword extends AppCompatActivity implements ResetPasswordViewInterface {

    private EditText curPwd, newPwd, confirmNewPwd;
    private Button submit;
    private ResetPasswordPresenter presenter;
    private AlertDialog aDialog;
    private ProgressDialog pDialog;
    private boolean logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        logout = true;
        setTitle("Reset Password");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        presenter = new ResetPasswordPresenter(this);

        TextView text = (TextView) findViewById(R.id.resetPwd_textViewConfirmSubtext_1);
        text.setText("Reset Password in " + Credential.getEmail());

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
                if(!Connectivity.isNetworkAvailable()){
                    showNetErrToast();
                    return;
                }

                if(confirmNewPwd.getText().toString().equals(newPwd.getText().toString())) {

                    // Check Current Password
                    //String hash = Hash.Hash(curPwd.getText().toString(), Credential.getPassword().getSalt());
                    //if(!hash.equals(Credential.getPassword().getPassword())){
                    //    Toast.makeText(ResetPassword.this, "Wrong password", Toast.LENGTH_SHORT).show();
                    //    return;
                    //}

                    presenter.changePassword();
                }
                else {
                    Toast.makeText(ResetPassword.this, "Confirmed New Password doesn't match with New Password", Toast.LENGTH_SHORT).show();
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

    public void showNetErrToast(){
        Toast.makeText(this, "Can not connect to internet. Please check your connection!",
                Toast.LENGTH_SHORT).show();
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
        TextView label = (TextView) findViewById(R.id.resetPwd_textViewConfirmNewPwdMessage);
        label.setText(str);
        confirmNewPwd.setBackground(getDrawable(R.drawable.text_border_error));
    }

    public void setLabelConfirmNewPwdOK(String str) {
        TextView label = (TextView) findViewById(R.id.resetPwd_textViewConfirmNewPwdMessage);
        label.setText(str);
        confirmNewPwd.setBackground(getDrawable(R.drawable.text_border_selector));
    }

    //  ---------------------------------------------------------------------------------
    public void showProgressDialog(String title, String msg) {
        pDialog = new ProgressDialog(ResetPassword.this);
        pDialog.setTitle(title);
        pDialog.setMessage(msg);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void dismissProgresDialog() {
        if(pDialog.isShowing()){
            pDialog.dismiss();
        }
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
                    if(result) {
                        logout = false;
                        exit(result);
                    }
                } catch (Exception e) {
                    //
                }
            }
        });
        aDialog = builder.create();
        aDialog.setCancelable(false);
        aDialog.show();
    }

    public void exit(boolean result) {
        Intent intent = new Intent();
        intent.putExtra("result",result);
        intent.putExtra("logout", logout);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onStop(){
        super.onStop();
        exit(false);
    }

    @Override
    public void onBackPressed(){
        logout = false;
        exit(false);
    }
}
