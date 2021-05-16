package com.xupt.event;

public class RecognitionEvent {
    private int movementType;

    public RecognitionEvent(int movementType) {
        this.movementType = movementType;
    }

    public int getMovementType() {
        return movementType;
    }

    public void setMovementType(int movementType) {
        this.movementType = movementType;
    }
}
