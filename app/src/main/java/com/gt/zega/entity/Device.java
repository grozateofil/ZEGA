package com.gt.zega.entity;

public class Device {

    private String deviceCompanyName;
    private String deviceName;
    private String deviceCode;

    public Device(String deviceCompanyName, String deviceName, String deviceCode) {
        this.deviceCompanyName = deviceCompanyName;
        this.deviceName = deviceName;
        this.deviceCode = deviceCode;
    }

    public Device() {
    }

    public String getDeviceCompanyName() {
        return deviceCompanyName;
    }

    public void setDeviceCompanyName(String deviceCompanyName) {
        this.deviceCompanyName = deviceCompanyName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getNameAndCode() {
        return deviceName + ",  " + deviceCode;
    }

    @Override
    public String toString() {
        return deviceName + ",  " + deviceCode + ", " + deviceCompanyName;
    }
}
