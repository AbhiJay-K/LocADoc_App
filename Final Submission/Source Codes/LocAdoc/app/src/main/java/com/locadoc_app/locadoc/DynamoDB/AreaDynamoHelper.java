package com.locadoc_app.locadoc.DynamoDB;

import android.os.AsyncTask;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper.OperationType;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.helper.Encryption;
import java.util.List;

/**
 * Created by Admin on 9/25/2017.
 */

public class AreaDynamoHelper {
    private static AreaDynamoHelper helper;

    private AreaDynamoHelper() {}

    public static AreaDynamoHelper getInstance()
    {
        if(helper == null)
        {
            helper = new AreaDynamoHelper();
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
    public void insert(Area area)
    {
        OperationType operation = OperationType.INSERT;
        new DynamoDBTask().execute(operation, area);
    }

    // call using thread
    public void insertToDB(Area area)
    {
        area.setOwner(getIdentity());
        Password pwd = Credential.getPassword();
        Encryption en = Encryption.getInstance(pwd.getPassword(),pwd.getSalt());
        area.setName(en.encryptString(area.getName()));
        area.setDescription(en.encryptString(area.getDescription()));
        area.setLatitude(en.encryptString(area.getLatitude()));
        area.setLongitude(en.encryptString(area.getLongitude()));
        area.setRadius(en.encryptString(area.getRadius()));

        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        try{
            mapper.save(area);
        }catch (AmazonServiceException ex){}
    }

    public void insertToDBWithoutEncryption(Area area)
    {
        area.setOwner(getIdentity());
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();

        try{
            mapper.save(area);
        }catch (AmazonServiceException ex){}
    }

    public void delete (Area area)
    {
        OperationType operation = OperationType.DELETE;
        new DynamoDBTask().execute(operation, area);
    }

    public void deleteFromDB (Area area)
    {
        area.setOwner(getIdentity());
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.delete(area);
    }

    public Area getAreaFromDB(int areaid)
    {
        String id = getIdentity();
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        try{
            Area area = mapper.load(Area.class, id, areaid);
            return area;
        } catch (AmazonServiceException ex){}
        return null;
    }

    public List<Area> getAllArea ()
    {
        String id = getIdentity();
        PaginatedQueryList<Area> result = null;
        try {
            Area area = new Area();
            area.setOwner(id);
            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                    .withHashKeyValues(area)
                    .withConsistentRead(false);
            DynamoDBMapper mapper = DynamoDBHelper.getMapper();
            result = mapper.query(Area.class, queryExpression);
        } catch (AmazonServiceException ex){}

        return result;
    }

    private class DynamoDBTask extends
            AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            OperationType operation = (OperationType) objects[0];

            if (operation == OperationType.INSERT) {
                Area area = (Area) objects[1];
                insertToDB(area);
            } else if (operation == OperationType.DELETE) {
                Area area = (Area) objects[1];
                deleteFromDB(area);
            }

            return null;
        }
    }
}
