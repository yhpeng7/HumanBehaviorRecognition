package com.xupt.event;

public class ServerChangeEvent {
    private int serverType;

    private String receiverEmailAddress;

    public ServerChangeEvent(int serverType) {
        this.serverType = serverType;
    }

    public int getServerType() {
        return serverType;
    }

    public void setServerType(int serverType) {
        this.serverType = serverType;
    }

    public String getReceiverEmailAddress() {
        return receiverEmailAddress;
    }

    public void setReceiverEmailAddress(String receiverEmailAddress) {
        this.receiverEmailAddress = receiverEmailAddress;
    }
}
