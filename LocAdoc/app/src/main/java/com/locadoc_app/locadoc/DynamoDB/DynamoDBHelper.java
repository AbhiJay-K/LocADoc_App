package com.locadoc_app.locadoc.DynamoDB;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 9/25/2017.
 */

public class DynamoDBHelper {
    private static DynamoDBHelper dynamoDBHelper;
    private static AmazonDynamoDBClient ddb;
    private static DynamoDBMapper mapper;
    public enum OperationType {
       INSERT, DELETE, GET_RECORD, GET_ALL
    }

    private DynamoDBHelper(Context context)
    {
        ddb = new AmazonDynamoDBClient(Credential.getCredentials());
        ddb.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
        mapper = DynamoDBMapper.builder().dynamoDBClient(ddb).build();
        new FetchIdentityId().execute();
    }

    public static AmazonDynamoDBClient getInstance()
    {
        return ddb;
    }

    public static DynamoDBMapper getMapper()
    {
        return mapper;
    }

    public static void init(Context context)
    {
        if(dynamoDBHelper == null){
            dynamoDBHelper = new DynamoDBHelper(context);
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

    private class FetchIdentityId extends
            AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            setIdentity();
            return null;
        }
    }
}
