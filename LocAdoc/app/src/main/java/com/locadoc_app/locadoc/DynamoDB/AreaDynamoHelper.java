package com.locadoc_app.locadoc.DynamoDB;

import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.DynamoDB.DynamoDBHelper.OperationType;

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

    // insert and update
    public void insert(Area area)
    {
        OperationType operation = OperationType.INSERT;
        new DynamoDBTask().execute(operation, area);
    }

    // call using thread
    public void insertToDB(Area area)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.save(area);
    }

    public void delete (Area area)
    {
        OperationType operation = OperationType.DELETE;
        new DynamoDBTask().execute(operation, area);
    }

    public void deleteFromDB (Area area)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.delete(area);
    }

    public void getArea(String id)
    {
        OperationType operation = OperationType.GET_RECORD;
        new DynamoDBTask().execute(operation, id);
    }

    public Area getAreaFromDB(String id)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        Area area = mapper.load(Area.class, id);
        return area;
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
            } else if (operation == OperationType.GET_RECORD) {
                String id = (String) objects[1];
                getAreaFromDB(id);
            }

            return null;
        }
    }
}
