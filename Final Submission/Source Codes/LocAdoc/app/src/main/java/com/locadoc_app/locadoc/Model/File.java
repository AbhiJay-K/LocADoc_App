package com.locadoc_app.locadoc.Model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Admin on 9/24/2017.
 */

@DynamoDBTable(tableName = "LocAdoc_file")
public class File {
    private String user;
    private int fileid;
    private String currentfilename;
    private String originalfilename;
    private String backedup;
    private String filesize;
    private int passwordid;
    private int areaid;

    @DynamoDBHashKey(attributeName = "user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @DynamoDBRangeKey(attributeName = "fileid")
    public int getFileId() {
        return fileid;
    }

    public void setFileId(int fileid) {
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

    @DynamoDBAttribute(attributeName = "backedup")
    public String getBackedup() {
        return backedup;
    }

    public void setBackedup(String backedup) {
        this.backedup = backedup;
    }

    @DynamoDBAttribute(attributeName = "filesize")
    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    @DynamoDBAttribute(attributeName = "passwordid")
    public int getPasswordId() {
        return passwordid;
    }

    public void setPasswordId(int passwordid) {
        this.passwordid = passwordid;
    }

    @DynamoDBAttribute(attributeName = "areaid")
    public int getAreaId() {
        return areaid;
    }

    public void setAreaId(int areaid) {
        this.areaid = areaid;
    }
}
