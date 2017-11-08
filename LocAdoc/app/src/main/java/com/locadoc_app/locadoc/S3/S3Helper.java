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
import com.locadoc_app.locadoc.DynamoDB.FileDynamoHelper;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.Model.Credential;

import java.io.File;

/**
 * Created by Admin on 10/10/2017.
 */

public class S3Helper {
    private static S3Helper s3Helper;
    private static AmazonS3Client sS3Client;
    private static TransferUtility transferUtility;
    private static boolean isUploading;
    private static int currFileId;
    private static int observerId;

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

    public static void setIsUploading (boolean isUploading2){
        isUploading = isUploading2;
    }

    public static boolean getIsUploading()
    {
        return isUploading;
    }

    public static void setCurrFileId (int currFileId2){
        currFileId = currFileId2;
    }

    public static int getCurrFileId()
    {
        return currFileId;
    }

    public static void setObserverId (int observerId2){
        observerId = observerId2;
    }

    public static void cancelCurrUpload(){
        if(transferUtility != null){
            transferUtility.cancel(observerId);
        }
    }

    public static void init()
    {
        if(s3Helper == null){
            s3Helper = new S3Helper();
        }
    }
    public static void setS3HelperToNull()
    {
        cancelCurrUpload();
        s3Helper = null;
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
        observerId = observer.getId();
    }

    public static void downloadFile (String key){
        File file = new File(LocAdocApp.getContext().getFilesDir().getAbsolutePath()+"/vault/" + key);
        key = getIdentity() + "/" + key;
        transferUtility.download(BUCKET_NAME, key, file);

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

    public static void updateFileBackedUp(){
        com.locadoc_app.locadoc.Model.File file = FileSQLHelper.getFile(currFileId, Credential.getPassword());
        file.setBackedup("true");
        FileSQLHelper.updateRecord(file, Credential.getPassword());
        FileDynamoHelper.getInstance().insert(file);
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
        public void onError(int id, Exception e) {}

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

        @Override
        public void onStateChanged(int id, TransferState newState) {
            if(newState == TransferState.COMPLETED){
                updateFileBackedUp();
                isUploading = false;
            }
        }
    }
}
