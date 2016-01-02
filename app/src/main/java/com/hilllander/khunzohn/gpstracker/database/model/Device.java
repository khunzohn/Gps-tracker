package com.hilllander.khunzohn.gpstracker.database.model;

/**
 * Created by ubunphyu on 1/2/16.
 */
public class Device {
    private long id;
    private String deviceName;
    private String deviceType;
    private String simNumber;
    private String password;
    private String authorization;
    private float latitude;
    private float longitude;
    private String trackedDate;
    private String trackedTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTrackedTime() {
        return trackedTime;
    }

    public void setTrackedTime(String trackedTime) {
        this.trackedTime = trackedTime;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getSimNumber() {
        return simNumber;
    }

    public void setSimNumber(String simNumber) {
        this.simNumber = simNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getTrackedDate() {
        return trackedDate;
    }

    public void setTrackedDate(String trackedDate) {
        this.trackedDate = trackedDate;
    }

    @Override
    public String toString() {
        return deviceName;
    }
}
