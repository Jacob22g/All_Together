package com.example.all_together.model;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private String sideAUid;
    private String sideBUid;
    private String chatID;
    private String receiverName;

//    private List<ChatMessage> chatMessages;

    public Chat() {

//        chatMessages = new ArrayList<>();
    }

//    public Chat(String chatID, String sideAUid, String sideBUid, String receiverName) {
//        this.sideAUid = sideAUid;
//        this.sideBUid = sideBUid;
//        this.chatID = chatID;
//        this.receiverName = receiverName;
//
////        chatMessages = new ArrayList<>();
//    }

    public Chat(String chatID, String sideAUid, String sideBUid) {
        this.sideAUid = sideAUid;
        this.sideBUid = sideBUid;
        this.chatID = chatID;
//        chatMessages = new ArrayList<>();
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

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
