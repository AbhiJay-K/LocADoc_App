package com.locadoc_app.locadoc.S3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.support.v7.app.AlertDialog;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.Model.Credential;

import java.io.File;

/**
 * Created by Admin on 10/10/2017.
 */

public class S3Helper {
    private static S3Helper s3Helper;
    private static AmazonS3Client sS3Client;
    private static TransferUtility transferUtility;

    public final static String BUCKET_NAME = "locadoc-user";

    private S3Helper()
    {
        sS3Client = new AmazonS3Client(Credential.getCredentials());
        sS3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
        transferUtility = TransferUtility.builder().s3Client(sS3Client).context(LocAdocApp.getContext()).build();
        new FetchIdentityId().execute();
    }

    public static S3Helper getHelper() {
        return s3Helper;
    }

    public static AmazonS3Client getInstance()
    {
        return sS3Client;
    }

    public static TransferUtility getUtility()
    {
        return transferUtility;
    }

    public static void init()
    {
        if(s3Helper == null){
            s3Helper = new S3Helper();
        }
    }

    public static String getIdentity()
    {
        return Credential.getIdentity();
    }

    public static synchronized void setIdentity()
    {
        if(Credential.getIdentity().isEmpty()){
            Credential.setIdentity();
        }
    }

    public static void uploadFile(File file){
        String key = getIdentity();
        key = key + "/" + file.getName();
        Log.d("LocAdoc", "UPLOAD: " + key);
        TransferObserver observer = transferUtility.upload(BUCKET_NAME, key, file);
        observer.setTransferListener(new UploadListener());
    }

    public static void downloadFile (String key){
        File file = new File(LocAdocApp.getContext().getFilesDir().getAbsolutePath()+"/vault/" + key);
        key = getIdentity() + "/" + key;
        TransferObserver observer = transferUtility.download(BUCKET_NAME, key, file);
        observer.setTransferListener(new DownloadListener());

    }

    public void removeFile (String key){
        key = getIdentity() + "/" + key;
        new RemoveObject().execute(key);
    }

    public static String getBytesString(long bytes) {
        String[] quantifiers = new String[] {
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0;; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }

    private class FetchIdentityId extends
            AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            setIdentity();
            return null;
        }
    }

    private class RemoveObject extends
            AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... args) {
            sS3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, args[0]));
            return null;
        }
    }

    private static class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e("LocAdoc", "Error during upload: " + id, e);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d("LocAdoc", String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d("LocAdoc", "onStateChanged: " + id + ", " + newState);
        }
    }

    private static class DownloadListener implements TransferListener {
        // Simply updates the list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e("LocAdoc", "onError: " + id, e);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d("LocAdoc", String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            Log.d("LocAdoc", "onStateChanged: " + id + ", " + state);
        }
    }
}
