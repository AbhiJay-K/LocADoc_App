package com.locadoc_app.locadoc.Model;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.LocAdocApp;
import com.locadoc_app.locadoc.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 10/3/2017.
 */

public class Credential {
    private static Password PASSWORD;
    private static String email;
    private static CognitoCachingCredentialsProvider credentials;
    private static String identityId = "";

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
        Map<String, String> logins = new HashMap<String, String>();
        logins.put("cognito-idp.ap-southeast-1.amazonaws.com/" + userPoolId,
                AppHelper.getCurrSession().getIdToken().getJWTToken());
        credentials.setLogins(logins);
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
}
