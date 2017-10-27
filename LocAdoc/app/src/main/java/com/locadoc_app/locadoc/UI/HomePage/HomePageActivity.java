package com.locadoc_app.locadoc.UI.HomePage;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.DynamoDB.AreaDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.FileDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.UserDynamoHelper;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.LocalDB.UserSQLHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.S3.S3Helper;
import com.locadoc_app.locadoc.UI.About.AboutActivity;
import com.locadoc_app.locadoc.UI.PDFViewer.PDFViewer;
import com.locadoc_app.locadoc.UI.Setting.SettingActivity;
import com.locadoc_app.locadoc.helper.Encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import com.locadoc_app.locadoc.UI.About.AboutActivity;

public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ImportFileFragment.ImportFileFragmentListener,
        FileExplorerFragment.FileExplorerFragmentListener,
        GoogleMapFragment.GoogleMapFragmentListener,
        SearchView.OnQueryTextListener,
        NewAreaFragment.NewAreaFragmentListener,
        EditAreaFragment.EditAreaFragmentListener,
        FileOperationsFragment.FileOperationDialogListener,HomePage_View_Interface{

    private final int PICKFILE = 1;
    private final int SETTING = 10;
    private List<String> AreaList;
    private RecyclerView mRecyclerView;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private String userName;
    private TextView username;
    private boolean requestFocus;
    private boolean isEditArea;
    private SimpleCursorAdapter mAdapter;

    private GoogleMapFragment gMapFrag;
    private FileExplorerFragment fileExplorerFragment;
    private ImportFileFragment importFileFragment;
    private NewAreaFragment newAreaFragment;
    private EditAreaFragment editAreaFragment;
    private HomePagePresenter presenter;
    private AlertDialog userDialog;
    // create area
    private Uri filePathUri;
    private String deletetext;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        S3Helper.init();
        isEditArea = false;
        AreaList = AreaSQLHelper.getSearchValue();
        final String[] from = new String[] {"AreaName"};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(HomePageActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkLocationPermission()){
                buildGoogleApiClient();
            }
        } else{
            buildGoogleApiClient();
        }

        requestFocus = true;
        gMapFrag = new GoogleMapFragment();
        fileExplorerFragment = new FileExplorerFragment();
        importFileFragment = new ImportFileFragment();
        newAreaFragment = new NewAreaFragment();
        editAreaFragment = new EditAreaFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, gMapFrag).commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.areaContainer, importFileFragment).commit();

        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
            if (extras.containsKey("name")) {
                userName = extras.getString("name");
            }
        }
        presenter = new HomePagePresenter(this);
    }

    @Override
    public void showImportFileFragment(){
        if(!importFileFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.areaContainer, importFileFragment).commit();
        } else{
            importFileFragment.updateAreaAround();
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        FrameLayout layout = (FrameLayout) findViewById(R.id.areaContainer);
        layout.setLayoutParams(lp);
        gMapFrag.hideFAB();
    }

    @Override
    public void hideAreaFragmentContainer(){
        if(isEditArea){
            isEditArea = false;
            gMapFrag.performMarkerClick();
        }else{
            gMapFrag.clearCircle();
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0);
        FrameLayout layout = (FrameLayout) findViewById(R.id.areaContainer);
        layout.setLayoutParams(lp);
        gMapFrag.showFAB();
    }

    @Override
    public void showNewAreaFragment(){
        if(!newAreaFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.areaContainer, newAreaFragment).commit();
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        FrameLayout layout = (FrameLayout) findViewById(R.id.areaContainer);
        layout.setLayoutParams(lp);
        gMapFrag.hideFAB();
    }

    @Override
    public void showEditAreaFragment(String areaName){
        if (!editAreaFragment.isVisible()) {
            Bundle args = new Bundle();
            args.putString("areaname", areaName);
            editAreaFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.areaContainer, editAreaFragment).commit();
        } else{
            editAreaFragment.init(areaName);
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        FrameLayout layout = (FrameLayout) findViewById(R.id.areaContainer);
        layout.setLayoutParams(lp);
        gMapFrag.hideFAB();
        isEditArea = true;
    }

    @Override
    public void removeLastClickedMarker(){
        gMapFrag.removeLastClickedMarker();
        gMapFrag.clearCircle();
    }

    @Override
    public void openFileExplorer(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fileExplorerFragment).commit();
    }

    @Override
    public void showFileOperationFragment(int fileid){
        Bundle args = new Bundle();
        args.putInt("fileid", fileid);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FileOperationsFragment fileOperationsFragment = new FileOperationsFragment();
        fileOperationsFragment.setArguments(args);
        fileOperationsFragment.show(fragmentManager, "File Settings");
    }

    @Override
    public void removeFile (String filename){
        fileExplorerFragment.removeFile(filename);
    }

    @Override
    public void openGoogleMap(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, gMapFrag).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_page, menu);
        username = (TextView) findViewById(R.id.USerEmail);
        username.setText(userName);
        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        if (searchMenuItem == null) {
            return true;
        }
        SearchManager searchManager = (SearchManager)
                getSystemService(getApplicationContext().SEARCH_SERVICE);
        searchView = (SearchView) searchMenuItem.getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setSuggestionsAdapter(mAdapter);
        //searchView.setSubmitButtonEnabled(true);
        //searchView.setOnQueryTextListener(HomePageActivity.this);
        // Getting selected (clicked) item suggestion
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                String name = cursor.getString(1);
                Toast.makeText(HomePageActivity.this, name,
                        Toast.LENGTH_SHORT).show();
                Area ar = AreaSQLHelper.getAreaName(name,Credential.getPassword());
                Location l = new Location("");
                l.setLongitude(Double.parseDouble(ar.getLongitude()));
                l.setLatitude(Double.parseDouble(ar.getLatitude()));
                gMapFrag.focusCamera (l);
                searchMenuItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {return false;}
            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("Search1 ",s);
                populateAdapter(s);
                return false;
            }

        });

        /*MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
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
        });*/

        return true;
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu){

        User user = UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());
        String size = "0KB";

        if(!user.getTotalsizeused().isEmpty()) {
            size = S3Helper.getBytesString(Long.parseLong(user.getTotalsizeused()));
        }

        this.menu = menu;

        size = size + " of 1GB used";
        changeFileSizeUsed(size);

        return true;
    }*/

    // You must implements your logic to get data using OrmLite
    private void populateAdapter(String query) {
        //AreaList.clear();
        //AreaList = AreaSQLHelper.getSearchValue();
        mAdapter.notifyDataSetChanged();
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "AreaName" });
        for (int i=0; i<AreaList.size(); i++) {
            if (AreaList.get(i).toLowerCase().startsWith(query.toLowerCase())) {
                c.addRow(new Object[]{i, AreaList.get(i)});
            }
        }
        //mAdapter.changeCursor(c);
        mAdapter.swapCursor(c);
    }
   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
   @Override
   public boolean onQueryTextSubmit(String query) {
       return false;
   }

    @Override
    public boolean onQueryTextChange(String newText) {
        //friendListAdapter.getFilter().filter(newText);

        return true;
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_import) {
            // Handle the camera action
        } else if (id == R.id.nav_Settings) {

            Log.d("SQLITEHELPER","HomePageActivity To SettingActivty--------------------------------------------------------------");
            User userInSQLite = UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());
            Log.d("SQLITEHELPER","User Email: " + userInSQLite.getUser() + " | User Name: " + userInSQLite.getLastname() + " " + userInSQLite.getFirstname());
            Log.d("SQLITEHELPER","User Credential Password: " + Credential.getPassword().getPassword());
            Log.d("SQLITEHELPER","HomePageActivity To SettingActivty--------------------------------------------------------------");

            // Test code to access Setting Activity
            Intent settingActivity = new Intent(this, SettingActivity.class);
            settingActivity.putExtra("name", userName);
            startActivityForResult(settingActivity, SETTING);
            //startActivity(settingActivity);


        } else if (id == R.id.nav_about) {
            Intent AboutActivity = new Intent(this, AboutActivity.class);
            startActivity(AboutActivity);
        } else if (id == R.id.nav_faq) {
            Intent faqpage = new Intent(Intent.ACTION_VIEW, Uri.parse("https://locadoc.github.io/LocAdoc/FAQ/FAQ.html"));
            startActivity(faqpage);
        }else if(id == R.id.nav_DeleteAcc){
            showDeleteAccountDialog();
        }
        else if (id == R.id.nav_logout) {
            Logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeFileSizeUsed(String size)
    {
        MenuItem filesize = menu.findItem(R.id.nav_cloudusage);
        filesize.setTitle(size);
    }

    public void showDeleteAccountDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");
        builder.setMessage("Please eneter 'DELETE' to delete your account (!WARNING all your backup data will also be deleted)");
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletetext = input.getText().toString();
                if(deletetext.equals("DELETE"))
                {
                    presenter.deleteAccount();
                }
                else{
                   showDeleteAccountDialog();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    public void Logout()
    {
        presenter.stopTimer();
        Log.d("Logout","Logout called");
        AppHelper.getPool().getCurrentUser().signOut();
        mGoogleApiClient.disconnect();
        finish();
    }
    public void LogoutLastTime()
    {

        presenter.stopTimer();
        Log.d("Logout","Logout called");
        AppHelper.getPool().getCurrentUser().signOut();
        mGoogleApiClient.disconnect();
        finish();
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    //Check if the Location is from a moc location provider
    public boolean isMockSettingsON(Context context) {
        boolean isMock = false;
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            isMock = mLastLocation.isFromMockProvider();
        } else {
            if (Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
                return false;
            else {
                return true;
            }
        }
        return isMock;
    }
    /*public boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " , e.getMessage());
            }
        }

        if (count > 0) {
            Log.d("Logout MockApps","True");
            return true;
        }
        return false;
    }*/
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // -------------------------------------------------------------
    // IMPORTANT FUNCTION THAT CHECK USER CURRENT LOCATION EVERY TIME
    // -------------------------------------------------------------
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if(requestFocus){
            gMapFrag.focusCamera(location);
            requestFocus = false;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        if (gMapFrag != null){
                            gMapFrag.enableMyLocation();
                        }
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        //intent.setType("image/*");
        startActivityForResult(intent, PICKFILE);
    }

    @Override
    public void drawCircle (int radius){
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        gMapFrag.drawCircle(latLng, radius);
    }

    @Override
    public void drawCircle (LatLng latLng, int radius){
        gMapFrag.drawCircle(latLng, radius);
    }

    //  --------------------------------------------
    //            onActivityResult
    //  --------------------------------------------

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case PICKFILE:
                    if (resultData != null) {
                        filePathUri = resultData.getData();
                        String fileName = getFileName(filePathUri);
                        importFileFragment.setFileName(fileName);
                    }
                    break;
                case SETTING:
                    Log.d("SQLITEHELPER","SettingActivity to HomePageActivity--------------------------------------------------------------");
                    User userInSQLite = UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());
                    Log.d("SQLITEHELPER","User Email: " + userInSQLite.getUser() + " | User Name: " + userInSQLite.getLastname() + " " + userInSQLite.getFirstname());
                    Log.d("SQLITEHELPER","User Credential Password: " + Credential.getPassword().getPassword());
                    Log.d("SQLITEHELPER","SettingActivity to HomePageActivity--------------------------------------------------------------");

                    break;

            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void removeAreaFromList(String areaName){
        for(int i = 0; i < AreaList.size(); i++){
            String s = AreaList.get(i);
            if(areaName.equals(s)){
                AreaList.remove(i);
                return;
            }
        }
    }

    @Override
    public int createNewArea(Area area) {
        AreaList.add(area.getName());
        int newAreaId = AreaSQLHelper.maxID() + 1;
        area.setAreaId(newAreaId);
        AreaSQLHelper.insert(area, Credential.getPassword());
        gMapFrag.addMarker(area);
        gMapFrag.clearCircle();
        AreaDynamoHelper.getInstance().insert(area);
        return newAreaId;
    }

    @Override
    public void saveFile(String filename, int areaid){
        Cursor returnCursor = getContentResolver().query(filePathUri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        // file size
        User user = UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());
        long totalSizeUsed = 0;
        if(!user.getTotalsizeused().isEmpty()){
            totalSizeUsed = Long.parseLong(user.getTotalsizeused());
        }

        // check size over 1GB
        if(totalSizeUsed + returnCursor.getLong(sizeIndex) > 1000000000){
            Toast.makeText(this, "You have exceeded 1GB of your data",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        File dir = new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/vault");
        if(!dir.exists()){
            dir.mkdir();
        }

        int newFileId = FileSQLHelper.maxID() + 1;
        String currFileName = Credential.getEmail() + newFileId;

        File dst = new File(dir.getAbsolutePath() + "/" + currFileName);
        InputStream in = null;
        OutputStream out = null;

        try{
            in = getContentResolver().openInputStream(filePathUri);
            out = new FileOutputStream(dst);
            Password password = Credential.getPassword();
            Encryption.getInstance(password.getPassword(), password.getSalt()).encryptFile(in,out);

            com.locadoc_app.locadoc.Model.File file = new com.locadoc_app.locadoc.Model.File();
            file.setFileId(newFileId);
            file.setPasswordId(Credential.getPassword().getPasswordid());
            file.setFilesize(dst.length() + "");
            file.setOriginalfilename(filename);
            file.setCurrentfilename(currFileName);
            file.setAreaId(areaid);

            totalSizeUsed += dst.length();
            user.setTotalsizeused("" + totalSizeUsed);
            UserSQLHelper.UpdateRecord(user, Credential.getPassword());
            //changeFileSizeUsed(user.getTotalsizeused());
            UserDynamoHelper.getInstance().updateTotalSizeUsed(totalSizeUsed + "");

            FileSQLHelper.insert(file, Credential.getPassword());
            FileDynamoHelper.getInstance().insert(file);
        } catch (Exception e){
            Log.e("LocAdoc", e.toString());
        }
        finally {
            try{
                in.close();
                out.close();
            } catch (Exception e){
                Log.e("LocAdoc", e.toString());
            }
        }

        // Upload to S3
        S3Helper.uploadFile(dst);
        String key = S3Helper.getIdentity() + "/" + dst.getName();
        showDownloadMessage();
        TransferObserver observer = S3Helper.getUtility().upload(S3Helper.BUCKET_NAME, key, dst);
        observer.setTransferListener(new UploadListener());
    }

    public void showDownloadMessage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Uploading to cloud storage")
                .setMessage("");
        userDialog = builder.create();
        userDialog.show();
    }

    private class UploadListener implements TransferListener {
        @Override
        public void onError(int id, Exception e) {
            Log.e("LocAdoc", "onError: " + id, e);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            String current = S3Helper.getBytesString(bytesCurrent);
            String total = S3Helper.getBytesString(bytesTotal);
            userDialog.setMessage(current + "/" + total);
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if(state == TransferState.COMPLETED){
                userDialog.setMessage("Upload Complete");
            }
        }
    }

    public void openFile(int fileid,String arn,String fn){
        Intent PDFVIEWER = new Intent(this, PDFViewer.class);
        PDFVIEWER.putExtra("fileid", fileid);
        PDFVIEWER.putExtra("areaname",arn);
        PDFVIEWER.putExtra("filename",fn);
        startActivity(PDFVIEWER);
    }

    @Override
    public Location getLastKnownLoc(){
        return mLastLocation;
    }

    @Override
    public boolean isInArea (Area a){
        boolean inArea = false;
        Location loc = new Location("");
        loc.setLatitude(Double.parseDouble(a.getLatitude()));
        loc.setLongitude(Double.parseDouble(a.getLongitude()));
        float rad = loc.distanceTo(mLastLocation);

        if(rad <= Integer.parseInt(a.getRadius())){
            inArea = true;
        }

        return inArea;
    }

    @Override
    public void requestFocus(){
        requestFocus = true;
    }
    public void printOutOfAreaMsg()
    {
        Toast.makeText(this,"You have currently moved out of the area",Toast.LENGTH_LONG).show();
    }
}
