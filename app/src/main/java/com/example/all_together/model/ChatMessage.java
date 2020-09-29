package com.example.all_together.model;

public class ChatMessage {

    private String messageText;
    private String sender;
    private String receiver;
    private long messageTime;

    public ChatMessage() {
    }

    public ChatMessage(String messageText, String sender, String receiver, long messageTime) {
        this.messageText = messageText;
        this.sender = sender;
        this.receiver = receiver;
        this.messageTime = messageTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
