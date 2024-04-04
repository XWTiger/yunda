package com.tiger.yunda.data.model;

public class AcceptMission {

    //巡检单元
    private String unit;

    //巡检人
    private String user;

    public AcceptMission(String unit, String user) {
        this.unit = unit;
        this.user = user;
    }

    public AcceptMission() {
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
