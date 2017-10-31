package com.locadoc_app.locadoc.Model;

import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.S3.S3Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 10/3/2017.
 */

public class Credential {
    private static Password PASSWORD;
    private static String email;
    private static CognitoCachingCredentialsProvider credentials;
    private static String identityId = "";
    private static List<Password> oldPasswords;

    private final static String identityPoolId = LocAdocApp.getContext().getString(R.string.aws_identitypool);
    private static final String userPoolId = LocAdocApp.getContext().getString(R.string.aws_userpool);

    public static Password getPassword (){
        return PASSWORD;
    }

    public static void setPassword (Password pass){
        PASSWORD = pass;
    }

    public static String getEmail (){
        return email;
    }

    public static void setEmail (String userEmail){
        email = userEmail;
    }

    public static CognitoCachingCredentialsProvider getCredentials() {
        if(credentials == null){
            setCredentials();
        }

        return credentials;
    }

    public static void setCredentials() {
        credentials = new CognitoCachingCredentialsProvider(
                LocAdocApp.getContext(),
                identityPoolId,
                Regions.AP_SOUTHEAST_1);
        credentials.clear();
        Map<String, String> logins = new HashMap<String, String>();
        logins.put("cognito-idp.ap-southeast-1.amazonaws.com/" + userPoolId,
                AppHelper.getCurrSession().getIdToken().getJWTToken());
        credentials.setLogins(logins);
    }

    public static String getIdentity()
    {
        return identityId;
    }

    public static void setOldPasswords(List<Password> allPassword){
        oldPasswords = new ArrayList<>();

        for(Password p: allPassword){
            if(p.getPasswordid() != PASSWORD.getPasswordid()){
                oldPasswords.add(p);
            }
        }
    }

    public static List<Password> getAllOldPasswords(){
        return oldPasswords;
    }

    public static Password getAnOldPass (int passId){
        for(Password p: oldPasswords){
            if(p.getPasswordid() == passId){
                return p;
            }
        }
        return null;
    }

    public static void addAnOldPass (Password password){
        oldPasswords.add(password);
    }

    public static synchronized void setIdentity()
    {
        if(identityId.isEmpty()){
            identityId = credentials.getIdentityId();
            Log.d("ID",identityId);
        }
    }
    public static void clearAll()
    {
        PASSWORD = null;
        email = "";
        credentials.clear();
        credentials = null;
        identityId = "";
        DynamoDBHelper.setDynamoDBHelperTOnull();
        S3Helper.setS3HelperToNull();
    }
}
