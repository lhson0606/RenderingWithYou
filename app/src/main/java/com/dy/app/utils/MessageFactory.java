package com.dy.app.utils;


import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.gameplay.player.PlayerProfile;
import com.dy.app.network.Message;
import com.dy.app.network.MessageType;

public class MessageFactory {
    private Player player = null;
    private Rival rival = null;

    public Message createDataMessage(byte[] data, int code) {
        Message msg = new Message();
        msg.setType(MessageType.DATA_MESSAGE_TYPE);
        msg.setSender(player.profile.get(PlayerProfile.KEY_USERNAME).toString().getBytes());
        msg.setRecipient(rival.getName().getBytes());
        msg.setTimestamp(System.currentTimeMillis());
        msg.setData(data);

        if(data != null)
        {
            msg.setLength(data.length);
        }else{
            msg.setLength(0);
        }

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
