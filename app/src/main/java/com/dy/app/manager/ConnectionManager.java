package com.dy.app.manager;

import android.util.Log;

import com.dy.app.network.Message;
import com.dy.app.network.IMessageHandler;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionManager {
    public static ConnectionManager getInstance(){
        if (instance == null) {
            throw new RuntimeException("ConnectionManager is not initialized or connection has been closed");
        }
        return instance;
    }

    public void postMessage(Message msg){
        try{
            semMsgQueue.acquire();
            //add message to queue
            msgQueue.add(msg);
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            semMsgQueue.release();
        }
        //notify worker
        worker.awake();
    }

    public static void startNewInstance(InputStream inputStream, OutputStream outputStream, ConnectionManagerCallback callback) {
        ConnectionManager.instance = new ConnectionManager(inputStream, outputStream);
        callback.onConnectionManagerReady();
    }


    private ConnectionManager(InputStream is, OutputStream os) {
        msgQueue = new Vector<>();
        semMsgQueue = new Semaphore(1);

        this.is = is;
        this.os = os;

        sender = new Sender();
        receiver = new Receiver();
        worker = new Worker();

        sender.start();
        worker.start();
    }

    public synchronized void startReceiving(IMessageHandler messageHandler){
        //if receiver is not running, start it
        if(!receiver.isRunning()){
            receiver.setMessageHandler(messageHandler);
            receiver.start();
        }else{//else simply set the message handler
            receiver.setMessageHandler(messageHandler);
        }
    }

    public void closeConnection(){
        //#todo
        worker.close();
        receiver.close();
        sender.close();
    }

    private class Sender extends Thread{
        private final Object lock = new Object();
        private boolean isRunning = false;
        private Semaphore semRunningSendingThread;
        private Vector<Message> sendingQueue;

        public Sender(){
            sendingQueue = new Vector<>();
            semRunningSendingThread = new Semaphore(1);
        }

        public void acquireMutex() throws InterruptedException {
            semRunningSendingThread.acquire();
        }

        public void releaseMutex(){
            semRunningSendingThread.release();
        }

        public void addMsg(Vector<Message> messages){
            sendingQueue.addAll(messages);
        }

        public void awake(){
            synchronized (lock){
                lock.notify();
            }
        }

        private void checkMessageValidity(String jsonData){
            if(jsonData.contains("\n")){
                throw new RuntimeException("message contains new line character");
            }
        }

        private void sendMessage(Message msg) throws IOException, InterruptedException {
            String json = gson.toJson(msg);
            //check for new line character because it's our delimiter
            checkMessageValidity(json);
            //add delimiter
            json += "\n";
            os.write(json.getBytes());
            os.flush();
        }

        @Override
        public void run() {
            isRunning = true;

            while(isRunning){
                try{
                    //currently sending, stop other threads from waking it up
                    acquireMutex();

                    for(Message msg : sendingQueue){
                        sendMessage(msg);
                    }

                    sendingQueue.clear();

                    synchronized (lock){
                        //sleep, wait for some threads to wake it up
                        releaseMutex();
                        //sleep
                        lock.wait();
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                }
            }

            Log.d(TAG, "run: sender thread closed");
        }

        public void close(){
            //#todo
            isRunning = false;
            awake();
        }
    }

    private class Receiver extends Thread{
        private boolean isRunning = false;
        private BufferedReader reader;
        public Receiver(){
            reader = new BufferedReader(new InputStreamReader(is));
        }
        private IMessageHandler handler;
        private final ReentrantLock handlerLock = new ReentrantLock();

        @Override
        public void run() {
            isRunning = true;

            while(isRunning){
                try {
                    String jsonMsg = reader.readLine();
                    processJsonString(jsonMsg);
                } catch (IOException e) {
                    //#todo
                    //throw new RuntimeException(e);
                }
            }

            Log.d(TAG, "run: receiver thread closed");
        }

        private void processJsonString(String json){
            Message receivedMsg = null;
            try{
                receivedMsg = gson.fromJson(json, Message.class);
            }catch (Exception e) {
                Log.e(TAG, "processJsonString: ", e);
            }

            if(receivedMsg == null) return;

            handlerLock.lock();
            handler.onNewMessageArrive(receivedMsg);
            handlerLock.unlock();
        }

        public void close(){
            //#todo
            isRunning = false;
        }

        public void setMessageHandler(IMessageHandler handler){
            handlerLock.lock();
            this.handler = handler;
            handlerLock.unlock();
        }

        public boolean isRunning(){
            return isRunning;
        }
    }

    private class Worker extends Thread{
        private final Object lock = new Object();
        private boolean isRunning = false;

        public void awake(){
            synchronized (lock){
                lock.notify();
            }
        }

        public Worker(){
        }

        @Override
        public void run() {
            isRunning = true;

            while(isRunning){
                try{
                    sender.acquireMutex();
                    semMsgQueue.acquire();
                    sender.addMsg(msgQueue);
                    msgQueue.clear();
                    sender.awake();
                    sender.releaseMutex();

                    synchronized (lock){
                        semMsgQueue.release();
                        lock.wait();
                    }

                } catch (InterruptedException e){
                    //#todo
                    //semMsgQueue.release();
                    throw new RuntimeException("not implemented");
                }
            }

            Log.d(TAG, "run: worker thread closed");
        }

        public void close(){
            isRunning = false;
            awake();
        }
    }

    public interface ConnectionManagerCallback {
        void onConnectionManagerReady();
    }

    public interface ConnectionStatusCallback {
        void onConnectionLost();
        void onWeakConnection();
    }

    private static ConnectionManager instance = null;
    private InputStream is;
    private OutputStream os;

    public final static String TAG = "ConnectionManager";
    private Vector<Message> msgQueue;
    private Semaphore semMsgQueue;
    private Sender sender;
    private Receiver receiver;
    private Worker worker;
    private final Gson gson = new Gson();
}
