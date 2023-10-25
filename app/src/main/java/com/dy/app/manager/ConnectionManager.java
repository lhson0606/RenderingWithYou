package com.dy.app.manager;

import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

public class ConnectionManager {
    private static ConnectionManager instance = null;
    private InputStream is;
    private OutputStream os;
    private ConnectionManager() {
        messages = new Vector<>();
    }

    public static ConnectionManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("ConnectionManager is not initialized");
        }
        return instance;
    }

    public static ConnectionManager startNewInstance(InputStream inputStream, OutputStream outputStream) {
        ConnectionManager.instance = new ConnectionManager();
        ConnectionManager.instance.is = inputStream;
        ConnectionManager.instance.os = outputStream;
        return instance;
    }

    private Vector<Object> messages;

    public void postMessage(Object o) {
        if(isSending){
            messages.add(o);
        }
        else{
            messages.clear();
            messages.add(o);
            startSending();
        }
    }
    private Thread sendingThread;
    private Thread receivingThread;
    private Handler handler;
    private void startReceiving(Handler handler){
        if(receivingThread != null){
            receivingThread.interrupt();
        }
        this.handler = handler;
        receivingThread = new Thread(new ReceivingThread());
        receivingThread.start();
    }

    private boolean isSending = false;

    private void startSending() {
        isSending = true;
        sendingThread = new Thread(new Runnable() {
            private void sendOneMessage(Object o) {
                Gson gson = new Gson();
                String json = gson.toJson(o);
                try {
                    os.write(json.getBytes());
                    os.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void run() {
                for(Object o : messages) {
                    sendOneMessage(o);
                }

                isSending = false;
            }
        });

        sendingThread.start();
    }

    public void closeConnection(){
        try{
            sendingThread.interrupt();
            instance = null;
            is.close();
            os.close();
        }catch (Exception e){

        }
    }

    private class ReceivingThread implements Runnable{
        @Override
        public void run() {
            while(true){
                try{
                    byte[] buffer = new byte[1024];
                    int length = is.read(buffer);
                    String json = new String(buffer, 0, length);
                    Gson gson = new Gson();
                    Object o = gson.fromJson(json, Object.class);
                    if(o != null){
                        handler.obtainMessage(0, length, -1, o).sendToTarget();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
