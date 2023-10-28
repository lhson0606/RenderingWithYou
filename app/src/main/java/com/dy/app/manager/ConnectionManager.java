package com.dy.app.manager;

import android.os.Handler;

import com.dy.app.core.dythread.MessageDispatcher;
import com.dy.app.network.Message;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class ConnectionManager {
    private static ConnectionManager instance = null;
    private InputStream is;
    private OutputStream os;
    private int ack = 0;
    private int seq = 0;
    public final static String TAG = "ConnectionManager";

    public boolean isListening() {
        return isRunning;
    }

    public interface ConnectionManagerCallback {
        void onConnectionManagerReady();
    }

    public interface ConnectionStatusCallback {
        void onConnectionLost();
        void onWeakConnection();
    }

    private ConnectionManager(ConnectionManagerCallback callback) {
        messages = new Vector<>();
        this.callback = callback;
    }

    public static ConnectionManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("ConnectionManager is not initialized or connection has been closed");
        }
        return instance;
    }

    public static ConnectionManager startNewInstance(InputStream inputStream, OutputStream outputStream, ConnectionManagerCallback callback) {
        ConnectionManager.instance = new ConnectionManager(callback);
        ConnectionManager.instance.is = inputStream;
        ConnectionManager.instance.os = outputStream;
        ConnectionManager.instance.isRunning = false;
        callback.onConnectionManagerReady();
        return ConnectionManager.instance;
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
    public void startReceiving(){
        if(receivingThread != null){
            isRunning = false;
            receivingThread.interrupt();
        }
        this.handler = MessageDispatcher.getInstance().getHandler();
        receivingThread = new Thread(new ReceivingThread());
        receivingThread.start();
    }

    private boolean isSending = false;
    private boolean isRunning = false;

    private void startSending() {
        sendingThread = new Thread(new Runnable() {
            private void sendOneMessage(Object o) throws IOException {
                Gson gson = new Gson();
                String json = gson.toJson(o);
                os.write(json.getBytes());
                os.flush();
            }

            @Override
            public void run() {
                isSending = true;

                for(Object o : messages) {
                    try {
                        sendOneMessage(o);
                    } catch (IOException e) {
                        isSending = false;
                        handlePeerConnectionLost();
                        break;
                    }
                }

                isSending = false;
            }
        });

        sendingThread.start();
    }

    public void closeConnection(){
        try{
            sendingThread.interrupt();
            is.close();
            os.close();
        }catch (Exception e){

        }
    }

    private class ReceivingThread implements Runnable{
        private final Gson gson = new Gson();
        private final int BUFFER_SIZE = 1024;
        @Override
        public void run() {
            isRunning = true;
            while(isRunning){
                try{
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int length = is.read(buffer);
                    //check if end of stream
                    if(length <0) continue;

                    String json = new String(buffer, 0, length, StandardCharsets.UTF_8);

                    Message o = gson.fromJson(json, Message.class);

                    if(o != null){
                        // Process the received message on the main (UI) thread
                        handler.obtainMessage(o.getType(), o.getCode(), -1, o).sendToTarget();
                    }
                }catch (Exception e){
                    handlePeerWeakConnection();
                }
            }
        }
    }

    public void setConnectionStatusCallback(ConnectionStatusCallback connectionStatusCallback){
        this.connectionLostCallback = connectionStatusCallback;
    }

    private void handlePeerWeakConnection() {
        if(connectionLostCallback != null){
            connectionLostCallback.onWeakConnection();
        }else{
            throw new RuntimeException("ConnectionLostCallback is not set");
        }
    }

    private void handlePeerConnectionLost(){
        //closeConnection();

        if(connectionLostCallback != null){
            connectionLostCallback.onConnectionLost();
        }else{
            throw new RuntimeException("ConnectionLostCallback is not set");
        }

    }

    public int getAck() {
        return ack;
    }

    public void setAck(int ack) {
        this.ack = ack;
    }

    public int newAck() {
        return ack++;
    }

    public int getSeq() {
        return seq;
    }

    public void increaseSeq() {
        seq++;
    }

    public int newSeq() {
        return seq++;
    }
    public void setConnectionLostCallback(ConnectionStatusCallback connectionLostCallback){
        this.connectionLostCallback = connectionLostCallback;
    }

    private ConnectionManagerCallback callback;
    private ConnectionStatusCallback connectionLostCallback;
}
