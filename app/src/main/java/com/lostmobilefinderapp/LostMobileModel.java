package com.lostmobilefinderapp;

public class LostMobileModel {

    private String location;
    private String description;
    private String lostImage;
    private String key;
    private String ownerName;

    public void setOwnerName(String OwnerName) {
        this.ownerName = OwnerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLostImage() {
        return lostImage;
    }

    public void setLostImage(String lostImage) {
        this.lostImage = lostImage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LostMobileModel(String dataLocation, String dataDescription, String dataImage, String ownerName) {
        this.location = dataLocation;
        this.description = dataDescription;
        this.lostImage = dataImage;
        this.ownerName = ownerName;
    }

    public LostMobileModel() {

    }
}
