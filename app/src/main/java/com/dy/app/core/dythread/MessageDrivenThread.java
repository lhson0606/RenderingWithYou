package com.dy.app.core.dythread;

import android.os.Looper;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageDrivenThread extends Thread{
    private Handler handler;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private Lock lock;
    private Condition condition;
    protected Vector<com.dy.app.network.Message> msgQueue;

    public MessageDrivenThread(){
        lock = new ReentrantLock();
        condition = lock.newCondition();
        msgQueue = new Vector<>();

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                addMessage((com.dy.app.network.Message)msg.obj);
            }
        };
    }

    private void addMessage(com.dy.app.network.Message obj) {
        msgQueue.add(obj);
        if(isPaused){
            resumeThread();
        }
    }

    @Override
    public void run() {
        isRunning = true;
        while(isRunning){
            if(isPaused){
                lock.lock();
                try{
                    condition.await();
                } catch (InterruptedException e) {
                    //stopThread();
                } finally {
                    lock.unlock();
                }
            }else{
                handleMsgQueue();
                msgQueue.clear();
                pauseThread();
            }
        }
    }

    public Handler getHandler(){
        return handler;
    }

    private void resumeThread() {
        isRunning = true;
        isPaused = false;
        lock.lock();
        try{
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public void pauseThread(){
        isPaused = true;
    }

    //will be implemented by subclass
    public void handleMsgQueue(){
        // Handle messages in the queue
    }

    public void stopThread(){
        isRunning = false;
        lock.lock();
        try{
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}
