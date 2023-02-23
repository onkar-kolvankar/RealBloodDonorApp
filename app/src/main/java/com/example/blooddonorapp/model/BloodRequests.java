package com.example.blooddonorapp.model;

public class BloodRequests {
    private String bloodGroup;
    private String hospital;
    private String latitude;
    private String longitude;
    private String pushKey;
    private String mobileNo;

    public BloodRequests() {
    }

    public BloodRequests(String pushKey , String bloodGroup, String hospital, String latitude, String longitude) {
        this.bloodGroup = bloodGroup;
        this.hospital = hospital;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pushKey = pushKey;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
