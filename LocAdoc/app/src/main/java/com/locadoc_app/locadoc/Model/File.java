package com.locadoc_app.locadoc.Model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Admin on 9/24/2017.
 */

@DynamoDBTable(tableName = "file")
public class File {
    private String user;
    private String fileid;
    private String currentfilename;
    private String originalfilename;
    private String modified;
    private String passwordid;
    private String areaid;

    @DynamoDBHashKey(attributeName = "user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    @DynamoDBAttribute(attributeName = "fileid")
    public String getFileId() {
        return fileid;
    }

    public void setFileId(String fileid) {
        this.fileid = fileid;
    }

    @DynamoDBAttribute(attributeName = "currentfilename")
    public String getCurrentfilename() {
        return currentfilename;
    }

    public void setCurrentfilename(String currentfilename) {
        this.currentfilename = currentfilename;
    }

    @DynamoDBAttribute(attributeName = "originalfilename")
    public String getOriginalfilename() {
        return originalfilename;
    }

    public void setOriginalfilename(String originalfilename) {
        this.originalfilename = originalfilename;
    }

    @DynamoDBAttribute(attributeName = "modified")
    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    @DynamoDBAttribute(attributeName = "passwordid")
    public String getPasswordId() {
        return passwordid;
    }

    public void setPasswordId(String passwordid) {
        this.passwordid = passwordid;
    }

    @DynamoDBAttribute(attributeName = "areaid")
    public String getAreaId() {
        return areaid;
    }

    public void setAreaId(String areaid) {
        this.areaid = areaid;
    }
}
