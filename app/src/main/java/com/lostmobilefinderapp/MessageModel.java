package com.lostmobilefinderapp;

public class MessageModel {
    private String messageId;
    private String senderUsername;
    private String message;

    public MessageModel(String messageId, String senderUsername, String message) {
        this.messageId = messageId;
        this.senderUsername = senderUsername;
        this.message = message;
    }

    public MessageModel() {
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getMessage() {
        return message;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
