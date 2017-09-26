package com.locadoc_app.locadoc.DynamoDB;


import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper.OperationType;

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

    // insert and update
    public void insert(User user)
    {
        OperationType operation = OperationType.INSERT;
        new DynamoDBTask().execute(operation, user);
    }

    // call using thread
    public void insertToDB(User user)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.save(user);
    }

    public void delete (User user)
    {
        OperationType operation = OperationType.DELETE;
        new DynamoDBTask().execute(operation, user);
    }

    public void deleteFromDB (User user)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.delete(user);
    }

    public void getUser(String id)
    {
        OperationType operation = OperationType.GET_RECORD;
        new DynamoDBTask().execute(operation, id);
    }

    public User getUserFromDB(String id)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        User user = mapper.load(User.class, id);
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
                String id = (String) objects[1];
                getUserFromDB(id);
            }

            return null;
        }
    }
}
