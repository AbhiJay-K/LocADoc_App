package com.locadoc_app.locadoc.UI.Setting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.LocalDB.UserSQLHelper;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.R;

import static com.locadoc_app.locadoc.R.id.CreateNewAreaBtn;
import static com.locadoc_app.locadoc.R.id.UserName;
import static com.locadoc_app.locadoc.R.id.profile_usrEmail;

public class SettingActivity extends AppCompatActivity  {

    private ListView listView;
    private String userEmail;
    private TextView text_userEmail;
    String[] settingMenuListArray = {"Phone Number", "Password", "Set Administration Area", "Backup", "Delete Account"};

    private SettingActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Presenter Setting
        presenter = new SettingActivityPresenter(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        // toolbar.setNavigationIcon(R.drawable.ic_setting_back_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        init();
    }

    public void init() {
        Log.d("CREDENTIALCHECK","Setting Activity Email: " + Credential.getEmail() + "\t Password: " + Credential.getPassword().getPassword());

        User user = UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());
        String firstName = user.getFirstname();
        String lastName = user.getLastname();
        String userName = firstName + " " + lastName;

        TextView userNameTextView = (TextView) findViewById(R.id.profile_usrName);
        userNameTextView.setText(userName);

        presenter.profileName(user.getFirstname(), user.getLastname());

        Log.d("SEPERATE" , "=======================================================================");
        Log.d("USER INFO", "Info: " + user.getLastname() + " " + user.getFirstname());
        Log.d("SEPERATE" , "=======================================================================");

        SettingListViewAdapter adapter = new SettingListViewAdapter();

        listView = (ListView) findViewById(R.id.setting_menuList);
        listView.setAdapter(adapter);

        adapter.addItem(settingMenuListArray[0], "subItem for phone Number");
        adapter.addItem(settingMenuListArray[1], "********");
        adapter.addItem(settingMenuListArray[2], "subItem for admin area");
        adapter.addItem(settingMenuListArray[3], "");
        adapter.addItem(settingMenuListArray[4], "");

        Bundle extras = getIntent().getExtras();

        if (extras !=null) {
            if (extras.containsKey("name")) {
                userEmail = extras.getString("name");
            }
        }

        text_userEmail = (TextView) findViewById(profile_usrEmail);
        text_userEmail.setText(userEmail);

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
                    case 0: openPhoneNumberActivity();      // Activity Num: 30
                        break;
                    case 1:	openResetPasswordActivity();    // Activity Num: 31
                        break;
                    case 2: openSetAdminAreaActivity();     // Activity Num: 32
                        break;
                    case 3:                                 // Activity Num: 33
                        break;
                    case 4:                                 // Activity Num: 34
                        break;
                }
            }
        });

        TextView tvBack = (TextView) findViewById(R.id.toolbar_setting_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_action_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.setting_save:
                Toast.makeText(SettingActivity.this, "Save option is selected to save modification in Setting", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void openPhoneNumberActivity() {
        // Intent homeActivity = new Intent(this, HomePageActivity.class);
        // homeActivity.putExtra("name", userIDView.getText().toString());
        // startActivity(homeActivity);
    }

    public void openResetPasswordActivity() {
        Intent resetPassword = new Intent(this, ResetPassword.class);
        resetPassword.putExtra("Email", text_userEmail.getText().toString());
        startActivityForResult(resetPassword, 30);
    }

    public void openSetAdminAreaActivity() {
        // Intent homeActivity = new Intent(this, HomePageActivity.class);
        // homeActivity.putExtra("name", userIDView.getText().toString());
        // startActivity(homeActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // Make sure the request was successful
       if (resultCode == RESULT_OK) {        // Check which request we're responding to
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
                   User userInSQLite = UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());
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

    public void setProfileInitial(String inital) {
        TextView profileText = (TextView) findViewById(R.id.profile_text);
        profileText.setText(inital);
    }

    public void exit(){
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
    }

    /********** View List Method **********/



    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_page, menu);
        username = (TextView) findViewById(R.id.USerEmail);
        username.setText(userName);

        MenuItem searchMenuItem = menu.findItem(R.id.toolbar);
        if (searchMenuItem == null) {
            return true;
        }

        searchView = (SearchView) searchMenuItem.getActionView();
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Set styles for expanded state here
                if (getSupportActionBar() != null) {
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Set styles for collapsed state here
                if (getSupportActionBar() != null) {

                }
                return true;
            }
        });

        return true;
    }
    */



}
