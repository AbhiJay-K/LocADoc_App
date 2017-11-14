package com.locadoc_app.locadoc.UI.PDFViewer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.helper.Encryption;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.crypto.SecretKey;

public class PDFViewer extends AppCompatActivity implements OnPageChangeListener,OnLoadCompleteListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    private static Uri PDFFILE;
    private static final String TAG = PDFViewer.class.getSimpleName();
    private PDFView pdfView;
    private Integer pageNumber = 0;
    private String filename;
    private String curArea;
    private String curFile;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Area ar;
    private boolean logout;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);

        logout = true;
        FloatingActionButton fileExplorerfab = (FloatingActionButton) findViewById(R.id.pdfBackFloatingActionButton);
        fileExplorerfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeActivity();
            }
        });

        pdfView = (PDFView)findViewById(R.id.pdfView);
        int fileid = 0;
        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
            if (extras.containsKey("fileid")) {
                fileid = extras.getInt("fileid");
            }
            if(extras.containsKey("areaname"))
            {
               curArea = extras.getString("areaname");
            }
            if(extras.containsKey("filename"))
            {
                curFile = extras.getString("filename");
            }
        }
        new DecryptTask().execute(fileid);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkLocationPermission()){
                buildGoogleApiClient();
            }
        } else{
            buildGoogleApiClient();
        }

        showProgressDialog("Open File", "Opening " + curFile + "...");
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle){
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            createLocationRequest();
        } catch (SecurityException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(5000);
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void showProgressDialog(String title, String msg) {
        pDialog = new ProgressDialog(this);
        pDialog.setTitle(title);
        pDialog.setMessage(msg);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void dismissProgresDialog() {
        try{
            pDialog.dismiss();
        } catch (Exception e){}
        pDialog = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            mLastLocation = location;
            Location l1 = new Location("");
            l1.setLatitude(Double.parseDouble(ar.getLatitude()));
            l1.setLongitude(Double.parseDouble(ar.getLongitude()));
            if(mLastLocation.distanceTo(l1) > Float.parseFloat(ar.getRadius()))
            {
                mGoogleApiClient.disconnect();
                logout = false;
                onStop();
            }
        }catch(NumberFormatException e)
        {
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    //============================
    private void openPDF()
    {
        pdfView.fromUri(PDFFILE).defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }
    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(curArea +"/"+ curFile);
    }

    @Override
    public void loadComplete(int nbPages) {
        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {
            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    public void closeActivity(){
        logout = false;
        exit(1);
    }

    public void exit(int n){
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e){}

        stopLocationUpdates();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        intent.putExtra("exittype",n);
        intent.putExtra("areaname",ar.getName());
        intent.putExtra("logout", logout);
        finish();
    }

    @Override
    public void onStop(){
        super.onStop();

        File dst = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/vault/~" + filename);
        if(dst.exists()) {
            dst.delete();
        }
        exit(2);
    }

    @Override
    public void onBackPressed(){
        logout = false;
        exit(1);
    }

    private class DecryptTask extends
            AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... objects) {
            int fileid = objects[0];
            com.locadoc_app.locadoc.Model.File file = FileSQLHelper.getFile(fileid, Credential.getPassword());
            ar = AreaSQLHelper.getRecord(file.getAreaId(),Credential.getPassword());
            int passwordid = file.getPasswordId();
            Encryption en = Encryption.getInstance("", "");
            SecretKey currKey = null;
            boolean differentPass = false;
            if(passwordid != Credential.getPassword().getPasswordid()){
                currKey = en.getCurrentKey();
                Password password = Credential.getAnOldPass(passwordid);
                en.setKey(password.getPassword(), password.getSalt());
                differentPass = true;
            }

            filename = file.getCurrentfilename();
            File src = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/vault/" + filename);
            File dst = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/vault/~" + filename);
            decryptFile(src, dst);
            PDFFILE = Uri.fromFile(dst);

            if(differentPass){
                en.setCurrKey(currKey);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void objects) {
            dismissProgresDialog();
            openPDF();
        }
    }

    public void decryptFile(File src, File dst){
        InputStream in = null;
        OutputStream out = null;

        try{
            in = new FileInputStream(src);
            out = new FileOutputStream(dst);
            Password password = Credential.getPassword();
            Encryption.getInstance(password.getPassword(), password.getSalt()).decryptFile(in,out);
        } catch (Exception e){}
        finally {
            try{
                in.close();
                out.close();
            } catch (Exception e){}
        }
    }
}
