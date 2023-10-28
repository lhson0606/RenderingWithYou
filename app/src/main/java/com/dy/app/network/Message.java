package com.dy.app.network;

import androidx.annotation.NonNull;

public class Message {
    private byte[] data;
    private int length;
    private int code;
    private int type;
    private String sender;
    private String recipient;
    private long timestamp;
    private int ack;
    private int seq;
    public Message(){
        this.data = null;
        this.length = 0;
        this.type = 0;
        this.sender = null;
        this.recipient = null;
        this.timestamp = 0;
        this.ack = 0;
        this.seq = 0;
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getAck() {
        return ack;
    }

    public void setAck(int ack) {
        this.ack = ack;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
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
        msg.setAck(this.ack);
        msg.setCode(this.code);
        msg.setData(this.data);
        msg.setLength(this.length);
        msg.setRecipient(new String(this.recipient));
        msg.setSender(new String(this.sender));
        msg.setSeq(this.seq);
        msg.setTimestamp(this.timestamp);
        msg.setType(this.type);
        return msg;
    }
}
