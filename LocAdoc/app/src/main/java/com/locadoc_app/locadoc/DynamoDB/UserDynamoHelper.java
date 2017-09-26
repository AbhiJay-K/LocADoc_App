package com.locadoc_app.locadoc.DynamoDB;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.locadoc_app.locadoc.Model.User;

/**
 * Created by Admin on 9/25/2017.
 */

public class UserDynamoHelper {
    // insert and update
    public void insert(User user)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.save(user);
    }

    public void delete (User user)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        mapper.delete(user);
    }

    public User getUser(String id)
    {
        DynamoDBMapper mapper = DynamoDBHelper.getMapper();
        User user = mapper.load(User.class, id);
        return user;
    }
}
