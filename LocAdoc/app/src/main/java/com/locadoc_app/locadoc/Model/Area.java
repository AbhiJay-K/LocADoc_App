package com.locadoc_app.locadoc.Model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Admin on 9/24/2017.
 */

@DynamoDBTable(tableName = "area")
public class Area {
    private String user;
    private int areaid;
    private String longitude;
    private String latitude;
    private String radius;

    @DynamoDBHashKey(attributeName = "user")
    public String getOwner() {
        return user;
    }

    public void setOwner(String user) {
        this.user = user;
    }

    @DynamoDBHashKey(attributeName = "areaid")
    public int getAreaId() {
        return areaid;
    }

    public void setAreaId(int areaid) {
        this.areaid = areaid;
    }

    @DynamoDBAttribute(attributeName = "longitude")
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @DynamoDBAttribute(attributeName = "latitude")
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @DynamoDBAttribute(attributeName = "radius")
    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }
}
