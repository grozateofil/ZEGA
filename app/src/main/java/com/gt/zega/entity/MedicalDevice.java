package com.gt.zega.entity;

public class MedicalDevice {

    private String deviceCompanyName;
    private String deviceName;
    private String deviceCode;
    private String hospital;
    private String section;

    public MedicalDevice(String deviceCompanyName, String deviceName, String deviceCode, String hospital, String section) {
        this.deviceCompanyName = deviceCompanyName;
        this.deviceName = deviceName;
        this.deviceCode = deviceCode;
        this.hospital = hospital;
        this.section = section;
    }

    public MedicalDevice() {
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

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    @Override
    public String toString() {
        return deviceName + ",  " + deviceCode + ", " + deviceCompanyName;
    }
}
