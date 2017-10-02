package com.locadoc_app.locadoc.DynamoDB;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.Model.Area;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 9/25/2017.
 */

public class DynamoDBHelper {
    private static DynamoDBHelper dynamoDBHelper;
    private static AmazonDynamoDBClient ddb;
    private static DynamoDBMapper mapper;
    private static CognitoCachingCredentialsProvider credentials;
    private static String identityId = "";
    //private final static String identityPoolId = "ap-southeast-1:c5bd72e5-6825-429f-8d33-f13046eda875";
    private final static String identityPoolId = "ap-southeast-1:365da5cf-e75f-4e4f-84bb-d99244df4408";
    private static final String userPoolId = "ap-southeast-1_SsME563KX";
    public enum OperationType {
       INSERT, DELETE, GET_RECORD, GET_ALL
    }

    private DynamoDBHelper(Context context)
    {
        credentials = new CognitoCachingCredentialsProvider(
                context,
                identityPoolId,
                Regions.AP_SOUTHEAST_1);
        Map<String, String> logins = new HashMap<String, String>();
        logins.put("cognito-idp.ap-southeast-1.amazonaws.com/" + userPoolId,
                AppHelper.getCurrSession().getIdToken().getJWTToken());
        credentials.setLogins(logins);
        ddb = new AmazonDynamoDBClient(credentials);
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

    public static CognitoCachingCredentialsProvider getCache()
    {
        return credentials;
    }

    public static String getIdentity()
    {
        return identityId;
    }

    public static synchronized void setIdentity()
    {
        if(identityId.isEmpty()){
            identityId = credentials.getIdentityId();
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
