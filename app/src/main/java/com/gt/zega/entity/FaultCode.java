package com.gt.zega.entity;

public class FaultCode {
    private String code;
    private String description;

    public FaultCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public FaultCode() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return code + ", " + description;
    }
}
