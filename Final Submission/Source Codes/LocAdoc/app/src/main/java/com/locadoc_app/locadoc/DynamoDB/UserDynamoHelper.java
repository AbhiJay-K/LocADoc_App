package com.locadoc_app.locadoc.DynamoDB;


import android.os.AsyncTask;
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

    public void updateTotalSizeUsed(String totalSizeUsed)
    {
        OperationType operation = OperationType.UPDATE_TOTAL_SIZE;
        new DynamoDBTask().execute(operation, totalSizeUsed);
    }

    // update total size only
    public void updateTotalSizeFromDB(String totalSizeUsed)
    {
        User user = getUserFromDB(Credential.getEmail());
        user.setTotalsizeused(totalSizeUsed);
        insertToDB(user);
    }

    // call using thread
    public void insertToDB(User user)
    {
        user.setIdentity(getIdentity());
        Password pwd = Credential.getPassword();
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        user.setFirstname(en.encryptString(user.getFirstname()));
        user.setLastname(en.encryptString(user.getLastname()));
        user.setTotalsizeused(en.encryptString(user.getTotalsizeused()));
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        try{
            mapper.save(user);
        }catch (AmazonServiceException ex){}
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

    public User getUserFromDB(String email)
    {
        String id = getIdentity();
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        User user = null;

        try {
            user = mapper.load(User.class, id, email);
            if (user != null) {
                Password pwd = PasswordDynamoHelper.getInstance().getPasswordFromDB(user.getPasswordid());
                Credential.setPassword(pwd);
                Encryption en = Encryption.getInstance(pwd.getPassword(), pwd.getSalt());
                user.setFirstname(en.decrypttString(user.getFirstname()));
                user.setLastname(en.decrypttString(user.getLastname()));
                user.setTotalsizeused(en.decrypttString(user.getTotalsizeused()));
            }
        } catch(Exception e){}

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
            } else if (operation == OperationType.UPDATE_TOTAL_SIZE) {
                String totalSizeUsed = (String) objects[1];
                updateTotalSizeFromDB(totalSizeUsed);
            }

            return null;
        }
    }
}
