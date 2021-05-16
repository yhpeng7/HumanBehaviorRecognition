package com.xupt.event;

public class DataTypeChangeEvent {
    private int dataType;

    public DataTypeChangeEvent(int dataType) {
        this.dataType = dataType;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}
