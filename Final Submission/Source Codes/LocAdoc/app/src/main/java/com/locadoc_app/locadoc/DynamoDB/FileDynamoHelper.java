package com.locadoc_app.locadoc.DynamoDB;

import android.os.AsyncTask;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper.OperationType;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.helper.Encryption;

import java.util.List;

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

    public String getIdentity(){
        if (DynamoDBHelper.getIdentity().isEmpty()){
            DynamoDBHelper.setIdentity();
        }

        return DynamoDBHelper.getIdentity();
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
        file.setUser(getIdentity());
        Password pwd = Credential.getPassword();
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        file.setCurrentfilename(en.encryptString(file.getCurrentfilename()));
        file.setOriginalfilename(en.encryptString(file.getOriginalfilename()));
        file.setBackedup(en.encryptString(file.getBackedup()));
        file.setFilesize(en.encryptString(file.getFilesize()));

        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        try{
            mapper.save(file);
        }catch (AmazonServiceException ex){}
    }

    // call using thread
    public void insertToDBWithoutEncryption(File file)
    {
        file.setUser(getIdentity());
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        try{
            mapper.save(file);
        }catch (AmazonServiceException ex){}
    }

    public void delete (File file)
    {
        OperationType operation = OperationType.DELETE;
        new DynamoDBTask().execute(operation, file);
    }

    public void deleteFromDB (File file)
    {
        file.setUser(getIdentity());
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.delete(file);
    }

    public File getFileFromDB(int fileid)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        String id = getIdentity();
        try{
            File file = mapper.load(File.class, id, fileid);
            return file;
        } catch (AmazonServiceException ex){}
        return null;
    }

    public List<File> getAllFile ()
    {
        String id = getIdentity();
        PaginatedQueryList<File> result = null;
        try {
            File file = new File();
            file.setUser(id);
            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                    .withHashKeyValues(file)
                    .withConsistentRead(false);

            DynamoDBMapper mapper = DynamoDBHelper.getMapper();
            result = mapper.query(File.class, queryExpression);
        } catch (AmazonServiceException ex){}
        return result;
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
            }

            return null;
        }
    }
}
