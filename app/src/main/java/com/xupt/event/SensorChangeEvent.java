package com.xupt.event;

import java.util.List;

public class SensorChangeEvent {
    private List<String> sensorName;

    public SensorChangeEvent(List<String> sensorName) {
        this.sensorName = sensorName;
    }

    public List<String> getSensorName() {
        return sensorName;
    }

    public void setSensorName(List<String> sensorName) {
        this.sensorName = sensorName;
    }
}
