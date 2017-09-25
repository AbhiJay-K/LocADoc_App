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
    private String areaid;
    private String longitude;
    private String latitude;
    private String radius;
    private String adminarea;

    @DynamoDBHashKey(attributeName = "user")
    public String getOwner() {
        return user;
    }

    public void setOwner(String user) {
        this.user = user;
    }

    @DynamoDBAttribute(attributeName = "areaid")
    public String getAreaId() {
        return areaid;
    }

    public void setAreaId(String areaId) {
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

    @DynamoDBAttribute(attributeName = "adminarea")
    public String getAdminArea() {
        return adminarea;
    }

    public void setAdminArea(String adminarea) {
        this.adminarea = adminarea;
    }
}
