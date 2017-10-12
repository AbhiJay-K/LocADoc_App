package com.locadoc_app.locadoc.DynamoDB;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper.OperationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String getIdentity(){
        if (DynamoDBHelper.getIdentity().isEmpty()){
            DynamoDBHelper.setIdentity();
        }

        return DynamoDBHelper.getIdentity();
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
        password.setUser(getIdentity());
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();

        try{
            mapper.save(password);
        }catch (AmazonServiceException ex){
            Log.e("LocAdoc", "Error: " + ex);
        }
    }

    public void delete (Password password)
    {
        OperationType operation = OperationType.DELETE;
        new DynamoDBTask().execute(operation, password);
    }

    public void deleteFromDB (Password password)
    {
        password.setUser(getIdentity());
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.delete(password);
    }

    public void getPassword(int passwordid)
    {
        OperationType operation = OperationType.GET_RECORD;
        new DynamoDBTask().execute(operation, passwordid);
    }

    public Password getPasswordFromDB(int passwordid)
    {
        String id = getIdentity();
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        try{
            Password password = mapper.load(Password.class, id, passwordid);
            Log.d("LocAdoc", "id: " + password.getPasswordid() + ", password: " + password.getPassword()
                    + ", salt: " + password.getSalt() + ", user: " + password.getUser());
            return password;
        } catch (AmazonServiceException ex){
            Log.e("LocAdoc", "Error: " + ex);
        }
        return null;
    }

    public void getAll()
    {
        OperationType operation = OperationType.GET_ALL;
        new DynamoDBTask().execute(operation);
    }

    public List<Password> getAllPassword ()
    {
        PaginatedQueryList<Password> result = null;
        String id = getIdentity();
        try {
            Password pass = new Password();
            pass.setUser(id);
            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                    .withHashKeyValues(pass)
                    .withConsistentRead(false);

            DynamoDBMapper mapper = DynamoDBHelper.getMapper();
            result = mapper.query(Password.class, queryExpression);
        } catch (AmazonServiceException ex){
            Log.e("LocAdoc", "Error: " + ex);
        }
        return result;
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
                int passwordid = (Integer) objects[1];
                getPasswordFromDB(passwordid);
            } else if (operation == OperationType.GET_ALL){
                List<Password> list = getAllPassword();
                if(list==null)
                    return null;
                for(Password password: list){
                    Log.d("LocAdoc", "id: " + password.getPasswordid() + ", password: " + password.getPassword()
                            + ", salt: " + password.getSalt() + ", user: " + password.getUser());
                }
            }

            return null;
        }
    }
}
