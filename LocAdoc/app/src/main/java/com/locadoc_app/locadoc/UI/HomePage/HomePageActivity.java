package com.locadoc_app.locadoc.UI.HomePage;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.provider.OpenableColumns;
import android.provider.Settings;
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
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.locadoc_app.locadoc.helper.Connectivity;
import com.locadoc_app.locadoc.helper.Encryption;
import com.locadoc_app.locadoc.helper.Hash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    private final int OPENFILE = 2;
    private final int ABOUT = 3;
    private final int SETTING = 10;

    private List<String> AreaList;
    private SearchView searchView;
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
    private boolean logout;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        S3Helper.init();
        isEditArea = false;
        logout = true;
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

    //will be called if there is no network connection
    public void remindUserDialog(){
        AlertDialog.Builder builder = new  AlertDialog.Builder(HomePageActivity.this);
        builder.setTitle("There is no network connection");
        builder.setMessage("Make sure you are connected to a Wi-Fi or " +
                "mobile network and try again");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Logout();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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

        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setIconified(false);
        searchView.setSuggestionsAdapter(mAdapter);
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
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {return false;}
            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return false;
            }

        });

        return true;
    }

    // You must implements your logic to get data using OrmLite
    private void populateAdapter(String query) {
        mAdapter.notifyDataSetChanged();
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "AreaName" });
        for (int i=0; i<AreaList.size(); i++) {
            if (AreaList.get(i).toLowerCase().startsWith(query.toLowerCase())) {
                c.addRow(new Object[]{i, AreaList.get(i)});
            }
        }
        mAdapter.swapCursor(c);
    }

   @Override
   public boolean onQueryTextSubmit(String query) {
       return false;
   }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_import) {
            showImportFileFragment();
        } else if (id == R.id.nav_Settings) {
            // Code to access Setting Activity
            logout = false;
            Intent settingActivity = new Intent(this, SettingActivity.class);
            startActivityForResult(settingActivity, SETTING);
        } else if (id == R.id.nav_about) {
            logout = false;
            Intent AboutActivity = new Intent(this, AboutActivity.class);
            startActivityForResult(AboutActivity, ABOUT);
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

    public void showDeleteAccountDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");
        builder.setMessage("Please enter your current password to delete your account!\n(WARNING: all your backup data will also be deleted, " +
            "please use change account in login page if you want to delete it only for this device)");
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setMinimumWidth(10);
        layout.setPadding(40,40,40,40);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        layout.addView(input, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(layout);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!Connectivity.isNetworkAvailable()){
                    Toast.makeText(HomePageActivity.this, "Can not connect to internet. Please check your connection!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String hash = Hash.Hash(input.getText().toString(), Credential.getPassword().getSalt());
                if(hash.equals(Credential.getPassword().getPassword())){
                    presenter.deleteAccount();
                } else{
                    Toast.makeText(HomePageActivity.this, "Wrong password",
                            Toast.LENGTH_SHORT).show();
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
        Credential.clearAll();
        AppHelper.getPool().getCurrentUser().signOut();
        // --------------------------------------------------------------------------------- TMP METHOD TO AVOID CRASH
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }catch(Exception e) {}
        // --------------------------------------------------------------------------------- TMP METHOD TO AVOID CRASH
        mGoogleApiClient.disconnect();
        exit(1);
    }
    public void LogoutLastTime()
    {

        presenter.stopTimer();
        AppHelper.getPool().getCurrentUser().signOut();
        mGoogleApiClient.disconnect();
        exit(2);
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
            if(mLastLocation != null){
                isMock = mLastLocation.isFromMockProvider();
            }
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
        logout = false;
        startTime = SystemClock.elapsedRealtime();
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
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
                        long timeElapsed = SystemClock.elapsedRealtime();
                        filePathUri = resultData.getData();
                        String fileName = getFileName(filePathUri);
                        importFileFragment.setFileName(fileName);

                        if(timeElapsed < startTime){
                            Logout();
                        }else{
                            timeElapsed = (timeElapsed - startTime) / 1000;
                            if(timeElapsed > 60){
                                Logout();
                            }
                        }
                    }
                    break;
                case SETTING:
                case OPENFILE:
                    int type = resultData.getIntExtra("exittype",0);
                    if(type == 2) {
                        String areaname = resultData.getStringExtra("areaname");
                        Toast.makeText(this, "You have moved out of the " + areaname,
                                Toast.LENGTH_LONG).show();
                    }
                case ABOUT:
                    if (resultData != null) {
                        boolean logOut = resultData.getBooleanExtra("logout", true);
                        if(logOut){
                            Logout();
                        }
                    } else {
                        Logout();
                    }
            }
        } else if(requestCode == PICKFILE){
            long timeElapsed = SystemClock.elapsedRealtime();
            if(timeElapsed < startTime){
                Logout();
            }else{
                timeElapsed = (timeElapsed - startTime) / 1000;
                if(timeElapsed > 60){
                    Logout();
                }
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

        final int newFileId = FileSQLHelper.maxID() + 1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String TimeStamp = simpleDateFormat.format(new Date());
        String currFileName = Credential.getEmail() + TimeStamp;

        final File dst = new File(dir.getAbsolutePath() + "/" + currFileName);
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
            file.setBackedup("false");

            totalSizeUsed += dst.length();
            user.setTotalsizeused("" + totalSizeUsed);
            UserSQLHelper.UpdateRecord(user, Credential.getPassword());
            UserDynamoHelper.getInstance().updateTotalSizeUsed(totalSizeUsed + "");

            FileSQLHelper.insert(file, Credential.getPassword());
            FileDynamoHelper.getInstance().insert(file);
        } catch (Exception e){}
        finally {
            try{
                in.close();
                out.close();
            } catch (Exception e){}
        }

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        try{
                            OutputStream out = getContentResolver().openOutputStream(filePathUri);
                            out.flush();
                        } catch(Exception e){}

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Upload to S3
                        String key = S3Helper.getIdentity() + "/" + dst.getName();
                        showDownloadMessage();
                        S3Helper.setIsUploading(true);
                        S3Helper.setCurrFileId(newFileId);
                        TransferObserver observer = S3Helper.getUtility().upload(S3Helper.BUCKET_NAME, key, dst);
                        observer.setTransferListener(new UploadListener());
                        S3Helper.setObserverId(observer.getId());
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to empty the original file from the device?\n" +
                "(WARNING: LocAdoc does not provide file export)")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
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
        public void onError(int id, Exception e) {}

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            String current = S3Helper.getBytesString(bytesCurrent);
            String total = S3Helper.getBytesString(bytesTotal);
            if(userDialog.isShowing()) {
                userDialog.setMessage(current + "/" + total);
            }
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if(state == TransferState.COMPLETED){
                if(userDialog.isShowing()) {
                    userDialog.setMessage("Upload Complete");
                }

                S3Helper.updateFileBackedUp();
                S3Helper.setIsUploading(false);
            }
        }
    }

    @Override
    public boolean checkGPS(){
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        return (gps_enabled || network_enabled);
    }

    public void openFile(int fileid,String arn,String fn){
        logout = false;
        Intent PDFVIEWER = new Intent(this, PDFViewer.class);
        PDFVIEWER.putExtra("fileid", fileid);
        PDFVIEWER.putExtra("areaname",arn);
        PDFVIEWER.putExtra("filename",fn);
        startActivityForResult(PDFVIEWER, OPENFILE);
    }
    @Override
    public void onStop()
    {
        super.onStop();
        if(logout) {
            Logout();
        } else{
            logout = true;
        }
    }
    @Override
    public Location getLastKnownLoc(){
        return mLastLocation;
    }

    @Override
    public boolean isInArea (Area a){
        if(mLastLocation == null){
            return false;
        }

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

    public void exit(int result)
    {
        Intent intent = new Intent();
        intent.putExtra("LogoutResult",result);
        setResult(RESULT_OK, intent);
        finish();
    }
}
