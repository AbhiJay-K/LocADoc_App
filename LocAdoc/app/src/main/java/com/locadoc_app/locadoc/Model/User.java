package com.locadoc_app.locadoc.Model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Admin on 9/24/2017.
 */

@DynamoDBTable(tableName = "LocAdoc_user")
public class User {
    private String identity;
    private String user;
    private String firstname;
    private String lastname;
    private String instanceid;
    private int passwordid;
    private int adminareaid;

    @DynamoDBHashKey(attributeName = "identity")
    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @DynamoDBRangeKey(attributeName = "user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @DynamoDBAttribute(attributeName = "firstname")
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @DynamoDBAttribute(attributeName = "lastname")
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @DynamoDBAttribute(attributeName = "instanceid")
    public String getInstanceID() {
        return instanceid;
    }

    public void setInstanceID(String instanceid) {
        this.instanceid = instanceid;
    }

    @DynamoDBAttribute(attributeName = "passwordid")
    public int getPasswordid() {
        return passwordid;
    }

    public void setPasswordid(int passwordid) {
        this.passwordid = passwordid;
    }

    @DynamoDBAttribute(attributeName = "adminareaid")
    public int getAdminareaid() {
        return adminareaid;
    }

    public void setAdminareaid(int adminareaid) {
        this.adminareaid = adminareaid;
    }
}
