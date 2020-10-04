package com.example.all_together.model;

public class Chat {

    private String sideAUid;
    private String sideBUid;
    private long chatID;
    private String receiverName;

    public Chat() {
    }

    public Chat(long chatID, String sideAUid, String sideBUid, String receiverName) {
        this.sideAUid = sideAUid;
        this.sideBUid = sideBUid;
        this.chatID = chatID;
        this.receiverName = receiverName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSideAUid() {
        return sideAUid;
    }

    public void setSideAUid(String sideAUid) {
        this.sideAUid = sideAUid;
    }

    public String getSideBUid() {
        return sideBUid;
    }

    public void setSideBUid(String sideBUid) {
        this.sideBUid = sideBUid;
    }

    public long getChatID() {
        return chatID;
    }

    public void setChatID(long chatID) {
        this.chatID = chatID;
    }
}
