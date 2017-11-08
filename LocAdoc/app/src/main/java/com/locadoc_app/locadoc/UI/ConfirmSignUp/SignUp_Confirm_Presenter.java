package com.locadoc_app.locadoc.UI.ConfirmSignUp;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.locadoc_app.locadoc.Cognito.AppHelper;
import com.locadoc_app.locadoc.LocalDB.ApplicationInstance;
import com.locadoc_app.locadoc.LocalDB.UserSQLHelper;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.helper.Hash;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by AbhiJay_PC on 15/9/2017.
 */

public class SignUp_Confirm_Presenter implements SignUp_Confirm_Presenter_Interface{


    private Activity_SignUp_Confirm activity;

    public SignUp_Confirm_Presenter(Activity_SignUp_Confirm activity) {
        this.activity = activity;
    }
    //Sends confirmation code to AWS
    public void sendConfCode(String userName, String confirmCode, String uhint, String Chint) {

        if(userName == null || userName.length() < 1) {
            activity.setLabelConfirmUserID(uhint + " cannot be empty");
            return;
        }
        if(confirmCode == null || confirmCode.length() < 1) {
            activity.setLabelConfirmCode(Chint +" cannot be empty");
            return;
        }
        AppHelper.getPool().getUser(userName).confirmSignUpInBackground(confirmCode, true, confHandler);
    }
    //request for new conformation code
    public void reqConfCode(String userName,String uhint) {
        if(userName == null || userName.length() < 1) {
            activity.setLabelConfirmUserID(uhint + " cannot be empty");
            return;
        }
        AppHelper.getPool().getUser(userName).resendConfirmationCodeInBackground(resendConfCodeHandler);
    }

    GenericHandler confHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
            String TimeStamp = simpleDateFormat.format(new Date());
            String random =  UUID.randomUUID().toString();
            String Instance = Hash.Hash(TimeStamp,random);
            ApplicationInstance.insert(Instance);
            Password p = new Password();
            String salt = Hash.SecureRandomGen();
            String pwdDigest = Hash.Hash(activity.getPassword(),salt);
            p.setPasswordid(1);
            p.setPassword(pwdDigest);
            p.setSalt(salt);
            Credential.setPassword(p);
            Credential.setEmail(activity.getUserName());
            User usr = new User();
            usr.setUser(activity.getUserName());
            usr.setFirstname(activity.getfName());
            usr.setLastname(activity.getlName());
            usr.setPasswordid(1);
            usr.setAdminareaid(0);
            UserSQLHelper.insert(usr,Credential.getPassword());
            activity.showDialogMessage("Success!",activity.getUsername()+" has been confirmed!", true);
        }

        @Override
        public void onFailure(Exception exception) {
            activity.SetConfhandlerMessage();
            activity.showDialogMessage("Confirmation failed", AppHelper.formatException(exception), false);
        }
    };

    VerificationHandler resendConfCodeHandler = new VerificationHandler() {
        @Override
        public void onSuccess(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {

            activity.SetReConfCodeHandlerSuccessMessage();
            activity.showDialogMessage("Confirmation code sent.","Code sent to "+cognitoUserCodeDeliveryDetails.getDestination()+" via "+cognitoUserCodeDeliveryDetails.getDeliveryMedium()+".", false);
        }

        @Override
        public void onFailure(Exception exception) {

            activity.SetReConfCodeHandlerFailMessage();
            activity.showDialogMessage("Confirmation code request has failed", AppHelper.formatException(exception), false);
        }
    };

}
