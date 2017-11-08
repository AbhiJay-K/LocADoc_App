package com.locadoc_app.locadoc.UI.Setting;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.LocalDB.UserSQLHelper;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.S3.S3Helper;

import java.util.List;

import static com.locadoc_app.locadoc.Model.Credential.getEmail;
import static com.locadoc_app.locadoc.R.id.profile_usrEmail;
import static com.locadoc_app.locadoc.R.id.profile_usrName;

public class SettingActivity extends AppCompatActivity implements SettingActivityViewInterface {

    // Setting Activity
    private ListView listView;
    private String userEmail;
    private TextView text_userEmail;
    private AlertDialog userDialog;
    // String[] settingMenuListArray = {"User Name", "Password", "Download backup","Storage used"};
    private String[] settingMenuListArray = {"User Name", "Password", "Download backup"};

    // Change User Name Dialog
    private EditText dialog_FirstName, dialog_LastName;
    private ProgressDialog pDialog;

    // File Recovery with S3
    private List<Integer> allFileId;
    private int noOfFilesProcessesed;
    private String currentFileName;
    private TransferObserver observer;
    private boolean continueDownload;
    private boolean logout;

    private SettingPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        logout = true;

        // Presenter Setting
        presenter = new SettingPresenter(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        init();
    }

    public void init() {
        Log.d("CREDENTIALCHECK","Setting Activity Email: " + getEmail() + "\t Password: " + Credential.getPassword().getPassword());

        // User Name
        User user = presenter.getUser();
        String userName = user.getFirstname().concat(" ").concat(user.getLastname());
        setUserNameTextView(userName, user);

        // User Email ID
        text_userEmail = (TextView) findViewById(profile_usrEmail);
        text_userEmail.setText(presenter.getEmail());

        // Storage Usage
        TextView text_Usage = (TextView) findViewById(R.id.setting_storage_usage);

        String size = "0KB";
        if(!user.getTotalsizeused().isEmpty()) {
            size = S3Helper.getBytesString(Long.parseLong(user.getTotalsizeused()));
        }
        size = size.concat(" of 1GB used");

        text_Usage.setText(size);

        SettingListViewAdapter adapter = new SettingListViewAdapter();

        /*
        String size = "0KB";
        if(!user.getTotalsizeused().isEmpty()) {
            size = S3Helper.getBytesString(Long.parseLong(user.getTotalsizeused()));
        }
        size = size.concat(" of 1GB used");
        */

        listView = (ListView) findViewById(R.id.setting_menuList);
        listView.setAdapter(adapter);

        adapter.addItem(settingMenuListArray[0], "Change Name");
        adapter.addItem(settingMenuListArray[1], "********");
        adapter.addItem(settingMenuListArray[2], "Recover files from cloud");
        //adapter.addItem(settingMenuListArray[3], size);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                SettingListViewItem item = (SettingListViewItem) parent.getItemAtPosition(position);

                String titleStr = item.getTitle();
                String descStr = item.getDesc();

                Log.d(Integer.toString(position), titleStr + "|" + descStr);
                Toast.makeText(SettingActivity.this, titleStr, Toast.LENGTH_SHORT).show();

                switch(position) {
                    case 0: changeUserName();      // Activity Num: 30
                        break;
                    case 1:	openResetPasswordActivity();    // Activity Num: 31
                        break;
                    case 2: confirmRecover();               // Activity Num: 32
                        break;
                    case 3:                // Activity Num: 32
                            break;
                }
            }
        });

        TextView tvUserName = (TextView) findViewById(R.id.profile_usrName);
        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserName();
            }
        });

        TextView tvBack = (TextView) findViewById(R.id.toolbar_setting_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout = false;
                exit();
            }
        });

    }

    // ------------------------------------------------------------------------------
    //             Dialog Update User First Name and Last NAme
    // ------------------------------------------------------------------------------
    public void changeUserName() {
        // INTERNAL DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater(); // builder.setView(inflater.inflate(R.layout.dialog_update_username, null))
        final View builderView = inflater.inflate(R.layout.dialog_update_username, null);
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("cancel",null);
        builder.setView(builderView);

        // SET CURRENT NAME INTO EDIT TEXT
        User usr = presenter.getUser();
        dialog_FirstName = (EditText) builderView.findViewById(R.id.updateUserName_FirstName);
        dialog_FirstName.setText(usr.getFirstname());
        dialog_LastName = (EditText) builderView.findViewById(R.id.updateUserName_LastName);
        dialog_LastName.setText(usr.getLastname());

        // TEXT WATCHER: Do not Allow new line as input
        dialog_FirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                for(int i = s.length(); i > 0; i--) {
                    if(s.subSequence(i-1, i).toString().equals("\n"))
                        s = s.replace(i-1, i, "");
                }
            }
        });

        dialog_LastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                for(int i = s.length(); i > 0; i--) {
                    if(s.subSequence(i-1, i).toString().equals("\n"))
                        s = s.replace(i-1, i, "");
                }
            }
        });

        // DIALOG SHOW
        final AlertDialog changeNameDialog = builder.create();
        changeNameDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                // POSITIVE SUBMIT
                Button positive = changeNameDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        Log.d("CHANGENAME", "First Name: " + dialog_FirstName.getText().toString());
                        Log.d("CHANGENAME", "Last Name: " + dialog_LastName.getText().toString());

                        switch(presenter.validName(dialog_FirstName.getText().toString(), dialog_LastName.getText().toString(), builderView)) {
                            case 0: Log.d("CHANGENAME", "FIRST NAME IS EMPTY!");
                                    break;
                            case 1: Log.d("CHANGENAME", "LAST NAME IS EMPTY!");
                                    break;
                            case 2: Log.d("CHANGENAME", "SAME NAME");
                                    changeNameDialog.dismiss();
                                    break;
                            case 3: Log.d("CHANGENAME", "UPDATE IN NAME");
                                    showProgressDialog("Change Name","Updating Name into Server...");
                                    presenter.changeToNewName(dialog_FirstName.getText().toString(), dialog_LastName.getText().toString());
                                    changeNameDialog.dismiss();
                                    break;
                        }

                    }
                });

                // NEGATIVE SUBMIT
                Button negative = changeNameDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeNameDialog.dismiss();
                    }
                });
            }

        });

        changeNameDialog.show();
    }

    public void openResetPasswordActivity() {
        Intent resetPassword = new Intent(this, ResetPassword.class);
        resetPassword.putExtra("Email", text_userEmail.getText().toString());
        logout = false;
        startActivityForResult(resetPassword, 30);
    }

    public void confirmRecover(){
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Start File Recovery").setMessage("Do you wish to recover all missing files from backup?" +
                "\n(Wifi Recommended)")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            userDialog.dismiss();
                            recoverAllMissingFiles();
                        } catch (Exception e) {}
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            userDialog.dismiss();
                        } catch (Exception e) {}
                    }
                });
        userDialog = builder.create();
        userDialog.show();
    }

    public void recoverAllMissingFiles(){
        noOfFilesProcessesed = 0;
        allFileId = FileSQLHelper.getAllFileID();
        continueDownload = true;

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Downloading All Files...")
                .setMessage("")
                .setNeutralButton("Cancel Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            continueDownload = false;
                            S3Helper.getUtility().cancel(observer.getId());
                            userDialog.dismiss();
                        } catch (Exception e) {}
                    }
                });
        userDialog = builder.create();
        userDialog.setCancelable(false);
        userDialog.show();

        updateDownloadProgress();
    }

    public void updateDownloadProgress(){
        boolean downloading = false;

        while(noOfFilesProcessesed < allFileId.size() && !downloading){
            userDialog.setMessage("Downloading " + (noOfFilesProcessesed + 1) + "/" + allFileId.size() + " files");
            File fileInfo = FileSQLHelper.getFile(allFileId.get(noOfFilesProcessesed), Credential.getPassword());
            String key = fileInfo.getCurrentfilename();
            java.io.File file = new java.io.File(LocAdocApp.getContext().getFilesDir().getAbsolutePath()+"/vault/" + key);

            if(!file.exists()){
                currentFileName = fileInfo.getOriginalfilename();
                beginDownload(key, file);
                downloading = true;
            }

            noOfFilesProcessesed++;
        }

        if(noOfFilesProcessesed >= allFileId.size() && !downloading){
            userDialog.dismiss();
            userDialog = null;

            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle("Download Finished")
                    .setMessage("ALl files have been downloaded");
            userDialog = builder.create();
            userDialog.setCancelable(true);
            userDialog.show();
        }
    }

    public void beginDownload(String key, java.io.File file){
        if(!continueDownload){
            return;
        }

        key = Credential.getIdentity() + "/" + key;
        observer = S3Helper.getUtility().download(S3Helper.BUCKET_NAME, key, file);
        observer.setTransferListener(new DownloadListener());
    }

    private class DownloadListener implements TransferListener {
        // Simply updates the list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e("LocAdoc", "onError: " + id, e);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            String current = S3Helper.getBytesString(bytesCurrent);
            String total = S3Helper.getBytesString(bytesTotal);

            userDialog.setMessage("Downloading " + noOfFilesProcessesed  + "/" + allFileId.size() + " files" +
                    "\n" + currentFileName + ": " + current + "/" + total);
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if(state == TransferState.COMPLETED){
                updateDownloadProgress();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // Make sure the request was successful
       if (resultCode == RESULT_OK) {        // Check which request we're responding to
           boolean logout = data.getBooleanExtra("logout", true);
           if(logout){
               exit();
           }

           switch(requestCode) {
               case 30:
                   break;
               case 31:
                   boolean result = data.getBooleanExtra("result", false);
                   Log.d("RECEIVING RESULT","RESULT IS " + result);

                   if(result) {
                       Toast.makeText(SettingActivity.this, "Success to Change Password", Toast.LENGTH_SHORT).show();
                   }

                   Log.d("SQLITEHELPER","ResetPassword to SettingActivity--------------------------------------------------------------");
                   User userInSQLite = UserSQLHelper.getRecord(getEmail(), Credential.getPassword());
                   Log.d("SQLITEHELPER","User Email: " + userInSQLite.getUser() + " | User Name: " + userInSQLite.getLastname() + " " + userInSQLite.getFirstname());
                   Log.d("SQLITEHELPER","User Credential Password: " + Credential.getPassword().getPassword());
                   Log.d("SQLITEHELPER","ResetPassword to SettingActivity--------------------------------------------------------------");
                   break;
               case 32:
                   break;
               case 33:
                   break;
               case 34:
                   break;
           }
        }
    }

    // ------------------------------------------------------------------------------
    //                              Accessor and Mutator
    // ------------------------------------------------------------------------------
    public void setProfileInitial(String inital) {
        TextView profileText = (TextView) findViewById(R.id.profile_text);
        profileText.setText(inital);
    }

    public void setUserNameTextView(String str, User user) {
        TextView userNameTextView = (TextView) findViewById(profile_usrName);
        userNameTextView.setText(str);

        presenter.profileName(user.getFirstname(), user.getLastname());
    }

    public void setLabelFirstName(String str, View v) {
        TextView label = (TextView) v.findViewById(R.id.updateUserName_Message);
        label.setText(str);
        dialog_FirstName.setBackground(getDrawable(R.drawable.text_border_error));
        dialog_LastName.setBackground(getDrawable(R.drawable.text_border_selector));
    }

    public void setLabelLastName(String str, View v) {
        TextView label = (TextView) v.findViewById(R.id.updateUserName_Message);
        label.setText(str);
        dialog_FirstName.setBackground(getDrawable(R.drawable.text_border_selector));
        dialog_LastName.setBackground(getDrawable(R.drawable.text_border_error));
    }

    public void setLabelFirstLastName(String str, View v) {
        TextView label = (TextView) v.findViewById(R.id.updateUserName_Message);
        label.setText(str);
        dialog_FirstName.setBackground(getDrawable(R.drawable.text_border_error));
        dialog_LastName.setBackground(getDrawable(R.drawable.text_border_error));
    }

    public void setLabelNameOK(String str, View v) {
        TextView label = (TextView) v.findViewById(R.id.updateUserName_Message);
        label.setText(str);
        dialog_FirstName.setBackground(getDrawable(R.drawable.text_border_selector));
        dialog_LastName.setBackground(getDrawable(R.drawable.text_border_selector));
    }

    public void showProgressDialog(String title, String msg) {
        Log.d("CHANGENAME","Progress Dialog is executed");

        pDialog = new ProgressDialog(SettingActivity.this);
        pDialog.setTitle(title);
        pDialog.setMessage(msg);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void dismissProgresDialog() {
        if(pDialog.isShowing()){
            Log.d("CHANGENAME","Progress Dialog is quit");
            pDialog.dismiss();
        }
    }

    public void exit(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        intent.putExtra("logout", logout);
        finish();
    }

    @Override
    public void onStop(){
        super.onStop();
        if(logout) {
            exit();
        } else{
            logout = true;
        }
    }

    @Override
    public void onBackPressed(){
        logout = false;
        exit();
    }
}
