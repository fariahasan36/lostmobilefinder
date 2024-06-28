package com.lostmobilefinderapp;

public class FoundMobileModel {
    private String location;
    private String description;
    private String foundImage;
    private String key;
    private String finderName;
    public void setFinderName(String finderName){
        this.finderName = finderName;
    }

    public String getFinderName(){
        return finderName;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFoundImage() {
        return foundImage;
    }

    public void setFoundImage(String foundImage) {
        this.foundImage = foundImage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public FoundMobileModel(String location, String description, String foundImage, String finderName) {
        this.location = location;
        this.description = description;
        this.foundImage = foundImage;
        this.finderName = finderName;
    }
    public FoundMobileModel(){

    }
}
