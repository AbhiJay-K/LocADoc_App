package com.locadoc_app.locadoc.DynamoDB;

import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper.OperationType;

/**
 * Created by Admin on 9/25/2017.
 */

public class FileDynamoHelper {
    private static FileDynamoHelper helper;

    private FileDynamoHelper() {}

    public static FileDynamoHelper getInstance()
    {
        if(helper == null)
        {
            helper = new FileDynamoHelper();
        }

        return helper;
    }

    // insert and update
    public void insert(File file)
    {
        OperationType operation = OperationType.INSERT;
        new DynamoDBTask().execute(operation, file);
    }

    // call using thread
    public void insertToDB(File file)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.save(file);
    }

    public void delete (File file)
    {
        OperationType operation = OperationType.DELETE;
        new DynamoDBTask().execute(operation, file);
    }

    public void deleteFromDB (File file)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.delete(file);
    }

    public void getFile(String id)
    {
        OperationType operation = OperationType.GET_RECORD;
        new DynamoDBTask().execute(operation, id);
    }

    public File getFileFromDB(String id)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        File file = mapper.load(File.class, id);
        return file;
    }

    private class DynamoDBTask extends
            AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            OperationType operation = (OperationType) objects[0];

            if (operation == OperationType.INSERT) {
                File file = (File) objects[1];
                insertToDB(file);
            } else if (operation == OperationType.DELETE) {
                File file = (File) objects[1];
                deleteFromDB(file);
            } else if (operation == OperationType.GET_RECORD) {
                String id = (String) objects[1];
                getFileFromDB(id);
            }

            return null;
        }
    }
}
