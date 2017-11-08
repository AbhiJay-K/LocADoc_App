package com.locadoc_app.locadoc.UI.ConfirmSignUp;

import android.content.DialogInterface;
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

public class Activity_SignUp_Confirm extends AppCompatActivity implements SignUp_Confirm_View_Interface {
    private TextView username;
    private EditText confCode;
    private Button confirm;
    TextView reqCode;

    String userName;
    String password;
    String fName;
    String lName;
    private AlertDialog userDialog;
    private SignUp_Confirm_Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__sign_up__confirm);
        setTitle("Confirm your account");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        presenter = new SignUp_Confirm_Presenter(this);
        init();
    }
    private void init()
    {
        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
            if(extras.containsKey("name")) {
                userName = extras.getString("name");
                username = (TextView) findViewById(R.id.editTextConfirmUserId);
                username.setText(userName);
                password = extras.getString("pwd");
                fName = extras.getString("fName");
                lName = extras.getString("lName");
                confCode = (EditText) findViewById(R.id.editTextConfirmCode);
                confCode.requestFocus();

                if(extras.containsKey("destination")) {
                    String dest = extras.getString("destination");
                    String delMed = extras.getString("deliveryMed");

                    TextView screenSubtext = (TextView) findViewById(R.id.textViewConfirmSubtext_1);
                    if(dest != null && delMed != null && dest.length() > 0 && delMed.length() > 0) {
                        screenSubtext.setText("A confirmation code was sent to "+dest+" via "+delMed);
                    }
                    else {
                        screenSubtext.setText("A confirmation code was sent");
                    }
                }
            }
            else {
                TextView screenSubtext = (TextView) findViewById(R.id.textViewConfirmSubtext_1);
                screenSubtext.setText("Request for a confirmation code or confirm with the code you already have.");
            }

        }
        username = (TextView) findViewById(R.id.editTextConfirmUserId);

        confCode = (EditText) findViewById(R.id.editTextConfirmCode);
        confCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewConfirmCodeLabel);
                    label.setText(confCode.getHint());
                    confCode.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewConfirmCodeMessage);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewConfirmCodeLabel);
                    label.setText("");
                }
            }
        });

        confirm = (Button) findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.sendConfCode(username.getText().toString(),confCode.getText().toString()
                        ,username.getHint().toString(),confCode.getHint().toString());
            }
        });

        reqCode = (TextView) findViewById(R.id.resend_confirm_req);
        reqCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.reqConfCode(username.getText().toString(),username.getHint().toString());
            }
        });
    }

    public void showDialogMessage(String title, String body, final boolean exitActivity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if(exitActivity) {
                        exit();
                    }
                } catch (Exception e) {
                    exit();
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public void setLabelConfirmUserID(String str)
    {
        TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
        label.setText(str);
        username.setBackground(getDrawable(R.drawable.text_border_error));
    }

    public void setLabelConfirmCode(String str)
    {
        TextView label = (TextView) findViewById(R.id.textViewConfirmCodeMessage);
        label.setText(str);
        confCode.setBackground(getDrawable(R.drawable.text_border_error));
    }
    public String getUsername() {
        return username.getText().toString();
    }

    public void setUsername(TextView username) {
        this.username = username;
    }

    public String getConfCode() {
        return confCode.getText().toString();
    }

    public void setConfCode(EditText confCode) {
        this.confCode = confCode;
    }
    public void SetConfhandlerMessage()
    {
        TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
        label.setText("Confirmation failed!");
        username.setBackground(getDrawable(R.drawable.text_border_error));

        label = (TextView) findViewById(R.id.textViewConfirmCodeMessage);
        label.setText("Confirmation failed!");
        confCode.setBackground(getDrawable(R.drawable.text_border_error));
    }
    public void SetReConfCodeHandlerSuccessMessage()
    {
        confCode = (EditText) findViewById(R.id.editTextConfirmCode);
        confCode.requestFocus();
    }
    public void SetReConfCodeHandlerFailMessage()
    {
        TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
        label.setText("Confirmation code resend failed");
        username.setBackground(getDrawable(R.drawable.text_border_error));
    }
    private void exit()
    {
        Intent intent = new Intent();
        if(userName == null)
            userName = "";
        intent.putExtra("name",userName);
        setResult(RESULT_OK, intent);
        finish();
    }
}
