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
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.itextpdf.text.List;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.S3.S3Helper;

import java.util.Map;

public class FileExplorerFragment extends Fragment
        implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener{
    public interface FileExplorerFragmentListener{
        void openGoogleMap();
        Location getLastKnownLoc();
        void openFile(int fileid);
    }

    private ListView listView;
    private boolean exploreArea;
    private Map<String, Integer> allAreaAround;
    private Map<String,Integer> allFileInArea;
    private AlertDialog userDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_file_explorer, container, false);
        exploreArea = true;

        getAllAreaAround();
        String[] strArr = allAreaAround.keySet().toArray(new String[allAreaAround.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, strArr);
        listView = (ListView) rootView.findViewById(R.id.ListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        FloatingActionButton fileExplorerfab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        fileExplorerfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(exploreArea){
                    FileExplorerFragmentListener listener = (FileExplorerFragmentListener) getActivity();
                    listener.openGoogleMap();
                } else{
                    getAllAreaAround();
                    String[] strArr = allAreaAround.keySet().toArray(new String[allAreaAround.size()]);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, strArr);
                    listView.setAdapter(adapter);
                    exploreArea = true;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> args, View v, int position, long id) {
        if(exploreArea) {
            String data = (String) args.getItemAtPosition(position);
            int areaid = allAreaAround.get(data);
            allFileInArea = FileSQLHelper.getFilesInArea(areaid, Credential.getPassword());
            String[] strArr = allFileInArea.keySet().toArray(new String[allFileInArea.size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, strArr);
            listView.setAdapter(adapter);
            exploreArea = false;
        } else{
            String data = (String) args.getItemAtPosition(position);
            int fileid = allFileInArea.get(data);
            File fileInfo = FileSQLHelper.getFile(fileid, Credential.getPassword());
            String key = fileInfo.getCurrentfilename();
            java.io.File file = new java.io.File(LocAdocApp.getContext().getFilesDir().getAbsolutePath()+"/vault/" + key);

            if(file.exists()){
                FileExplorerFragmentListener listener = (FileExplorerFragmentListener) getActivity();
                listener.openFile(fileid);
                //NEW ONE
                // listener.openFile(file)
            } else{
                showDownloadMessage(key, fileInfo.getOriginalfilename(), file);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> args, View v, int position, long id) {
        if(exploreArea) {
            String data = (String) args.getItemAtPosition(position);
            int areaid = allAreaAround.get(data);
            Toast.makeText(getActivity(), "Area name: " + data + " selected",
                    Toast.LENGTH_SHORT).show();

        } else{
            String data = (String) args.getItemAtPosition(position);
            int fileid = allFileInArea.get(data);
            Toast.makeText(getActivity(), "File name: " + data + " selected",
                    Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    public void getAllAreaAround()
    {
        FileExplorerFragmentListener activity = (FileExplorerFragmentListener) getActivity();
        Location loc = activity.getLastKnownLoc();
        allAreaAround = AreaSQLHelper.getAreaNameInLoc(loc, Credential.getPassword());
    }

    public void beginDownload(String key, java.io.File file){
        key = Credential.getIdentity() + "/" + key;
        TransferObserver observer = S3Helper.getUtility().download(S3Helper.BUCKET_NAME, key, file);
        observer.setTransferListener(new DownloadListener());
    }

    public void showDownloadMessage(final String key, final String fileName, final java.io.File file) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("File Not Found").setMessage("Do you wish to download the file from backup?")
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
        builder.setTitle("Downloading " + fileName).setMessage("").setNeutralButton("Hide", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
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
            Log.d("LocAdoc", String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
            String current = S3Helper.getBytesString(bytesCurrent);
            String total = S3Helper.getBytesString(bytesTotal);
            userDialog.setMessage(current + "/" + total);
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            Log.d("LocAdoc", "onStateChanged: " + id + ", " + state);
        }
    }
}
