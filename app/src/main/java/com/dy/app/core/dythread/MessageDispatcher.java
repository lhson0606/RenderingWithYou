package com.dy.app.core.dythread;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import android.os.Handler;

import com.dy.app.network.Message;


public class MessageDispatcher extends MessageDrivenThread{
    private static MessageDispatcher instance = null;

    public static MessageDispatcher getInstance(){
        return instance = (instance == null) ? new MessageDispatcher() : instance;
    }

    private MessageDispatcher(){
        super();
        subscribers = new Vector[2];
        for(int i=0; i<2; i++) subscribers [i] = new Vector<>();
    }

    @Override
    public void handleMsgQueue() {
        Iterator<Message> iterator = msgQueue.iterator();
        while (iterator.hasNext()) {
            Message msg = iterator.next();
            handleOneMsg(msg);
        }
    }

    private Vector<Handler> subscribers [];

    public void subscribe(Handler handler, int channel){
        if(channel<0 || channel>2) throw new RuntimeException("Invalid channel");
        this.subscribers [channel].add(handler);
    }

    private void handleOneMsg(Message msg) {

//        for(Handler handler: subscribers [msg.getType()]){
//
//            try {
//                handler.obtainMessage(msg.getCode(), 0, 0, msg.clone()).sendToTarget();
//            } catch (CloneNotSupportedException e) {
//                throw new RuntimeException(e);
//            }
//        }

        // Create a list to hold the messages for each subscriber
        List<android.os.Message> messagesToSend = new ArrayList<>();

        for (Handler handler : subscribers[msg.getType()]) {
            try {
                messagesToSend.add(handler.obtainMessage(msg.getCode(), 0, 0, msg.clone()));
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        // Now send the collected messages to their respective subscribers
        for (android.os.Message message : messagesToSend) {
            message.sendToTarget();
        }

    }

    public void unsubscribe(Handler handler, int i) {
        if(i<0 || i>2) throw new RuntimeException("Invalid channel");
        this.subscribers [i].remove(handler);
    }
}
