package com.locadoc_app.locadoc.DynamoDB;

import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper.OperationType;

/**
 * Created by Admin on 9/25/2017.
 */

public class PasswordDynamoHelper {
    private static PasswordDynamoHelper helper;

    private PasswordDynamoHelper() {}

    public static PasswordDynamoHelper getInstance()
    {
        if(helper == null)
        {
            helper = new PasswordDynamoHelper();
        }

        return helper;
    }

    // insert and update
    public void insert(Password password)
    {
        OperationType operation = OperationType.INSERT;
        new DynamoDBTask().execute(operation, password);
    }

    // call using thread
    public void insertToDB(Password password)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.save(password);
    }

    public void delete (Password password)
    {
        OperationType operation = OperationType.DELETE;
        new DynamoDBTask().execute(operation, password);
    }

    public void deleteFromDB (Password password)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.delete(password);
    }

    public void getPassword(String id)
    {
        OperationType operation = OperationType.GET_RECORD;
        new DynamoDBTask().execute(operation, id);
    }

    public Password getPasswordFromDB(String id)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        Password password = mapper.load(Password.class, id);
        return password;
    }

    private class DynamoDBTask extends
            AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            OperationType operation = (OperationType) objects[0];

            if (operation == OperationType.INSERT) {
                Password password = (Password) objects[1];
                insertToDB(password);
            } else if (operation == OperationType.DELETE) {
                Password password = (Password) objects[1];
                deleteFromDB(password);
            } else if (operation == OperationType.GET_RECORD) {
                String id = (String) objects[1];
                getPasswordFromDB(id);
            }

            return null;
        }
    }
}
