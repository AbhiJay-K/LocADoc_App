package com.locadoc_app.locadoc.UI.HomePage;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.S3.S3Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileExplorerFragment extends Fragment
        implements AdapterView.OnItemClickListener {
    public interface FileExplorerFragmentListener{
        void openGoogleMap();
        Location getLastKnownLoc();
        void openFile(int fileid,String arn,String fn);
        void showFileOperationFragment(int fileid);
        boolean isInArea (Area a);
        boolean checkGPS();
    }
    private int areaID;
    private ListView listView;
    private boolean exploreArea;
    Map<String,Integer> allFileInArea;
    private Map<String, Integer> allAreaAround;
    private AlertDialog userDialog;
    private FloatingActionButton fileExplorerfab;
    private String curAreaName;
    private String curFileName;
    private boolean isDownloading;
    private TransferObserver observer;
    private FileExplorerFragmentListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_file_explorer, container, false);
        exploreArea = true;

        getAllAreaAround();
        ArrayList<String> areaList = new ArrayList<>(allAreaAround.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, areaList);
        listView = (ListView) rootView.findViewById(R.id.ListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listener = (FileExplorerFragmentListener) getActivity();

        fileExplorerfab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        fileExplorerfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!listener.checkGPS()){
                    Toast.makeText(getActivity(), "GPS is off, please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(exploreArea){
                    listener.openGoogleMap();
                } else{
                    getAllAreaAround();
                    ArrayList<String> areaList = new ArrayList<>(allAreaAround.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, areaList);
                    listView.setAdapter(adapter);
                    exploreArea = true;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> args, View v, int position, long id) {
        if(!listener.checkGPS()){
            Toast.makeText(getActivity(), "GPS is off, please turn on GPS", Toast.LENGTH_SHORT).show();
            return;
        }

        if(exploreArea) {
            String data = (String) args.getItemAtPosition(position);
            areaID = allAreaAround.get(data);
            Area a = AreaSQLHelper.getRecord(areaID,Credential.getPassword());
            curAreaName = a.getName();
            if(!listener.isInArea(a))
            {
                //activity.printOutOfAreaMsg();
                //fileExplorerfab.performClick();
                listener.openGoogleMap();
            }
            else {
                allFileInArea = FileSQLHelper.getFilesInArea(areaID, Credential.getPassword());
                ArrayList<String> fileList = new ArrayList<>(allFileInArea.keySet());
                MyCustomAdapter adapter = new MyCustomAdapter(getActivity(), R.layout.item_file, fileList);

                listView.setAdapter(adapter);
                exploreArea = false;
            }
        } else{
            Area a = AreaSQLHelper.getRecord(areaID,Credential.getPassword());
            if(!listener.isInArea(a))
            {
                //activity.printOutOfAreaMsg();
                //fileExplorerfab.performClick();
                getAllAreaAround();
                ArrayList<String> areaList = new ArrayList<>(allAreaAround.keySet());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, areaList);
                listView.setAdapter(adapter);
                exploreArea = true;
            }
            else {
                String data = (String) args.getItemAtPosition(position);
                int fileid = allFileInArea.get(data);
                File fileInfo = FileSQLHelper.getFile(fileid, Credential.getPassword());
                curFileName = fileInfo.getOriginalfilename();
                String key = fileInfo.getCurrentfilename();
                java.io.File file = new java.io.File(LocAdocApp.getContext().getFilesDir().getAbsolutePath() + "/vault/" + key);

                if (file.exists()) {
                    listener.openFile(fileid,curAreaName,curFileName);
                } else {
                    Log.d("LocAdoc", "curr: " + fileInfo.getOriginalfilename());
                    showDownloadMessage(key, fileInfo.getOriginalfilename(),
                            S3Helper.getBytesString(Long.parseLong(fileInfo.getFilesize())), file);
                }
            }
        }
    }

    public void getAllAreaAround()
    {
        Location loc = listener.getLastKnownLoc();
        allAreaAround = AreaSQLHelper.getAreaNameInLoc(loc, Credential.getPassword());
    }

    public void removeFile(String filename){
        allFileInArea.remove(filename);
        ArrayList<String> fileList = new ArrayList<>(allFileInArea.keySet());
        MyCustomAdapter adapter = new MyCustomAdapter(getActivity(), R.layout.item_file, fileList);
        listView.setAdapter(adapter);
    }

    public void beginDownload(String key, java.io.File file){
        key = Credential.getIdentity() + "/" + key;
        observer = S3Helper.getUtility().download(S3Helper.BUCKET_NAME, key, file);
        observer.setTransferListener(new DownloadListener());
        isDownloading = true;
    }

    public void showDownloadMessage(final String key, final String fileName, String fileSize, final java.io.File file) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("File Not Found").setMessage("Do you wish to download the file from backup? (Size: " +
                fileSize + ")")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            userDialog.dismiss();
                        } catch (Exception e) {}
                        showProgressMessage(fileName);
                        beginDownload(key, file);
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

    public void showProgressMessage(String fileName){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Downloading " + fileName)
                .setMessage("")
                .setNeutralButton("Cancel download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if(isDownloading){
                        S3Helper.getUtility().cancel(observer.getId());
                        isDownloading = false;
                    }

                    userDialog.dismiss();
                } catch (Exception e) {}
            }
        });
        userDialog = builder.create();
        userDialog.setCancelable(false);
        userDialog.show();
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
            userDialog.setMessage(current + "/" + total);
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if(state == TransferState.COMPLETED){
                isDownloading = true;
                userDialog.dismiss();
                userDialog = null;

                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Download Finished")
                        .setMessage(curFileName + " has been downloaded");
                userDialog = builder.create();
                userDialog.setCancelable(true);
                userDialog.show();
            }
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<String> {

        private ArrayList<String> fileList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> fileList) {
            super(context, textViewResourceId, fileList);
            this.fileList = fileList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.item_file, null);

                TextView fileName = (TextView) convertView.findViewById(R.id.ItemFileName);
                fileName.setText(fileList.get(position));
                ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.FileSettingsButton);
                imageButton.setImageResource(R.drawable.ic_menu_black_24dp);
                imageButton.setTag(position);

                imageButton.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        String filename = fileList.get(position);
                        int fileid = allFileInArea.get(filename);
                        listener.showFileOperationFragment(fileid);

                    }
                });
            }

            return convertView;
        }

    }

}
