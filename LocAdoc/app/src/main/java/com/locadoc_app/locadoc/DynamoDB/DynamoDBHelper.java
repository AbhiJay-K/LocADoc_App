package com.locadoc_app.locadoc.DynamoDB;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.locadoc_app.locadoc.Cognito.AppHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 9/25/2017.
 */

public class DynamoDBHelper {
    private static AmazonDynamoDBClient ddb;
    private static DynamoDBMapper mapper;

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
        CognitoCachingCredentialsProvider credentials = new CognitoCachingCredentialsProvider(
                context,
                "",//identity pool id
                Regions.AP_SOUTHEAST_1);
        Map<String, String> logins = new HashMap<String, String>();
        logins.put("cognito-idp.ap-southeast-1.amazonaws.com/ap-southeast-1_SsME563KX",
                AppHelper.getCurrSession().getIdToken().getJWTToken());
        credentials.setLogins(logins);
        ddb = new AmazonDynamoDBClient(credentials);
        ddb.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
        mapper = DynamoDBMapper.builder().dynamoDBClient(ddb).build();
    }

    public enum OperationType {
        INSERT, DELETE, GET_RECORD
    }
}
