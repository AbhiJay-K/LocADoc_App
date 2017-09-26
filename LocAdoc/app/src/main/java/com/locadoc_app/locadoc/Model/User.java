package com.locadoc_app.locadoc.Model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Admin on 9/24/2017.
 */

@DynamoDBTable(tableName = "user")
public class User {
    private String user;
    private String firstname;
    private String lastname;
    private String loggedin;
    private String macaddress;
    private int passwordid;
    private int adminareaid;

    @DynamoDBHashKey(attributeName = "user")
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

    @DynamoDBAttribute(attributeName = "loggedin")
    public String getLoggedin() {
        return loggedin;
    }

    public void setLoggedin(String loggedin) {
        this.loggedin = loggedin;
    }

    @DynamoDBAttribute(attributeName = "macaddress")
    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
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
