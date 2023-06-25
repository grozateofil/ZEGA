package com.gt.zega.entity;

public class ConsumablesOfMedicalDevice {

    private String name;
    private String code;
    private String brand;

    public ConsumablesOfMedicalDevice(String name, String code, String brand) {
        this.name = name;
        this.code = code;
        this.brand = brand;
    }

    public ConsumablesOfMedicalDevice() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getNameAndCode() {
        return name + ", " + code;
    }

    @Override
    public String toString() {
        return name + ", " + code + ", " + brand;
    }
}
