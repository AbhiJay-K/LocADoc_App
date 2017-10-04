package com.locadoc_app.locadoc.UI.Setting;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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

import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.UI.HomePage.HomePageActivity;

public class SettingActivity extends AppCompatActivity  {

    private ListView listView;
    private String userName;
    private TextView text_userName;
    String[] settingMenuListArray = {"Phone Number", "Password", "Set Administration Area", "Backup", "Delete Account"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);

        ListView listview;
        SettingListViewAdapter adapter = new SettingListViewAdapter();

        listview = (ListView) findViewById(R.id.setting_menuList);
        listview.setAdapter(adapter);

        adapter.addItem(settingMenuListArray[0], "subItem for phone Number");
        adapter.addItem(settingMenuListArray[1], "subItem for ResetPassword");
        adapter.addItem(settingMenuListArray[2], "subItem for admin area");
        adapter.addItem(settingMenuListArray[3], "");
        adapter.addItem(settingMenuListArray[4], "");


        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
            if (extras.containsKey("name")) {
                userName = extras.getString("name");
            }
        }

        text_userName = (TextView) findViewById(R.id.profile_usrEmail);
        text_userName.setText(userName);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                SettingListViewItem item = (SettingListViewItem) parent.getItemAtPosition(position);

                String titleStr = item.getTitle();
                String descStr = item.getDesc();

                Log.d(Integer.toString(position), titleStr + "|" + descStr);
                Toast.makeText(SettingActivity.this, titleStr, Toast.LENGTH_SHORT).show();

                switch(position) {
                    case 0: openPhoneNumberActivity();
                        break;
                    case 1:	openResetPasswordActivity();
                        break;
                    case 2: openSetAdminAreaActivity();
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
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
        Intent homeActivity = new Intent(this, HomePageActivity.class);
       // homeActivity.putExtra("name", userIDView.getText().toString());
        startActivity(homeActivity);    	
    }

    public void openResetPasswordActivity() {
        Intent homeActivity = new Intent(this, HomePageActivity.class);
       // homeActivity.putExtra("name", userIDView.getText().toString());
        startActivity(homeActivity);
    }

    public void openSetAdminAreaActivity() {
        
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
