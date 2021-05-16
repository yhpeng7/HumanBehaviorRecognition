package com.xupt.bean;

public class SensorDataBean {

    /**
     * 用户行为类别
     * 0.走 1.跑 2.跳 3.上楼 4.下楼 -1.未知状态
     */
    private int behaviorType;

    private int age;

    private int gender;

    private String region;

    private String sensorData;

    public int getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(int behaviorType) {
        this.behaviorType = behaviorType;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSensorData() {
        return sensorData;
    }

    public void setSensorData(String sensorData) {
        this.sensorData = sensorData;
    }

    @Override
    public String toString() {
        return "{" +
                "behaviorType:" + behaviorType +
                ", age:" + age +
                ", gender:" + gender +
                ", region:'" + region + '\'' +
                ", sensorData:'" + sensorData + '\'' +
                '}';
    }
}