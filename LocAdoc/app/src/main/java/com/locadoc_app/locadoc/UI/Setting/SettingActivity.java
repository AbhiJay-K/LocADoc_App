package com.locadoc_app.locadoc.UI.Setting;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.locadoc_app.locadoc.R;

import static com.locadoc_app.locadoc.R.id.setting_menuList;

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

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.item_settings, R.id.textViewList, settingMenuListArray); 	// textViewList is in itemSettings
        ListView listView = (ListView) findViewById(setting_menuList);
        listView.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
            if (extras.containsKey("name")) {
                userName = extras.getString("name");
            }
        }

        text_userName = (TextView) findViewById(R.id.profile_usrEmail);
        text_userName.setText(userName);
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
