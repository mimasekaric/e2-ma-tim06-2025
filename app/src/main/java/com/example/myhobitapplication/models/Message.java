package com.example.myhobitapplication.models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Message {
    private String senderId;
    private String senderName;
    private String text;
    private Date messageSent;

    public Message() {}

    public Message(String senderId, String senderName, String text, Date timestamp) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.messageSent = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getText() {
        return text;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMessageSent(Date messageSent) {
        this.messageSent = messageSent;
    }

    public Date getMessageSent() {
        return messageSent;
    }
}
