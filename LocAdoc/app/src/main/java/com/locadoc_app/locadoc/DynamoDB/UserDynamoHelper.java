package com.locadoc_app.locadoc.DynamoDB;


import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper.OperationType;
import com.locadoc_app.locadoc.helper.Encryption;

/**
 * Created by Admin on 9/25/2017.
 */

public class UserDynamoHelper {
    private static UserDynamoHelper helper;

    private UserDynamoHelper() {}

    public static UserDynamoHelper getInstance()
    {
        if(helper == null)
        {
            helper = new UserDynamoHelper();
        }

        return helper;
    }

    public String getIdentity(){
        if (DynamoDBHelper.getIdentity().isEmpty()){
            DynamoDBHelper.setIdentity();
        }

        return DynamoDBHelper.getIdentity();
    }

    // insert and update
    public void insert(User user)
    {
        OperationType operation = OperationType.INSERT;
        new DynamoDBTask().execute(operation, user);
    }

    // call using thread
    public void insertToDB(User user)
    {
        user.setIdentity(getIdentity());
        Password pwd = Credential.getPassword();
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        user.setFirstname(en.encryptString(user.getFirstname()));
        user.setLastname(en.encryptString(user.getLastname()));
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        try{
            mapper.save(user);
        }catch (AmazonServiceException ex){
            Log.e("LocAdoc", "Error: " + ex);
        }
    }

    public void delete (User user)
    {
        OperationType operation = OperationType.DELETE;
        new DynamoDBTask().execute(operation, user);
    }

    public void deleteFromDB (User user)
    {
        user.setIdentity(getIdentity());
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.delete(user);
    }

    public void getUser(String email)
    {
        OperationType operation = OperationType.GET_RECORD;
        new DynamoDBTask().execute(operation, email);
    }

    public User getUserFromDB(String email)
    {
        String id = getIdentity();
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        User user = mapper.load(User.class, id, email);
        return user;
    }

    private class DynamoDBTask extends
            AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            OperationType operation = (OperationType) objects[0];

            if (operation == OperationType.INSERT) {
                User user = (User) objects[1];
                insertToDB(user);
            } else if (operation == OperationType.DELETE) {
                User user = (User) objects[1];
                deleteFromDB(user);
            } else if (operation == OperationType.GET_RECORD) {
                String email = (String) objects[1];
                getUserFromDB(email);
            }

            return null;
        }
    }
}
