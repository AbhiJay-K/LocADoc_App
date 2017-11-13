package com.locadoc_app.locadoc.DynamoDB;

import android.os.AsyncTask;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper.OperationType;
import java.util.List;

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
        }catch (AmazonServiceException ex){}
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

    public Password getPasswordFromDB(int passwordid)
    {
        String id = getIdentity();
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        try{
            Password password = mapper.load(Password.class, id, passwordid);
            return password;
        } catch (AmazonServiceException ex){}
        return null;
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
        } catch (AmazonServiceException ex){}
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
            }

            return null;
        }
    }
}
