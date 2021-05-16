package com.xupt.event;

public class IsCollectEvent {

    //0 正在收集   1 停在原地或未开始收集   2 未选择运动状态   3 未选择运动状态并停在原地
    private int isMovement;

    public IsCollectEvent(int isMovement) {
        this.isMovement = isMovement;
    }

    public int isMovement() {
        return isMovement;
    }

    public void setMovement(int movement) {
        isMovement = movement;
    }
}
