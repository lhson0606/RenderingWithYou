package com.dy.app.network;

import androidx.annotation.NonNull;

public class Message {
    private byte[] data;
    private int length;
    private int code;
    private int type;
    private byte[] sender;
    private byte[] recipient;
    private long timestamp;
    public Message(){
        this.data = null;
        this.length = 0;
        this.type = 0;
        this.sender = null;
        this.recipient = null;
        this.timestamp = 0;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getSender() {
        return sender;
    }

    public void setSender(byte[] sender) {
        this.sender = sender;
    }

    public byte[] getRecipient() {
        return recipient;
    }

    public void setRecipient(byte[] recipient) {
        this.recipient = recipient;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        Message msg = new Message();
        msg.setCode(this.code);
        msg.setData(this.data);
        msg.setLength(this.length);
        msg.setRecipient(recipient);
        msg.setSender(sender);
        msg.setTimestamp(this.timestamp);
        msg.setType(this.type);
        return msg;
    }
}
