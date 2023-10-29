package com.dy.app.manager;

import android.os.Handler;
import android.util.Log;

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
    private int seq = 0;
    private int ack = 0;
    private boolean clientReceived = false;
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

    public boolean isSending(){
        return isSending;
    }

    public static ConnectionManager startNewInstance(InputStream inputStream, OutputStream outputStream, ConnectionManagerCallback callback) {
        ConnectionManager.instance = new ConnectionManager(callback);
        ConnectionManager.instance.is = inputStream;
        ConnectionManager.instance.os = outputStream;
        ConnectionManager.instance.isRunning = false;
        callback.onConnectionManagerReady();
        return ConnectionManager.instance;
    }

    private Vector<Message> messages;
    private int currentMsg = 0;


    public synchronized void postMessage(Message o) {
        messages.add(o);
        if(!isSending){
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
    private Gson gson = new Gson();

    private synchronized void startSending() {
        sendingThread = new Thread(new Runnable() {
            private void sendOneMessage(Message o) throws IOException, InterruptedException {
                clientReceived = false;
                while(!clientReceived){
                    o.setSeq(seq);
                    String json = gson.toJson(o);
                    os.write(json.getBytes());
                    os.flush();
                    Thread.sleep(69);
                    Log.d(TAG, "sendOneMessage: " + seq);
                }
                Log.d(TAG, "ack: " + seq);
                ++seq;
            }

            @Override
            public void run() {
                isSending = true;

                for(; currentMsg<messages.size(); currentMsg++){
                    try {
                        sendOneMessage(messages.get(currentMsg));
                    } catch (IOException e) {
                        isSending = false;
                        handlePeerConnectionLost();
                        break;
                    } catch (InterruptedException e) {
                        //#todo did not handle yet
                        throw new RuntimeException(e);
                    }
                }

                isSending = false;
            }
        });

        sendingThread.start();
    }

    public void closeConnection(){
        try{
            isRunning = false;
            receivingThread.interrupt();
            //wait for sending to complete
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(isSending){
                        try {
                            Thread.sleep(69);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    sendingThread.interrupt();
                    try {
                        is.close();
                        os.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            thread.start();
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

                    if(o==null) continue;

                    if(o.getType() == -1){

                        if(seq == o.getAck()){
                            clientReceived = true;
                            Log.d(TAG, "client echo: " + o.getAck());
                        }
                        //ackMsg.setSeq(seq);
                        continue;
                    }

                    Message ackMsg = new Message();
                    ackMsg.setType(-1);
                    ackMsg.setAck(o.getSeq());

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String json = gson.toJson(ackMsg);
                            try {
                                os.write(json.getBytes());
                                os.flush();
                            } catch (IOException e) {
                                handlePeerConnectionLost();
                            }
                        }
                    });

                    t.start();

                    if(o.getSeq() != ack){
                        Log.d(TAG, "run: " + o.getSeq() + " " + ack);
                        continue;
                    }

                    ++ack;

                    handler.obtainMessage(o.getType(), o.getCode(), -1, o).sendToTarget();
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


    public int getNextSeq() {
        return seq++;
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
