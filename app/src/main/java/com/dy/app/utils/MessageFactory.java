package com.dy.app.utils;


import com.dy.app.gameplay.Player;
import com.dy.app.gameplay.Rival;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.network.Message;
import com.dy.app.network.MessageType;

public class MessageFactory {
    private Player player = null;
    private Rival rival = null;

    public Message createChatMessage(String message, int code) {
        Message msg = new Message();
        msg.setType(MessageType.CHAT.ordinal());
        msg.setSender(player.getName());
        msg.setRecipient(rival.getName());
        msg.setTimestamp(System.currentTimeMillis());
        msg.setData(message.getBytes());
        msg.setLength(message.getBytes().length);
        msg.setAck(ConnectionManager.getInstance().getAck());
        msg.setSeq(ConnectionManager.getInstance().getSeq());
        msg.setCode(code);
        return msg;
    }

    public Message createSystemMessage(String message, int code) {
        Message msg = new Message();
        msg.setType(MessageType.SYSTEM.ordinal());
        msg.setSender(player.getName());
        msg.setRecipient(rival.getName());
        msg.setTimestamp(System.currentTimeMillis());
        msg.setData(message.getBytes());
        msg.setLength(message.getBytes().length);
        msg.setAck(ConnectionManager.getInstance().getAck());
        msg.setSeq(ConnectionManager.getInstance().getSeq());
        msg.setCode(code);
        return msg;
    }

    private MessageFactory() {
        player = Player.getInstance();
        rival = Rival.getInstance();
    }

    private static MessageFactory instance = null;

    public static MessageFactory getInstance() {
        if(instance != null) return instance;
        instance = new MessageFactory();
        return instance;
    }
}
